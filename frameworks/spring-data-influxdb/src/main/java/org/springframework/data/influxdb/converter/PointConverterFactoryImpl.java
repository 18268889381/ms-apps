package org.springframework.data.influxdb.converter;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.data.influxdb.InfluxUtils;
import org.springframework.data.influxdb.annotations.InfluxIgnore;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Converter的管理类
 */
public class PointConverterFactoryImpl implements PointConverterFactory {

    /**
     * PoJo的转换类
     */
    private final InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

    /**
     * BeanPointConverter缓存
     */
    private final Map<Class<?>, PointConverter> converterCache = new ConcurrentHashMap<>();

    public PointConverterFactoryImpl() {
    }

    @Override
    public <T> PointConverter createConverter(Class<T> type) {
        return new DefaultPointConverter<>(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PointConverter<T> getConverter(final Class<?> type) {
        return converterCache.computeIfAbsent(type, k -> createConverter(type));
    }

    @Override
    public <T> Point convert(T t) {
        if (t instanceof Point) {
            return (Point) t;
        }
        return getConverter(t.getClass()).convert(t);
    }

    @Override
    public <T> Point convert(T t, long timestamp) {
        return getConverter(t.getClass()).convert(t, timestamp);
    }

    @Override
    public <T> Point convert(T t, long timestamp, TimeUnit timeUnit) {
        return getConverter(t.getClass()).convert(t, timestamp, timeUnit);
    }

    public <T> List<T> mapperTo(QueryResult result, Class<T> type) {
        return resultMapper.toPOJO(result, type);
    }

    /**
     * Bean与Point的转换
     */
    public static class DefaultPointConverter<T> implements PointConverter<T> {

        /**
         * bean的Class
         */
        private volatile Class<T> type;
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

        protected DefaultPointConverter() {
        }

        public DefaultPointConverter(Class<T> type) {
            this.init(type);
        }

        protected void init(Class<T> type) {
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

            final Map<String, Field> tagMap = new ConcurrentHashMap<>();
            final Map<String, Field> columnMap = new ConcurrentHashMap<>();
            InfluxUtils.foreachField(type,
                    // 排除被static和final修饰的字段
                    field -> !InfluxUtils.isStaticOrFinal(field.getModifiers())
                            // 没有被显示忽略
                            && !field.isAnnotationPresent(InfluxIgnore.class),
                    field -> {
                        Column column = field.getAnnotation(Column.class);
                        if (column != null) {
                            if (column.tag()) {
                                if (field.getType() != String.class) {
                                    throw new IllegalStateException("InfluxDB中tag只能为java.lang.String类型, \""
                                            + column.name() + "\"的类型为\"" + field.getType() + "\"");
                                }
                                tagMap.put(column.name(), field);
                            } else {
                                columnMap.put(column.name(), field);
                            }
                        } else {
                            // 所有没有注解的字段全都默认为Measurement的column
                            columnMap.put(field.getName(), field);
                        }
                    });

            try {
                // 默认time字段为时间戳
                this.timeField = type.getField("time");
            } catch (NoSuchFieldException e) {/* ignore */}

            this.tags = Collections.unmodifiableMap(tagMap);
            this.columns = Collections.unmodifiableMap(columnMap);
        }

        public Class<T> getType() {
            return type;
        }

        public Field getTime() {
            return timeField;
        }

        public Map<String, Field> getTags() {
            return tags;
        }

        public Map<String, Field> getColumns() {
            return columns;
        }

        public Field getColumnField(String column) {
            return columns.get(column);
        }

        public Field getTagField(String tag) {
            return tags.get(tag);
        }

        public void setTagNull(boolean tagNull) {
            this.tagNull = tagNull;
        }

        public boolean isTagNull() {
            return tagNull;
        }

        private long now() {
            return System.currentTimeMillis();
        }

        @Override
        public Point convert(final T t) {
            // 设置时间戳
            Object value = InfluxUtils.getFieldValue(timeField, t);
            long timestamp = (value != null ? (Long) value : now());
            return convert(t, timestamp);
        }

        @Override
        public Point convert(final T t, long time) {
            return convert(t, time, timeUnit);
        }

        @Override
        public Point convert(final T t, long time, TimeUnit timeUnit) {
            final Point.Builder builder = Point.measurement(measurement);

            // 设置时间戳
            builder.time(time, timeUnit);

            // tag
            getTags().forEach((tag, field) -> {
                Object value = InfluxUtils.getFieldValue(field, t);
                if (isTagNull() && value == null) {
                    throw new NullPointerException("tag is null.");
                }
                builder.tag(tag, String.valueOf(value));
            });

            // column
            getColumns().forEach((name, field) -> {
                Object value = InfluxUtils.getFieldValue(field, t);
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
            });

            return builder.build();
        }

        @Override
        public List<Point> convert(final List<T> beans) {
            List<Point> list = new ArrayList<>(beans.size());
            beans.forEach(t -> list.add(convert(t)));
            return list;
        }

    }

}
