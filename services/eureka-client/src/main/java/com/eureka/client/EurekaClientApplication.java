package com.eureka.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.function.Consumer;

/**
 * Eureka客户端
 *
 * @author DINGXIUAN
 * @date 2018/8/1.
 */
@EnableEurekaClient
@SpringBootApplication
public class EurekaClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

    @EnableScheduling
    @Configuration
    public static class ScheduleConfig {

        private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);

        @Autowired
        private DiscoveryClient discoveryClient;

        @Scheduled(fixedRate = 5000)
        public void checkDiscoveryClient() {
            List<String> services = discoveryClient.getServices();

            log.info("Services: {}", services);

            if (services == null || services.isEmpty()) {
                return;
            }

            List<ServiceInstance> instances = discoveryClient.getInstances(services.get(0));

            instances.forEach(si ->
                    log.info("ServiceId: {}, host: {}, port: {}, scheme: {}, uir: {}",
                            si.getServiceId(), si.getHost(), si.getPort(), si.getScheme(), si.getUri()));
        }
    }
}
