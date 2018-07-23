package org.springframework.data.influxdb.converter;

import org.influxdb.dto.Point;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Bean转换器
 *
 * @param <T>
 */
public interface PointConverter<T> {

    /**
     * 将bean对象转换成Point
     *
     * @param bean 需要转换的bean
     * @return 返回转换的Point
     */
    Point convert(final T bean);

    /**
     * 将bean对象转换成Point
     *
     * @param bean 需要转换的bean
     * @param time 时间戳
     * @return 返回转换的Point
     */
    Point convert(final T bean, long time);

    /**
     * 将bean对象转换成Point
     *
     * @param bean     需要转换的bean
     * @param time     时间戳
     * @param timeUnit TimeUnit
     * @return 返回转换的Point
     */
    Point convert(final T bean, long time, TimeUnit timeUnit);

    /**
     * 将bean对象转换成Point
     *
     * @param beans 需要转换的bean集合
     * @return 返回转换的Point集合
     */
    List<Point> convert(final List<T> beans);

}