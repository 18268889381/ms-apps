package org.springframework.data.influxdb.wrapper;

import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryResult的数据
 *
 * @author DINGXIUAN
 */
public class QueryResultWrapper {

    private QueryResult result;
    private List<QueryResult.Result> results;
    private List<SeriesWrapper> seriesWrappers;

    protected QueryResultWrapper() {
        // ~
    }

    public QueryResultWrapper(QueryResult result) {
        this.wrapper(result);
    }

    public void wrapper(QueryResult result) {
        if (result == null) {
            throw new NullPointerException("result is null.");
        }
        this.result = result;
        this.results = result.getResults();

        int size = results != null ? results.size() : 1;
        this.seriesWrappers = new ArrayList<>(size);

        if (results != null) {
            results.forEach(r -> r.getSeries().forEach(
                    series -> seriesWrappers.add(new SeriesWrapper(series))));
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

    public List<SeriesWrapper> getSeriesWrappers() {
        return seriesWrappers;
    }
}
