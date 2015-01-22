package com.netaporter.productservice.solr.client.singlechannel;

import com.netaporter.test.utils.http.QueryStringBuilder;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.productservice.solr.client.pojos.SolrProductServiceResults;
import com.netaporter.productservice.solr.client.SolrProductSearch;
import com.netaporter.productservice.solr.client.SolrSkuSearchResult;
import com.netaporter.productservice.solr.client.StockLevel;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.netaporter.test.utils.http.QueryStringBuilder.newQueryString;
import static com.netaporter.test.client.product.dsl.ProductDsl.*;
import static com.netaporter.test.client.product.dsl.ProductDsl.ProductAvailability.*;
import static com.netaporter.productservice.solr.client.pojos.SolrProductServiceResults.*;

/**
 * Date: 23/04/2013
 * Time: 12:02
 */
@Component
public class SingleChannelSolrProductSearch extends SolrProductSearch {

    @Override
    public String findSku(final ProductSearchCriteria search) {
        List<String> skus = getSkus(search, 1);
        if (skus.isEmpty()) {
            return null;
        }
        return skus.get(0);
    }

    @Override
    public List<String> findSkus(ProductSearchCriteria search) {
        return getSkus(search, search.getMaxResults());
    }

    private List<String> getSkus(ProductSearchCriteria search, int maxResults) {
        List<String> matchingSkus = new ArrayList<String>();

        String query = buildSolrQuery(search);
        QueryStringBuilder queryString = newQueryString()
                .param("wt", "json")
                .param("facet.field", "product_id")
                .param("fl", "product_id,channel_id,sku_ids,sku_stock_levels_saleable,visible")
                .param("q", query)
                .param("rows", "200");

        SolrProductServiceResults res = getSkuDocs(queryString);

        //This is single channel impl so we can just get first
        ProductChannelAvailability channelAvail = search.getChannelAvails().get(0);

        for(Response.Doc doc: res.getResponse().getDocs()) {
            if (matchingSkus.size() >= maxResults) {
                break;
            }

            //Visibility and Category have already been filtered as part of the SOLR query
            //All that there is left to do now is to work out which sku in this product ps thinks matches our stock criteria
            for(String sku: doc.getSku_ids()) {
                if (matchingSkus.size() >= maxResults) {
                    break;
                }

                StockLevel stockLevel = getStockLevelForSku(doc, sku, channelAvail.getChannel());

                // If we must check db and PS stock are the same
                if (search.getEnsureDbAndPsStockAgree() && !stockLevel.dbAndPsAgree()) {
                    // They should be the same and they are not, so skipping...

                } else {
                    if(channelAvail.getAvailability() == SOLD_OUT && stockLevel.getProductServiceStockLevel() == 0) {
                        matchingSkus.add(sku);
                    } else if(channelAvail.getAvailability() == IN_STOCK && stockLevel.getProductServiceStockLevel() > 2) {
                        matchingSkus.add(sku);
                    } else if(channelAvail.getAvailability() == ON_SALE && stockLevel.getProductServiceStockLevel() > 2) {
                        matchingSkus.add(sku);
                    } else if(channelAvail.getAvailability() == LOW_STOCK && stockLevel.getProductServiceStockLevel() > 0 && stockLevel.getProductServiceStockLevel() < 3) {
                        matchingSkus.add(sku);
                    } else if(channelAvail.getAvailability() == ONLY_ONE_LEFT && stockLevel.getProductServiceStockLevel() ==1 ) {
                    matchingSkus.add(sku);
                }

                }
            }
        }

        return matchingSkus;
    }

    public String buildSolrQuery(ProductSearchCriteria search) {
        ProductChannelAvailability channelAvail = search.getChannelAvails().get(0);

        String query = addTerm("", "channel_id:" + channelAvail.getChannel().getId());

        if(!search.getIgnoreSkus().isEmpty()) {
            query = addTerm(query, "-sku_ids:(" + StringUtils.join(search.getIgnoreSkus(), " OR ") + ")");
        }

        if(search.getCategory() != null) {
            query = addTerm(query, "classification:" + search.getCategory().toSolrString());
        }

        switch(channelAvail.getVisibility()) {
            case VISIBLE:   query = addTerm(query, "visible:true"); break;
            case INVISIBLE: query = addTerm(query, "visible:false"); break;
        }

        switch(channelAvail.getAvailability()) {
            case IN_STOCK:
                query = addTerm(query, "sku_stock_levels_saleable:[3 TO *] AND -current_markdown_percentage:*");
                query = addTerm(query, "-(list_types:sale_flagging)");
                break;
            case ON_SALE:   query = addTerm(query, "sku_stock_levels_saleable:[3 TO 1000] AND current_markdown_percentage:[1 TO *]");
                break;
            case LOW_STOCK: query = addTerm(query, "sku_stock_levels_saleable:[1 TO 2]  AND -current_markdown_percentage:*");
                query = addTerm(query, "-(list_types:sale_flagging)");
                break;
            case SOLD_OUT:  query = addTerm(query, "sku_stock_levels_saleable:0");
                break;
            case ONLY_ONE_LEFT: query = addTerm(query,"sku_stock_levels_saleable:1");
                break;
        }

        if(query.isEmpty()) {
            query = "*:*";
        }

        return query;
    }
}
