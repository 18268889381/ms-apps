package com.frameworks.aop.log;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 工具类
 *
 * @date 2018/8/15.
 */
public final class AopTool {

    private static final String EMPTY = "";

    /**
     * 拼接参数
     *
     * @param sb    StringBuffer
     * @param split 分隔符
     * @param args  参数
     * @return 返回拼接后的字符串
     */
    public static StringBuffer join(StringBuffer sb, String split, Object... args) {
        split = (split == null) ? EMPTY : split;
        for (Object arg : args) {
            sb.append(arg).append(split);
        }
        if (EMPTY.equals(split)) {
            sb.lastIndexOf(split);
        }
        return sb;
    }

    /**
     * 拼接参数
     *
     * @param sb    StringBuilder
     * @param split 分隔符
     * @param args  参数
     * @return 返回拼接后的字符串
     */
    public static StringBuilder join(StringBuilder sb, String split, Object... args) {
        split = (split == null) ? EMPTY : split;
        for (Object arg : args) {
            sb.append(arg).append(split);
        }
        if (EMPTY.equals(split)) {
            sb.lastIndexOf(split);
        }
        return sb;
    }

    /**
     * 获取请求参数
     *
     * @param request Http请求
     * @return 返回参数
     */
    public static Map<String, String> getRequestParameters(HttpServletRequest request) {
        return forEachToMap(request.getParameterNames(), request::getParameter);
    }

    /**
     * 获取请求头
     *
     * @param request Http请求
     * @return 返回请求头
     */
    public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
        return forEachToMap(request.getHeaderNames(), request::getHeader);
    }

    public static <K, V> Map<K, V> forEachToMap(Enumeration<K> e, Iterator<K, V> it) {
        Map<K, V> map = new HashMap<>();
        while (e.hasMoreElements()) {
            K key = e.nextElement();
            map.put(key, it.onNext(key));
        }
        return map;
    }

    public interface Iterator<K, V> {
        V onNext(K key);
    }

    private AopTool() {
    }

}
