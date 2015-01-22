package com.netaporter.productservice.api.APIClientUtils.APIRequests;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 03/07/2013
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class APIRequest {

    public APIRequest(){
        path = new LinkedList<String>();
        query= new LinkedHashMap<String, String>();
    }

    public LinkedList<String> getPath() {
        return path;
    }

    public void setPath(LinkedList<String> path) {
        this.path = path;
    }

    private LinkedList<String> path;

    public LinkedHashMap<String, String> getQuery() {
        return query;
    }

    public void setQuery(LinkedHashMap<String, String> query) {
        this.query = query;
    }

    private LinkedHashMap<String,String> query;


}
