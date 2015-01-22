package com.netaporter.test.client.product;

import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;

import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 02/07/2013
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */
public interface ProductDataAccessInterface {

    /**
     * Main method for searching
     * Usage: findSku(availabilities(intl(availability, visibility)).inCategory(category).ignoreSkus(ignoreSkus))
     * @param search
     * @return
     */
    public String findSku(ProductSearchCriteria search);


    /*
    Below are helper methods for different kinds of single channel searches
     */

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability);

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param ensureDbAndPsStockAgree
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, Boolean ensureDbAndPsStockAgree);


    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param ignoreSkus
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus);

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param ignoreSkus
     * @param ensureDbAndPsStockAgree
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree);


    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param visibility
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility);

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel
     * @param category
     * @param availability
     * @param visibility
     * @param ensureDbAndPsStockAgree
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, Boolean ensureDbAndPsStockAgree);

    /**
     * Returns a sku matching the specified search criteria. Note, item visibilty is set as VISIBLE
     * @param salesChannel - whether to search on intl, am or apac and which website
     * @param category - e.g clothing, bags, shoes etc
     * @param availability - SOLD_OUT, IN_STOCK, LOW STOCK
     * @param visibility visible or invisible
     * @param ignoreSkus - a list of sku's to ignore. Useful if you need to search for a list of sku's all with the same search criteria
     *                   so stops the same sku being returned each time
     * @return -  String - a sku of the specified search criteria
     */
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus);

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
    public String findSku(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category, ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, List<String> ignoreSkus, Boolean ensureDbAndPsStockAgree);

    /*
     * Following methods derived from legacy webapp test suite
     */

    public List<String> findSkuWithDBStockLevelUpdatedToSameAsPS(SalesChannelEnum salesChannel, Integer numberOfSkus, ProductDsl.ProductAvailability productAvailability,
                                                                 ProductDsl.ProductCategory category, List<String> ignoreSkus);

    Integer getStockLevelForProductSku(SalesChannelEnum salesChannel, String productSku);

    boolean isPidInStock(SalesChannelEnum salesChannel, String pid);

    boolean isPidOrSkuInStock(SalesChannelEnum salesChannel, String pidOrSku);

    boolean isPidRestrictedForCountry(SalesChannelEnum salesChannel, String pid, String countryName);

    Integer getInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String countryCode);

    List<SearchableProduct> getRandomAvailableSearchableProductsByKeywordAndTitle(SalesChannelEnum salesChannel, int max, String keyword, String title);

    List<SearchableProduct> getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum salesChannel, int max, String designerName);

    List<SearchableProduct> getRandomAvailableSearchableProducts(SalesChannelEnum salesChannel, int max);

    List<SearchableProduct> getRandomAvailableSearchableProductsForWearItWith(SalesChannelEnum salesChannel, int max);

    String returnAnyInStockProductIdFromProductIds(SalesChannelEnum salesChannel, List<String> productIds);

    /* Returns an in-stock product for a given channel. The stock product is just the first one found */
    Map<String, Object> findInStockProduct(SalesChannelEnum salesChannel);

    Map<String, Object> findOutOfStockProduct(SalesChannelEnum salesChannel);

    List<String> getCategoriesForPid(SalesChannelEnum salesChannel, Integer pid);

    String findInStockRestrictedShippingProductIdForCountry(SalesChannelEnum salesChannel, String country);

    boolean hasMultipleSizesInStock(SalesChannelEnum salesChannel, String pidOrSku);

    boolean isItemLowInStock(SalesChannelEnum salesChannel, String pidOrSku);

    // returns first level category of given product
    String returnFirstLevelProductCategory(SalesChannelEnum salesChannel, String pid);

    //returns second level category of given product
    String returnSecondLevelProductCategory(SalesChannelEnum salesChannel, String pid);

    Map<String, Object> findRandomMultiSizedProduct(SalesChannelEnum salesChannel);

    // returns list of url keys for designers that either only have full priced items or have full priced and sale items and they have at least one item available
    List<Map> findDesignersWithAllFullPricedProductsOrFullPricedAndSaleProducts(SalesChannelEnum salesChannel);

    //returns list of designer names that have no items in stock and are not coming soon
    List<Map> findDesignersWithNoProducts(SalesChannelEnum salesChannel);

    //returns list of designer url keys that have sale items only
    List<Map> findDesignersWithSaleProductsOnly(SalesChannelEnum salesChannel);

    Map<String, Object> findRandomProductStockLevelAndSale(SalesChannelEnum salesChannel, int minStockLevel, int maxStockLevel, boolean sale);

    Map<String, Object> findRandomLowStockProduct(SalesChannelEnum salesChannel);

    Map<String, Object> findRandomOnlyOneLeftProduct(SalesChannelEnum salesChannel);

    Map<String, Object> getDesignerForPid(SalesChannelEnum salesChannel, String pid);

    List<String> getSizesForPid(SalesChannelEnum salesChannel, String pid);

    /**
     *  Returns a list as pid's can have multiple level 3 categories
    **/
    List<Map> getNavCatForPid(SalesChannelEnum salesChannel, Integer navCatLevel, String pid);

    String getRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupRegion);

    String getNonRestrictedShippingCountry(SalesChannelEnum salesChannel, String restrictionType, String pid, String countryLookupRegion);

    String getInStockSellableProductSkuFromPid(SalesChannelEnum salesChannel, String pid);

    Map.Entry<String, String> findRestrictedProductPidAndCountryForRegion(SalesChannelEnum salesChannel, String restriction, String countryLookupRegion);


    // *********************************API calls*******************************************************************

    //Get an in-stock sku for provided channelId
    String getInStockSKUforChannel(SalesChannelEnum channel);

    List<String> getInStockSKUs(SalesChannelEnum channel, int numOfSkus);
}
