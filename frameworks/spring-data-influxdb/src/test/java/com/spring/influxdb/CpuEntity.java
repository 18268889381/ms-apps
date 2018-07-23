package com.spring.influxdb;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

/**
 * 测试InfluxDB的实体类
 */
@Measurement(name = "cpu")
public class CpuEntity {

    @Column(name = "tenant", tag = true)
    private String tenant = "default";

    @Column(name = "idle")
    private Long idle;

    @Column(name = "user")
    private Long user;

    @Column(name = "system")
    private Long system;

    @Column(name = "str")
    private String str;

    @Column(name = "boo")
    private Boolean boo;

//    @InfluxIgnore
    @Column(name = "time")
    private long time;

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Long getIdle() {
        return idle;
    }

    public void setIdle(Long idle) {
        this.idle = idle;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getSystem() {
        return system;
    }

    public void setSystem(Long system) {
        this.system = system;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public Boolean getBoo() {
        return boo;
    }

    public void setBoo(Boolean boo) {
        this.boo = boo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "CpuEntity{" +
                "tenant='" + tenant + '\'' +
                ", idle=" + idle +
                ", user=" + user +
                ", system=" + system +
                ", str=" + str +
                ", boo=" + boo +
                '}';
    }
}
