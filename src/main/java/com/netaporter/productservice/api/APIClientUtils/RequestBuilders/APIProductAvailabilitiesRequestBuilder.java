package com.netaporter.productservice.api.APIClientUtils.RequestBuilders;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIProductAvailabilitiesRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIProductDetailsRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;

/**
 * Created by a.makarenko on 7/15/14.
 */
public class APIProductAvailabilitiesRequestBuilder extends APIRequestBuilder {
    APIRequest request = new APIProductAvailabilitiesRequest();
    public String path = "/product/availabilities";
    @Override
    public APIRequest build(){
        request.getPath().add(path);
        request.setQuery(super.request.getQuery());
        return request;
    }
}
