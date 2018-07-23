package org.springframework.data.influxdb.enable;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.influxdb.InfluxDBProperties;

import java.lang.annotation.*;

/**
 * 自动注入InfluxDB的bean
 */
@SuppressWarnings("deprecation")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Lazy
@EnableConfigurationProperties(InfluxDBProperties.class)
@ConditionalOnMissingBean(InfluxDBConfiguration.class)
@Import(InfluxDBConfiguration.class)
public @interface EnableAutoInfluxDBConfiguration {
    // ~
}
