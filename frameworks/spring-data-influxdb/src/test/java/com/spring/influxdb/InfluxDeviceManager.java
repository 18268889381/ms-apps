package com.spring.influxdb;

import com.spring.influxdb.entity.EcgPoint;
import com.spring.influxdb.entity.HardwarePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.influxdb.InfluxDBTemplate;

/**
 * InfluxDB设备数据插入和查询的工具类
 *
 * @date 2018/7/24.
 */
public class InfluxDeviceManager implements CommandLineRunner {

    private final InfluxDBTemplate influxDBTemplate;

    @Autowired
    public InfluxDeviceManager(InfluxDBTemplate influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    private InfluxDBTemplate getTemplate() {
        return influxDBTemplate;
    }

    @Override
    public void run(final String... args) throws Exception {
        // Create database...
        getTemplate().createDatabase();
    }

    /**
     * 存入心电列表
     *
     * @param hp 设备上传的数据包
     */
    public void writePackage(HardwarePackage hp) {
        int initialCapacity = hp.getEcgList().size();
        Object[] points = new Object[initialCapacity];
        long time = hp.getDateTime() * 1000;

        for (int i = 0; i < initialCapacity; i++) {
            EcgPoint entity = new EcgPoint();
            // 用户ID
            entity.setUserId(hp.getUserId());
            // 设备的MAC地址: 83cd07e1e800
            entity.setApMac(hp.getApMac());
            // 硬件地址: 00000131
            entity.setDeviceId(hp.getDeviceId());
            // 病区ID
            entity.setEndemicAreaId(hp.getEndemicAreaId());
            // 位置, 第几个数据包
            entity.setPackageSn(hp.getPackageSn());
            // 心电值
            entity.setPoint(hp.getEcgList().get(i));
            // 时间戳
            entity.setTime(time + i);
            // 转换成Point
            points[i] = getTemplate().getConverterFactory().convert(entity);
        }
        getTemplate().write(points);
    }

}
