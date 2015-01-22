package com.netaporter.test.client.product.impl;

import com.netaporter.test.client.product.ProductDataAccessInterface;
import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.dataaccess.database.LegacyWebAppProductDatabaseClient;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 *
 * Legacy DB impelementation of the Product Data Access interface to support brands not using Product Service
 *
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 02/07/2013
 * Time: 12:42
 */
public class LegacyWebAppDBDataAccess implements ProductDataAccessInterface {

    @Autowired
    private LegacyWebAppProductDatabaseClient legacyDBClient;

    @Override
    public String findSku(ProductSearchCriteria search) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree) {
        // TODO Implement for legacy webapp DB
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<String> findSkuWithDBStockLevelUpdatedToSameAsPS(SalesChannelEnum salesChannel, Integer numberOfSkus, ProductDsl.ProductAvailability productAvailability,
                                                                 ProductDsl.ProductCategory category, List<String> ignoreSkus) {
        // TODO Implement for API
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Integer getStockLevelForProductSku(SalesChannelEnum salesChannel, String productSku) {
        return legacyDBClient.getStockLevelForProductSku(salesChannel.getRegion(), productSku);
    }

    @Override
    public boolean isPidInStock(SalesChannelEnum salesChannel, String pid) {
        return legacyDBClient.isPidInStock(salesChannel.getRegion(), pid);
    }

    @Override
    public boolean isPidOrSkuInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        return legacyDBClient.isPidOrSkuInStock(salesChannel.getRegion(), pidOrSku);
    }

    @Override
    public boolean isPidRestrictedForCountry(SalesChannelEnum salesChannel, String pid, String countryName) {
        return legacyDBClient.isPidRestrictedForCountry(salesChannel.getRegion(), pid, countryName);
    }

    @Override
    public Integer getInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String countryCode) {
        return legacyDBClient.getInStockRestrictedShippingProductIdForCountry(salesChannel.getRegion(), countryCode);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsByKeywordAndTitle(SalesChannelEnum salesChannel, int max, String keyword, String title) {
        return legacyDBClient.getRandomAvailableSearchableProductsByKeywordAndTitle(salesChannel.getRegion(), max, keyword, title);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum salesChannel, int max, String designerName) {
        return legacyDBClient.getRandomAvailableSearchableProductsByDesigner(salesChannel.getRegion(), max, designerName);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProducts(SalesChannelEnum salesChannel, int max) {
        return legacyDBClient.getRandomAvailableSearchableProducts(salesChannel.getRegion(), max);
    }

    @Override
    public List<SearchableProduct> getRandomAvailableSearchableProductsForWearItWith(SalesChannelEnum salesChannel, int max) {
        return legacyDBClient.getRandomAvailableSearchableProductsForWearItWith(salesChannel.getRegion(), max);
    }

    @Override
    public String returnAnyInStockProductIdFromProductIds(SalesChannelEnum salesChannel, List<String> productIds) {
        return legacyDBClient.returnAnyInStockProductIdFromProductIds(salesChannel.getRegion(), productIds);
    }

    @Override
    public Map<String, Object> findInStockProduct(SalesChannelEnum salesChannel) {
        return legacyDBClient.findInStockProduct(salesChannel.getRegion());
    }

    @Override
    public Map<String, Object> findOutOfStockProduct(SalesChannelEnum salesChannel) {
        return legacyDBClient.findOutOfStockProduct(salesChannel.getRegion());
    }

    @Override
    public List<String> getCategoriesForPid(SalesChannelEnum salesChannel, Integer pid) {
        return legacyDBClient.getCategoriesForPid(salesChannel.getRegion(), pid);
    }

    @Override
    public String findInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String country) {
        return legacyDBClient.findInStockRestrictedShippingProductIdForCountry(salesChannel.getRegion(), country);
    }

    @Override
    public boolean hasMultipleSizesInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        return legacyDBClient.hasMultipleSizesInStock(salesChannel.getRegion(), pidOrSku);
    }

    @Override
    public boolean isItemLowInStock(SalesChannelEnum salesChannel, String pidOrSku) {
        return legacyDBClient.isItemLowInStock(salesChannel.getRegion(), pidOrSku);
    }

    @Override
    public String returnFirstLevelProductCategory(SalesChannelEnum salesChannel, String pid) {
        return legacyDBClient.returnFirstLevelProductCategory(salesChannel.getRegion(), pid);
    }

    @Override
    public String returnSecondLevelProductCategory(SalesChannelEnum salesChannel, String pid) {
        return legacyDBClient.returnSecondLevelProductCategory(salesChannel.getRegion(), pid);
    }

    @Override
    public Map<String, Object> findRandomMultiSizedProduct(SalesChannelEnum salesChannel) {
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
    public String getRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupRegion) {
        return legacyDBClient.getRestrictedShippingCountry(salesChannel.getRegion(), restrictionType, pid, countryLookupRegion);
    }

    @Override
    public String getNonRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupRegion) {
        return legacyDBClient.getNonRestrictedShippingCountry(salesChannel.getRegion(), restrictionType, pid, countryLookupRegion);
    }

    @Override
    public String getInStockSellableProductSkuFromPid(SalesChannelEnum salesChannel, String pid) {
        return legacyDBClient.getInStockSellableProductSkuFromPid(salesChannel.getRegion(), pid);
    }

    @Override
    public Map.Entry<String, String> findRestrictedProductPidAndCountryForRegion(SalesChannelEnum salesChannel, String restriction, String countryLookupRegion) {
        return legacyDBClient.findRestrictedProductPidAndCountryForRegion(salesChannel.getRegion(), restriction, countryLookupRegion);
    }

     @Override
    public String getInStockSKUforChannel(SalesChannelEnum channel) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public List<String> getInStockSKUs(SalesChannelEnum channel, int numOfSkus) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
