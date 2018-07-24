package com.spring.influxdb.entity;

import org.influxdb.annotation.Measurement;

/**
 * 单个心电数据
 *
 * @date 2018/7/24.
 */
@Measurement(name = "ecg_point")
public class EcgPoint extends BasePoint<Short> {
    //~
}
