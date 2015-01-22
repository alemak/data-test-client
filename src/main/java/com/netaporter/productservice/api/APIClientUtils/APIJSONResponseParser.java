package com.netaporter.productservice.api.APIClientUtils;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.*;
import com.netaporter.productservice.api.APIClientUtils.pojos.ProductDetails;
import com.netaporter.productservice.api.RESTClient;
import com.netaporter.productservice.util.HTMLUtils;
import com.netaporter.test.client.product.dsl.ProductDsl;
import com.netaporter.test.client.product.pojos.SearchableProduct;
import com.netaporter.test.utils.enums.RegionEnum;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.experimental.categories.Categories;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 03/07/2013
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */
public class APIJSONResponseParser {
     RESTClient client;
     JSONObject json;
     JSONArray data;

    public APIJSONResponseParser(RESTClient client, String response){
          this.client = client;
          try {
              json = new JSONObject(response);
              data = json.getJSONArray("data");
          } catch (JSONException e) {
              if(e.getLocalizedMessage().contains("A JSONObject text must begin with '{' ")){
                  try {
                      data = new JSONArray(response);
                      return;
                  } catch (JSONException e1) {
                      e1.printStackTrace();
                  }
              };
              e.printStackTrace();
          }
      }
    public APIJSONResponseParser(RESTClient client){
       this.client = client;
       json = null;
       data = null;
    }
      public void parse(String response){
          try {
              json = new JSONObject(response);
              data = json.getJSONArray("data");
          } catch (JSONException e) {
              if(e.getLocalizedMessage().contains("A JSONObject text must begin with '{' ")){
                  try {
                      data = new JSONArray(response);
                      return;
                  } catch (JSONException e1) {
                      e1.printStackTrace();
                  }
              };
              e.printStackTrace();
          }
      }

      //Returns the first sku in the json response object
      public String getFirstSku() {
          try {
              return json.getJSONArray("data").getJSONObject(0).getJSONArray("skus").getJSONObject(0).getString("skuId");
          } catch (JSONException e) {
              e.printStackTrace();
          }
          return null;
      }

