package com.spring.influxdb.entity;

import org.influxdb.annotation.Measurement;

/**
 * 原始的腹部呼吸数据
 *
 * @date 2018/7/24.
 */
@Measurement(name = "old_abdominal_resp_point")
public class OldAbdominalRespPoint extends BasePoint<Integer> {
    //~
}
