package com.netaporter.productservice.api.APIClientUtils;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.*;
import com.netaporter.productservice.api.APIClientUtils.RequestBuilders.*;
import com.netaporter.productservice.api.APIClientUtils.pojos.ProductDetails;
import com.netaporter.productservice.api.RESTClient;
import com.netaporter.productservice.util.PidFromSkuExtractor;
import com.netaporter.productservice.util.QueryFormatHelper;
import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import com.netaporter.test.utils.enums.WebsiteEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 04/07/2013
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
@Component
public class APIClientFacade {
    @Autowired
    RESTClient restClient;
    public static final String PRODUCT = "/product";
    public static final String PRODUCT_AVAILABILITIES = PRODUCT + "/availabilities";
    public static final String PRODUCT_DETAILS = PRODUCT + "/details";
    public static final String BRANDS = "/brands";
    public static final String CATEGORIES = "/categories";
    public static final String PRODUCT_AVAILABILITY = "stockLevel.availability";
    public static final String PID = "productId";
    public static final String NAME = "name";
    public static final String SKU_ID = "skus.skuId";
    public static final String DESIGNER_NAME = "designer.name";
    public static final String DESIGNER_ID = "designerId";
    public static final String IN_STOCK = "NORMAL,LOW_STOCK";
    public static final String NOT_IN_STOCK = "OUT_OF_STOCK,NEVER_IN_STOCK";
    public static final String PIDS = "pids";

    public RESTClient getRestClient() {
        return restClient;
    }

    public void setRestClient(RESTClient restClient) {
        this.restClient = restClient;

    }

    //Get an in-stock sku for provided channelId
    public String getInStockSKUforChannel(SalesChannelEnum channel){
        return getInStockSKUsForChannel(channel, 1).get(0);

    }

    public List<String> getInStockSKUsForChannel(SalesChannelEnum channel, int numOfSkus){
          List<SearchableProduct> products = getProductsWithAvailability(channel,numOfSkus,Arrays.asList(StockAvailability.IN_STOCK, StockAvailability.LOW_STOCK));
          List<String> pids = new ArrayList<String>();
          for(SearchableProduct p:products){
              pids.add(p.getId().toString());
          }
           APIProductDetailsRequest request = (APIProductDetailsRequest)new APIProductDetailsRequestBuilder()
                      .withSalesChannel(channel)
                      .withSelector(PIDS, QueryFormatHelper.listToCommaValues(pids))
                      .build();


          return new APIJSONResponseParser(restClient).getSkus(request, Arrays.asList(StockAvailability.IN_STOCK, StockAvailability.LOW_STOCK)).subList(0,numOfSkus);

    }

    //Get an in-stock  sku for provided region
    public String getInStockSKUforRegion(RegionEnum region){
        return getInStockSKUsForRegion(region, 1).get(0);

    }
    //Get an in-stock skus for provided region (NAP website)
    public List<String> getInStockSKUsForRegion(RegionEnum region, int numOfSkus){
       return getInStockSKUsForChannel(SalesChannelEnum.getByWebsiteAndRegion(WebsiteEnum.NAP,region), numOfSkus);

    }
    //SolrClient method conversion
    public List<String> findSkus(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category,
                                 ProductDsl.ProductAvailability availability, int numOfSkus){

        return findSkus(salesChannel, category, availability, ProductDsl.Visibility.VISIBLE, numOfSkus);

    }

