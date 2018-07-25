package org.springframework.data.influxdb;

/**
 * Created by HSRG on 2018/7/25.
 */
public enum RetentionPolicy {
    ;

    public static class Builder {
        /**
         * 策略名
         */
        private String name;
        /**
         * 数据库名
         */
        private String database;
        /**
         * 保存时间: 如 3m(3分钟)/3h(3小时)/3d(3天)/3w(3周)
         */
        private String duration;
        /**
         * 每隔多长时间执行一次: 如 3m(3分钟)/3h(3小时)/3d(3天)/3w(3周)
         */
        private String shardDuration;

        public Builder setName(String name) {
            this.name = name.trim();
            return this;
        }

        public Builder setDatabase(String database) {
            this.database = database.trim();
            return this;
        }

        public Builder setDuration(String duration) {
            this.duration = duration.trim();
            return this;
        }

        public Builder setShardDuration(String shardDuration) {
            this.shardDuration = shardDuration.trim();
            return this;
        }

        public String build() {
            return "CREATE DATABASE '" + database + "'"
                    + " WITH DURATION " + duration
                    + " REPLICATION 1 SHARD DURATION " + shardDuration
                    + " NAME '" + name + "'";
        }
    }
}
