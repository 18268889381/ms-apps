package org.springframework.data.influxdb.converter;

import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.springframework.data.influxdb.PlusInfluxDBResultMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Converter的管理类
 */
public class PointConverterFactoryImpl implements PointConverterFactory {

    /**
     * PoJo的转换类
     */
    private final PlusInfluxDBResultMapper resultMapper = new PlusInfluxDBResultMapper();

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


}
