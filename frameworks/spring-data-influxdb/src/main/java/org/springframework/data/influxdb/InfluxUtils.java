package org.springframework.data.influxdb;

import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 工具类
 */
public final class InfluxUtils {

    private InfluxUtils() {
    }


    public static final String UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final ThreadLocal<SoftReference<SimpleDateFormat>> SDF_CACHE = new ThreadLocal<>();

    /**
     * 解析UTC格式的时间
     *
     * @param utcTime UTC时间字符串
     * @return 返回解析后的Date
     */
    public static Date parseUTC(String utcTime) {
        return parseUTC(utcTime, null);
    }

    /**
     * 解析UTC格式的时间
     *
     * @param utcTime      UTC时间字符串
     * @param defaultValue 默认值
     * @return 返回解析后的Date
     */
    public static Date parseUTC(String utcTime, Date defaultValue) {
        SimpleDateFormat utcSdf = null;
        SoftReference<SimpleDateFormat> reference = SDF_CACHE.get();
        if (reference != null) {
            utcSdf = reference.get();
            if (utcSdf == null) {
                utcSdf = new SimpleDateFormat(UTC);
                reference = null;
            }
        }

        if (reference == null) {
            utcSdf = (utcSdf != null ? utcSdf : new SimpleDateFormat(UTC));
            reference = new SoftReference<>(utcSdf);
        }
        SDF_CACHE.set(reference);

        try {
            return utcSdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取父类的泛型参数类型
     */
    public static Class getGenericSuperclassBounds(Class clazz) {
        Type type = clazz.getGenericSuperclass();
        while (!(type instanceof Class)) {
            if (type instanceof WildcardType) {
                type = ((WildcardType) type).getUpperBounds()[0];
            } else if (type instanceof TypeVariable<?>) {
                type = ((TypeVariable<?>) type).getBounds()[0];
            } else if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                if (types == null || types.length == 0) {
                    return Object.class;
                }
                if (types.length > 1) {
                    throw new RuntimeException(clazz.getName()
                            + "继承的泛型" + parameterizedType + "的实参数量多于1个");
                }
                type = parameterizedType.getActualTypeArguments()[0];
            } else if (type instanceof GenericArrayType) {
                type = ((GenericArrayType) type).getGenericComponentType();
            }
        }
        return (Class) type;
    }

    /**
     * 迭代Class
     *
     * @param type        类
     * @param interceptor 拦截器
     * @param filter      过滤器
     * @param handler     处理器
     * @return 返回存储字段的Map
     */
    public static <T> void foreach(final Class<?> type,
                                   final Call<Class<?>, T[]> call,
                                   final Predicate<T> interceptor,
                                   final Predicate<T> filter,
                                   final Handler<T> handler) {
        if (type == null || type == Object.class) {
            return;
        }
        T[] ts = call.apply(type);
        for (T field : ts) {
            if (filter != null) {
                if (filter.test(field)) {
                    handler.onHandle(field);
                }
            } else {
                handler.onHandle(field);
            }
            if (interceptor.test(field)) {
                return;
            }
        }
        foreach(type.getSuperclass(), call, interceptor, filter, handler);
    }

    /**
     * 获取声明的类
     */
    public static Class<?> getDeclaringClass(Member member) {
        return member != null ? member.getDeclaringClass() : null;
    }

    /**
     * 获取声明的类
     */
    public static boolean isDeclaringClass(Member member, Class<?> type) {
        return getDeclaringClass(member) == type;
    }

    /**
     * 是否被static修饰
     *
     * @param modifiers 修饰符
     * @return 返回结果
     */
    public static boolean isStatic(int modifiers) {
        return Modifier.isStatic(modifiers);
    }

    /**
     * 是否被static修饰
     *
     * @param member 修饰符
     * @return 返回结果
     */
    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * 是否被final修饰
     *
     * @param modifiers 修饰符
     * @return 返回结果
     */
    public static boolean isFinal(int modifiers) {
        return Modifier.isFinal(modifiers);
    }

    /**
     * 是否被static修饰
     *
     * @param member 修饰符
     * @return 返回结果
     */
    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    /**
     * 是否为常量(被static和final修饰)
     *
     * @param modifiers 修饰符
     * @return 返回结果
     */
    public static boolean isConst(int modifiers) {
        return Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers);
    }

