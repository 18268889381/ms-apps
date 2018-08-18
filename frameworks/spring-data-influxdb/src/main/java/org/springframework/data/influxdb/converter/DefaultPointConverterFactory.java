package org.springframework.data.influxdb.converter;

import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.data.influxdb.InfluxDBResultMapperPlus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Converter的管理类
 */
public class DefaultPointConverterFactory implements PointConverterFactory {

    /**
     * PoJo的转换类
     */
    private final InfluxDBResultMapperPlus resultMapper = new InfluxDBResultMapperPlus();

    /**
     * PointConverter缓存
     */
    private final Map<Class<?>, PointConverter> converterCache = new ConcurrentHashMap<>();

    private final Function<Class<?>, PointConverter> funcConvert = this::createConverter;

    public DefaultPointConverterFactory() {
    }

    @Override
    public <T> PointConverter createConverter(Class<T> type) {
        return new DefaultPointConverter<>(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> PointConverter<T> getConverter(final Class<?> type) {
        return converterCache.computeIfAbsent(type, funcConvert);
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


}