    //SolrClient method conversion
    /*
    * First finds pids with required availability and visibility
    * Then looks through those pids to find ones with required category
    * If not enough skus found - repeats step one with next page of 100 pids (limited to 1000 pids)
    * */
    public List<String> findSkus(SalesChannelEnum salesChannel, ProductDsl.ProductCategory category,
                                 ProductDsl.ProductAvailability availability, ProductDsl.Visibility visibility, int numOfSkus){
        List<String> skus = new ArrayList<String>();
        int counter = 1;
        while(skus.size()<numOfSkus){
            if(counter>30){
                System.out.println("Could not find " + numOfSkus + "skus out of " + counter*100 + "pids");
                break;
            }
            APIProductAvailabilitiesRequest availabilitiesRequest = (APIProductAvailabilitiesRequest)new APIProductAvailabilitiesRequestBuilder()
                    .withBusiness(salesChannel)
                    .withOffset(counter*100)
                    .build();
            List<SearchableProduct> products = new APIJSONResponseParser(restClient).getSearchableProducts(availabilitiesRequest, salesChannel.getRegion(),
                    Arrays.asList(StockAvailability.valueOf(availability.toAPIString())), ProductDsl.Visibility.VISIBLE, null);
            if(products.isEmpty()){
                counter ++;
                continue;
            }
            List<String> pids = new ArrayList<String>();
            for(SearchableProduct p:products){
                pids.add(p.getId().toString());
            }
            //Need to filter the required category now - use product summaries
            APIProductSummariesRequest summariesRequest = (APIProductSummariesRequest)new APIProductSummariesRequestBuilder()
                    .withSalesChannel(salesChannel)
                    .withCategory(category.getCategoryId())
                    .withSelector(PIDS, QueryFormatHelper.listToCommaValues(pids))
                    .build();
            List<String> categoryPids = new APIJSONResponseParser(restClient).getPids(summariesRequest);
            if(!categoryPids.isEmpty()) {
                APIProductDetailsRequest detailsRequest = (APIProductDetailsRequest) new APIProductDetailsRequestBuilder()
                        .withSalesChannel(salesChannel)
                        .withSelector(PIDS, QueryFormatHelper.listToCommaValues(categoryPids))
                        .build();

                List<String> found = new APIJSONResponseParser(restClient).getSkus(detailsRequest, Arrays.asList(StockAvailability.valueOf(availability.toAPIString())));
                System.out.println("Found " + found.size() + " products");
                for (String s : found) {
                    skus.add(s);
                }
            }
            counter++;
        }

        return skus.size()< numOfSkus?skus:skus.subList(0, numOfSkus);

    }
  //TODO: modify for new api
    public String findSku(ProductSearchCriteria search) {
        int channelNum = search.getChannelAvails().size();
        List<SortedSet<String>> skus = new ArrayList<SortedSet<String>>(channelNum);
               for(int i = 0; i<channelNum; i++){
                   APIProductDetailsRequest request = (APIProductDetailsRequest)new APIProductDetailsRequestBuilder()
                  //     .withPath(PRODUCT)
                       .withBusiness(search.getChannelAvails().get(i).getChannel())
                       .withFields(SKU_ID)
                       .withLimit(1)
                       .withSelector(CATEGORIES, search.getCategory().toSolrString())
                       .withSelector(PRODUCT_AVAILABILITY, search.getChannelAvails().get(i).getAvailability().toAPIString())
                       .build();
                   skus.add(new APIJSONResponseParser(restClient).getSortedSkus(request));
                }
      outer:  for(String sku:skus.get(0)){
            for(int c = 1; c< channelNum; c++){
                if(!skus.get(c).contains(sku)){
                    continue outer;
                }
                return sku;
            }
        }
       return null;
    }
    //Get stock availability level enum for product id and channel
    public StockAvailability getStockLevelForProductId(SalesChannelEnum salesChannel, String productId){
        APIProductDetailsRequest request = (APIProductDetailsRequest)new APIProductDetailsRequestBuilder()
                .withSalesChannel(salesChannel)
                .withSelector(PIDS, productId)
                .build();
        return new APIJSONResponseParser(restClient).getStockAvailability(request);
    }

    //Get stock availability level enum for  sku id and channel
    public StockAvailability getStockLevelForSkuId(SalesChannelEnum salesChannel, String skuId){
        //Get pid from sku
        String pid = PidFromSkuExtractor.getPidForSku(skuId);
        APIProductDetailsRequest request = (APIProductDetailsRequest)new APIProductDetailsRequestBuilder()
                .withSalesChannel(salesChannel)
                .withSelector(PIDS, pid)
                .build();
        return new APIJSONResponseParser(restClient).getSkuStockAvailability(request, skuId);
    }

