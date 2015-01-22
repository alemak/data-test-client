package com.netaporter.productservice.solr.client.crosschannel;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.netaporter.test.utils.http.QueryStringBuilder;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.productservice.solr.client.pojos.SolrProductServiceResults;
import com.netaporter.productservice.solr.client.SolrProductSearch;
import com.netaporter.productservice.solr.client.SolrSkuSearchResult;
import com.netaporter.productservice.solr.client.StockLevel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.netaporter.test.utils.http.QueryStringBuilder.newQueryString;
import static com.netaporter.test.client.product.dsl.ProductDsl.*;

/**
 * Horrible horrible brute force of product service to find products for cross channel queries such as:
 *    I want a sku that is IN_STOCK on AM, but LOW_STOCK on INTL
 *
 * Product Service team are looking to make sku level documents in the future and then we can kill this
 * and use the product service API instead.
 */
@Component
public class CrossChannelSolrProductSearch extends SolrProductSearch {

    @Override
    public String findSku(final ProductSearchCriteria search) {

        Integer inStock = getNumOfChannelsRequestedInStock(search.getChannelAvails());
        boolean facetQueryIncStock = (inStock > 0);

        Integer uploadedCount = getNumOfChannelsRequestedNotUploaded(search.getChannelAvails());
        final Integer checkCount = facetQueryIncStock? inStock: uploadedCount;

        SolrSkuChannelFilter filter = new SolrSkuChannelFilter() {
            @Override
            public boolean queryForProduct(String sku, Integer channelCount) {
                return channelCount == checkCount && !search.getIgnoreSkus().contains(sku);
            }

            @Override
            public List<SolrProductServiceResults.Response.Doc> filter(String sku, SolrProductServiceResults results) {
                SolrProductServiceResults.Response skuDocs = results.getResponse();

                for(ProductChannelAvailability channelAvail: search.getChannelAvails()) {

                    SolrProductServiceResults.Response.Doc doc = skuDocs.getDocForChannel(channelAvail.getChannel());

                    if(channelAvail.getAvailability() == ProductAvailability.NOT_UPLOADED && doc != null) {
                        return null;
                    }

                    if (channelAvail.getAvailability() != ProductAvailability.NOT_UPLOADED) {
                        if(channelAvail.getVisibility() == Visibility.VISIBLE
                                && (doc == null || doc.isVisible() == null || !doc.isVisible() || !doc.skuExists(sku))) {
                            return null;
                        }


                        if (channelAvail.getVisibility() == Visibility.INVISIBLE && doc.isVisible()) {
                            return null;
                        }


                        StockLevel stockLevel = getStockLevelForSku(doc, sku, channelAvail.getChannel());

                        // If we must check db and PS stock are the same
                        if (search.getEnsureDbAndPsStockAgree() && !stockLevel.dbAndPsAgree()) {
                            // They should be the same and they are not...
                            return null;

                        } else {
                            switch (channelAvail.getAvailability()) {
                                case IN_STOCK: {
                                    if (stockLevel.getProductServiceStockLevel() == 0) {
                                        return null;
                                    }
                                    break;
                                }
                                case LOW_STOCK: {
                                    if (stockLevel.getProductServiceStockLevel() < 0 || stockLevel.getProductServiceStockLevel() > 2) {
                                        return null;
                                    }
                                    break;
                                }
                                case SOLD_OUT: {
                                    if (stockLevel.getProductServiceStockLevel() != 0) {
                                        return null;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                return skuDocs.getDocs();
            }
        };

        String query = buildSolrQuery(search);
        QueryStringBuilder queryString = newQueryString()
                .param("wt", "json")
                .param("rows", "0")
                .param("facet", "true")
                .param("facet.field", "sku_ids")
                .param("facet.sort", "count")
                .param("fl", "sku_ids")
                .param("q", query);

        return pageUntilSkuFound(queryString, filter);
    }

    @Override
    public List<String> findSkus(ProductSearchCriteria search) {
        // TODO Implement Multi channel findSkus
        throw new RuntimeException("Multi channel findSkus not implemented yet");
    }

    private String buildSolrQuery(ProductSearchCriteria search) {
        String query = "";

        if(search.getChannelAvails().size() == 1) {
            query = addTerm(query, "channel_id:" + search.getChannelAvails().get(0).getChannel().getId());
        }

        if(search.getCategory() != null) {
            query = addTerm(query, "classification:" + search.getCategory().toSolrString());
        }

        Integer inStock = getNumOfChannelsRequestedInStock(search.getChannelAvails());
        if(inStock > 0) {
            query = addTerm(query, "sku_stock_levels_saleable:[3 TO *] ");
            query = addTerm(query, "-(list_types:sale_flagging)");
            //query = addTerm(query, "sku_stock_levels_saleable:[3 TO 1000] NOT current_markdown_percentage:[1 TO *]");
        }

        if(query.isEmpty()) {
            query = "*:*";
        }

        return query;
    }

    private String pageUntilSkuFound(QueryStringBuilder facetQuery, SolrSkuChannelFilter filter) {
        Integer offset = 0;
        Integer limit = 5000;
        SolrSkuSearchResult doc = null;

        //Search product service page by page
        while(doc == null) {

            QueryStringBuilder limitedFacetQuery = facetQuery.clone()
                    .param("facet.offset", offset.toString())
                    .param("facet.limit", limit.toString());

            doc = findDocument(limitedFacetQuery, filter);
            offset += limit;
        }

        return doc.getSku();
    }

    private SolrSkuSearchResult findDocument(QueryStringBuilder facetQuery, SolrSkuChannelFilter filter) {
        Map<String,Integer> pidRegionCounts = getSkuRegionCount(facetQuery);

        if(pidRegionCounts.isEmpty()) {
            throw new RuntimeException("No products for offset and limit. Maybe we have run out of products to search!");
        }

        for(Map.Entry<String, Integer> pidRegionCount: pidRegionCounts.entrySet()) {
            String sku = pidRegionCount.getKey();
            if(!sku.isEmpty()) {
                Integer regionCount = pidRegionCount.getValue();

                if(filter.queryForProduct(sku, regionCount)) {
                    QueryStringBuilder queryString = newQueryString()
                            .param("wt", "json")
                            .param("facet.field", "product_id")
                            .param("fl", "product_id,channel_id,sku_ids,sku_stock_levels_saleable,visible")
                            .param("q", "sku_ids:" + sku);

                    SolrProductServiceResults skuDocs = getSkuDocs(queryString);

                    //Stock levels

                    List<SolrProductServiceResults.Response.Doc> returnDoc = filter.filter(sku, skuDocs);

                    if(returnDoc != null) {
                        return new SolrSkuSearchResult(returnDoc, sku);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns a Map of sku -> region count
     */
    private Map<String,Integer> getSkuRegionCount(QueryStringBuilder facetQuery) {
        //String httpQuery = "http://prodserv-prodservdev.dave.net-a-porter.com:8092/productservice/nap/select/?facet=true&facet.field=product_id&q=*:*&rows=0&fl=product_id&facet.limit=100&facet.sort=count&wt=json";
        //System.out.println(solrProductService + "?" + facetQuery.toString());

        RequestSpecBuilder request = new RequestSpecBuilder();
        request.addQueryParameters(facetQuery.getParams());
        SolrProductServiceResults results = getSolrProductServiceSearchResponse(request);

        Map<String,Integer> counts = new HashMap<String, Integer>();
        List<Object> facets = results.getFacet_counts().getFacet_fields().getSku_ids();

        for(int i=0; i< facets.size(); i+=2) {
            String sku = (String)facets.get(i);
            Integer count = ((Double)facets.get(i+1)).intValue();
            counts.put(sku, count);
        }

        return counts;
    }
}
