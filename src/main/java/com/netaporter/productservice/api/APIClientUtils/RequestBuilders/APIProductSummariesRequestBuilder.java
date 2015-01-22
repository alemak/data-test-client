package com.netaporter.productservice.api.APIClientUtils.RequestBuilders;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIProductDetailsRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIProductSummariesRequest;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;
import com.netaporter.test.utils.enums.SalesChannelEnum;

/**
 * Created by a.makarenko on 16/01/2015.
 */
public class APIProductSummariesRequestBuilder extends APIRequestBuilder {
    APIRequest request = new APIProductSummariesRequest();
    public String path = "/product/summaries";

    @Override
    public APIRequest build(){
        request.getPath().add(path);
        request.setQuery(super.request.getQuery());
        return request;
    }
    public APIProductSummariesRequestBuilder withCategory(Integer categoryId){
        super.request.getQuery().put("categoryIds", categoryId.toString());
        return this;
    }
    @Override
    public APIProductSummariesRequestBuilder withSalesChannel(SalesChannelEnum channel){
        return (APIProductSummariesRequestBuilder) withBusiness(channel).withCountry(channel);
    }
}
