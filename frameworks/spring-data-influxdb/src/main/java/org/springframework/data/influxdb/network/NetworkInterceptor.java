package org.springframework.data.influxdb.network;

import okhttp3.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OkHttp的网络请求拦截器
 *
 * @author DINGXIUAN
 */

/**
 * 网络请求的拦截器
 */
public class NetworkInterceptor implements Interceptor {

    private Callback callback;

    public NetworkInterceptor() {
    }

    public NetworkInterceptor(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (callback != null) {
            RequestInfo info = new RequestInfo();

            info.setMethod(request.method());
            info.setHttps(request.isHttps());
            info.setTag(request.tag());

            HttpUrl url = request.url();
            info.setHttpUrl(url.toString());

            HashMap<String, String> queryMap = new HashMap<>();
            for (String name : url.queryParameterNames()) {
                queryMap.put(name, url.queryParameter(name));
            }
            info.setQueries(queryMap);

            RequestBody body = request.body();
            info.setMediaType(body != null ? body.contentType() : null);

            if (body != null && body instanceof MultipartBody) {
                StringBuilder builder = new StringBuilder();
                MultipartBody multipartBody = (MultipartBody) body;
                List<MultipartBody.Part> parts = multipartBody.parts();
                builder.append("MultipartBody: [ ");
                for (MultipartBody.Part part : parts) {
                    RequestBody fileBody = part.body();
                    long length = fileBody.contentLength();
                    MediaType mediaType = fileBody.contentType();
                    builder.append("Length: ").append(length);
                    builder.append(", MediaType: ").append(mediaType);
                    builder.append("; \t");
                }
                builder.append("]");
                info.setMultipart(builder.toString());
            }

            Headers headers = request.headers();
            if (headers != null) {
                HashMap<String, String> headerMap = new HashMap<>();
                for (String headerName : headers.names()) {
                    headerMap.put(headerName, headers.get(headerName));
                }
                info.setHeaders(headerMap);
            }

            callback.onRequest(request, info);
        }


        Response response = chain.proceed(request);

        if (callback != null) {
            callback.onResponse(response);
        }

        return response;
    }


    /**
     * 监听
     */
    public NetworkInterceptor addCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 网络监听
     */
    public interface Callback {
        /**
         * 当请求时
         *
         * @param request 请求
         * @param info    请求信息
         */
        void onRequest(Request request, RequestInfo info);

        /**
         * 当相应时
         *
         * @param response 响应
         */
        void onResponse(Response response);
    }

    /**
     * 网络请求信息
     */
    public static class RequestInfo {
        /**
         * 设置标签
         */
        private Object tag;
        /**
         * 是否为Https
         */
        private boolean https;
        /**
         * 请求路径
         */
        private String httpUrl;
        /**
         * 请求方法
         */
        private String method;
        /**
         * 端口
         */
        private int port;
        /**
         * 类型
         */
        private MediaType mediaType;
        /**
         * query
         */
        private HashMap<String, String> queries = new HashMap<>();
        /**
         * 请求头
         */
        private HashMap<String, String> headers = new HashMap<>();
        /**
         * 多请求体的信息
         */
        private String multipart;

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public boolean isHttps() {
            return https;
        }

        public void setHttps(boolean https) {
            this.https = https;
        }

        public String getHttpUrl() {
            return httpUrl;
        }

        public void setHttpUrl(String httpUrl) {
            this.httpUrl = httpUrl;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public void setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
        }

        public HashMap<String, String> getQueries() {
            return queries;
        }

        public void setQueries(HashMap<String, String> queries) {
            this.queries = queries;
        }

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(HashMap<String, String> headers) {
            this.headers = headers;
        }

        public String getMultipart() {
            return multipart;
        }

        public void setMultipart(String multipart) {
            this.multipart = multipart;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            append(sb, "path: ", httpUrl);
            append(sb, "\ntag: ", tag);
            append(sb, "\nhttps: ", https);
            append(sb, "\nmethod: ", method);
            append(sb, "\nmedia-type: ", mediaType);
            append(sb, "\nqueries: ", queries);
            append(sb, "\nheaders: ", headers);
            append(sb, "\nmultipart: ", multipart);
            return sb.toString();
        }

        public <T> RequestInfo append(StringBuilder builder, String tag, T o) {
            if (o != null) {
                if ((o instanceof Collection && ((Collection) o).isEmpty())
                        || (o instanceof Map && ((Map) o).isEmpty())) {
                    return this;
                }
                builder.append(tag).append(o);
            }
            return this;
        }

        public static <T> String get(T o) {
            return o != null ? String.valueOf(o) : "";
        }

    }

}
