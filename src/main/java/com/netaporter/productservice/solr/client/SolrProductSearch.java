package com.netaporter.productservice.solr.client;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;

import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.utils.dataaccess.database.LegacyWebAppProductDatabaseClient;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.productservice.solr.client.pojos.SolrProductServiceResults;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import com.netaporter.test.utils.http.QueryStringBuilder;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.netaporter.test.utils.enums.RegionEnum.*;
import static com.netaporter.test.utils.http.QueryStringBuilder.newQueryString;


/**
 * Date: 23/04/2013
 * Time: 11:59
 */
public abstract class SolrProductSearch {

    static Logger logger = LoggerFactory.getLogger(SolrProductSearch.class);
    @Autowired
    private LegacyWebAppProductDatabaseClient dbUtils;

    @Value("${product.service.solr.base.url}")
    private String productServiceSolrBaseUrl;

    private static final String PRODUCT_SERVICE_SOLR_QUERY_PATH = "/nap/select/";

    Gson gson = new Gson();

    public abstract String findSku(ProductSearchCriteria search);
    public abstract List<String> findSkus(ProductSearchCriteria search);

    public SolrProductServiceResults getSkuDocs(QueryStringBuilder queryString){
        logger.info("Sending SOLR request:" + productServiceSolrBaseUrl + PRODUCT_SERVICE_SOLR_QUERY_PATH + queryString);
        RequestSpecBuilder request = new RequestSpecBuilder();
        request.addQueryParameters(queryString.getParams());
        return getSolrProductServiceSearchResponse(request);
    }

    public SolrProductServiceResults getSolrProductServiceSearchResponse(RequestSpecBuilder request){

        //call the service passing in parameters if need be
        long callStart = System.currentTimeMillis();
        com.jayway.restassured.response.Response response = given(request.build(), new ResponseSpecBuilder().build()).get(productServiceSolrBaseUrl + PRODUCT_SERVICE_SOLR_QUERY_PATH);
        logger.debug(">>>>> SOLR API call time: " + (System.currentTimeMillis()-callStart) + "ms");
        logger.info("<<<<< SOLR response status: " + response.getStatusLine().toString());
        String body = response.getBody().asString();
        logger.debug("SOLR response: " + body);
        //convert JSON response to java object
        if(body!=null && body!=""){
            SolrProductServiceResults expResults = gson.fromJson(body, SolrProductServiceResults.class);
            return expResults;
        }
        else throw new RuntimeException("Response body is " + body);


    }


    public String addTerm(String query, String newQuery) {
        if(StringUtils.isBlank(query)) {
            return newQuery;
        }

        return query + " AND " + newQuery;
    }



    protected StockLevel getStockLevelForSku(SolrProductServiceResults.Response.Doc doc, String sku, SalesChannelEnum channel) {
        return new StockLevel(
            dbUtils,
            sku,
            channel,
            doc.getSolrStockLevel(sku)
        );
    }




    public Integer getNumOfChannelsRequestedNotUploaded(List<ProductDsl.ProductChannelAvailability> channelAvails) {
        int count = 0;
        for(ProductDsl.ProductChannelAvailability channelAvail: channelAvails) {
            if(channelAvail.getAvailability() != ProductDsl.ProductAvailability.NOT_UPLOADED) {
                count++;
            }
        }
        return count;
    }

    public Integer getNumOfChannelsRequestedInStock(List<ProductDsl.ProductChannelAvailability> channelAvails) {
        int count = 0;
        for(ProductDsl.ProductChannelAvailability channelAvail: channelAvails) {
            if(channelAvail.getAvailability() == ProductDsl.ProductAvailability.IN_STOCK) {
                count++;
            }
        }
        return count;
    }
    public boolean isPidOrSkuInStock(SalesChannelEnum channel, String id){
        RequestSpecBuilder request = new RequestSpecBuilder();
        String query = addTerm("", "channel_id:" + channel.getId());
        query = addTerm(query, (id.contains("-")?"sku_ids:":"product_id:") + id);
        QueryStringBuilder queryString = newQueryString()
                .param("wt", "json")
                .param("fl", "stock_level_saleable,product_id,channel_id,sku_ids,sku_stock_levels_saleable,visible")
                .param("q", query);
        request.addQueryParameters(queryString.getParams());
        logger.debug("Sending SOLR request:" + productServiceSolrBaseUrl + PRODUCT_SERVICE_SOLR_QUERY_PATH + queryString);
        SolrProductServiceResults result = getSolrProductServiceSearchResponse(request);
        if(id.contains("-")){
            return result.getResponse().getDocForChannel(channel).isInStock(id, channel);
        }
        else {
            return result.getResponse().getDocForChannel(channel).isPidInStock(id, channel);
        }
    }
}