    /**
     * 是否被static 和 final 修饰
     *
     * @param modifiers 修饰符
     * @return 返回结果
     */
    public static boolean isStaticOrFinal(int modifiers) {
        return Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers);
    }

    /**
     * 是否为静态或者final字段
     *
     * @param member 字段
     * @return 返回判断的值
     */
    public static boolean isStaticOrFinal(Member member) {
        return member == null || isStaticOrFinal(member.getModifiers());
    }

    /**
     * 获取某个字段
     *
     * @param type  类型
     * @param field 字段
     * @return 返回获取的字段对象
     */
    public static Field getField(Class<?> type, String field) {
        if (isNonNull(type, field)
                && !field.isEmpty()
                && type != Object.class) {
            try {
                return type.getDeclaredField(field);
            } catch (NoSuchFieldException e) {/* ignore */}
            return getField(type.getSuperclass(), field);
        }
        return null;
    }

    /**
     * 获取存储类字段的Map
     *
     * @param type    类
     * @param filter  过滤器
     * @param handler 处理器
     * @return 返回存储字段的Map
     */
    public static void foreachField(final Class<?> type,
                                    final Predicate<Field> filter,
                                    final Handler<Field> handler) {
        foreachField(type, FIELD_INTERCEPTOR, filter, handler);
    }

    /**
     * 获取存储类字段的Map
     *
     * @param type        类
     * @param interceptor 拦截器
     * @param filter      过滤器
     * @param handler     处理器
     * @return 返回存储字段的Map
     */
    public static void foreachField(final Class<?> type,
                                    final Predicate<Field> interceptor,
                                    final Predicate<Field> filter,
                                    final Handler<Field> handler) {
        foreach(type, FIELDS_CALL, interceptor, filter, handler);
    }

    /**
     * 获取存储类字段的Map
     *
     * @param type   类
     * @param filter 过滤器
     * @return 返回存储字段的Map
     */
    public static Map<String, Field> getFieldMap(
            Class<?> type, Predicate<Field> filter) {
        Map<String, Field> fieldMap = new ConcurrentHashMap<>();
        return getFieldMap(type, fieldMap, filter);
    }

    /**
     * 获取存储类字段的Map，不包含final和static的field
     *
     * @param type 类
     * @return 返回存储字段的Map
     */
    public static Map<String, Field> getFieldMap(Class<?> type) {
        Map<String, Field> fieldMap = new ConcurrentHashMap<>();
        return getFieldMap(type, fieldMap, field -> !isStaticOrFinal(field));
    }

    /**
     * 获取存储类字段的Map
     *
     * @param type     类
     * @param fieldMap 存储字段的Map
     * @param filter   过滤器
     * @return 返回存储字段的Map
     */
    public static Map<String, Field> getFieldMap(final Class<?> type,
                                                 final Map<String, Field> fieldMap,
                                                 final Predicate<Field> filter) {
        foreachField(type, filter, field -> fieldMap.putIfAbsent(field.getName(), field));
        return fieldMap;
    }

    /**
     * 给对象中某个字段设置值
     *
     * @param o     对象
     * @param field 字段
     * @param value 值
     * @param <T>   类型
     */
    public static <T> void setFieldValue(T o, Field field, Object value) {
        if (field != null && o != null) {
            setAccessible(field, true);
            try {
                field.set(o, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取对象某字段的值
     *
     * @param field 字段对象
     * @param o     对象
     * @return 返回获取的值
     */
    public static <T> Object getFieldValue(Field field, T o) {
        return getFieldValue(field, o, null);
    }

    /**
     * 获取对象某字段的值
     *
     * @param field 字段对象
     * @param o     对象
     * @return 返回获取的值
     */
    public static Object getFieldValue(Field field, Object o, Object defaultValue) {
        if (isNonNull(o, field)) {
            setAccessible(field, true);
            try {
                Object value = field.get(o);
                return value != null ? value : defaultValue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    /**
     * 获取基本的字段和值对象
     *
     * @param o 对象
     * @return 返回字段集合对应的值
     */
    public static Map<String, Object> getFieldValues(Object o) {
        if (o == null || o.getClass() == Object.class) {
            return new LinkedHashMap<>(0);
        }
        Class<?> type = o.getClass();
        Map<String, Field> fieldMap = getFieldMap(type, field -> !isStaticOrFinal(field));
        return getFieldValues(fieldMap, o);
    }

    /**
     * 获取基本的字段和值对象
     *
     * @param fieldMap 字段集合
     * @param o        对象
     * @return 返回字段集合对应的值
     */
    public static Map<String, Object> getFieldValues(Map<String, Field> fieldMap, Object o) {
        if (fieldMap == null || fieldMap.isEmpty()) {
            return new LinkedHashMap<>(0);
        }
        final Map<String, Object> values = new LinkedHashMap<>(fieldMap.size());
        for (String name : fieldMap.keySet()) {
            Object value = getFieldValue(fieldMap.get(name), o);
            if (value != null
                    && value.getClass() != Object.class
                    && !(value instanceof java.util.Date)) {
                value = getFieldValues(value);
            }
            values.put(name, value);
        }
        return values;
    }

    /**
     * 根据构造函数实例化某个类的对象
     *
     * @param constructor 构造函数
     * @param <T>         类型
     * @return 返回实例的对象
     */
    public static <T> T newInstance(Constructor<T> constructor) {
        if (constructor != null) {
            setAccessible(constructor, true);
            try {
                return constructor.newInstance();
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取某个类的构造函数
     *
     * @param type           类
     * @param parameterTypes 参数列表
     * @param <T>            类型
     * @return 返回获取的构造函数，如果没有对应参数的构造函数，返回null
     */
    public static <T> Constructor<T> getConstructorOne(
            Class<T> type, Class<?>... parameterTypes) {
        return getConstructorAny(type, true, false, parameterTypes);
    }

    /**
     * 获取声明的构造函数
     *
     * @param type           类
     * @param parameterTypes 参数列表
     * @param <T>            类型
     * @return 返回对应的构造函数
     */
    public static <T> Constructor<T> getDeclaredConstructor(
            Class<T> type, Class<?>... parameterTypes) {
        return getConstructorAny(type, false, true);
    }

    /**
     * 获取某个类的构造函数
     *
     * @param type           类
     * @param force          是否强制获取
     * @param declared       是否声明
     * @param parameterTypes 参数列表
     * @param <T>            类型
     * @return 返回对应的构造函数
     */
    private static <T> Constructor<T> getConstructorAny(
            Class<T> type, boolean force, boolean declared, Class<?>... parameterTypes) {
        if (force || declared) {
            try {
                return type.getDeclaredConstructor(parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (force || !declared) {
            try {
                return type.getConstructor(parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /****************************************************************/
    // method

    /**
     * 迭代Class的Method
     *
     * @param type        类
     * @param interceptor 拦截器
     * @param filter      过滤器
     * @param handler     处理器
     */
    public static void foreachMethod(final Class<?> type,
                                     final Predicate<Method> interceptor,
                                     final Predicate<Method> filter,
                                     final Handler<Method> handler) {
        foreach(type, METHODS_CALL, interceptor, filter, handler);
    }

    /**
     * 获取Class的Method集合
     *
     * @param type 类
     */
    public static Set<Method> getDeclaredMethods(final Class<?> type) {
        return getMethods(type, false, true);
    }

    /**
     * 获取Class的Method集合
     *
     * @param type 类
     */
    public static Set<Method> getMethods(final Class<?> type) {
        return getMethods(type, false, false);
    }

    /**
     * 获取Class的Method集合
     *
     * @param type 类
     */
    public static Set<Method> getMethodAll(final Class<?> type) {
        return getMethods(type, true, false);
    }

    /**
     * 获取Class的Method集合
     *
     * @param type 类
     */
    public static Set<Method> getMethods(final Class<?> type, boolean force, boolean declared) {
        final Set<Method> methods = Collections.synchronizedSet(new HashSet<>());
        if (type != null) {
            if (force || declared) {
                Collections.addAll(methods, type.getDeclaredMethods());
            }

            if (force || !declared) {
                Collections.addAll(methods, type.getMethods());
            }
        }
        return methods;
    }

    /**
     * 获取Method
     *
     * @param type           Class
     * @param name           方法名
     * @param parameterTypes 参数类型
     * @return 返回获取的Method
     */
    public static Method getMethod(Class<?> type,
                                   String name,
                                   Class<?>[] parameterTypes) {
        return getMethod(type, name, parameterTypes, true, false);
    }

    /**
     * 获取Method
     *
     * @param type           Class
     * @param name           方法名
     * @param parameterTypes 参数类型
     * @param declared       是否声明
     * @return 返回获取的Method
     */
    public static Method getMethod(Class<?> type,
                                   String name,
                                   Class<?>[] parameterTypes,
                                   boolean declared) {
        return getMethod(type, name, parameterTypes, false, declared);
    }

    /**
     * 获取Method
     *
     * @param type           Class
     * @param name           方法名
     * @param parameterTypes 参数类型
     * @param force          是否任意满足的Method
     * @param declared       是否声明
     * @return 返回获取的Method
     */
    public static Method getMethod(Class<?> type,
                                   String name,
                                   Class<?>[] parameterTypes,
                                   boolean force,
                                   boolean declared) {
        if (isNonNull(type, name)) {
            Method method = null;
            if (force || declared) {
                try {
                    method = type.getDeclaredMethod(name, parameterTypes);
                } catch (NoSuchMethodException e) {/* ignore */}
            }
            if (force || !declared) {
                try {
                    method = type.getMethod(name, parameterTypes);
                } catch (NoSuchMethodException e) {/* ignore */}
            }
            return method;
        }
        return null;
    }

    /**
     * 设置是否可以访问
     *
     * @param ao   可访问对象
     * @param flag 是否可以访问
     */
    public static void setAccessible(AccessibleObject ao, boolean flag) {
        if (ao == null) {
            return;
        }
        if (!(flag && ao.isAccessible())) {
            ao.setAccessible(flag);
        }
    }


    private static final Predicate<Field> FIELD_INTERCEPTOR = field -> false;
    private static final Call<Class<?>, Field[]> FIELDS_CALL = Class::getDeclaredFields;
    private static final Call<Class<?>, Method[]> METHODS_CALL = Class::getDeclaredMethods;

    /**
     * 回调
     *
     * @param <T>
     */
    public interface Handler<T> {
        void onHandle(T t);
    }


    public interface Call<T, V> {
        /**
         * @param t
         * @return
         */
        V apply(T t);
    }

    private static boolean isNonNull(Object... os) {
        for (Object o : os) {
            if (o == null) {
                return false;
            }
        }
        return true;
    }
}
