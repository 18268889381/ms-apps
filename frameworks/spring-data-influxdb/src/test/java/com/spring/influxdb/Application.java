/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spring.influxdb;

import com.dxa.framework.utils.JsonUtils;
import com.spring.influxdb.entity.EcgPoint;
import com.spring.influxdb.entity.HardwarePackage;
import com.spring.influxdb.entity.OldAbdominalRespPoint;
import org.apache.commons.io.FileUtils;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.influxdb.enable.EnableAutoInfluxDBConfiguration;
import org.springframework.data.influxdb.wrapper.QueryResultWrapper;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;
import java.util.List;

@EnableScheduling // 开启定时任务
@EnableAutoInfluxDBConfiguration
@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private InfluxDBTemplate influxDBTemplate;

    @Override
    public void run(final String... args) throws Exception {
        // Create database...
        influxDBTemplate.createDatabase();

//        insert();
//        query();
        query2();
    }

    private void insert() throws IOException {

        String path = getClass().getClassLoader().getResource("data.json").getPath();
        String json = FileUtils.readFileToString(new File(path), "utf-8");

        HardwarePackage hp = JsonUtils.fromJson(json, HardwarePackage.class);

        long start = now();

        int initialCapacity = hp.getEcgList().size();

        Object[] points = new Object[initialCapacity];

        println("hp.getDateTime(): " + hp.getDateTime());
        long time = hp.getDateTime() * 1000;
        println("time: " + time);

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
            points[i] = influxDBTemplate.getConverterFactory().convert(entity);
        }

        influxDBTemplate.write(points);

        println("插入" + points.length + "条数据");
        println(points[10].toString());

        long end = now();

        println("插入数据所用时间: " + (end - start));
    }


//    private final String[] tags = new String[]{"tenant", "haha", "2333", "hello", "world"};
//
//    private void insert() {
//        long sum = 0L;
//        for (int i = 0; i < 1000000; i++) {
//            sum += (i % 2 == 0 ? sum - i : sum + i);
//        }
//
//        int count = 10;
//        int initialCapacity = 10000;
//        long start = now();
//
//        Map<Long, Long> middleTime = new HashMap<>();
//        Map<Long, Long> insertTime = new HashMap<>();
//        Map<Long, Long> totalTime = new HashMap<>();
//
//        Object[] points = new Object[initialCapacity];
//        for (int k = 0; k < count; k++) {
//
//            long middleStart = now();
//
//            for (int i = 0; i < initialCapacity; i++) {
//                CpuEntity entity = new CpuEntity();
//                entity.setTenant(tags[i % tags.length]);
//                entity.setTag2("tag" + (i % 2));
//                entity.setBoo(i % 4 != 0);
//                entity.setIdle(91L);
//                entity.setStr("666-" + i);
//                entity.setSystem(1L);
//                entity.setUser(i + 1L);
//                // 时间戳
//                entity.setTime(now());
//
//                points[i] = converterManager.convert(entity);
//
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            long middleNow = now();
//
//            // 中间时间
//            middleTime.put(middleNow, (middleNow - middleStart));
//
//            influxDBTemplate.write(points);
//
//            long now = now();
//
//            insertTime.put(middleNow, (now - middleNow));
//            totalTime.put(middleStart, now);
//
//            println("中间时间：" + (middleNow - start));
//            println("插入所用时间: " + (now - middleNow));
//            println("本次总共所用时间: " + (now - middleStart));
//
//            println("\n\n");
//        }
//
//        long end = now();
//        println("总共所用时间: " + (end - start));
//
//        println("\n\n");
//        middleTime.forEach((middle, spend) -> println("middle: " + middle + ", spend: " + spend));
//        println("\n\n");
//        insertTime.forEach((middle, spend) -> println("insert middle: " + middle + ", spend: " + spend));
//        println("\n\n");
//        totalTime.forEach((mStart, spend) -> println("insert start: " + mStart + ", now: " + spend));
//        println("\n\n");
//
//        AtomicLong timeSum = new AtomicLong();
//        insertTime.forEach((middle, spend) -> timeSum.addAndGet(spend));
//        println("插入所用时间为: " + timeSum.get() + "毫秒, " + (timeSum.get() / 1000) + "秒");
//        println("\n\n");
//
//        println("全部时间: " + (end - start) + "毫秒, "
//                + ((end - start) / 1000) + "秒, "
//                + ((end - start) / 60000.0f) + "分钟");
//        println("\n\n");
//
//        println("\n\n");
//
//    }

    private static long now() {
        return System.currentTimeMillis();
    }

    public void query() {
        // ... and query the latest data
        final Query q = new Query("SELECT * FROM cpu", influxDBTemplate.getDatabase());

        // 异步查询
        influxDBTemplate.query(q, 100, qr -> {
                    QueryResultWrapper qrw = new QueryResultWrapper(qr);
                    qrw.getSeriesWrappers().forEach(
                            sw -> logger.info(String.valueOf(sw.getData())));
                }
        );

//        println("\n开始查询: " + now(), "\n\n");
//        long start = now();
//
//        // 查询并转换为某个类的对象
////        List<CpuEntity> entities = influxDBTemplate.query(q, TimeUnit.MILLISECONDS, CpuEntity.class);
//
//        // 同步查询
//        QueryResult result = influxDBTemplate.query(q, TimeUnit.MILLISECONDS);
//        if (result != null) {
//            long end = now();
//            result.getResults().forEach(
//                    result1 -> println("\n====== 结束查询 ====== ",
//                            "\n所用时间: " + (end - start),
//                            "\n数据量: " + result1.getSeries().size(),
//                            "\n\n"));
//        }
    }

    private void query2() {
        // ... and query the latest data
        final Query q = new Query("SELECT * FROM old_abdominal_resp_point", influxDBTemplate.getDatabase());

        // 异步查询
        influxDBTemplate.query(q, 100, qr -> {
//                    QueryResultWrapper qrw = new QueryResultWrapper(qr);
//                    qrw.getSeriesWrappers().forEach(
//                            sw -> logger.info(String.valueOf(sw.getData())));
                    List<OldAbdominalRespPoint> list = influxDBTemplate.getConverterFactory().mapperTo(qr, OldAbdominalRespPoint.class);
                    logger.info(list.toString());
                }
        );
    }



    private static void println(String... ss) {
        for (String s : ss) {
            System.out.println(s);
        }
    }
}
