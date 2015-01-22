package com.netaporter.test.client.product.impl;

import com.netaporter.productservice.api.APIClientUtils.APIClientFacade;
import com.netaporter.productservice.api.APIClientUtils.StockAvailability;
import com.netaporter.productservice.solr.client.SolrClient;
import com.netaporter.test.client.product.ProductDataAccessInterface;
import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.dataaccess.database.LegacyWebAppProductDatabaseClient;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Hybrid implementation that integrates with Product Service API, SOLR and the Legacy DB to access the product data
 *
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 02/07/2013
 * Time: 12:36
  */
public class HybridProductDataAccess implements ProductDataAccessInterface {


    @Autowired
    private APIClientFacade apiClientFacade;
    @Autowired
    private SolrClient solrClient;
    @Autowired
    private LegacyWebAppProductDatabaseClient legacyDBClient;
    public APIClientFacade getApiClientFacade() {
        return apiClientFacade;
    }

    public void setApiClientFacade(APIClientFacade apiClientFacade) {
        this.apiClientFacade = apiClientFacade;
    }

    public SolrClient getSolrClient() {
        return solrClient;
    }

    public void setSolrClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    public LegacyWebAppProductDatabaseClient getLegacyDBClient() {
        return legacyDBClient;
    }

    public void setLegacyDBClient(LegacyWebAppProductDatabaseClient legacyDBClient) {
        this.legacyDBClient = legacyDBClient;
    }

