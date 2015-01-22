package com.netaporter.productservice.api.APIClientUtils.RequestBuilders;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.APICategoriesRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;

/**
 * Created by a.makarenko on 7/15/14.
 */
public class APICategoriesRequestBuilder extends APIRequestBuilder{
    APIRequest request = new APICategoriesRequest();
    public String path = "/categories";
    @Override
    public APIRequest build(){
        request.getPath().add(path);
        request.setQuery(super.request.getQuery());
        return request;
    }
}
