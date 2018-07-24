package com.spring.influxdb.entity;

import org.influxdb.annotation.Column;

/**
 * 包的基本数据字段
 *
 * @date 2018/7/24.
 */
public abstract class BasePoint<T extends Number> {

    /**
     * 用户ID
     */
    @Column(name = "userId", tag = true)
    private String userId;
    /**
     * 设备ID, 如: 00000131
     */
    @Column(name = "deviceId", tag = true)
    private String deviceId;
    /**
     * 设备的MAC地址: 83cd07e1e800
     */
    @Column(name = "apMac", tag = true)
    private String apMac;
    /**
     * 病区ID: 40287f81641c869701641c8819cc0001
     */
    @Column(name = "endemicAreaId", tag = true)
    private String endemicAreaId;
    /**
     * 包的位置(第几个包)
     */
    @Column(name = "packageSn")
    private Integer packageSn;

    /**
     * 时间戳
     */
    @Column(name = "time")
    private Long time;
    /**
     * 数据值
     */
    @Column(name = "point")
    private T point;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getApMac() {
        return apMac;
    }

    public void setApMac(String apMac) {
        this.apMac = apMac;
    }

    public String getEndemicAreaId() {
        return endemicAreaId;
    }

    public void setEndemicAreaId(String endemicAreaId) {
        this.endemicAreaId = endemicAreaId;
    }

    public Integer getPackageSn() {
        return packageSn;
    }

    public void setPackageSn(Integer packageSn) {
        this.packageSn = packageSn;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public T getPoint() {
        return point;
    }

    public void setPoint(T point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return String.valueOf(point);
    }
}
