package org.springframework.data.influxdb.converter;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.Point;
import org.springframework.data.influxdb.InfluxUtils;
import org.springframework.data.influxdb.annotations.InfluxIgnore;
import org.springframework.data.influxdb.annotations.InfluxTimestamp;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Model与Point的转换
 */
public class DefaultPointConverter<T> implements PointConverter<T> {

    /**
     * bean的Class
     */
    private volatile Class<T> type;
    /**
     * 所有字段的集合
     */
    private Map<String, Field> fieldMap;
    /**
     * 时间戳字段
     */
    private Field timeField;
    /**
     * 时间戳类型
     */
    private TimeUnit timeUnit;
    /**
     * 表名
     */
    private String measurement;
    /**
     * 字段
     */
    private Map<String, Field> columns;
    /**
     * 标签
     */
    private Map<String, Field> tags;
    /**
     * 标签的值是否允许为Null
     */
    private boolean tagNull = false;
    /**
     * 时间戳的字段名
     */
    private String timeFieldName = "time";

    protected DefaultPointConverter() {
    }

    public DefaultPointConverter(Class<T> type) {
        this.initialize(type);
    }

    protected void initialize(Class<T> type) {
        this.type = type;
        if (type == null) {
            throw new NullPointerException("Class's instance 'type' is null.");
        }

        Measurement mAnnotation = type.getAnnotation(Measurement.class);
        if (mAnnotation != null) {
            this.measurement = mAnnotation.name();
            this.timeUnit = mAnnotation.timeUnit();
        } else {
            this.measurement = type.getSimpleName();
            this.timeUnit = TimeUnit.MILLISECONDS;
        }

        this.fieldMap = InfluxUtils.getFieldMap(type, field -> {
            // 排除被static和final修饰的字段、忽略InfluxIgnore注解的字段
            return !(InfluxUtils.isStaticOrFinal(field) || field.isAnnotationPresent(InfluxIgnore.class));
        });

        final Map<String, Field> tagMap = new ConcurrentHashMap<>();
        final Map<String, Field> columnMap = new ConcurrentHashMap<>();

        final Collection<Field> fields = fieldMap.values();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (column.tag()) {
                    if (field.getType() != String.class) {
                        throw new IllegalStateException("InfluxDB中tag只能为java.lang.String类型, \""
                                + column.name() + "\"的类型为\"" + field.getType() + "\"");
                    }
                    tagMap.putIfAbsent(column.name(), field);
                } else {
                    columnMap.putIfAbsent(column.name(), field);
                }
            } else {
                // 所有没有注解的字段全都默认为Measurement的column
                columnMap.putIfAbsent(field.getName(), field);
            }

            if (field.isAnnotationPresent(InfluxTimestamp.class)) {
                if (getTimeField() != null) {
                    throw new IllegalStateException("不支持多个时间戳!");
                }
                // 设置时间戳字段
                this.setTimeField(field);
            }
        }


        // 默认time字段为时间戳
        String timeFieldName = getTimeFieldName();

        if (getTimeField() == null) {
            Field field = InfluxUtils.getField(type, timeFieldName);
            this.setTimeField(field);
        }

        if (getTimeField() != null) {
            // 移除字段
            columnMap.remove(timeFieldName);
            tagMap.remove(timeFieldName);
        }

        this.tags = Collections.unmodifiableMap(tagMap);
        this.columns = Collections.unmodifiableMap(columnMap);
    }

    public Class<T> getType() {
        return type;
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public Field getTimeField() {
        return timeField;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String getMeasurement() {
        return measurement;
    }

    public Map<String, Field> getColumns() {
        return columns;
    }

    public Map<String, Field> getTags() {
        return tags;
    }

    public void setTagNull(boolean tagNull) {
        this.tagNull = tagNull;
    }

    public boolean isTagNull() {
        return tagNull;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    protected void setTimeField(Field timeField) {
        this.timeField = timeField;
    }

    protected void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

    public Field getColumnField(String column) {
        return columns.get(column);
    }

    public Field getTagField(String tag) {
        return tags.get(tag);
    }


    private long now() {
        return System.currentTimeMillis();
    }

    @Override
    public Point convert(final T item) {
        // 设置时间戳
        Object value = InfluxUtils.getFieldValue(timeField, item);
        if (value instanceof Date) {
            value = ((Date) value).getTime();
        }
        long timestamp = (value != null ? (Long) value : now());
        return convert(item, timestamp);
    }

    @Override
    public Point convert(final T item, long time) {
        return convert(item, time, timeUnit);
    }

    @Override
    public Point convert(final T item, long time, TimeUnit timeUnit) {
        if (item instanceof Point) {
            return (Point) item;
        }

        final Point.Builder builder = Point.measurement(measurement);

        // 设置时间戳
        builder.time(time, timeUnit);

        final Map<String, Field> tags = getTags();
        for (String tag : tags.keySet()) {
            Object value = InfluxUtils.getFieldValue(tags.get(tag), item);
            // 检查是否允许tag为null，默认不允许
            if (!isTagNull() && value == null) {
                throw new NullPointerException("tag is null.");
            }

            if (value != null) {
                builder.tag(tag, String.valueOf(value));
            }
            // ~ 忽略值为null的tag
        }

        // column
        final Map<String, Field> columns = getColumns();
        for (String name : columns.keySet()) {
            Object value = InfluxUtils.getFieldValue(columns.get(name), item);
            if (value instanceof Long) {
                builder.addField(name, (long) value);
            } else if (value instanceof Double) {
                builder.addField(name, (double) value);
            } else if (value instanceof Number) {
                builder.addField(name, (Number) value);
            } else if (value instanceof Boolean) {
                builder.addField(name, (Boolean) value);
            } else if (value instanceof String) {
                builder.addField(name, (String) value);
            } else {
                if (value != null) {
                    builder.addField(name, String.valueOf(value));
                }
                // ~ 忽略值为null的字段
            }
        }
        return builder.build();
    }

    @Override
    public List<Point> convert(final List<T> items) {
        final List<Point> points = new ArrayList<>(items.size());
        for (T t : items) {
            points.add(convert(t));
        }
        return points;
    }

}
