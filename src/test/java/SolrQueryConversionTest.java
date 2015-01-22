import com.netaporter.productservice.api.APIClientUtils.StockAvailability;
import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.client.product.impl.HybridProductDataAccess;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 08/07/2013
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public class SolrQueryConversionTest {
    HybridProductDataAccess dataAccess;
    @Before
    public void Setup(){
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
         dataAccess = (HybridProductDataAccess)context.getBean("productDataAccess");
    }

  /*  @Test
    public void findSkuWithChannelProductcategoryProductavailability(){
         assertEquals("Compared values not same",dataAccess.findSku(SalesChannelEnum.NAP_INTL, ProductDsl.ProductCategory.CLOTHING,ProductDsl.ProductAvailability.IN_STOCK),
                 dataAccess.API_findSku(SalesChannelEnum.NAP_INTL, ProductDsl.ProductCategory.CLOTHING,ProductDsl.ProductAvailability.IN_STOCK));
    }

    //Cross-channel search
   *//* @Test
    public void findSkuWithProductSearch(){
        ProductSearchCriteria criteria = ProductSearchCriteria.availabilities(ProductDsl.intl(ProductDsl.ProductAvailability.IN_STOCK), ProductDsl.am(ProductDsl.ProductAvailability.SOLD_OUT));
        criteria = criteria.inCategory(ProductDsl.ProductCategory.CLOTHING);
        System.out.println(dataAccess.findSku(criteria));
        System.out.println(dataAccess.API_findSku(criteria));
    }*//*

    @Test
    public void findMultipleSkus(){
        List<String> lst = (dataAccess.API_findSku(SalesChannelEnum.NAP_INTL, ProductDsl.ProductCategory.CLOTHING, ProductDsl.ProductAvailability.IN_STOCK, 5));
        for(String s: lst){
            System.out.println(s);
        }
    }

    @Test
    public void getStockLevelForPID(){
        assertEquals(StockAvailability.NEVER_IN_STOCK,dataAccess.API_getStockLevelForProductSku(SalesChannelEnum.NAP_INTL, "338349"));
    }



    @Test
    public void isPidInStock(){
        assertTrue(dataAccess.API_isPidInStock(SalesChannelEnum.NAP_INTL, "101884"));
    }

    @Test
    public void isSkuInStock(){
        assertTrue(dataAccess.isSkuInStock(SalesChannelEnum.NAP_INTL, "101884-090"));
    }
    @Test
    public void getRandomAvailableSearchableProductsByDesigner(){
        SearchableProduct product = dataAccess.API_getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum.NAP_INTL,5,"Gucci").get(2);
        assertTrue(product.getDesigner().equals("Gucci"));
    }
    @Test
    public void getRandomAvailableSearchableProducts(){
        SearchableProduct product = dataAccess.API_getRandomAvailableSearchableProducts(SalesChannelEnum.NAP_INTL, 5).get(2);
        assertTrue(product != null);
    }
    @Test
    public void returnAnyInStockProductIdFromProductIds(){
        String[] arr = new String[]{"320469", "374358", "338349"};

        String result = dataAccess.API_returnAnyInStockProductIdFromProductIds(SalesChannelEnum.NAP_INTL, Arrays.asList(arr));
        System.out.println(result);
        assertTrue(result!=null);
    }
    @Test
    public void getRandomOutOfStockSearchableProducts(){
        SearchableProduct product = dataAccess.getRandomOutOfStockSearchableProducts(SalesChannelEnum.NAP_INTL, 5).get(2);
        System.out.println(product.getId() + " " + product.getTitle());
        assertTrue(product != null);
    }*/
    @Test
    public void getOnlyOneLeftProduct(){
        ProductSearchCriteria criteria = ProductSearchCriteria.availabilities(ProductDsl.intl(ProductDsl.ProductAvailability.ONLY_ONE_LEFT));
        String sku = dataAccess.findSku(criteria);
        System.out.println(sku);
    }
}
