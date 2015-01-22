import com.netaporter.productservice.api.APIClientUtils.StockAvailability;
import com.netaporter.productservice.api.APIClientUtils.pojos.ProductDetails;
import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.dsl.ProductSearchCriteria;
import com.netaporter.test.client.product.impl.HybridProductDataAccess;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.enums.RegionEnum;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 02/07/2013
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        HybridProductDataAccess dataAccess = (HybridProductDataAccess) context.getBean("productDataAccess");
        //productservice.getProduct();
      /*  System.out.println(dataAccess.getInStockSKUforCurrentChannel());
        System.out.println(dataAccess.getInStockSKUforCurrentRegion());
        System.out.println(dataAccess.getInStockSKUsForCurrentChannel(5));
        System.out.println(dataAccess.API_findSku(SalesChannelEnum.NAP_APAC, ProductDsl.ProductCategory.CLOTHING, ProductDsl.ProductAvailability.IN_STOCK)) ;
        System.out.println(dataAccess.API_findSku(SalesChannelEnum.NAP_APAC,
                ProductDsl.ProductCategory.CLOTHING, ProductDsl.ProductAvailability.IN_STOCK, ProductDsl.Visibility.VISIBLE));*/
      /*  System.out.println(dataAccess.API_findSku(SalesChannelEnum.NAP_APAC,
                ProductDsl.ProductCategory.CLOTHING, ProductDsl.ProductAvailability.IN_STOCK, ProductDsl.Visibility.VISIBLE, 5));*/

        //   System.out.println(dataAccess.Solr_isPidOrSkuInStock(SalesChannelEnum.NAP_INTL, "423857-011"));
        //   List<String> l = dataAccess.getApiClientFacade().getInStockSKUsForChannel(SalesChannelEnum.NAP_INTL, 5);
        //   System.out.println(dataAccess.getApiClientFacade().getInStockSKUsForRegion(RegionEnum.AM, 5));
      /*  System.out.println(dataAccess.getApiClientFacade().findSkus(SalesChannelEnum.NAP_INTL, ProductDsl.ProductCategory.BAGS,
             ProductDsl.ProductAvailability.IN_STOCK, ProductDsl.Visibility.VISIBLE, 3));*/
     //   System.out.println(dataAccess.getApiClientFacade().getStockLevelForProductId(SalesChannelEnum.NAP_INTL, "430362"));
      /*  List<SearchableProduct> l = dataAccess.getApiClientFacade().getRandomAvailableSearchableProductsByDesigner(SalesChannelEnum.NAP_INTL, 4, "By Terry");
        System.out.println(dataAccess.getApiClientFacade().isSkuInStock(SalesChannelEnum.NAP_INTL, "485106-005"));*/
      /*  ProductDetails pd = dataAccess.getApiClientFacade().getProductDetails(SalesChannelEnum.NAP_INTL, "437903");
        List<String> categories = dataAccess.getApiClientFacade().getCategoryNames(SalesChannelEnum.NAP_INTL, pd.getCategories());
        for(String i:categories){
            System.out.println(i);
        }*/
      /*  ProductDetails pd = dataAccess.getApiClientFacade().getProductDetails(SalesChannelEnum.NAP_INTL, "437903");
        List<String> categories = dataAccess.getApiClientFacade().getCategoryNames(SalesChannelEnum.NAP_INTL, pd.getCategories());
        for(String i:categories){
            System.out.println(i);
        }*/
       // ProductSearchCriteria criteria = ProductSearchCriteria.availabilities(ProductDsl.intl(ProductDsl.ProductAvailability.SOLD_OUT));

        System.out.println(dataAccess.getApiClientFacade().getOutOfStockClothingProduct(SalesChannelEnum.NAP_INTL));
       /* List<String> skus = dataAccess.getInStockSKUs(SalesChannelEnum.NAP_INTL,5);
        for (String sku:skus){
            System.out.println(sku);
        }*/
    }

}
