package org.springframework.data.influxdb.wrapper;

import org.influxdb.dto.QueryResult;

import java.util.LinkedList;
import java.util.List;

/**
 * QueryResult的数据
 */
public class QueryResultWrapper<T> {

    private QueryResult result;
    private List<QueryResult.Result> results;
    private List<SeriesWrapper<T>> seriesWrappers;

    protected QueryResultWrapper() {
        // ~
    }

    public QueryResultWrapper(QueryResult result) {
        this(result, null);
    }

    public QueryResultWrapper(QueryResult result, final Class<T> type) {
        this.wrapper(result, type);
    }

    public void wrapper(QueryResult results, final Class<T> type) {
        if (results == null) {
            throw new NullPointerException("results is null.");
        }

        this.result = results;
        this.results = results.getResults();

        this.seriesWrappers = new LinkedList<>();
        if (this.results != null) {
            this.results.stream()
                    .filter(r -> r != null && r.getSeries() != null)
                    .forEach(r -> r.getSeries().forEach(
                            series -> seriesWrappers.add(new SeriesWrapper<>(series, type))));
        }

    }

    public void setResult(QueryResult result) {
        this.result = result;
    }

    public QueryResult getResult() {
        return result;
    }

    public List<QueryResult.Result> getResults() {
        return results;
    }

    public List<SeriesWrapper<T>> getSeriesWrappers() {
        return seriesWrappers;
    }
}
