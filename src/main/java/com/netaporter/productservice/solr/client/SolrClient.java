package com.netaporter.productservice.solr.client;

import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductDsl.*;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.netaporter.test.client.product.dsl.ProductDsl.*;
import static com.netaporter.test.client.product.dsl.ProductSearchCriteria.availabilities;
import static com.netaporter.test.utils.enums.RegionEnum.*;

/**
 * Created with IntelliJ IDEA.
 * User: c.dawson@london.net-a-porter.com
 * Date: 11/09/2012
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SolrClient  {

    @Autowired
    @Qualifier("crossChannelSolrProductSearch")
    private SolrProductSearch crossChannelSearch;

    @Autowired
    @Qualifier("singleChannelSolrProductSearch")
    private SolrProductSearch singleChannelSearch;


    /**
     * Main overal method for searching solr.
     * Usage: findSku(availabilities(intl(availability, visibility)).inCategory(category).ignoreSkus(ignoreSkus))
     * @param search
     * @return
     */
    public String findSku(ProductSearchCriteria search) {
        if(search.getChannelAvails().size() == 1) {
            return singleChannelSearch.findSku(search);
        } else {
            return crossChannelSearch.findSku(search);
        }
    }

    /**
     *
     * @param search
     * @return
     */
    public List<String> findSkus(ProductSearchCriteria search) {
        if(search.getChannelAvails().size() == 1) {
            return singleChannelSearch.findSkus(search);
        } else {
            return crossChannelSearch.findSkus(search);
        }
    }

    /*
    Below are helper methods for different kinds of single channel searches
     */

    public List<String> findSkus(SalesChannelEnum salesChannel,ProductDsl.ProductCategory category,ProductDsl.ProductAvailability availability, int numOfSkus){
        List<String> skuList = new ArrayList<String>();
        ProductSearchCriteria criteria = ProductSearchCriteria
                .availabilities(new ProductDsl.ProductChannelAvailability(salesChannel, availability))
                .inCategory(category)
                .ignoreSkus(skuList)
                .maxResults(numOfSkus)
                .ensureDbAndPsStockAgree(false);

        skuList = findSkus(criteria);

        for(int i=0; i < skuList.size(); i++) {
            System.out.println(category + ":" + (i+1) + "/" + numOfSkus + " = " + skuList.get(i));
        }

        return skuList;
    }

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param ensureDbAndPsStockAgree
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Boolean ensureDbAndPsStockAgree) {
        return findSku(salesChannel, category, availability, ProductDsl.Visibility.VISIBLE, new ArrayList<String>(), ensureDbAndPsStockAgree);
    }

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability) {
        return findSku(salesChannel, category, availability, ProductDsl.Visibility.VISIBLE, new ArrayList<String>(), false);
    }

    public boolean isPidOrSkuInStock(SalesChannelEnum salesChannel, String id){
        return singleChannelSearch.isPidOrSkuInStock(salesChannel,id);
    }

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param ignoreSkus
     * @param ensureDbAndPsStockAgree
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {
        return findSku(salesChannel, category, availability, ProductDsl.Visibility.VISIBLE, ignoreSkus, ensureDbAndPsStockAgree);
    }


    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param ignoreSkus
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus) {
        return findSku(salesChannel, category, availability, ProductDsl.Visibility.VISIBLE, ignoreSkus, false);
    }


    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param visibility
     * @param ensureDbAndPsStockAgree
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Visibility visibility, Boolean ensureDbAndPsStockAgree) {
        return findSku(salesChannel, category, availability, visibility, new ArrayList<String>(), ensureDbAndPsStockAgree);
    }

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param visibility
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Visibility visibility) {
        return findSku(salesChannel, category, availability, visibility, new ArrayList<String>(), false);
    }


    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel - whether to search on intl, am or apac and which brand
     * @param category - e.g clothing, bags, shoes etc
     * @param availability - SOLD_OUT, IN_STOCK, LOW STOCK
     * @param visibility visible or invisible
     * @param ignoreSkus - a list of sku's to ignore. Useful if you need to search for a list of sku's all with the same search criteria
     *                   so stops the same sku being returned each time
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Visibility visibility, List<String> ignoreSkus) {
        return findSku(salesChannel, category, availability, visibility, ignoreSkus, false);
    }

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel - whether to search on intl, am or apac and which brand
     * @param category - e.g clothing, bags, shoes etc
     * @param availability - SOLD_OUT, IN_STOCK, LOW STOCK
     * @param visibility visible or invisible
     * @param ignoreSkus - a list of sku's to ignore. Useful if you need to search for a list of sku's all with the same search criteria
     *                   so stops the same sku being returned each time
     * @param ensureDbAndPsStockAgree - queries the db to see if the stock levels between PS and DB match. If not updates the DB to match PS
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Visibility visibility, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {

        if (salesChannel.getRegion() == AM) {
            return findSku(availabilities(am(availability, visibility)).inCategory(category).ignoreSkus(ignoreSkus).ensureDbAndPsStockAgree(ensureDbAndPsStockAgree));
        } else if (salesChannel.getRegion() == APAC) {
            return findSku(availabilities(apac(availability, visibility)).inCategory(category).ignoreSkus(ignoreSkus).ensureDbAndPsStockAgree(ensureDbAndPsStockAgree));
        } else {
            // Assume INTL
            return findSku(availabilities(intl(availability, visibility)).inCategory(category).ignoreSkus(ignoreSkus).ensureDbAndPsStockAgree(ensureDbAndPsStockAgree));
        }
    }
}
