package com.netaporter.productservice.api.APIClientUtils.RequestBuilders;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.APICategoriesRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIProductDetailsRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;

/**
 * Created by a.makarenko on 7/15/14.
 */
public class APIProductDetailsRequestBuilder extends APIRequestBuilder {
    APIRequest request = new APIProductDetailsRequest();
    public String path = "/product/details";

    @Override
    public APIRequest build(){
        request.getPath().add(path);
        request.setQuery(super.request.getQuery());
        return request;
    }
}
