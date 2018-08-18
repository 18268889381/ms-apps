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
package org.springframework.data.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.data.influxdb.converter.PointConverterFactory;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class InfluxDBTemplate extends InfluxDBAccessor implements InfluxDBOperations {

    private PointConverterFactory converterFactory;

    public InfluxDBTemplate() {
        // ~
    }

    public InfluxDBTemplate(final InfluxDBConnectionFactory connectionFactory,
                            final PointConverterFactory converterFactory) {
        setConnectionFactory(connectionFactory);
        setConverterFactory(converterFactory);
    }


    public void setConverterFactory(final PointConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(converterFactory, "PointConverterFactory is required");
    }

    /**
     * 获取Point转换器工厂
     *
     * @return PointConverterFactory
     */
    @Override
    public PointConverterFactory getConverterFactory() {
        return converterFactory;
    }

    @Override
    public void createDatabase() {
        final String database = getDatabase();
        getConnection().createDatabase(database);
    }

    /**
     * Write a single measurement to the database.
     *
     * @param payload the measurement to write to
     */
    @Override
    public <T> void write(T payload) {
        this.write(Collections.singletonList(payload));
    }

    @Override
    public final <T> void write(final T[] payload) {
        this.write(Arrays.asList(payload));
    }

    @Override
    public <T> void write(final List<T> payload) {
        if (payload == null || payload.isEmpty()) {
            return;
        }

        // 检查是否存在类型不一致的对象
        final Class<?> standard = payload.get(0).getClass();
        payload.forEach(t -> {
            if (t == null) {
                throw new NullPointerException("无法插入Null值");
            }
            if (t.getClass() != standard) {
                throw new IllegalArgumentException("插入的数据中存在多种类型的对象!");
            }
        });

        final String database = getDatabase();
        final String retentionPolicy = getConnectionFactory()
                .getProperties()
                .getRetentionPolicy();
        final BatchPoints ops = BatchPoints.database(database)
                .retentionPolicy(retentionPolicy)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        payload.forEach(t -> ops.point(converterFactory.convert(t)));
        getConnection().write(ops);
    }

    @Override
    public QueryResult query(final Query query) {
        return getConnection().query(query);
    }

    @Override
    public QueryResult query(final Query query, final TimeUnit timeUnit) {
        return getConnection().query(query, timeUnit);
    }

    /**
     * Executes a query against the database.
     *
     * @param query    the query to execute
     * @param timeUnit the time unit to be used for the query
     * @param type     对象类型
     * @return a List of time series data matching the query
     */
    @Override
    public <T> List<T> query(Query query, TimeUnit timeUnit, Class<T> type) {
        QueryResult result = this.query(query, timeUnit);
        return converterFactory.mapperTo(result, type);
    }

    @Override
    public void query(Query query, int chunkSize, Consumer<QueryResult> consumer) {
        getConnection().query(query, chunkSize, consumer);
    }

    /**
     * Execute a streaming query against the database.
     *
     * @param query     the query to execute
     * @param chunkSize the number of QueryResults to process in one chunk
     * @param type      对象类型
     * @param consumer  the consumer to invoke for each received QueryResult
     */
    @Override
    public <T> void query(Query query, int chunkSize, final Class<T> type, final Consumer<List<T>> consumer) {
        this.query(query, chunkSize,
                result -> consumer.accept(converterFactory.mapperTo(result, type)));
    }

    @Deprecated
    public <T> List<T> query(Query query, final Class<T> type) {
        QueryResult result = this.query(query);
        return converterFactory.mapperTo(result, type);
    }

    @Override
    public Pong ping() {
        return getConnection().ping();
    }

    @Override
    public String version() {
        return getConnection().version();
    }
}
