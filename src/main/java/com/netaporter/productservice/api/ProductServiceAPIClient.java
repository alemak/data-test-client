package com.netaporter.productservice.api;

import org.springframework.beans.factory.annotation.Value;


/**
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 03/07/2013
 * Time: 14:20
 */
public class ProductServiceAPIClient {

    @Value("${product.service.api.base.url}")
    private String productServiceAPIBaseUrl;



    /*public Map<String, Object> getDesignerForPid(RegionEnum region, String pid) {
        new RequestSpecBuilder();

        "productserviceapi/products/" + pid + "x1/designer";


    }*/



}