    //Return true if PID on the channel is in stock
    public boolean isPidInStock(SalesChannelEnum salesChannel, String productId){
        StockAvailability level = getStockLevelForProductId(salesChannel, productId);
        System.out.println("Stock level:" + level);
        return level.equals(StockAvailability.IN_STOCK)||level.equals(StockAvailability.LOW_STOCK);
    }

    //Return true if Sku on the channel is in stock
    public boolean isSkuInStock(SalesChannelEnum salesChannel, String skuId){
        StockAvailability level = getStockLevelForSkuId(salesChannel, skuId);
        System.out.println("Stock level:" + level);
        return level.equals(StockAvailability.IN_STOCK)||level.equals(StockAvailability.LOW_STOCK);
    }

    //Return true if PID on the channel is in low stock
    public boolean isPidLowInStock(SalesChannelEnum salesChannel, String productId){
        return  getStockLevelForProductId(salesChannel,productId).equals(StockAvailability.LOW_STOCK);
    }
    //Return true if Sku on the channel is low stock
    public boolean isSkuLowInStock(SalesChannelEnum salesChannel, String skuId){
        return getStockLevelForSkuId(salesChannel,skuId).equals(StockAvailability.LOW_STOCK);
    }
    //Return list of products for designer
    public List<SearchableProduct> getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum salesChannel, int max, String designerName) {

       /* APIRequest request = new APIRequestBuilder()
                .withPath(PRODUCTS)
                .withLimit(max)
                .withChannel(salesChannel)
                .withFields(DESIGNER_NAME, PID, NAME)
                .withSelector(DESIGNER_NAME, designerName)
                .build(); //Should be this request but not implemented yet in API (PS-2205)*/

        Integer brandId = getBrandId(salesChannel, designerName);

        List<SearchableProduct> sp  = new ArrayList<SearchableProduct>();
        int counter = 1;
        while(sp.size()<max){
            if(counter>10){
                System.out.println("Could not find " + max + "searcheable products from " + counter*100 + "pids");
                break;
            }
            APIProductAvailabilitiesRequest request = (APIProductAvailabilitiesRequest)new APIProductAvailabilitiesRequestBuilder()
                    .withBusiness(salesChannel)
                    .withOffset(counter*100)
                    .build();

            List<SearchableProduct> found = new APIJSONResponseParser(restClient).getSearchableProducts(request, salesChannel.getRegion(),Arrays.asList(StockAvailability.IN_STOCK, StockAvailability.LOW_STOCK), ProductDsl.Visibility.VISIBLE, brandId);
            System.out.println("Found " + found.size() + " products");
            for (SearchableProduct s:found){
                sp.add(s);
            }
            counter++;
        }
        return sp.subList(0,max);
    }
//TODO: modify for new api
    //Return list of available products for region
    public List<SearchableProduct> getRandomAvailableSearchableProducts(RegionEnum region, int max) {
        APIProductAvailabilitiesRequest request = (APIProductAvailabilitiesRequest)new APIProductAvailabilitiesRequestBuilder()
            //    .withPath(PRODUCT)
                .withLimit(max)
                .withRegion(region)
                .withFields(DESIGNER_NAME, PID, NAME)
                .withSelector(PRODUCT_AVAILABILITY, StockAvailability.IN_STOCK.toString())
                .build();


        return new APIJSONResponseParser(restClient).getSearchableProducts(request, region, Arrays.asList(StockAvailability.IN_STOCK));
    }
    //Get designer id by name
    public Integer getBrandId(SalesChannelEnum channel, String designerName) {
        APIBrandsRequest request = (APIBrandsRequest)new APIBrandsRequestBuilder()
                .withSalesChannel(channel)
                .build();
        return new APIJSONResponseParser(restClient).getBrandId(request,designerName);
    }
    //Get category name by id
    public String getCategoryName(SalesChannelEnum channel, Integer categoryId) {
        return getCategoryNames(channel, Arrays.asList(categoryId)).get(0);
    }
    //Get list of category name for list of category ids
    public List<String> getCategoryNames(SalesChannelEnum channel, List<Integer> categoryIds){
        APICategoriesRequest request = (APICategoriesRequest)new APICategoriesRequestBuilder()
                .withSalesChannel(channel)
                .build();
        return new APIJSONResponseParser(restClient).getCategoryNames(request, categoryIds);
    }