    //Returns get all pids from the response to summaries request
    public  List<String> getPids(APIProductSummariesRequest request){
        parse(client.getResponse(request));
        List<String> result = new ArrayList<String>();
        try{
            for(int i = 0; i< json.getJSONArray("data").length(); i++){
                result.add(String.valueOf(json.getJSONArray("data").getJSONObject(i).getInt("id")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

      //Returns list of skus
      public  List<String> getSkus(APIProductDetailsRequest request, List<StockAvailability> availabilities){
          parse(client.getResponse(request));
           List<String> result = new ArrayList<String>();
           List<String> av= new ArrayList<String>();
          for(StockAvailability a:availabilities){
              av.add(a.toString());
          }
           try{
               for(int i = 0; i< json.getJSONArray("data").length(); i++){
                   JSONArray skus = json.getJSONArray("data").getJSONObject(i).getJSONArray("skus");
                   for(int z = 0; z<skus.length(); z++){
                       if(av.contains(skus.getJSONObject(z).getString("stockLevel").toUpperCase())){
                           result.add(skus.getJSONObject(z).getString("id"));
                       }
                   }
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }
           return result;
      }
     //Returns sorted set of skus
    public  SortedSet<String> getSortedSkus(APIProductDetailsRequest request){
        parse(client.getResponse(request));
        SortedSet<String> result = new TreeSet<String>();
            try{
                for(int i = 0; i< json.getJSONArray("data").length(); i++){
                    result.add(json.getJSONArray("data").getJSONObject(i).getJSONArray("skus").getJSONObject(0).getString("skuId"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result.isEmpty()? null:result;

    }
    //Get total number data entries returned
    public int getTotal(){
       int result = 0;
        try{
            result = json.getInt("total");
        }  catch (JSONException e) {
            e.printStackTrace();
        }
       return result;
    }

    public  StockAvailability getStockAvailability(APIProductDetailsRequest request){
        parse(client.getResponse(request));
        StockAvailability result = null;
        try{
            JSONArray badges = data.getJSONObject(0).getJSONArray("badges");
            for(int i = 0; i<badges.length(); i++) {
                if (badges.getString(i).contains("Stock")) {
                }
                result = StockAvailability.valueOf((data.getJSONObject(0).getJSONArray("badges").getString(i)).toUpperCase());
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public  StockAvailability getSkuStockAvailability(APIProductDetailsRequest request, String sku){
        parse(client.getResponse(request));
        try{
            JSONArray skus = (json.getJSONArray("data").getJSONObject(0).getJSONArray("skus"));
            for(int i = 0; i<skus.length(); i++){
                if(skus.getJSONObject(i).getString("id").equals(sku)){
                    return StockAvailability.valueOf(skus.getJSONObject(i).getString("stockLevel").toUpperCase());
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  ProductDetails parseProductDetails(APIProductDetailsRequest request){
        parse(client.getResponse(request));
        ProductDetails pd = new ProductDetails();
        try {
            pd.setDetails(HTMLUtils.extractText(data.getJSONObject(0).getJSONObject("longDescription").getString("en")));
            pd.setTitle(data.getJSONObject(0).getJSONObject("name").getString("en"));
            pd.setDesignerName(data.getJSONObject(0).getJSONObject("brand").getJSONObject("name").getString("en"));
            pd.setEditorsNotes(HTMLUtils.extractText(data.getJSONObject(0).getJSONObject("editorsComments").getString("en")));
            pd.setOnSale(data.getJSONObject(0).getBoolean("onSale"));
            pd.setCurrency(data.getJSONObject(0).getJSONObject("price").getString("currency"));
            pd.setPrice(new Double(data.getJSONObject(0).getJSONObject("price").getInt("gross")/data.getJSONObject(0).getJSONObject("price").getInt("divisor")));
            pd.setSizeFit(HTMLUtils.extractText(data.getJSONObject(0).getJSONObject("sizeFit").getString("en")));
            List<Integer>categories = new ArrayList<Integer>();
            JSONArray cat = data.getJSONObject(0).getJSONArray("categories");
            for(int i = 0; i<cat.length(); i++){
                CategoriesTraversal ct = new CategoriesTraversal();
                ct.traverse(cat.getJSONObject(i));
                categories.addAll(ct.getIds());
            }
            pd.setCategories(categories);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pd;
    }

    public  String getCategoryName(APICategoriesRequest request, Integer categoryId) {
        parse(client.getResponse(request));
        String name = null;
        for(int i=0;i<data.length();i++){
            try{
                if(data.getJSONObject(i).getInt("id")== categoryId){
                    name = data.getJSONObject(i).getJSONObject("name").getString("en");
                    break;
                }

            }  catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return name;
    }
    public  List<String> getCategoryNames(APICategoriesRequest request, List<Integer> categoryIds) {
        parse(client.getResponse(request));
        List<String> names = new ArrayList<String>();
        String name = null;
        for(Integer Id:categoryIds) {
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (data.getJSONObject(i).getInt("id") == Id) {
                        names.add(data.getJSONObject(i).getJSONObject("name").getString("en"));
                        break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return names;
    }

   class CategoriesTraversal{
        private ArrayList<Integer> ids = new ArrayList<Integer>();
        public void traverse(JSONObject child){
            ids.add(child.getInt("id"));
            try {
                for (int i = 0; i<child.getJSONArray("children").length(); i++){
                traverse(child.getJSONArray("children").getJSONObject(i));
            }
            }catch (JSONException e){

            }
        }
        public List<Integer> getIds(){

            return ids;
        }
    }


  /*  public List<SearchableProduct> getSearchableProducts(RegionEnum region, List<StockAvailability> availability) {
        List<SearchableProduct> result = new ArrayList<SearchableProduct>();
        List<String>availabilities= new ArrayList<String>();
        for(StockAvailability a:availability){
            availabilities.add(a.toString());
        }
        try{
            for(int i = 0; i< json.getJSONArray("data").length(); i++){
                JSONArray avail = json.getJSONArray("data").getJSONObject(i).getJSONArray("distributionCentres");
                for(int z = 0; z<avail.length(); z++){
                    if(avail.getJSONObject(z).getString("id").equals(region.getDC())&&
                            avail.getJSONObject(z).getBoolean("visible")&& availabilities.contains(avail.getJSONObject(z).getString("stockLevel").toUpperCase())){
                        SearchableProduct product = new SearchableProduct(
                                json.getJSONArray("data").getJSONObject(i).getInt("id"),
                                json.getJSONArray("data").getJSONObject(i).getJSONObject("name").getString("en"),
                                json.getJSONArray("data").getJSONObject(i).getJSONObject("brand").getJSONObject("name").getString("en"));
                        ;
                        result.add(product);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.isEmpty()? null:result;
    }*/
    public  List<SearchableProduct> getSearchableProducts(APIProductAvailabilitiesRequest request, RegionEnum region, List<StockAvailability> availability){
        return getSearchableProducts(request, region,availability, ProductDsl.Visibility.VISIBLE, null);
    }
    public  List<SearchableProduct> getSearchableProducts(APIProductAvailabilitiesRequest request, RegionEnum region, List<StockAvailability> availability, ProductDsl.Visibility vis, Integer brandId) {
        parse(client.getResponse(request));
        List<SearchableProduct> result = new ArrayList<SearchableProduct>();
        List<String>availabilities= new ArrayList<String>();
        Boolean visibility = vis.equals(ProductDsl.Visibility.VISIBLE);
        for(StockAvailability a:availability){
            availabilities.add(a.toString());
        }
        try{
            for(int i = 0; i< json.getJSONArray("data").length(); i++){
                JSONArray avail = json.getJSONArray("data").getJSONObject(i).getJSONArray("distributionCentres");
                for(int z = 0; z<avail.length(); z++){
                    if(avail.getJSONObject(z).getString("id").equals(region.getDC())&&
                            visibility.equals(avail.getJSONObject(z).getBoolean("visible"))&&
                            availabilities.contains(avail.getJSONObject(z).getString("stockLevel").toUpperCase())&&
                                    (brandId == null || brandId.equals(data.getJSONObject(i).getJSONObject("brand").getInt("id")))){
                        SearchableProduct product = new SearchableProduct(
                                json.getJSONArray("data").getJSONObject(i).getInt("id"),
                                json.getJSONArray("data").getJSONObject(i).getJSONObject("name").getString("en"),
                                json.getJSONArray("data").getJSONObject(i).getJSONObject("brand").getJSONObject("name").getString("en"));
                     result.add(product);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Get brand id field from JSON
    public  Integer getBrandId(APIBrandsRequest request, String designerName){
        parse(client.getResponse(request));
        Integer id = null;
        for(int i=0;i<data.length();i++){
            try{
                if(data.getJSONObject(i).getJSONObject("name").getString("en").equals(designerName)){
                    id = data.getJSONObject(i).getInt("id");
                }

            }  catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return id;
    }


    //Get product id field from JSON
    public String getProductId(){
        String id = null;
        try{
            id = json.getJSONArray("data").getJSONObject(0).getString("productId");
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
}
