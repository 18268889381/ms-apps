package org.springframework.data.influxdb.converter;

import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Converter的管理类
 */
public interface PointConverterFactory {

    /**
     * 创建新的BeanPointConverter对象
     *
     * @param type bean类型
     * @param <T>  泛型类型
     * @return 返回新的BeanPointConverter对象
     */
    <T> PointConverter createConverter(Class<T> type);

    /**
     * 获取Bean对应的转换器
     *
     * @param type bean类型
     * @param <T>  泛型类型
     * @return 返回BeanPointConverter对象
     */
    <T> PointConverter<T> getConverter(final Class<?> type);

    /**
     * 将对象转换成Point
     *
     * @param t 对象
     * @return 返回转换后的Point
     */
    <T> Point convert(T t);


    /**
     * 将对象转换成Point
     *
     * @param t         对象
     * @param timestamp 时间戳
     * @return 返回转换后的Point
     */
    <T> Point convert(T t, long timestamp);

    /**
     * 将对象转换成Point
     *
     * @param t         对象
     * @param timestamp 时间戳
     * @param timestamp TimeUnit
     * @return 返回转换后的Point
     */
    <T> Point convert(T t, long timestamp, TimeUnit timeUnit);

    /**
     * 转换成bean对象
     *
     * @param result 查询的结果集
     * @param type   bean类型
     * @param <T>    泛型类型
     * @return 返回解析的对象
     */
    <T> List<T> mapperTo(QueryResult result, Class<T> type);
}
