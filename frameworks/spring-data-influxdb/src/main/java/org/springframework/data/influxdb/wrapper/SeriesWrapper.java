package org.springframework.data.influxdb.wrapper;

import org.influxdb.dto.QueryResult;
import org.springframework.data.influxdb.InfluxUtils;
import org.springframework.data.influxdb.annotations.InfluxIgnore;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * InfluxDB的Measurement的表名、字段、列名、数据
 */
public class SeriesWrapper<T> {
    private static final Map<Class<?>, Map<String, Field>> CLAZZ_CACHE = new ConcurrentHashMap<>();

    private QueryResult.Series series;
    /**
     * 表名
     */
    private String name;
    /**
     * 标签
     */
    private Map<String, String> tags;
    /**
     * 字段
     */
    private List<String> columns;
    /**
     * 数据列表
     */
    private List<List<Object>> values;
    /**
     * 数据：每一列字段的映射
     */
    private List<Map<String, Object>> data;

    private Class<T> type;

    private boolean lazyInit = true;

    protected SeriesWrapper() {
        // ~
    }

    public SeriesWrapper(final QueryResult.Series series) {
        this.wrapper(series, null);
    }

    public SeriesWrapper(final QueryResult.Series series, Class<T> type) {
        this.wrapper(series, type);
    }

    public void wrapper(final QueryResult.Series series, final Class<T> type) {
        if (series == null) {
            throw new NullPointerException("series is null.");
        }

        this.type = type;

        this.series = series;

        this.name = series.getName();
        this.tags = series.getTags();
        this.columns = series.getColumns();
        this.values = series.getValues();

        if (!lazyInit) {
            requiredInitializeData();
        }

    }

    protected void requiredInitializeData() {
        if (data != null) {
            return;
        }
        // 映射成对应的Class字段类型的值
        this.data = new LinkedList<>();
        this.values.stream()
                .filter(objects -> objects != null && !objects.isEmpty())
                .flatMap(os -> {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < os.size(); i++) {
                        // 时间戳字段的值不做处理
                        if ("time".equals(columns.get(i))) {
                            map.put(columns.get(i), os.get(i));
                        } else {
                            map.put(columns.get(i), getTypeOfValue(columns.get(i), os.get(i)));
                        }
                    }
                    return Stream.of(map);
                })
                .forEach(map -> data.add(map));
    }

    public Object getTypeOfValue(String column, Object value) {
        if (type == null) {
            return value;
        }

        Map<String, Field> fieldMap = CLAZZ_CACHE.computeIfAbsent(
                type, k -> InfluxUtils.getFieldMap(type,
                        field -> !(InfluxUtils.isStaticOrFinal(field)
                                || field.isAnnotationPresent(InfluxIgnore.class))));
        Field field = fieldMap.get(column);
        if (field == null) {
            return value;
        }
        Class<?> fieldType;
        if (field.getGenericType() instanceof TypeVariable) {
            fieldType = InfluxUtils.getGenericSuperclassBounds(this.type);
        } else {
            fieldType = field.getType();
        }

        try {
            if (value instanceof Double) {
                // 包装类型
                if (Double.class.isAssignableFrom(fieldType)) {
                    return value;
                } else if (Short.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).shortValue();
                } else if (Integer.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).intValue();
                } else if (Long.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).longValue();
                } else if (Float.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).floatValue();
                }

                // 基本数据类型
                if (double.class.isAssignableFrom(fieldType)) {
                    return value;
                } else if (float.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).floatValue();
                } else if (long.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).longValue();
                } else if (int.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).intValue();
                } else if (short.class.isAssignableFrom(fieldType)) {
                    return ((Double) value).shortValue();
                }
            } else if (value instanceof String) {
                if (Boolean.class.isAssignableFrom(fieldType)) {
                    return Boolean.valueOf(String.valueOf(value));
                }

                if (boolean.class.isAssignableFrom(fieldType)) {
                    return Boolean.valueOf(String.valueOf(value));
                }
            }
        } catch (Exception e) {
            System.err.printf("field: %s, filedType: %s\n", field.getName(), fieldType);
            throw e;
        }
        return value;
    }

    public QueryResult.Series getSeries() {
        return series;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    public List<Map<String, Object>> getData() {
        if (lazyInit) {
            requiredInitializeData();
        }
        return data;
    }

}
