package com.netaporter.test.utils.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 19/09/12
 * Time: 12:34
 * To change this template use File | Settings | File Templates.
 */
public class QueryStringBuilder {

    private Map<String,String> nameValues;

    public QueryStringBuilder() {
        nameValues = new HashMap<String,String>();
    }

    public QueryStringBuilder(Map<String,String> nameValues) {
        this.nameValues = nameValues;
    }

    public static QueryStringBuilder newQueryString() {
        return new QueryStringBuilder();
    }

    @Override
    public QueryStringBuilder clone() {
        return new QueryStringBuilder(new HashMap<String,String>(this.nameValues));
    }

    public QueryStringBuilder param(String key, String value) {
        nameValues.put(key, value);
        return this;
    }

    public Map<String, String> getParams() {
        return nameValues;
    }

    public String toString() {
        String res = "";
        for(Map.Entry<String,String> entry: nameValues.entrySet()) {
            if(!res.isEmpty()) {
                res += "&";
            }

            res += entry.getKey() + "=" + entry.getValue();
        }

        return res;
    }
}
