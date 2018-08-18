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
package org.springframework.data.influxdb.enable;

import okhttp3.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBConnectionFactory;
import org.springframework.data.influxdb.InfluxDBProperties;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.influxdb.converter.PointConverterFactory;
import org.springframework.data.influxdb.converter.DefaultPointConverterFactory;

@Configuration
public class InfluxDBConfiguration {

    private static Logger logger = LoggerFactory.getLogger("InfluxDB");

    @Bean
    @ConditionalOnMissingBean(InfluxDBConnectionFactory.class)
    public InfluxDBConnectionFactory connectionFactory(InfluxDBProperties properties,
                                                       @Autowired(required = false) Interceptor requestInfo) {
        return new InfluxDBConnectionFactory(properties, requestInfo);
    }

    @Bean
    @ConditionalOnMissingBean(InfluxDBTemplate.class)
    public InfluxDBTemplate influxDBTemplate(InfluxDBConnectionFactory connectionFactory,
                                             PointConverterFactory converterFactory) {
        return new InfluxDBTemplate(connectionFactory, converterFactory);
    }

    @Bean
    @ConditionalOnMissingBean(PointConverterFactory.class)
    public PointConverterFactory converterFactory() {
        return new DefaultPointConverterFactory();
    }

//    /**
//     * OkHttp的拦截器，主要用于打印日志
//     */
//    @Bean("requestInfo")
//    @ConditionalOnMissingBean(Interceptor.class)
//    public Interceptor requestInfo() {
//        return new NetworkInterceptor();
//    }

}