    @Override
    public String findSku(ProductSearchCriteria search) {
        return solrClient.findSku(search);
    }
    //API Conversion
    public String API_findSku(ProductSearchCriteria search) {
        return apiClientFacade.findSku(search);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability) {
        return solrClient.findSku(salesChannel, category, availability);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Boolean ensureDbAndPsStockAgree) {
        return solrClient.findSku(salesChannel, category, availability, ensureDbAndPsStockAgree);
    }
    //API conversion
    public String API_findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability) {
        return API_findSku(salesChannel, category, availability, ProductDsl.Visibility.VISIBLE, 1).get(0);
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus) {
        return solrClient.findSku(salesChannel, category, availability, ignoreSkus);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {
        return solrClient.findSku(salesChannel, category, availability, ignoreSkus, ensureDbAndPsStockAgree);
    }
    //List of skus instead of 'ignoreSkus'
    public List<String> API_findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, int numOfSkus) {
        return apiClientFacade.findSkus(salesChannel, category, availability, numOfSkus);
    }

    public List<String> Solr_findSkus(SalesChannelEnum salesChannel,ProductDsl.ProductCategory category,ProductDsl.ProductAvailability availability, int numOfSkus){
        return solrClient.findSkus(salesChannel, category, availability, numOfSkus);
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility) {
        return solrClient.findSku(salesChannel, category, availability, visibility);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, Boolean ensureDbAndPsStockAgree) {
        return solrClient.findSku(salesChannel, category, availability, visibility, ensureDbAndPsStockAgree);
    }
    //API conversion
    public String API_findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility) {
        return apiClientFacade.findSkus(salesChannel, category, availability, visibility, 1).get(0);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {
        return solrClient.findSku(salesChannel, category, availability, visibility, ignoreSkus, ensureDbAndPsStockAgree);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus) {
        return solrClient.findSku(salesChannel, category, availability, visibility, ignoreSkus);
    }
    //List of skus instead of 'ignoreSkus'
    public List<String> API_findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, int numOfSkus) {
        return getApiClientFacade().findSkus(salesChannel, category, availability, visibility, numOfSkus);
    }

    @Override
    public Integer getStockLevelForProductSku(SalesChannelEnum salesChannel, String productSku) {
        // Current working assumption: webapp DB more authoratative with regards to stock levels.  Product Service only gives indicator of stock
        return legacyDBClient.getStockLevelForProductSku(salesChannel.getRegion(), productSku);
    }

    //API conversion - api does not return the number of items in stock, only stock level indicator

    public StockAvailability API_getStockLevelForProductSku(SalesChannelEnum salesChannel, String productSku){
        return apiClientFacade.getStockLevelForProductId(salesChannel,productSku);
    }

    @Override
    public boolean isPidInStock(SalesChannelEnum salesChannel, String pid) {
        // Current working assumption: webapp DB more authoratative with regards to stock levels.  Product Service only gives indicator of stock
        return legacyDBClient.isPidInStock(salesChannel.getRegion(), pid);
    }
    //Solr version

    public boolean Solr_isPidOrSkuInStock(SalesChannelEnum salesChannel, String id){
        return solrClient.isPidOrSkuInStock(salesChannel, id);
    }

    //API conversion
    public boolean API_isPidInStock(SalesChannelEnum salesChannel, String pid){
        return apiClientFacade.isPidInStock(salesChannel, pid);
    }

    public boolean API_isSkuInStock(SalesChannelEnum salesChannel, String skuid){
        return apiClientFacade.isSkuInStock(salesChannel, skuid);
    }

    @Override
    public boolean isPidOrSkuInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        // Current working assumption: webapp DB more authoratative with regards to stock levels.  Product Service only gives indicator of stock
        return legacyDBClient.isPidOrSkuInStock(salesChannel.getRegion(), pidOrSku);
    }
    //API conversion - there is already a isPidInStock method for PID, restricting this method to Sku
    public boolean isSkuInStock(SalesChannelEnum salesChannel, String sku){
        return apiClientFacade.isSkuInStock(salesChannel,sku);
    }

    @Override
    public String getInStockSellableProductSkuFromPid(SalesChannelEnum salesChannel, String pid) {
        //TODO convert to API
        return legacyDBClient.getInStockSellableProductSkuFromPid(salesChannel.getRegion(), pid);
    }

    //API conversion - is this applicable for the Solr/API???

    @Override
    public boolean isItemLowInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        // Current working assumption: webapp DB more authoratative with regards to stock levels.  Product Service only gives indicator of stock
        return legacyDBClient.isItemLowInStock(salesChannel.getRegion(), pidOrSku);
    }
   //API conversion - separate methods for pid and sku
   //NB: LOW_STOCK not yet implemented in API

    public boolean isProductLowInStock(SalesChannelEnum salesChannel, String pid){
        return apiClientFacade.isPidLowInStock(salesChannel, pid);
    }
    public boolean isSkuLowInStock(SalesChannelEnum salesChannel, String skuId){
        return apiClientFacade.isSkuLowInStock(salesChannel, skuId);
    }


    public List<String> findSkuWithDBStockLevelUpdatedToSameAsPS(SalesChannelEnum salesChannel, Integer numberOfSkus, ProductDsl.ProductAvailability productAvailability,
                                                          ProductDsl.ProductCategory category, List<String> ignoreSkus) {

//        List<String> skuList = new ArrayList<String>();
//        if ((scenarioSession.getData("listOfSkus")) != null) {
//            skuList = (List<String>) scenarioSession.getData("listOfSkus");
//        }

        ProductSearchCriteria criteria = ProductSearchCriteria
                .availabilities(new ProductDsl.ProductChannelAvailability(salesChannel, productAvailability))
                .inCategory(category)
                .ignoreSkus(ignoreSkus)
                .maxResults(numberOfSkus)
                .ensureDbAndPsStockAgree(false);

        long start = System.currentTimeMillis();
        List<String> foundSkus = solrClient.findSkus(criteria);

        for(int i=0; i < foundSkus.size(); i++) {
            System.out.println(category + ":" + (i+1) + "/" + numberOfSkus + " = " + foundSkus.get(i));
        }
        System.out.println(">>>>>>>>>> TIME FOR " + numberOfSkus + " skus = " + (System.currentTimeMillis() - start) + "ms");

//        scenarioSession.putData("listOfSkus",skuList);

        // To limit the impact of db and product service stock levels going out of sync, for testing robustness purposes
        // we're forcing the stock level in the db to a value suitable for the requested availability
        if (!productAvailability.equals(ProductDsl.ProductAvailability.NOT_UPLOADED)) {
            int forcedStock = 5; // Default to 5 for in stock
            if (productAvailability.equals(ProductDsl.ProductAvailability.LOW_STOCK)) {
                forcedStock = 1;
            } else if (productAvailability.equals(ProductDsl.ProductAvailability.SOLD_OUT)) {
                forcedStock = 0;
            }

            //just implemented for NAP at the moment
            if(salesChannel.isNap()) {
                if(salesChannel.isAm()) {
                    legacyDBClient.updateStockLevel(RegionEnum.AM, foundSkus, forcedStock);
                } else  if(salesChannel.isApac()) {
                    legacyDBClient.updateStockLevel(RegionEnum.APAC, foundSkus, forcedStock);
                } else {
                    legacyDBClient.updateStockLevel(RegionEnum.INTL, foundSkus, forcedStock);
                }
            }
        }

        return foundSkus;

    }

    /*
     * TODO Following methods need to be ported to PS API ASAP as the data they rely on may not be populated in the DB
     */


    @Override
    public boolean isPidRestrictedForCountry(SalesChannelEnum salesChannel, String pid, String countryName) {
        // TODO Port to Product Service API
        return legacyDBClient.isPidRestrictedForCountry(salesChannel.getRegion(), pid, countryName);
    }

    @Override
    public Integer getInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String countryCode) {
        // TODO Port to Product Service API
        return legacyDBClient.getInStockRestrictedShippingProductIdForCountry(salesChannel.getRegion(), countryCode);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsByKeywordAndTitle(SalesChannelEnum salesChannel, int max, String keyword, String title) {
        // TODO Port to Product Service API (keywords not yet implemented in API as of 11/07/13
        return legacyDBClient.getRandomAvailableSearchableProductsByKeywordAndTitle(salesChannel.getRegion(), max, keyword, title);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum salesChannel, int max, String designerName) {
        return legacyDBClient.getRandomAvailableSearchableProductsByDesigner(salesChannel.getRegion(), max, designerName);
    }

    //API conversion
    public List<SearchableProduct> API_getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum salesChannel, int max, String designerName){
        return apiClientFacade.getRandomAvailableSearchableProductsByDesigner(salesChannel, max, designerName);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProducts(SalesChannelEnum salesChannel, int max) {
         return legacyDBClient.getRandomAvailableSearchableProducts(salesChannel.getRegion(), max);
    }
    //API conversion
    public List<SearchableProduct> API_getRandomAvailableSearchableProducts(SalesChannelEnum salesChannel, int max) {
        return apiClientFacade.getRandomAvailableSearchableProducts(salesChannel.getRegion(), max);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsForWearItWith(SalesChannelEnum salesChannel, int max) {
        // TODO Port to Product Service API
        return legacyDBClient.getRandomAvailableSearchableProductsForWearItWith(salesChannel.getRegion(), max);
    }

    @Override
    public String returnAnyInStockProductIdFromProductIds(SalesChannelEnum salesChannel, List<String> productIds) {
       return legacyDBClient.returnAnyInStockProductIdFromProductIds(salesChannel.getRegion(), productIds);
    }
    //API conversion
    public String API_returnAnyInStockProductIdFromProductIds(SalesChannelEnum salesChannel, List<String> productIds) {
        return apiClientFacade.getAnyInStockProductIdFromProductIds(salesChannel.getRegion(), productIds);
    }

    //Looks like a duplicate  - use getRandomAvailableSearchableProducts(SalesChannelEnum salesChannel, int max) with max= 1
    @Override
    public Map<String, Object> findInStockProduct(SalesChannelEnum salesChannel) {
        return legacyDBClient.findInStockProduct(salesChannel.getRegion());
    }

    @Override
    public Map<String, Object> findOutOfStockProduct(SalesChannelEnum salesChannel) {
        return legacyDBClient.findOutOfStockProduct(salesChannel.getRegion());
    }
    //API conversion  - Use searchable product object instead of a map
    public List<SearchableProduct> getRandomOutOfStockSearchableProducts(SalesChannelEnum salesChannel, int max) {
        return apiClientFacade.getOutOfStockProducts(salesChannel, max);
    }

    @Override
    public List<String> getCategoriesForPid(SalesChannelEnum salesChannel, Integer pid) {
        // TODO Port to Product Service API
        return legacyDBClient.getCategoriesForPid(salesChannel.getRegion(), pid);
    }
    //API conversion
    public List<Integer> API_getCategoriesForPid(SalesChannelEnum salesChannel, String pid) {
         return apiClientFacade.getCategoriesForPid(salesChannel, pid);
    }

    @Override
    public String findInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String country) {
        // TODO Port to Product Service API
        return legacyDBClient.findInStockRestrictedShippingProductIdForCountry(salesChannel.getRegion(), country);
    }

    @Override
    public boolean hasMultipleSizesInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        // TODO Port to Product Service API
        return legacyDBClient.hasMultipleSizesInStock(salesChannel.getRegion(), pidOrSku);
    }

    @Override
    public String returnFirstLevelProductCategory(SalesChannelEnum salesChannel, String pid) {
        // TODO Port to Product Service API
        return legacyDBClient.returnFirstLevelProductCategory(salesChannel.getRegion(), pid);
    }

    @Override
    public String returnSecondLevelProductCategory(SalesChannelEnum salesChannel, String pid) {
        // TODO Port to Product Service API
        return legacyDBClient.returnSecondLevelProductCategory(salesChannel.getRegion(), pid);
    }

    @Override
    public Map<String, Object> findRandomMultiSizedProduct(SalesChannelEnum salesChannel) {
        // TODO Port to Product Service API
        return legacyDBClient.findRandomMultiSizedProduct(salesChannel.getRegion());
    }

    @Override
    public List<Map> findDesignersWithAllFullPricedProductsOrFullPricedAndSaleProducts(SalesChannelEnum salesChannel) {
        return legacyDBClient.findDesignersWithAllFullPricedProductsOrFullPricedAndSaleProducts(salesChannel.getRegion());
    }

    @Override
    public List<Map> findDesignersWithNoProducts(SalesChannelEnum salesChannel) {
        return legacyDBClient.findDesignersWithNoProducts(salesChannel.getRegion());
    }

    @Override
    public List<Map> findDesignersWithSaleProductsOnly(SalesChannelEnum salesChannel) {
        return legacyDBClient.findDesignersWithSaleProductsOnly(salesChannel.getRegion());
    }

    @Override
    public Map<String, Object> findRandomProductStockLevelAndSale(SalesChannelEnum salesChannel, int minStockLevel, int maxStockLevel, boolean sale) {
        return legacyDBClient.findRandomProductStockLevelAndSale(salesChannel.getRegion(), minStockLevel, maxStockLevel, sale);
    }

    @Override
    public Map<String, Object> findRandomLowStockProduct(SalesChannelEnum salesChannel) {
        return legacyDBClient.findRandomLowStockProduct(salesChannel.getRegion());
    }

    @Override
    public Map<String, Object> findRandomOnlyOneLeftProduct(SalesChannelEnum salesChannel) {
        return legacyDBClient.findRandomOnlyOneLeftProduct(salesChannel.getRegion());
    }

    @Override
    public Map<String, Object> getDesignerForPid(SalesChannelEnum salesChannel, String pid) {
        return legacyDBClient.getDesignerForPid(salesChannel.getRegion(), pid);
    }

    @Override
    public List<String> getSizesForPid(SalesChannelEnum salesChannel, String pid) {
        return legacyDBClient.getSizesForPid(salesChannel.getRegion(), pid);
    }

    @Override
    public List<Map> getNavCatForPid(SalesChannelEnum salesChannel, Integer navCatLevel, String pid) {
        return legacyDBClient.getNavCatForPid(salesChannel.getRegion(), navCatLevel, pid);
    }

    @Override
    public String getRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupsalesChannel) {
        return legacyDBClient.getRestrictedShippingCountry(salesChannel.getRegion(), restrictionType, pid, countryLookupsalesChannel);
    }

    @Override
    public String getNonRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupsalesChannel) {
        return legacyDBClient.getNonRestrictedShippingCountry(salesChannel.getRegion(), restrictionType, pid, countryLookupsalesChannel);
    }

    @Override
    public Map.Entry<String, String> findRestrictedProductPidAndCountryForRegion(SalesChannelEnum salesChannel, String restriction, String countryLookupRegion) {
        return legacyDBClient.findRestrictedProductPidAndCountryForRegion(salesChannel.getRegion(), restriction, countryLookupRegion);
    }
   // *********************************API calls*******************************************************************

    @Override
    //Get an in-stock sku for provided channelId
    public String getInStockSKUforChannel(SalesChannelEnum channel){
        return getInStockSKUs(channel, 1).get(0);

    }
    @Override
    public List<String> getInStockSKUs(SalesChannelEnum channel, int numOfSkus){
        return apiClientFacade.getInStockSKUsForChannel(channel, numOfSkus);
    }

}