//TODO: modify for new api
    //Gets not more than 1 in-stock product from a list of product ids
    public String getAnyInStockProductIdFromProductIds(RegionEnum region, List<String> productIds) {
        String[] pids = (String[])productIds.toArray();
        APIRequest request = new APIRequestBuilder()
                //     .withPath(PRODUCT)
                .withLimit(1)
                .withRegion(region)
                .withSelector(PID, pids)
                .withSelector(PRODUCT_AVAILABILITY, IN_STOCK)
                .build();

        return new APIJSONResponseParser(restClient).getProductId();
    }
    //Get list of out of stock products for the channel
    public List<SearchableProduct> getOutOfStockProducts(SalesChannelEnum channel, int max) {
       return getProductsWithAvailability(channel,max, Arrays.asList(StockAvailability.NEVER_IN_STOCK,StockAvailability.OUT_OF_STOCK));
    }

    //Get list of products for the channel with required availability levels
    public List<SearchableProduct> getProductsWithAvailability(SalesChannelEnum channel, int max, List<StockAvailability> availabilities) {
       return getProductsWithAvailability(channel,max,availabilities, ProductDsl.Visibility.VISIBLE);
    }
    //Get list of products for the channel with required availability levels
    public List<SearchableProduct> getProductsWithAvailability(SalesChannelEnum channel, int max, List<StockAvailability> availabilities, ProductDsl.Visibility vis) {

        List<SearchableProduct> sp  = new ArrayList<SearchableProduct>();
        int counter = 1;
        while(sp.size()<max){
            if(counter>10){
                System.out.println("Could not find " + max + "searcheable products from " + counter*100 + "pids");
                break;
            }
            APIProductAvailabilitiesRequest request = (APIProductAvailabilitiesRequest)new APIProductAvailabilitiesRequestBuilder()
                    .withBusiness(channel)
                    .withOffset(counter*100)
                    .build();

            List<SearchableProduct> found = new APIJSONResponseParser(restClient).getSearchableProducts(request, channel.getRegion(),availabilities, vis,null);
            System.out.println("Found " + found.size() + " products");
            for (SearchableProduct s:found){
                sp.add(s);
            }
            counter++;
        }
        return sp.subList(0,max);
    }


    public List<Integer> getCategoriesForPid(SalesChannelEnum channel, String pid) {
        APIProductDetailsRequest request = (APIProductDetailsRequest)new APIProductDetailsRequestBuilder()
                .withSalesChannel(channel)
                .withSelector(PIDS, pid)
                .build();

       return new APIJSONResponseParser(restClient).parseProductDetails(request).getCategories();
    }

    public ProductDetails getProductDetails(SalesChannelEnum salesChannel, String pid){
        APIProductDetailsRequest request = (APIProductDetailsRequest)new APIProductDetailsRequestBuilder()
                .withSalesChannel(salesChannel)
                .withSelector(PIDS, pid)
                .build();
       return (new APIJSONResponseParser(restClient).parseProductDetails(request));
    }

    public String getOutOfStockClothingProduct(SalesChannelEnum channel){
        return PidFromSkuExtractor.getPidForSku(findSkus(SalesChannelEnum.NAP_INTL, ProductDsl.ProductCategory.CLOTHING, ProductDsl.ProductAvailability.SOLD_OUT,1).get(0));
    }

}
