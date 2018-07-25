package com.xjj;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
public class MySpringBootApplication implements HealthIndicator {
    //private static Logger logger = LoggerFactory.getLogger(MySpringBootApplication.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
//		//logger.info("args: {}", args);
//		System.out.println(objectMapper.writeValueAsString(args));
//
//		String env = "dev";
//		if(args.length>0){
//			String[] profile = args[0].split("=");
//			if(profile.length>1){
//				env = profile[1];
//			}
//		}
//
//		String consoleLevel = "trace";
//		String xjjLevel = "trace";
//
//		switch (env){
//			case "test":
//				consoleLevel = "warn";
//				xjjLevel = "trace";
//				break;
//			case "prod":
//				consoleLevel = "warn";
//				xjjLevel = "info";
//				break;
//			default:
//
//		}
//
//		MainMapLookup.setMainArguments("projectName", "test-project-name",
//				"consoleLevel", consoleLevel,
//				"xjjLevel", xjjLevel);
//		SpringApplication.run(MySpringBootApplication.class, args);
//		//logger.warn("My Spring Boot Application Started");

////        long timestamp = 1532488238 * 1000L;
//        long timestamp = 1532487250000000000L / 1000 / 1000;
//        // 2018-07-25T07:16:49.995Z
//        // 2018-07-25T02:54:10.465Z
////        SimpleDateFormat utcSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        SimpleDateFormat utcSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
////        SimpleDateFormat utcSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
////        String utcSdf = utcSdf.utcSdf(timestamp);
//        String utcSdf = utcSdf.utcSdf(new java.util.Date(timestamp));
//        System.out.println("格式化时间: " + utcSdf);


        // 此方法是将2017-11-18T07:12:06.615Z格式的时间转化为秒为单位的Long类型。
//        String time = "2017-11-30T10:41:44.651Z";

//        String utcTime = "2018-07-25T08:00:21.995Z";
        String utcTime = "2018-07-25T08:17:16.995Z";

        // UTC是本地时间
//        String time = utcTime.replace("Z", " UTC");
//        SimpleDateFormat utcSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
//        utcSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date d = null;
//        try {
//            d = utcSdf.parse(time);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println(d); // 这里输出的是date类型的时间
//
//        // 此处是将date类型装换为字符串类型，比如：Sat Nov 18 15:12:06 CST 2017转换为2017-11-18 15:12:06
//        SimpleDateFormat localSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(d.getTime()); // 这里输出的是以秒为单位的long类型的时间。如果需要一毫秒为单位，可以不用除1000.
//        System.out.println(localSdf.format(d)); // 这里输出的是字符串类型的时间


        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        try {
            // "2018-01-22T09:12:43.083Z"
            Date date = sdf1.parse(utcTime);//拿到Date对象
            String str = sdf2.format(date);//输出格式：2017-01-22 09:28:33
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 在/health接口调用的时候，返回多一个属性："mySpringBootApplication":{"status":"UP","hello":"world"}
     */
    @Override
    public Health health() {
        return Health.up().withDetail("hello", "world").build();
    }
}
