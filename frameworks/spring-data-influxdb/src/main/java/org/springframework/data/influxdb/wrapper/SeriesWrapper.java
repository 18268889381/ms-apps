package org.springframework.data.influxdb.wrapper;

import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InfluxDB的Measurement的表名、字段、列名、数据
 *
 * @author DINGXIUAN
 */
public class SeriesWrapper {

    private QueryResult.Series series;
    /**
     * 表名
     */
    private String name;
    /**
     * 标签
     */
    private Map<String, String> tags;
    /**
     * 字段
     */
    private List<String> columns;
    /**
     * 数据列表
     */
    private List<List<Object>> values;
    /**
     * 数据：每一列字段的映射
     */
    private List<Map<String, Object>> data;

    protected SeriesWrapper(){
        // ~
    }

    public SeriesWrapper(QueryResult.Series series) {
        this.wrapper(series);
    }

    public void wrapper(QueryResult.Series series) {
        if (series == null) {
            throw new NullPointerException("series is null.");
        }

        this.series = series;

        this.name = series.getName();
        this.tags = series.getTags();
        this.columns = series.getColumns();
        this.values = series.getValues();

        this.data = new ArrayList<>();
        this.values.forEach(os -> {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < os.size(); i++) {
                map.put(columns.get(i), os.get(i));
            }
            data.add(map);
        });
    }

    public QueryResult.Series getSeries() {
        return series;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }
}
