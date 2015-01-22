package com.netaporter.productservice.api.APIClientUtils.RequestBuilders;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIBrandsRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APICategoriesRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;

/**
 * Created by a.makarenko on 7/16/14.
 */
public class APIBrandsRequestBuilder extends APIRequestBuilder {
    APIRequest request = new APIBrandsRequest();
    public String path = "/brands";
    @Override
    public APIRequest build(){
        request.getPath().add(path);
        request.setQuery(super.request.getQuery());
        return request;
    }
}
