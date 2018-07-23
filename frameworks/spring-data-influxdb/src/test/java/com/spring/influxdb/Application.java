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

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.influxdb.converter.PointConverterFactory;
import org.springframework.data.influxdb.enable.EnableAutoInfluxDBConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@EnableScheduling // 开启定时任务
@EnableAutoInfluxDBConfiguration
@SpringBootApplication
public class Application implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private InfluxDBTemplate influxDBTemplate;

    @Autowired
    private PointConverterFactory converterManager;

    @Override
    public void run(final String... args) throws Exception {
        // Create database...
        influxDBTemplate.createDatabase();

        insert();
//        query();

    }


    private final String[] tags = new String[]{"tenant", "haha", "2333", "hello", "world"};

    private void insert() {
        long sum = 0L;
        for (int i = 0; i < 1000000; i++) {
            sum += (i % 2 == 0 ? sum - i : sum + i);
        }

        int count = 50;
        int initialCapacity = 10000;
        long start = now();

        Map<Long, Long> middleTime = new HashMap<>();
        Map<Long, Long> insertTime = new HashMap<>();

        Object[] points = new Object[initialCapacity];
        for (int k = 0; k < count; k++) {
            try {
                for (int i = 0; i < initialCapacity; i++) {
                    CpuEntity entity = new CpuEntity();
                    entity.setTenant(tags[i % tags.length]);
                    entity.setBoo(i % 4 != 0);
                    entity.setIdle(91L);
                    entity.setStr("666-" + i);
                    entity.setSystem(1L);
                    entity.setUser(i + 1L);
                    // 时间戳
                    entity.setTime(now());

                    points[i] = converterManager.convert(entity);

                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long middle = now();
            println("中间时间：" + (middle - start));

            // 中间时间
            middleTime.put(middle, (middle - start));

            influxDBTemplate.write(points);

            long insertSpend = now() - middle;
            insertTime.put(middle, insertSpend);
            println("插入所用时间: " + insertSpend);
        }

        long end = now();
        println("总共所用时间: " + (end - start));

        println("\n\n");

        middleTime.forEach((middle, spend) -> println("middle: " + middle + ", spend: " + spend));
        insertTime.forEach((middle, spend) -> println("insert middle: " + middle + ", spend: " + spend));

        AtomicLong timeSum = new AtomicLong();
        insertTime.forEach((middle, spend) -> timeSum.addAndGet(spend));
        println("插入所用时间为: " + timeSum.get() + "毫秒, " + (timeSum.get() / 1000) + "秒");

        println("\n\n");

    }

    private static long now() {
        return System.currentTimeMillis();
    }

    public void query() {
        // ... and query the latest data
        final Query q = new Query("SELECT * FROM cpu GROUP BY tenant", influxDBTemplate.getDatabase());

        // 异步查询
//        influxDBTemplate.query(q, 10, qr -> {
//                    QueryResultWrapper qrw = new QueryResultWrapper(qr);
//                    qrw.getSeriesWrappers().forEach(
//                            sw -> logger.info(String.valueOf(sw.getData())));
//                }
//        );

        println("\n开始查询: " + now(), "\n\n");
        long start = now();

        // 查询并转换为某个类的对象
//        List<CpuEntity> entities = influxDBTemplate.query(q, TimeUnit.MILLISECONDS, CpuEntity.class);

        // 同步查询
        QueryResult result = influxDBTemplate.query(q, TimeUnit.MILLISECONDS);
        if (result != null) {
            long end = now();
            result.getResults().forEach(
                    result1 -> println("\n====== 结束查询 ====== ",
                            "\n所用时间: " + (end - start),
                            "\n数据量: " + result1.getSeries().size(),
                            "\n\n"));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    private static void println(String... ss) {
        for (String s : ss) {
            System.out.println(s);
        }
    }
}
