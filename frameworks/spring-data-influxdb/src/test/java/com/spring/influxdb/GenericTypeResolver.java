package com.spring.influxdb;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 泛型类型的解析
 */
public abstract class GenericTypeResolver {

    private static final Map TYPE_VARIABLE_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    private GenericTypeResolver() {
    }


    /**
     * 解析一个泛型方法的返回值
     *
     * @param method
     * @param clazz
     * @return
     */
    public static Class resolveReturnType(Method method, Class clazz) {
        Type genericType = method.getGenericReturnType();
        Map typeVariableMap = getTypeVariableMap(clazz);
        Type rawType = getRawType(genericType, typeVariableMap);
        return (rawType instanceof Class) ? (Class) rawType : method.getReturnType();
    }

    /**
     * @param genericType
     * @param typeVariableMap
     * @return
     */
    public static Class resolveType(Type genericType, Map typeVariableMap) {
        Type rawType = getRawType(genericType, typeVariableMap);
        return (rawType instanceof Class) ? (Class) rawType : java.lang.Object.class;
    }

    /**
     * @param genericType
     * @param typeVariableMap
     * @return
     */
    public static Type getRawType(Type genericType, Map typeVariableMap) {
        Type resolvedType = genericType;
        if (genericType instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable) genericType;
            resolvedType = (Type) typeVariableMap.get(tv);
            if (resolvedType == null)
                resolvedType = extractBoundForTypeVariable(tv);
        }
        if (resolvedType instanceof ParameterizedType) {
            return ((ParameterizedType) resolvedType).getRawType();
        } else {
            return resolvedType;
        }
    }

    /**
     * 获取对象反省的 所有参数类型
     *
     * @param clazz
     * @return
     */
    public static Map getTypeVariableMap(Class<?> clazz) {
        Map typeVariableMap = (Map) TYPE_VARIABLE_CACHE.get(clazz);
        if (typeVariableMap == null) {
            typeVariableMap = new HashMap();
            extractTypeVariablesFromGenericInterfaces(clazz.getGenericInterfaces(), typeVariableMap);
            Type genericType = clazz.getGenericSuperclass();
            for (Class type = clazz.getSuperclass(); type != null && type != Object.class; type = type.getSuperclass()) {
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    populateTypeMapFromParameterizedType(pt, typeVariableMap);
                }
                extractTypeVariablesFromGenericInterfaces(type.getGenericInterfaces(), typeVariableMap);
                genericType = type.getGenericSuperclass();
            }

            for (Class type = clazz; type.isMemberClass(); type = type.getEnclosingClass()) {
                genericType = type.getGenericSuperclass();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    populateTypeMapFromParameterizedType(pt, typeVariableMap);
                }
            }

            TYPE_VARIABLE_CACHE.put(clazz, typeVariableMap);
        }
        return typeVariableMap;
    }

    /**
     * 泛型中范围限制类型那个的解析 TypeVariable<D extends GenericDeclaration>
     *
     * @param typeVariable
     * @return
     */
    public static Type extractBoundForTypeVariable(TypeVariable typeVariable) {
        Type bounds[] = typeVariable.getBounds();
        if (bounds.length == 0)
            return java.lang.Object.class;
        Type bound = bounds[0];
        if (bound instanceof TypeVariable)
            bound = extractBoundForTypeVariable((TypeVariable) bound);
        return bound;
    }

    /**
     * 泛型接口的使用
     *
     * @param genericInterfaces
     * @param typeVariableMap
     */
    public static void extractTypeVariablesFromGenericInterfaces(Type genericInterfaces[], Map typeVariableMap) {
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;
                populateTypeMapFromParameterizedType(pt, typeVariableMap);
                if (pt.getRawType() instanceof Class)
                    extractTypeVariablesFromGenericInterfaces(
                            ((Class) pt.getRawType()).getGenericInterfaces(), typeVariableMap);
                continue;
            }
            if (genericInterface instanceof Class){
                extractTypeVariablesFromGenericInterfaces(
                        ((Class) genericInterface).getGenericInterfaces(), typeVariableMap);
            }
        }

    }

    private static void populateTypeMapFromParameterizedType(ParameterizedType type, Map typeVariableMap) {
        if (type.getRawType() instanceof Class) {
            Type actualTypeArguments[] = type.getActualTypeArguments();
            TypeVariable typeVariables[] = ((Class) type.getRawType()).getTypeParameters();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArgument = actualTypeArguments[i];
                TypeVariable variable = typeVariables[i];
                if (actualTypeArgument instanceof Class) {
                    typeVariableMap.put(variable, actualTypeArgument);
                    continue;
                }
                if (actualTypeArgument instanceof GenericArrayType) {
                    typeVariableMap.put(variable, actualTypeArgument);
                    continue;
                }
                if (actualTypeArgument instanceof ParameterizedType) {
                    typeVariableMap.put(variable, actualTypeArgument);
                    continue;
                }
                if (!(actualTypeArgument instanceof TypeVariable)){
                    continue;
                }
                TypeVariable typeVariableArgument = (TypeVariable) actualTypeArgument;
                Type resolvedType = (Type) typeVariableMap.get(typeVariableArgument);
                if (resolvedType == null){
                    resolvedType = extractBoundForTypeVariable(typeVariableArgument);
                }
                typeVariableMap.put(variable, resolvedType);
            }

        }
    }

}
