package com.netaporter.test.client.product.impl;

import com.netaporter.productservice.api.APIClientUtils.APIClientFacade;
import com.netaporter.productservice.api.APIClientUtils.StockAvailability;
import com.netaporter.test.client.product.ProductDataAccessInterface;
import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 12/07/2013
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class ApiProductDataAccess implements ProductDataAccessInterface {
    @Autowired
    private APIClientFacade apiClientFacade;


    public APIClientFacade getApiClientFacade() {
        return apiClientFacade;
    }

    public void setApiClientFacade(APIClientFacade apiClientFacade) {
        this.apiClientFacade = apiClientFacade;
    }

    @Override
    //Get an in-stock sku for provided channelId
    public String getInStockSKUforChannel(SalesChannelEnum channel){
        return apiClientFacade.getInStockSKUforChannel(channel);

    }

    @Override
    public List<String> getInStockSKUs(SalesChannelEnum channel, int numOfSkus){
        return apiClientFacade.getInStockSKUsForChannel(channel, numOfSkus);
    }

    @Override
    public String findSku(ProductSearchCriteria search) {
            return apiClientFacade.findSku(search);
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability) {
        return findSku(salesChannel, category, availability, 1).get(0);
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for API
        throw new RuntimeException("Not implemented yet");
    }

    public List<String> findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, int numOfSkus) {
        return apiClientFacade.findSkus(salesChannel, category, availability, numOfSkus);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for API
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility) {
            return apiClientFacade.findSkus(salesChannel, category, availability, visibility, 1).get(0);
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for API
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for API
        throw new RuntimeException("Not implemented yet");
    }
    //List of skus instead of 'ignoreSkus'
    public List<String> API_findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, int numOfSkus) {
        return getApiClientFacade().findSkus(salesChannel, category, availability, visibility, numOfSkus);
    }

    @Override
    public List<String> findSkuWithDBStockLevelUpdatedToSameAsPS(SalesChannelEnum salesChannel, Integer numberOfSkus, ProductDsl.ProductAvailability productAvailability,
                                                                 ProductDsl.ProductCategory category, List<String> ignoreSkus) {
        // TODO Implement for API
        throw new RuntimeException("Not implemented yet");
    }


    @Override
    public Integer getStockLevelForProductSku(SalesChannelEnum salesChannel, String productSku) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    //API conversion - api does not return the number of items in stock, only stock level indicator

    public StockAvailability API_getStockLevelForProductSku(SalesChannelEnum salesChannel, String productSku){
        return apiClientFacade.getStockLevelForProductId(salesChannel,productSku);
    }

    @Override
    public boolean isPidInStock(SalesChannelEnum salesChannel, String pid){
        return apiClientFacade.isPidInStock(salesChannel, pid);
    }

    @Override
    public boolean isPidOrSkuInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
    //API conversion - there is already a isPidInStock method for PID, restricting this method to Sku
    public boolean isSkuInStock(SalesChannelEnum salesChannel, String sku){
        return apiClientFacade.isSkuInStock(salesChannel,sku);
    }

    @Override
    public boolean isPidRestrictedForCountry(SalesChannelEnum salesChannel, String pid, String countryName) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String countryCode) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsByKeywordAndTitle(SalesChannelEnum salesChannel, int max, String keyword, String title) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum salesChannel, int max, String designerName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProducts(SalesChannelEnum salesChannel, int max) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsForWearItWith(SalesChannelEnum salesChannel, int max) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String returnAnyInStockProductIdFromProductIds(SalesChannelEnum salesChannel, List<String> productIds) {
        return apiClientFacade.getAnyInStockProductIdFromProductIds(salesChannel.getRegion(), productIds);
    }
    @Override
    public Map<String, Object> findInStockProduct(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> findOutOfStockProduct(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getCategoriesForPid(SalesChannelEnum salesChannel, Integer pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String findInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String country) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasMultipleSizesInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isItemLowInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String returnFirstLevelProductCategory(SalesChannelEnum salesChannel, String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String returnSecondLevelProductCategory(SalesChannelEnum salesChannel, String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> findRandomMultiSizedProduct(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Map> findDesignersWithAllFullPricedProductsOrFullPricedAndSaleProducts(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Map> findDesignersWithNoProducts(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Map> findDesignersWithSaleProductsOnly(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> findRandomProductStockLevelAndSale(SalesChannelEnum salesChannel, int minStockLevel, int maxStockLevel, boolean sale) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> findRandomLowStockProduct(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> findRandomOnlyOneLeftProduct(SalesChannelEnum salesChannel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> getDesignerForPid(SalesChannelEnum salesChannel, String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getSizesForPid(SalesChannelEnum salesChannel, String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Map> getNavCatForPid(SalesChannelEnum salesChannel, Integer navCatLevel, String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupRegion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getNonRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupRegion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInStockSellableProductSkuFromPid(SalesChannelEnum salesChannel, String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map.Entry<String, String> findRestrictedProductPidAndCountryForRegion(SalesChannelEnum salesChannel, String restriction, String countryLookupRegion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
