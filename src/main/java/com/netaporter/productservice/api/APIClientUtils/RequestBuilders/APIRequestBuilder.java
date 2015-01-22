package com.netaporter.productservice.api.APIClientUtils.RequestBuilders;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 03/07/2013
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class APIRequestBuilder {
    APIRequest request = new APIRequest();
    public String path = "";
    public APIRequest build(){
        request.getPath().add(path);
        return request;
    }
    public APIRequestBuilder withQuery(String name, String value){
        request.getQuery().put(name,value);
        return this;
    }

    public APIRequestBuilder withSalesChannel(SalesChannelEnum channel){
        return withBusiness(channel).withCountry(channel);
    }
    public APIRequestBuilder withBusiness(SalesChannelEnum channel){
        request.getQuery().put("business", String.valueOf(channel.getWebsite()));
        return this;
    }
    public APIRequestBuilder withCountry(SalesChannelEnum channel){
        request.getQuery().put("country", String.valueOf(channel.getRegion().getCountry()));
        return this;
    }
    public APIRequestBuilder withBrand(Integer brandId){
        request.getQuery().put("brandIds", brandId.toString());
        return this;
    }
    public APIRequestBuilder withOffset(Integer offset){
        request.getQuery().put("offset", offset.toString());
        return this;
    }

    public APIRequestBuilder withRegion(RegionEnum region){
        List<SalesChannelEnum> channellist = SalesChannelEnum.getByRegion(region);
        String channels = "";
        for(SalesChannelEnum ch:channellist){
            if(ch.isAProductionChannel()) {
                if(channels!=""){
                    channels+=",";
                }
                channels+=ch.getId();
            }
        }
        request.getQuery().put("channelId", channels);
        return this;
    }
    public APIRequestBuilder withSelector(String selector, String... value) {
        String v = "";
        for(String s:value){
            if(v!=""){
                v+=",";
            }
            v+=s;
        }
        request.getQuery().put(selector, v);
        return this;
    }


    public APIRequestBuilder withFields(String... fields){
        String f = "";
        for(String s:fields){
            if(f!=""){
                f+=",";
            }
            f+=s;
        }
        request.getQuery().put("fields", f);
        return this;
    }
    public APIRequestBuilder withLimit(int limit){
        request.getQuery().put("limit", String.valueOf(limit));
        return this;
    }

    public APIRequestBuilder withVisible(boolean visible){
        request.getQuery().put("visible", String.valueOf(visible));
        return this;
    }
}
