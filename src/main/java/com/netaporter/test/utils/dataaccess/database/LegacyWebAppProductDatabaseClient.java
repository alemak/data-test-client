package com.netaporter.test.utils.dataaccess.database;

import com.google.common.base.Joiner;
import com.netaporter.test.client.product.pojos.*;
import com.netaporter.test.utils.enums.RegionEnum;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.TestCase.fail;

/**
 * Created with IntelliJ IDEA.
 * User: c.dawson@london.net-a-porter.com
 * Date: 26/03/2013
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
@Component
public class LegacyWebAppProductDatabaseClient  {

    /*
     * TODO current assumption that this will only work with one Website/brand at a time, hence
     * only the 3 db connections.  To get inline with PS, would need all 9 channels.
     */

    @Autowired
    private LegacyWebAppChannelisedDatabaseClient dbClientINTL;
    @Autowired
    private LegacyWebAppChannelisedDatabaseClient dbClientAM;
    @Autowired
    private LegacyWebAppChannelisedDatabaseClient dbClientAPAC;

    @Value("${db.defaultChannel}")
    private String dbDefaultChannel;
    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN);

    static Logger logger  = Logger.getLogger(LegacyWebAppProductDatabaseClient.class);

    public Integer getStockLevelForProductSku(RegionEnum region, String productSku) {
        String query = "SELECT no_in_stock FROM stock_location WHERE sku = '" + productSku + "'";
        List<Map> results = executeSelect(region, query);

        if(results != null && !results.isEmpty()) {
            return (Integer) results.get(0).get("no_in_stock");
        }

        return null;
    }

    public boolean isPidInStock(RegionEnum region, String pid) {
        String query = " SELECT * FROM stock_location WHERE sku LIKE '"+ pid + "%' AND no_in_stock > 0;";
        List<Map> results = executeSelect(region, query);

        if (results.size() > 0) {
            return true;
        }

        return false;
    }


    public boolean isPidOrSkuInStock(RegionEnum region, String pidOrSku) {
        return isInStockOfMultipleSizes(region, pidOrSku, 1);
    }


    public boolean isPidRestrictedForCountry(RegionEnum region, String pid, String countryName) {
        if (countryName == null) {
            return false;
        }

        String query = "SELECT count(*) as count FROM shipping_restriction sr \n" +
                "join country_lookup cl on cl.id = sr.location\n" +
                "WHERE sr.pid = '" + pid + "' AND sr.location_type = 'COUNTRY' AND cl.display_name = '" + countryName + "'";

        List<Map> results = executeSelect(region, query);

        Map queryResult = results.get(0);
        Long count = (Long) queryResult.get("count");

        return (count > 0);
    }


    public Integer getInStockRestrictedShippingProductIdForCountry(RegionEnum region, String countryCode) {
        List<Map> results = executeSelect(region, "select min(pid) as pid from shipping_restriction\n" +
                "where pid in (\n" +
                "    select distinct(p.search_prod_id) from searchable_product sp\n" +
                "    join product p on sp.id = p.search_prod_id\n" +
                "    join stock_location stock on p.sku = stock.sku\n" +
                "    where stock.no_in_stock > 0\n" +
                "    and sp.is_visible = \"T\"\n" +
                "    and p.is_visible = \"T\"\n" +
                ")\n" +
                "and location = \"" + countryCode + "\";");

        Integer pid = (Integer) results.get(0).get("pid");
        return pid;
    }


    public List<SearchableProduct> getRandomAvailableSearchableProductsByKeywordAndTitle(RegionEnum region, int max, String keyword, String title) {
        String query = "SELECT sp.id, sp.title, d.name AS designerName FROM searchable_product sp " +
                "JOIN designer d ON sp.designer_id = d.id " +
                "JOIN product p ON sp.id = p.search_prod_id " +
                "JOIN stock_location stock ON p.sku = stock.sku " +
                "WHERE stock.no_in_stock > 0 " +
                "AND is_sellable = \"T\" " +
                "AND sp.is_visible = \"T\" " +
                "AND p.is_visible = \"T\" " +
                "AND sp.keywords like \"%"+ keyword +"%\" " +
                "AND sp.title like \"%"+ title +"%\" " +
                "ORDER BY RAND() LIMIT " + max;
        List<Map> searchableProductRows = executeSelect(region, query);
        return asSearchableProducts(searchableProductRows);
    }


    public List<SearchableProduct> getRandomAvailableSearchableProductsByDesigner(RegionEnum region, int max, String designerName) {
        String query = "SELECT sp.id, sp.title, d.name AS designerName FROM searchable_product sp " +
                "JOIN designer d ON sp.designer_id = d.id " +
                "JOIN product p ON sp.id = p.search_prod_id " +
                "JOIN stock_location stock ON p.sku = stock.sku " +
                "WHERE stock.no_in_stock > 0 " +
                "AND is_sellable = \"T\" " +
                "AND sp.is_visible = \"T\" " +
                "AND p.is_visible = \"T\" " +
                "AND d.name = \"" + designerName + "\" " +
                "ORDER BY RAND() LIMIT " + max;
        List<Map> searchableProductRows = executeSelect(region, query);
        return asSearchableProducts(searchableProductRows);
    }


    public List<SearchableProduct> getRandomAvailableSearchableProducts(RegionEnum region, int max) {
        List<Map> searchableProductRows = executeSelect(region, "SELECT sp.id, sp.title, d.name AS designerName FROM searchable_product sp\n" +
                "JOIN designer d ON sp.designer_id = d.id\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                "WHERE stock.no_in_stock > 2\n" +
                "AND sp.is_visible = \"T\"\n" +
                "AND p.is_visible = \"T\"\n" +
                "ORDER BY RAND() LIMIT " + max);

        return asSearchableProducts(searchableProductRows);
    }


    public List<SearchableProduct> getRandomAvailableSearchableProductsForWearItWith(RegionEnum region, int max) {
        List<Map> searchableProductRows = executeSelect(region, "SELECT sp.id, sp.title, d.name AS designerName FROM searchable_product sp\n" +
                "JOIN designer d ON sp.designer_id = d.id\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                "JOIN related_product rp ON rp.search_prod_id = sp.id\n" +
                "WHERE stock.no_in_stock > 2\n" +
                "AND sp.is_visible = \"T\"\n" +
                "AND p.is_visible = \"T\"\n" +
                "ORDER BY rp.created_dts desc, RAND() LIMIT " + max);

        return asSearchableProducts(searchableProductRows);
    }

    /**
     * @param productIds A list of product ids to check for stock availability
     * @return
     */

    public String returnAnyInStockProductIdFromProductIds(RegionEnum region, List<String> productIds) {
        String whereClause = "";
        for (int i = 0; i < productIds.size(); i++) {
            String productId = productIds.get(i);
            whereClause += "sku LIKE \"" + productId + "%\"";
            if (i + 1 < productIds.size()) {
                whereClause += " || ";
            }
        }
        String query = "SELECT distinct(substring_index(sl.sku, '-', 1)) as productId FROM stock_location sl " +
                "left join shipping_restriction sr on (cast(substring_index(sl.sku, '-', 1) as unsigned ) = sr.pid) " +
                "WHERE (" + whereClause + ") " +
                "AND sl.no_in_stock > 0 " +
                "AND sr.pid is null";

        List<Map> inStockProductIds = executeSelect(region, query);

        int numberOfInStockProducts = inStockProductIds.size();
        int randomIndex = (int) Math.random() * numberOfInStockProducts;
        String randomProductId = (String) inStockProductIds.get(randomIndex).get("productId");
        return randomProductId;
    }


    /* Returns an in-stock product for a given channel. The stock product is just the first one found */

    public Map<String, Object> findInStockProduct(RegionEnum region) {
        String inStock = "SELECT sp.id, sp.title, d.name AS designerName, p.sku, stock.no_in_stock\n" +
                "FROM searchable_product sp\n" +
                "JOIN designer d ON sp.designer_id = d.id\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                "WHERE stock.no_in_stock > 2\n" +
                "AND is_sellable = 'T'\n" +
                "AND sp.is_visible = 'T'\n" +
                "AND p.is_visible = 'T'\n" +
                "ORDER BY RAND() LIMIT 1";
        List<Map> result = executeSelect(region, inStock);
        return result.get(0);
    }


    /**
     * Finds a random out of stock product
     *
     * @return
     */

    public Map<String, Object> findOutOfStockProduct(RegionEnum region) {

        String inStock = "SELECT sp.id, sp.title, d.name AS designerName FROM searchable_product sp\n" +
                "JOIN designer d ON sp.designer_id = d.id\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                "WHERE stock.no_in_stock = 0\n" +
                "AND sp.is_visible = 'T'\n" +
                "AND p.is_visible = 'T'\n" +
                "ORDER BY RAND() LIMIT 1";

        /*def data = runQuery(channel, inStock) { row ->
                resultTemp << row.toRowResult()*/
        List<Map> result = (List<Map>) executeSelect(region, inStock);
        return result.get(0);
    }


    public List<String> getCategoriesForPid(RegionEnum region, Integer pid) {
        String getNavigationCategoriesQuery = "SELECT nc.name FROM searchable_product sp\n" +
                "\tJOIN product_navigation_category pnc ON pnc.spid = sp.id\n" +
                "\tJOIN  _navigation_category nc ON nc.id = pnc.nav_level3\n" +
                "WHERE sp.id = " + pid;
        List<Map> categoriesMap = executeSelect(region, getNavigationCategoriesQuery);
        LinkedList<String> result = new LinkedList<String>();
        for (Map<String, Object> map : categoriesMap) {
            result.add(map.get("name").toString());
        }
        return result;
    }


    public String findInStockRestrictedShippingProductIdForCountry(RegionEnum region, String country) {


        String restricted =
                "select min(pid) as pid from shipping_restriction\n" +
                        "where pid in (\n" +
                        "    select distinct(p.search_prod_id) from searchable_product sp\n" +
                        "    join product p on sp.id = p.search_prod_id\n" +
                        "    join stock_location stock on p.sku = stock.sku\n" +
                        "    where stock.no_in_stock > 0\n" +
                        "    and sp.is_visible = 'T'\n" +
                        "    and p.is_visible = 'T'\n" +
                        ")\n" +
                        "and location = \"" + country + "\";";


        List<Map> result = executeSelect(region, restricted);


        return result.get(0).get("pid").toString();
    }



    public boolean hasMultipleSizesInStock(RegionEnum region, String pidOrSku) {
        return isInStockOfMultipleSizes(region, pidOrSku, 2);
    }


    public boolean isItemLowInStock(RegionEnum region, String pidOrSku){
        String query = " SELECT * FROM stock_location WHERE sku LIKE '" + pidOrSku + "%' AND no_in_stock <= 2;";
        List<Map> results = executeSelect(region, query);

        return (results.size() > 0);
    }

    private boolean isInStockOfMultipleSizes(RegionEnum region, String pidOrSku, int minimumOfSizes) {
        String query = " SELECT * FROM stock_location WHERE sku LIKE '" + pidOrSku + "%' AND no_in_stock > 0;";
        List<Map> results = executeSelect(region, query);

        if (results.size() >= minimumOfSizes) {
            return true;
        }

        return false;
    }

    // returns first level category of given product

    public String returnFirstLevelProductCategory(RegionEnum region, String pid) {
        String productCategory = "select name from product_summary ps \n" +
                "join _navigation_category nc on nc.id = ps.av_nav_level1\n" +
                "where ps.sp_id = '" + pid + "'";

        List<Map> results = executeSelect(region, productCategory);
        String result = results.get(0).get("name").toString();
        return result;
    }

    //returns second level category of given product

    public String returnSecondLevelProductCategory(RegionEnum region, String pid) {
        String productSubCategory = "select name from product_summary ps \n" +
                "join _navigation_category nc on nc.id = ps.av_nav_level2\n" +
                "where ps.sp_id = '" + pid + "'";
        List<Map> results = executeSelect(region, productSubCategory);
        String result = results.get(0).get("name").toString();
        return result;
    }

    public Map<String, Object> findRandomMultiSizedProduct(RegionEnum region) {
        String query = "SELECT * FROM product \n" +
                "   JOIN ( \n" +
                "   SELECT * FROM stock_location sl \n" +
                "WHERE sl.no_in_stock > 0) AS fsl \n" +
                "ON product.sku = fsl.sku \n" +
                "JOIN ( \n" +
                "   SELECT * FROM searchable_product sp \n" +
                "   WHERE sp.is_visible = 'T' \n" +
                "   ORDER BY RAND()) AS fsp \n" +
                "ON fsp.id = product.search_prod_id \n" +
                "WHERE size NOT LIKE 'n/a' AND size NOT LIKE 'One size'\n" +
                "LIMIT 0,1;";

        List<Map> result = executeSelect(region, query);
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }
    public Map<String, Object> findRandomMultiSizedInStockProduct(RegionEnum region) {

        final int MAX_ATTEMPTS = 5;

        String pid;
        int attempt = 0;
        List<String> pidList = new ArrayList<String>();

        Map<String, Object> result;

        do { // attempt to find a product with multiple sizes in stock up to 5 times before failing
            if (attempt>=MAX_ATTEMPTS)
                throw new RuntimeException("Failed to find a product with multiple sizes in stock after " + MAX_ATTEMPTS + " attempts. PIDs queried: " + pidList.toString());

            result = findInStockProduct(region, "", "AND size NOT LIKE 'n/a' AND size NOT LIKE 'One size'");

            pid = result.get("id").toString();
            pidList.add(pid);
            attempt++;
        } while (!hasMultipleSizesInStock(region, pid));


        return result;
    }


    // returns list of url keys for designers that either only have full priced items or have full priced and sale items and they have at least one item available

    public List<Map> findDesignersWithAllFullPricedProductsOrFullPricedAndSaleProducts(RegionEnum region){
        String designersFullPriceAndFullPriceAndSaleQuery =
                "select d.url_key from designer d\n" +
                        "join searchable_product sp on sp.designer_id = d.id\n" +
                        "join product p on p.search_prod_id = sp.id\n" +
                        "join stock_location sl on sl.sku = p.sku\n" +
                        "join attribute_value av on av.search_prd_id = sp.id\n" +
                        "where p.is_visible = 'T' and sp.is_visible = 'T' and d.state = 'Visible'\n" +
                        "group by d.name\n" +
                        "order by rand()";
        List<Map> result = executeSelect(region, designersFullPriceAndFullPriceAndSaleQuery);
        return result;
    }

    //returns list of designer names that have no items in stock and are not coming soon

    public List<Map> findDesignersWithNoProducts(RegionEnum region) {
        String designersWithNoProductsQuery =
                "select d.name from designer d, searchable_product sp, product p, stock_location sl\n" +
                        "where d.id = sp.designer_id\n" +
                        "and sp.id = p.search_prod_id\n" +
                        "and p.sku = sl.sku\n" +
                        "and d.state != 'coming soon'\n" +
                        "group by d.name having sum(no_in_stock) <1\n" +
                        "order by rand()";
        List<Map> result = executeSelect(region, designersWithNoProductsQuery);
        return result;
    }


    //returns list of designer url keys that have sale items only

    public List<Map> findDesignersWithSaleProductsOnly(RegionEnum region) {
        String designersWithSaleProductsOnlyQuery =
                "SELECT d.url_key, av.value FROM designer d, attribute_value av, searchable_product sp\n" +
                        "where d.id = sp.designer_id\n" +
                        "and sp.id = av.search_prd_id\n" +
                        "group by d.url_key having av.value = 'T'\n" +
                        "order by rand()";
        List<Map> result = executeSelect(region, designersWithSaleProductsOnlyQuery);
        return result;
    }

    /*


    SELECT *
    FROM   product
       JOIN (SELECT *
             FROM   stock_location sl
             WHERE  sl.no_in_stock = 1) AS fsl
         ON product.sku = fsl.sku
       JOIN (SELECT *
             FROM   searchable_product sp
             WHERE  sp.is_visible = 'T'  AND NOT EXISTS (SELECT * FROM attribute_value av WHERE av.pa_id = 'SALE' AND av.value = 'T' AND av.search_prd_id = sp.id)
             ORDER  BY RAND()) AS fsp
         ON fsp.id = product.search_prod_id

    LIMIT  0, 1

     */
    public Map<String, Object> findRandomProductStockLevelAndSale(RegionEnum region, int minStockLevel, int maxStockLevel, boolean sale) {
        String qry = "SELECT * \n" +
                "FROM   product \n" +
                "       JOIN (SELECT * \n" +
                "             FROM   stock_location sl \n" +
                "             WHERE  sl.no_in_stock >= " + minStockLevel + " AND sl.no_in_stock <= " + maxStockLevel + ") AS fsl \n" +
                "         ON product.sku = fsl.sku \n" +
                "       JOIN (SELECT * \n" +
                "             FROM   searchable_product sp \n" +
                "             WHERE  sp.is_visible = 'T'  AND " + (sale ? "" : "NOT") + " EXISTS (SELECT * FROM attribute_value av WHERE av.pa_id = 'SALE' AND av.value = 'T' AND av.search_prd_id = sp.id)\n" +
                "             ORDER  BY RAND()) AS fsp \n" +
                "         ON fsp.id = product.search_prod_id \n" +
                "       \n" +
                "LIMIT  0, 1 ";
        List<Map> result = executeSelect(region, qry);
        if (result.size() == 0) {
            return null;
        }
        return result.get(0);
    }


    public Map<String, Object> findRandomLowStockProduct(RegionEnum region) {
        return findRandomProductStockLevelAndSale(region, 2, 2, false);
    }


    public Map<String, Object> findRandomOnlyOneLeftProduct(RegionEnum region) {
        return findRandomProductStockLevelAndSale(region, 1, 1, false);
    }


    public Map<String, Object> getDesignerForPid(RegionEnum region, String pid) {

        String query =
                "SELECT * FROM designer d \n" +
                        "JOIN searchable_product s\n" +
                        "ON s.designer_id = d.id\n" +
                        "WHERE s.id =" + pid;

        List<Map> result = executeSelect(region, query);

        return result.get(0);
    }


    public List<String> getSizesForPid(RegionEnum region, String pid) {

        String query =
                "SELECT size FROM product WHERE search_prod_id = " + pid + " AND is_visible = 'T'";

        List<Map> results = executeSelect(region, query);
        List<String> sizes = new LinkedList<String>();
        for (Map result : results) {
            sizes.add((String) result.get("size"));
        }
        return sizes;
    }
    /*
        Returns a list as pid's can have multiple level 3 categories
     */

    public List<Map> getNavCatForPid(RegionEnum region, Integer navCatLevel, String pid) {
        String qry =
                "SELECT nc.name FROM _navigation_category nc \n" +
                        "JOIN product_navigation_category pnc \n" +
                        "ON pnc.nav_level"+navCatLevel.toString()+" = nc.id \n" +
                        "WHERE pnc.spid = " + pid;

        return executeSelect(region, qry);

    }


    public String getRestrictedShippingCountry(RegionEnum region, String restrictionType, String pid, String countryLookupRegion) {

        String restrictedShippingCountry = "SELECT cl.display_name FROM country_lookup cl JOIN shipping_restriction sr ON sr.location " +
                " = cl.id WHERE pid = " + pid +
                " AND restriction_code = '" + restrictionType + "' AND region = '"+
                countryLookupRegion + "'  and is_enabled = 'T';";

        List<Map> result = executeSelect(region, restrictedShippingCountry);
        if (result.size() == 0) {
            return null;
        }

        return result.get(0).get("display_name").toString();
    }


    public String getNonRestrictedShippingCountry(RegionEnum region, String restrictionType, String pid, String countryLookupRegion) {

        String nonRestrictedShippingCountry = "SELECT cl.display_name FROM country_lookup cl where id not in " +
                " (SELECT cl.id FROM country_lookup cl JOIN shipping_restriction sr ON sr.location = cl.id WHERE pid = " + pid +
                " AND restriction_code = '" + restrictionType + "' AND region = '"+
                countryLookupRegion + "') AND region = '" + countryLookupRegion + "' and is_enabled = 'T';";

        List<Map> result = executeSelect(region, nonRestrictedShippingCountry);
        return result.get(0).get("display_name").toString();

    }


    public String getInStockSellableProductSkuFromPid(RegionEnum region, String pid) {
        String gettingSkuQuery = "SELECT min(sku) FROM stock_location WHERE sku LIKE '" + pid + "%' AND no_in_stock > 0 AND is_sellable = 'T';";

        List<Map> sku = executeSelect(region, gettingSkuQuery);

        if (sku.isEmpty())
        {
            return null;
        }

        return sku.get(0).get("min(sku)").toString();
    }



    public Map.Entry<String, String> findRestrictedProductPidAndCountryForRegion(RegionEnum region, String restriction, String countryLookupRegion) {

        String sqlQuery = "SELECT sr.pid, cl.display_name\n" +
                "FROM shipping_restriction sr, country_lookup cl, stock_location sl\n" +
                "WHERE sr.location = cl.id AND cl.region = '"+countryLookupRegion+"' AND sr.restriction_code = '"+restriction+"' AND cl.is_enabled = 'T' AND sl.sku LIKE CONCAT(sr.pid, '%')";

        List<Map> maps = executeSelect(region, sqlQuery);

        Collections.shuffle(maps);

        for (Map map : maps) {
            String pid = map.get("pid").toString();
            String displayName = (String) map.get("display_name");

            return new AbstractMap.SimpleEntry<String, String>(pid, displayName);
        }

        return null;
    }


    private List<SearchableProduct> asSearchableProducts(List<Map> searchableProductRows) {
        List<SearchableProduct> searchableProducts = new ArrayList<SearchableProduct>();
        for (Map map : searchableProductRows) {
            SearchableProduct searchableProduct = new SearchableProduct((Integer) map.get("id"),
                    (String) map.get("title"),
                    (String) map.get("designerName"));
            searchableProducts.add(searchableProduct);
        }

        return searchableProducts;
    }


    private LegacyWebAppChannelisedDatabaseClient getChannelisedDbClient(RegionEnum region) {

        // Mimicing behaviour from testingutils LegacyWebAppChannelisedDatabaseClient
        RegionEnum regionToUse = region;
        if (regionToUse == null) {
            // TODO review the need for default channel.  Might go away when we refactor this.
            if ((dbDefaultChannel != null) && (!dbDefaultChannel.equals("${db.defaultChannel}"))) {
                regionToUse = parseRegion(dbDefaultChannel);
            } else {
                throw new RuntimeException("Region must not be null or db.defaultDefaultChannel must be defined " +
                        "before calling getChannelisedDbClient()");
            }
        }

        LegacyWebAppChannelisedDatabaseClient dbClient = null;
        switch (regionToUse){
            case INTL: {
                dbClient = dbClientINTL;
                break;
            }
            case AM: {
                dbClient = dbClientAM;
                break;
            }
            case APAC: {
                dbClient = dbClientAPAC;
                break;
            }
        }
        return dbClient;
    }


    public List<Map> executeSelect(RegionEnum region, String query) {
        return getChannelisedDbClient(region).executeSelect(query);
    }

    private RegionEnum parseRegion(String channel) {
        return RegionEnum.valueOf(channel.toUpperCase());
    }

    public int updateStockLevel(RegionEnum region, List<String> skus, int stockLevel) {
        String updateStockQuery = "UPDATE stock_location SET no_in_stock = " + stockLevel + " WHERE sku in (";
        for (String sku:  skus) {
            updateStockQuery += "'" + sku + "',";
        }
        // Remove last comma
        updateStockQuery = updateStockQuery.substring(0, updateStockQuery.length()-1);
        updateStockQuery += ")";
        return executeUpdate(region, updateStockQuery);
    }

    public int executeUpdate(RegionEnum region, String updateQuery) {
        return getChannelisedDbClient(region).executeUpdate(updateQuery);
    }

    public String executeUpdateAndReturnTheLastInsertedId(RegionEnum region, String query, String lastIdTable){
        return getChannelisedDbClient(region).executeUpdateAndReturnTheLastInsertedId(query, lastIdTable);
    }

    private List<SearchableProduct> createSearchableProducts(List<Map> searchableProductRows) {
        List<SearchableProduct> searchableProducts = new ArrayList<SearchableProduct>();
        for (Map map : searchableProductRows) {
            SearchableProduct searchableProduct = new SearchableProduct((Integer) map.get("id"),
                    (String) map.get("title"),
                    (String) map.get("designerName"));
            searchableProducts.add(searchableProduct);
        }

        return searchableProducts;
    }
    public void derestrictProductAsHazmat(RegionEnum region, String pid, String country_code) {
        String query = "DELETE FROM shipping_restriction where pid="+pid+" AND location='"+country_code+"' AND restriction_code = 'HAZMAT';";
        executeUpdate(region, query);
    }

    public void expireCustomerReservation(RegionEnum region, Integer customerId, String sku) {

        String reservationSql = "UPDATE simple_reservation SET reserved_quantity=redeemed_quantity" +
                " WHERE customer_id=" + customerId + " AND sku='" + sku + "'";

        executeUpdate(region, reservationSql);
    }

    public List<CestaItem> findBasketItems(RegionEnum region, String basketId) {
        String basketSql =
                "SELECT id, ip_address, user_agent, sku, quantity, reserved_quantity FROM basket_item where basket_id = '" + basketId +
                        "' UNION SELECT id, ip_address, user_agent, sku, 1, 0 FROM basket_voucher_item where basket_id = '" + basketId + "'";

        List<Map> basketItemRecords = executeSelect(region, basketSql);

        if (basketItemRecords.isEmpty()) return null;

        List<CestaItem> basketItems = new ArrayList<CestaItem>();

        for(Map bi: basketItemRecords){
            CestaItem cestaItem = new CestaItem();
            cestaItem.setBasketId(basketId);
            cestaItem.setId((Integer) bi.get("id"));
            cestaItem.setSku((String) bi.get("sku"));
            cestaItem.setIpAddress((String) bi.get("ip_address"));
            cestaItem.setUserAgent((String) bi.get("user_agent"));
            cestaItem.setQuantity(((Long) bi.get("quantity")).intValue());
            cestaItem.setReservedQuantity(((Long) bi.get("reserved_quantity")).intValue());
            basketItems.add(cestaItem);
        }

        return basketItems;
    }

     //returns url key list of "coming soon" designers
    public List<Map> findComingSoonDesigners(RegionEnum region) {
        String comingSoonDesignersQuery = "SELECT d.name, d.url_key FROM designer d\n" +
                "where state = 'coming soon'\n" +
                "order by rand()";
        List<Map> result = executeSelect(region, comingSoonDesignersQuery);
        return result;
    }

    public Map<String, Object> findInStockMultiColourProduct(RegionEnum region) {
        String joinClause = "JOIN related_product rp on sp.id = rp.search_prod_id";
        String whereClause = "AND rp.type_id='COLOUR'";
        return findInStockProduct(region, joinClause, whereClause);
    }

    /**
     * Returns in stock product ID. Override of findInStockProduct()
     * @param joinClause Start with JOIN..
     *                   Can be empty
     * @param whereClause Start with AND..
     *                    Can be empty
     */
    public Map<String, Object> findInStockProduct(RegionEnum region, String joinClause, String whereClause) {
        String inStock = "SELECT sp.id, sp.title, d.name AS designerName, p.sku, stock.no_in_stock\n" +
                "FROM searchable_product sp\n" +
                "JOIN designer d ON sp.designer_id = d.id\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                joinClause + "\n" +
                "WHERE stock.no_in_stock > 2\n" +
                "AND is_sellable = 'T'\n" +
                "AND sp.is_visible = 'T'\n" +
                "AND p.is_visible = 'T'\n" +
                "AND sp.designer_id != 0\n" + // ignore designer_id 0
                whereClause + "\n" +
                "ORDER BY RAND() LIMIT 1";
        List<Map> result = executeSelect(region, inStock);
        return result.get(0);
    }

    /**
     * Return in stock product ID by navigation category
     * @param level Nav cat level (1,2 or 3)
     * @param navcat Nav cat name as in _navigation_category.name field in . Will use OR logic when given more than one value
     *
     */
    public Map<String, Object> findInStockProductByNavCat(RegionEnum region, int level, List<String> navcat) {
        String joinClause = "JOIN product_navigation_category pnc on pnc.spid = sp.id\n" +
                "JOIN _navigation_category nc on nc.id = pnc.nav_level"+level;
        String whereClause = "AND (nc.name='"+navcat.get(0)+"'";

        if (navcat.size()>1){
            navcat.remove(0); // already added above so removing
            for (String n : navcat) {
                whereClause = whereClause + "\nOR nc.name='"+n+"'";
            }
        }

        whereClause = whereClause + ")";

        return findInStockProduct(region, joinClause, whereClause);
    }

    public Map<String, Object> findInStockProductWithCountrySalesTaxByCountry(RegionEnum region, String countryCode) {
        String sql = "select sp.id, cl.vat_percentage, cst.tax_rate\n" +
                "from country_sales_tax cst\n" +
                "inner join product_type pt\n" +
                "on cst.product_type = pt.code\n" +
                "inner join searchable_product sp\n" +
                "on pt.code = sp.product_type\n" +
                "inner join country_lookup cl\n" +
                "on cl.id = cst.country\n" +
                "inner join product p ON sp.id = p.search_prod_id\n" +
                "inner join stock_location stock ON p.sku = stock.sku\n" +
                "where cl.id = '"+ countryCode +"'\n" +
                "AND is_sellable = 'T'\n" +
                "AND sp.is_visible = 'T'\n" +
                "AND p.is_visible = 'T'\n" +
                "AND sp.designer_id != 0\n";

        List<Map> result = executeSelect(region, sql);
        // randomize the products and return the first
        Collections.shuffle(result);
        return result.get(0);
    }

    public Integer findInStockProductWithoutCountrySalesTaxByCountry(RegionEnum region, String countryCode) {
        String sql = "SELECT sp.id\n" +
                "FROM searchable_product sp\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                "WHERE stock.no_in_stock > 2\n" +
                "AND is_sellable = 'T'\n" +
                "AND sp.is_visible = 'T'\n" +
                "AND p.is_visible = 'T'\n" +
                "and sp.id not in (\n" +
                "select sp.id from country_sales_tax cst\n" +
                "inner join product_type pt on cst.product_type = pt.code\n" +
                "inner join searchable_product sp on pt.code = sp.product_type\n" +
                "inner join country_lookup cl on cl.id = cst.country\n" +
                "where cl.id = '"+countryCode+"')";

        List<Map> result = executeSelect(region, sql);
        // randomize the products and return the first
        Collections.shuffle(result);
        return (Integer) result.get(0).get("id");
    }
    public Integer findInStockProductWithPriceThreshold(RegionEnum region, BigDecimal threshold, Boolean priceIsOverThreshold) {

        String operator =  priceIsOverThreshold ? " >= " : " < ";
        String query = "SELECT sp.id, pa.sku AS paSKU\n" +
                "FROM searchable_product sp\n" +
                "JOIN designer d ON sp.designer_id = d.id\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                "JOIN channel_pricing cp ON cp.sku = stock.sku\n" +
                "LEFT JOIN price_adjustment pa ON p.sku=pa.sku\n" +
                "WHERE stock.no_in_stock > 2\n" +
                "AND is_sellable = 'T'\n" +
                "AND sp.is_visible = 'T'\n" +
                "AND p.is_visible = 'T'\n" +
                "And cp.offer_price " + operator + "'" + threshold + "'\n" +
                "HAVING paSKU IS NULL;";
        List<Map> results = executeSelect(region, query);

        // randomize the products and return the first
        Collections.shuffle(results);
        return (Integer) results.get(0).get("id");
    }

    //return the pid of an instock product with price relative to a threshold, taking the countryCode into account
    public Integer findInStockProductWithPriceThreshold(RegionEnum region,BigDecimal threshold, String countryCode, Boolean priceIsOverThreshold) {
        logger.info("Looking for pid in the db...");
        long startTime = System.nanoTime();
        String operator =  priceIsOverThreshold ? " >= " : " < ";
        String query = "SELECT sp.id, pa.sku AS paSKU\n" +
                "FROM searchable_product sp\n" +
                "JOIN designer d ON sp.designer_id = d.id\n" +
                "JOIN product p ON sp.id = p.search_prod_id\n" +
                "JOIN stock_location stock ON p.sku = stock.sku\n" +
                "JOIN channel_pricing cp ON cp.sku = stock.sku\n" +
                "LEFT JOIN price_adjustment pa ON p.sku=pa.sku\n" +
                "WHERE stock.no_in_stock > 2\n" +
                "AND is_sellable = 'T'\n" +
                "AND sp.is_visible = 'T'\n" +
                "AND p.is_visible = 'T'\n" +
                "AND cp.locality = '" + countryCode + "'\n" +
                "AND cp.offer_price " + operator + "'" + threshold + "'\n" +
                "AND d.state = 'Visible'\n" +
                "HAVING paSKU IS NULL;";
        List<Map> results = executeSelect(region, query);
        long stopTime = System.nanoTime();
        String duration = String.valueOf((stopTime-startTime)/1000000);
        // randomize the products and return the first
        Collections.shuffle(results);
        Integer id = (Integer) results.get(0).get("id");
        logger.info("Database query found product with pid "+id+" in "+duration+"ms");
        return id;
    }

    public Map<String, Object> findInStockSingleColourProduct(RegionEnum region) {
        String joinClause = "JOIN related_product rp on sp.id = rp.search_prod_id";
        String whereClause = "AND rp.type_id != 'COLOUR'";
        return findInStockProduct(region, joinClause, whereClause);
    }

    public List<OrderItemSource> findOrderItemSource(RegionEnum region, Integer orderId) {
        String orderItemSourceSql =
                "SELECT ois.id, ois.order_item_id, ois.user_agent, ois.ip_address " +
                        "FROM order_item oi " +
                        "JOIN order_item_source ois on oi.id=order_item_id " +
                        "WHERE oi.order_id = " + orderId ;

        List<Map> orderItemSourceRecords = executeSelect(region, orderItemSourceSql);

        if (orderItemSourceRecords.isEmpty()) return null;

        List<OrderItemSource> orderItemsSource = new ArrayList<OrderItemSource>();

        for(Map ois: orderItemSourceRecords){
            OrderItemSource orderItem = new OrderItemSource();
            orderItem.setId((Integer) ois.get("id"));
            orderItem.setOrderItemId((Integer) ois.get("order_item_id"));
            orderItem.setIpAddress((String) ois.get("ip_address"));
            orderItem.setUserAgent((String) ois.get("user_agent"));
            orderItemsSource.add(orderItem);
        }

        return orderItemsSource;
    }

    public String findPostcodeByShippingOptionAndShippingMethodType(RegionEnum region, String shippingOption, String shippingMethodType) {
        String sql = "select outward from postcode p\n" +
                "inner join shipping_zone sz on sz.id = p.shipping_zone_id\n" +
                "inner join shipping_method sm on sm.postcode_zone_id = sz.id\n" +
                "where sm.type = '"+shippingMethodType+"'\n" +
                "and p.outward in (\n" +
                "select outward from postcode p\n" +
                "inner join shipping_zone sz on sz.id = p.shipping_zone_id\n" +
                "inner join shipping_method sm on sm.postcode_zone_id = sz.id\n" +
                "inner join shipping_option_product sop on sop.sku = sm.sku\n" +
                "inner join shipping_option so on so.id = sop.shipping_option_id\n" +
                "where so.name = '"+shippingOption+"')";

        List<Map> results = executeSelect(region, sql);
        Collections.shuffle(results);
        return results.get(0).get("outward").toString();
    }

    public String findPostCodePrefixForShippingOption(RegionEnum region, String shippingOption) {
        String sql = "select p.outward from postcode p\n" +
                "inner join shipping_zone sz on sz.id = p.shipping_zone_id\n" +
                "inner join shipping_method sm on sm.postcode_zone_id = sz.id\n" +
                "inner join shipping_option_product sop on sop.sku = sm.sku\n" +
                "inner join shipping_option so on so.id = sop.shipping_option_id\n" +
                "where so.name = '"+shippingOption+"'";

        List<Map> results = executeSelect(region, sql);
        Collections.shuffle(results);
        return results.get(0).get("outward").toString();
    }

    public Integer findRandomOneSizedProductInStock(RegionEnum region) {
        String randomOneSizeQry = "SELECT * FROM product \n" +
                "\tJOIN (\n" +
                "\t\t\tSELECT * \n" +
                "\t\t\tFROM stock_location sl\n" +
                "\t\t\tWHERE sl.no_in_stock > 0\n" +
                "\t\t\t) AS fsl ON product.sku = fsl.sku\n" +
                "\tJOIN (\n" +
                "\t\t\tSELECT *\n" +
                "\t\t\tFROM searchable_product sp\n" +
                "\t\t\tWHERE sp.is_visible = 'T'\n" +
                "\t\t\tORDER BY RAND()\n" +
                "\t) AS fsp ON fsp.id = product.search_prod_id\n" +
                "\tWHERE size LIKE 'n/a' \n" +
                "\tLIMIT 0,1";
        List<Map> result = executeSelect(region, randomOneSizeQry);
        if (result.size() == 0) {
            return null;
        }
        String pidStr = result.get(0).get("id").toString();
        Integer resInt = null;
        resInt = Integer.parseInt(pidStr);
        return resInt;
    }

    public List<Map> getAffiliateOrderlinesForOrderId(RegionEnum region, String id) {
        return executeSelect(region, "SELECT * FROM affiliate_orderlines WHERE order_id = " + id);

    }
    public String getCanonicalPid(RegionEnum region) {
        String listOfCanonicalPids = "SELECT canonical_product_id FROM searchable_product WHERE canonical_product_id IS NOT NULL";
        List<Map> results = executeSelect(region, listOfCanonicalPids);
        Collections.shuffle(results);
        try{
            return results.get(0).get("canonical_product_id").toString();
        }
        catch (IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("No canonical products found on this channel.\n"+e);
        }
    }

    public Map<String, String> getCountryCodeAndName(RegionEnum region, List<String> Countries) {
        //list of countries needs to be in a csv format for the select to work
        String sqlQuery = "select id, display_name from country_lookup where display_name IN ('"+ Joiner.on(",").join(Countries) +"')";
        List<Map> getCountryCodeAndNameResultSet = executeSelect(region, sqlQuery);

        Map<String,String> queryResults = new HashMap<String, String>();
        for (Map country : getCountryCodeAndNameResultSet) {
            String countryCode = country.get("id").toString();
            String countryName = country.get("display_name").toString();
            queryResults.put(countryCode,countryName);
        }
        return queryResults;
    }

    public Map<String, String> getCustomerCarePhoneNumber(RegionEnum region) {
        Map<String,String> theResults = new HashMap<String, String>();
        String sqlQuery = "Select id, value from system_properties \n" +
                "where id like '%CUSTOMER_CARE_PHONE%'";

        List<Map> results = executeSelect(region,sqlQuery);
        for (Map m : results) {
            String key = m.get("id").toString();
            String value = m.get("value").toString();
            theResults.put(key,value);
        }
        return theResults;
    }

    public String getCustomerEncodedPassword(RegionEnum region, String email) {
        List<Map> results = executeSelect(region, "select encoded_password as encoded_password from customer " +
                " where email = \"" + email + "\"");
        return (String) results.get(0).get("encoded_password");
    }

    public String getCustomerGlobalIdByEmail(RegionEnum region, String email) {
        List<Map> results = executeSelect(region, "select global_id as global_id from customer " +
                " where email = \"" + email + "\"");
        return (String) results.get(0).get("global_id");
    }
    public Integer getCustomerIdByEmail(RegionEnum region, String email) {
        List<Map> results = executeSelect(region, "select id as id from customer " +
                " where email = \"" + email + "\"");
        return (Integer) results.get(0).get("id");
    }
    public String getCustomerLanguageFromOrderid(RegionEnum region, Integer orderId){
        String query = "select c.language from orders o join " +
                "customer c on c.id = o.customer_id where o.id =" + orderId;
        List<Map> results = executeSelect(region, query);
        if (results.size() !=1)
            return null;
        else {
            Object storedLanguage = results.get(0).get("language");
            return storedLanguage == null ? "null" : storedLanguage.toString();
        }
    }
    public SimpleReservation getCustomerReservation(RegionEnum region, int customerId, String sku) {
        String reservationSql =
                "SELECT reserved_quantity, redeemed_quantity, status," +
                        " created_by, created_dts, last_updated_by, last_updated_dts, expiry_date" +
                        " FROM simple_reservation where customer_id = " + customerId + " and sku='" + sku + "'";

        List<Map> reservations = executeSelect(region, reservationSql);

        Map reservationRecord = reservations.get(0);

        SimpleReservation reservation = new SimpleReservation();
        reservation.setSku(sku);
        reservation.setReservedQuantity((Integer) reservationRecord.get("reserved_quantity"));
        reservation.setRedeemedQuantity((Integer) reservationRecord.get("redeemed_quantity"));
        reservation.setCreatedBy((String) reservationRecord.get("created_by"));
        reservation.setCreatedDts((Date) reservationRecord.get("created_dts"));
        reservation.setLastUpdatedBy((String) reservationRecord.get("last_updated_by"));
        reservation.setLastUpdatedDts((Date) reservationRecord.get("last_updated_dts"));
        reservation.setExpiryDate((Date) reservationRecord.get("expiry_date"));
        reservation.setStatus((String)reservationRecord.get("status"));

        return reservation;
    }
    /**
     * Returns list of in stock standardised sizes (XS,S,M etc) for a given PID
     */
    public List<String> getInStockStandardisedSizesForPid(RegionEnum region, String pid) {

        String query =
                "SELECT standardised_size FROM product pr, standardised_size ss, stock_location sl " +
                        "WHERE search_prod_id= " + pid + " " +
                        "AND pr.standardised_size_id=ss.id " +
                        "AND is_visible='T' " +
                        "AND pr.sku=sl.sku " +
                        "AND sl.no_in_stock>0;";

        List<Map> results = executeSelect(region, query);
        List<String> sizes = new LinkedList<String>();
        for (Map result : results) {
            sizes.add((String) result.get("standardised_size"));
        }
        return sizes;
    }

    public String getKeywordsForPid(RegionEnum region, Integer pid) {
        String getKeywordsQuery = "select keywords from searchable_product where id=" + pid;
        List<Map> result = executeSelect(region, getKeywordsQuery);

        return result.size()>0 ? result.get(0).get("keywords").toString() : "";
    }

    public String getProductColour(RegionEnum region, String pid) {

        String productColour = "select distinct(colour) from product \n" +
                "where search_prod_id = '" + pid + "'";

        List<Map> results = executeSelect(region, productColour);
        if(results.size()>1) throw new RuntimeException("More than 1 colour returned for PID " + pid);
        String result = results.get(0).get("colour").toString();
        return result;

    }

    public List<String> getRecommendedPids(RegionEnum region, String pid) {
        String sql =
                "SELECT s.id, r.position, r.sort_order\n" +
                        "FROM related_product r\n" +
                        "INNER JOIN searchable_product s ON s.id = r.related_prod_id\n" +
                        "INNER JOIN designer d ON d.id = s.designer_id\n" +
                        "WHERE r.search_prod_id = '" +pid+ "'\n" +
                        "AND s.is_visible='T'\n" +
                        "AND r.type_id = 'Recommended'";

        List<Map> result = executeSelect(region, sql);
        List<String> recommendedPids = new ArrayList<String>();

        // Map holds position and related PID
        Map<Integer,Integer> relatedPidsSortOrder1 = new HashMap<Integer,Integer>();
        Map<Integer,Integer> relatedPidsSortOrder2 = new HashMap<Integer,Integer>();

        for(Map r : result) {
            Integer relatedPid = (Integer) r.get("id");
            Integer position = (Integer) r.get("position");
            Integer sortOrder = (Integer) r.get("sort_order");

            // split related PIDs by sort order
            if (sortOrder==1) {
                relatedPidsSortOrder1.put(position, relatedPid);
            } else if (sortOrder == 2) {
                relatedPidsSortOrder2.put(position, relatedPid);
            } else {
                throw new RuntimeException("Unexpected sort order: " + sortOrder);
            }
        }

        Map<Integer,Integer> merged = new  HashMap<Integer,Integer>();
        merged.putAll(relatedPidsSortOrder2);
        merged.putAll(relatedPidsSortOrder1); // sort order 1 is higher priority so this will overwrite sort order 2 PIDs

        for (Map.Entry<Integer, Integer> entry : merged.entrySet())
        {
            String relatedPid = String.valueOf(entry.getValue());
            recommendedPids.add(relatedPid);
        }

        return recommendedPids;
    }

    public List<ShippingMethod> getShippingMethods(RegionEnum region) {
        List<ShippingMethod> shippingMethods = new ArrayList<ShippingMethod>();
        String sqlQuery = "select id, name from shipping_option";

        List<Map> shippingMethodResultset = executeSelect(region, sqlQuery);
        for (Map shippingOption : shippingMethodResultset) {
            ShippingMethod shippingMethod = new ShippingMethod();
            shippingMethod.setId((Integer)shippingOption.get("id"));
            shippingMethod.setName((String) shippingOption.get("name"));

            shippingMethods.add(shippingMethod);
        }
        return shippingMethods;
    }
    public String getSizeForSku(RegionEnum region, String sku) {

        String query =
                "SELECT size FROM product WHERE sku = '" + sku + "' AND is_visible = 'T'";

        List<Map> results = executeSelect(region, query);
        return (String)results.get(0).get("size");
    }

    public String getSourceAppNameOfOrder(RegionEnum region, Integer orderId) {
        String sourceAppNameSql = "SELECT source_app_name FROM orders where id = " + orderId;

        List<Map> sourceApp = executeSelect(region, sourceAppNameSql);

        Map source = sourceApp.get(0);

        Object source_app_name = source.get("source_app_name");

        return source_app_name == null ? null : source_app_name.toString() ;
    }

    public String getSourceAppVersionOfOrder(RegionEnum region, Integer orderId) {
        String sourceAppVersionSql = "SELECT source_app_version FROM orders where id = " + orderId;

        List<Map> sourceApp = executeSelect(region, sourceAppVersionSql);

        Map source = sourceApp.get(0);

        Object source_app_version = source.get("source_app_version");

        return source_app_version == null ? null : source_app_version.toString();
    }

    public String getStateByCountry(RegionEnum region, String country) {
        String sqlQuery = "select sl.display_name \n" +
                "from country_lookup cl\n" +
                "join state_lookup sl on cl.id=sl.country_id\n" +
                "where cl.display_name='"+country+"'";

        List<Map> results = executeSelect(region, sqlQuery);
        Collections.shuffle(results);
        return results.get(0).get("display_name").toString();
    }

    public String getSystemProperty(RegionEnum region, String id) {
        List<Map> results = executeSelect(region, "select value as value from system_properties " +
                " where id = \"" + id + "\"");
        return (String) results.get(0).get("value");
    }

    public String getTitleForPid(RegionEnum region, Integer pid) {
        String getTitleQuery = "select title from searchable_product where id=" + pid;
        List<Map> result = executeSelect(region, getTitleQuery);

        return result.size()>0 ? result.get(0).get("title").toString() : "";
    }

    public Double getVatRateByCountryCode(RegionEnum region, String countryCode) {
        String sql = "SELECT vat_percentage / 100 VAT FROM country_lookup where id = '"+countryCode+"';";
        List<Map> result = executeSelect(region, sql);
        return (Double)result.get(0).get("VAT");
    }

    public void giveCustomerStoreCredit(RegionEnum region, int customerID, String currency, double amount) {
        String storeCreditSql = "INSERT INTO customer_credit" +
                " (`customer_id`, `currency_code`, `value`, `description`, `created_dts`, `created_by`, `last_updated_dts`, `last_updated_by`, `expiry_date`, `notified_of_expiry`)" +
                " VALUES (" + customerID + ", '" + currency + "', " + amount + ", 'Store Credit', now(), 'automation', now(), 'automation', now() + INTERVAL 1 DAY, 'F')";

        executeUpdate(region, storeCreditSql);
    }
    public boolean isAccountActive(RegionEnum region, String email) {
        List<Map> results = executeSelect(region, "select id as id from customer" +
                " where isActive = 'T' " +
                " and email = \"" + email + "\"");
        return results.size() > 0;
    }

    public boolean isBeautyLinkOn(RegionEnum region) {
        String isBeautyLinkOn = "SELECT value FROM system_properties  WHERE id = 'BEAUTY_GO_LIVE_DATE'";
        List<Map> results = executeSelect(region, isBeautyLinkOn);
        if (results.size()==0){
            fail("Beauty system property does not exist in the webDB for this channel");
        }
        String result = results.get(0).get("value").toString();
        boolean isTrue=false;
        if (result !=null){
            long now = DateTimeUtils.currentTimeMillis();
            try{
                if (DATE_TIME_FORMATTER.parseDateTime(result).isBefore(now))
                    isTrue= true;
            }
            catch(Exception e) {
                System.out.println("The system properties BEAUTY_GO_LIVE_DATE are in the wrong format" +e);
            }
        }
        return isTrue;
    }

    public boolean isPidInShippingRestrictionsTable(RegionEnum region, String pid) {
        String checkPidIsInShippingRestrictionTableQuery = "SELECT id FROM shipping_restriction where pid = '" + pid + "';";
        List<Map> result = executeSelect(region, checkPidIsInShippingRestrictionTableQuery);
        return !result.isEmpty();
    }

    public boolean isProductInSale(RegionEnum region, String pid){
        String sqlQuery = new StringBuilder().append("SELECT count(ep.product_id) \n")
                .append("FROM event_product ep \n")
                .append("JOIN event_detail ed \n")
                .append("ON ed.id = ep.event_id \n")
                .append("WHERE now() between ed.start_date \n")
                .append("AND ed.close_date \n")
                .append("AND ed.enabled \n")
                .append("AND product_id = ")
                .append(pid).toString();
        List<Map> results = executeSelect(region, sqlQuery);
        if (Integer.parseInt(results.get(0).get("count(ep.product_id)").toString()) == 0){
            return true;
        }
        return false;
    }
    public boolean isSaleOnCheckByDate(RegionEnum region)    {

        boolean isSaleOn = false;
        String saleStartDateQuery = "SELECT value from system_properties where id = 'SALES_ENABLED_DATE_START';";
        List<Map> startDateResults = executeSelect(region, saleStartDateQuery);
        String saleStartDate = startDateResults.get(0).get("value").toString();
        String saleEndDateQuery = "SELECT value from system_properties where id = 'SALES_ENABLED_DATE_END';";
        List<Map> endDateResults = executeSelect(region, saleEndDateQuery);
        String saleEndDate = endDateResults.get(0).get("value").toString();

        if(saleStartDate != null && !saleStartDate.isEmpty() && saleEndDate != null && !saleEndDate.isEmpty()){
            long now = DateTimeUtils.currentTimeMillis();
            try{

                if(DATE_TIME_FORMATTER.parseDateTime(saleStartDate).isBefore(now)
                        && DATE_TIME_FORMATTER.parseDateTime(saleEndDate).isAfter(now)){
                    isSaleOn = true;
                }

            }catch(Exception e){
                System.out.println("The system properties SALES_ENABLED_DATE_START or SALES_ENABLED_DATE_END are in the wrong format (please use iso format i.e. '2011-11-15T16:01:07.440Z') "+e);
            }

        }
        return isSaleOn;

    }
    public boolean isSubscriptionOn(RegionEnum region) {
        String isSubscriptionOn = "Select s.value from system_properties s\n" +
                "where id = 'SUBSCRIPTION_LINK_ENABLED'";
        List<Map> results = executeSelect(region, isSubscriptionOn);
        String result = results.get(0).get("value").toString();
        boolean isTrue = false;
        if (result.equalsIgnoreCase("T")){
            isTrue = true;
        }
        return isTrue;
    }
    public void makeReservation(RegionEnum region, int customerId, String sku, int reservedQuantity) {
        String reservationSql =
                "INSERT INTO simple_reservation" +
                        " (`customer_id`, `sku`, `reserved_quantity`, `redeemed_quantity`, `expiry_date`, `created_dts`, `created_by`, `last_updated_by`, `status`)" +
                        " VALUES (" + customerId + ", '" + sku + "', " + reservedQuantity + ", 0, now() + INTERVAL 1 DAY, now(), 'TEST', 'TEST', 'PENDING')";

        executeUpdate(region, reservationSql);
    }
    public void optUserInToNewWishlist(RegionEnum region, Integer customerID) {
        String reservationSql =
                "INSERT INTO customer_migrated_wishlist (`customer_id`) " +
                        "VALUES (" + customerID + ")";

        executeUpdate(region, reservationSql);
    }
    public void restrictProductAsHazmat(RegionEnum region, String pid, String countryCode) {
        String query = "INSERT INTO shipping_restriction(pid,restriction_code,location,location_type) values (" + pid + ",'HAZMAT','" + countryCode  + "','COUNTRY');";
        executeUpdate(region, query);
    }
    /*
        INSERT INTO attribute_value (`value`, `pa_id`, `search_prd_id`) VALUES('T', 'SALE', '300109')
     */
    public void setProductOnSale(RegionEnum region, String pid) {
        String qry =
                "INSERT INTO attribute_value (`value`, `pa_id`, `search_prd_id`) VALUES('T', 'SALE', '" + pid + "')";
        executeUpdate(region, qry);
    }
    public void updateLoginAttemptsForCustomer(RegionEnum region, int customerId, int duration) {
        executeUpdate(region, " UPDATE login_attempts set last_updated_dts =  TIMESTAMPADD(HOUR,-" + duration + ",now()) where customer_id = " + customerId);

    }
    public int updateStockLevel(RegionEnum region, String productSku, int stockLevel) {
        String updateStockQuery = "UPDATE stock_location SET no_in_stock = " + stockLevel + " WHERE sku = '" + productSku + "'";
        return executeUpdate(region, updateStockQuery);
    }

    public List<String> getSkusForPid(RegionEnum region, String pid) {
        String getSkusForPid = "SELECT sku FROM product where sku like '"+pid+"%'";
        List<Map> skuMapList = executeSelect(region, getSkusForPid);
        List<String> skuList = new ArrayList<String>();
        if (skuMapList.size()==0)
            return null;
        for (Map map : skuMapList) {
            skuList.add(map.values().iterator().next().toString());
        }
        return skuList;
    }
    public Map<String, Object> findPidWithAllSizesSoldOut(RegionEnum region) {
        String query = "SELECT sp_id FROM product_summary\n" +
                "WHERE av_slug_image = 'soldout'\n" +
                "AND event_id is null\n" +
                "AND visible = 'T'\n" +
                "ORDER BY RAND() LIMIT 1";
        List<Map> results = executeSelect(region, query);
        return results.get(0);
    }

    public String getCountryCurrency(RegionEnum region, String country){
        String sqlQuery = new StringBuilder().append("SELECT currency from country_lookup \n")
                .append("WHERE id = '")
                .append(country)
                .append("'").toString();
        List<Map> results = executeSelect(region, sqlQuery);
        return results.get(0).get("currency").toString();

    }
    public Double getRuleValue(RegionEnum region, String country, String ruleName){
        String sqlQuery = new StringBuilder().append("SELECT csp.value \n")
                .append("FROM country_saletax_and_duties_rule_param csp \n")
                .append("JOIN country_saletax_and_duties_rule cs \n")
                .append("ON csp.country_saletax_and_duties_rule_id = cs.id \n")
                .append("LEFT JOIN country_lookup cl \n")
                .append("ON cl.id = cs.country_id \n")
                .append("WHERE cl.display_name = '")
                .append(country)
                .append("' \n")
                .append("AND csp.name = '")
                .append(ruleName)
                .append("' \n").toString();
        List<Map> results = executeSelect(region, sqlQuery);
        if (results.size() == 0){
            return null;
        }
        return Double.parseDouble(results.get(0).get("value").toString());
    }

    public String getDutiesPercentage(RegionEnum region, String sku, String country) {
        String sqlQuery = new StringBuilder().append("SELECT cd.duty_percentage from product p \n ")
                .append("JOIN country_duties cd on p.hs_code=cd.hs_code \n")
                .append("JOIN country_lookup cl on cd.country=cl.id \n")
                .append("WHERE p.sku = '")
                .append(sku)
                .append("' and cl.display_name = '")
                .append(country)
                .append("'").toString();


        List<Map> results = executeSelect(region, sqlQuery);
        if (results.size() == 0){
            return "0.00";
        }
        return results.get(0).get("duty_percentage").toString();
    }

    public String getPriceApproxToggleStatus(RegionEnum region){
        String sqlQuery = new StringBuilder().append("SELECT value from system_properties \n")
                .append("WHERE id = 'ENABLE_PAYMENT_PATH_PRICE_APPROXIMATIONS'").toString();
        List<Map> results = executeSelect(region, sqlQuery);
        return results.get(0).get("value").toString();
    }

/*
 * The below method returns back 1 row of information in relation to the product price.
 *
 * @param country - String - Country code used to calculate the currency the product is priced in
 * @param pid     - String - Product ID
 * @return pid, country_id, original_price, current_price, discount
 */

    public Map<String, Object> getProductPrice(RegionEnum region, String country, Integer pid){

        String productPrice = new StringBuilder().append("SELECT * FROM (SELECT sp.id as pid, cl.id as countryId, \n")
                .append("TRUNCATE ( \n")
                .append("CASE \n")
                .append("WHEN cp1.offer_price IS NOT NULL THEN cp1.offer_price * cr1.rate \n")
                .append("WHEN cp2.offer_price IS NOT NULL THEN cp2.offer_price * cr2.rate \n")
                .append("WHEN @is_channel_intl=0 THEN cp3.offer_price * cr3.rate \n")
                .append("WHEN ( ctrc.rule_class  is NOT NULL) and ctrc.rule_class = 'com.netaporter.domain.pricing.taxrulecalculator.ThresholdTaxRuleCalculator' \n")
                .append("AND ((cp3.offer_price * cr3.rate  )<=(ctrc.threshold_value * cr3.rate)) \n")
                .append("THEN  cp3.offer_price * cr3.rate \n")
                .append("WHEN  ( ctrc.rule_class  is NOT NULL) and ctrc.rule_class = 'com.netaporter.domain.pricing.taxrulecalculator.FixedDutiesTaxRuleCalculator' \n")
                .append("THEN \n")
                .append("cp3.offer_price * cr3.rate * \n")
                .append("(1+(coalesce(cst.tax_rate, cl.vat_percentage)/100)) \n")
                .append("WHEN ( ctrc.rule_class  is NOT NULL) and  ctrc.rule_class = 'com.netaporter.domain.pricing.taxrulecalculator.PercentageDutiesTaxRuleCalculator' \n")
                .append("THEN \n")
                .append("cp3.offer_price * cr3.rate * \n")
                .append("(1+(coalesce(cd.duty_percentage * ctrc.duties_percentage, 0)/10000)) * \n")
                .append("(1+(coalesce(cst.tax_rate, cl.vat_percentage)/100)) \n")
                .append("ELSE cp3.offer_price * cr3.rate * \n")
                .append("(1+(coalesce(cd.duty_percentage, 0)/100)) * \n")
                .append("(1+(coalesce(cst.tax_rate, cl.vat_percentage)/100)) \n")
                .append("END , 2 ) as ORIGINAL_PRICE , \n")
                .append("TRUNCATE ( \n")
                .append("CASE \n")
                .append("WHEN cp1.offer_price IS NOT NULL THEN cp1.offer_price * cr1.rate *  (1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100)) \n")
                .append("WHEN cp2.offer_price IS NOT NULL THEN cp2.offer_price * cr2.rate *  (1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100)) \n")
                .append("WHEN @is_channel_intl=0 THEN cp3.offer_price * cr3.rate *  (1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100)) \n")
                .append("WHEN ( ctrc.rule_class  is NOT NULL) and ctrc.rule_class = 'com.netaporter.domain.pricing.taxrulecalculator.ThresholdTaxRuleCalculator' \n")
                .append("AND ((cp3.offer_price * cr3.rate *  (1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100))  )<=(ctrc.threshold_value * cr3.rate)) \n")
                .append("THEN  cp3.offer_price * cr3.rate *  (1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100)) \n")
                .append("WHEN  ( ctrc.rule_class  is NOT NULL) and ctrc.rule_class = 'com.netaporter.domain.pricing.taxrulecalculator.FixedDutiesTaxRuleCalculator' \n")
                .append("THEN \n")
                .append("(cp3.offer_price * cr3.rate * \n")
                .append("(1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100)) + (ctrc.fixed_duties_value * cr3.rate) ) * \n")
                .append("(1+(coalesce(cst.tax_rate, cl.vat_percentage)/100)) \n")
                .append("WHEN ( ctrc.rule_class  is NOT NULL) and  ctrc.rule_class = 'com.netaporter.domain.pricing.taxrulecalculator.PercentageDutiesTaxRuleCalculator' \n")
                .append("THEN \n")
                .append("cp3.offer_price * cr3.rate * \n")
                .append("(1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100)) * \n")
                .append("(1+(coalesce(cd.duty_percentage * ctrc.duties_percentage, 0)/10000)) * \n")
                .append("(1+(coalesce(cst.tax_rate, cl.vat_percentage)/100)) \n")
                .append("ELSE cp3.offer_price * cr3.rate * \n")
                .append("(1-(coalesce(ABS(coalesce(padj.percentage,0)), 0)/100)) * \n")
                .append("(1+(coalesce(cd.duty_percentage, 0)/100)) * \n")
                .append("(1+(coalesce(cst.tax_rate, cl.vat_percentage)/100)) \n")
                .append("END , 2 ) as CURRENT_PRICE , \n")
                .append("TRUNCATE ( coalesce(abs(padj.percentage), 0 ) , 0 ) as DISCOUNT \n")
                .append("FROM  product_summary ps \n")
                .append("INNER JOIN searchable_product sp ON ps.sp_id = sp.id \n")
                .append("INNER JOIN product p ON p.search_prod_id = ps.sp_id \n")
                .append("LEFT JOIN  product_summary_current_default_price pscdp on pscdp.pid = sp.id \n")
                .append("INNER JOIN country_lookup cl ON cl.id IN ('")
                .append(country)
                .append("') \n")
                .append("LEFT JOIN country_tax_rule_configuration ctrc ON  ctrc.country_id = cl.id \n")
                .append("inner join channel_pricing cp3 on cp3.id = 1 and cp3.sku = p.sku and cp3.locality='default' \n")
                .append("left join full_season_rate cr3 on cr3.season = sp.season and cr3.destination_code = cl.currency and cr3.source_code = cp3.currency \n")
                .append("left join channel_pricing cp2 on cp2.id = 1 and cp2.sku = p.sku and cp2.locality_type='TERRITORY' and cp2.locality = cl.region \n")
                .append("left join full_season_rate cr2 on cr2.season = sp.season and cr2.destination_code = cl.currency and cr2.source_code = cp2.currency \n")
                .append("left join channel_pricing cp1 on cp1.id = 1 and cp1.sku = p.sku and cp1.locality_type='country' and cp1.locality = cl.id \n")
                .append("left join full_season_rate cr1 on cr1.season = sp.season and cr1.destination_code =cl.currency and cr1.source_code = cp1.currency \n")
                .append("LEFT JOIN country_duties cd ON p.hs_code=cd.hs_code AND cd.country=cl.id \n")
                .append("LEFT JOIN price_adjustment padj ON p.sku=padj.sku AND ( now() BETWEEN padj.start_date AND padj.end_date) \n")
                .append("LEFT JOIN country_sales_tax cst ON cst.country=cl.id AND cst.product_type=ps.av_product_type \n")
                .append("WHERE  ps.sp_id IN ( ")
                .append(pid)
                .append(" ) \n")
                .append("GROUP BY ps.sp_id, cl.id) x")
                .toString();

        List<Map> result = executeSelect(region, productPrice);
        return result.get(0);
    }
    public void updateWishListAlert(RegionEnum region, Integer customerID, String alertType, String sku) {
        String sqlQuery = new StringBuilder().append("INSERT INTO `alert` (`viewed`, `expiry`, `updated`, `customer_id`, `type`, `product_alert_type`, `sku`, `discount_percentage`, `created_dts`)\n")
                .append("VALUES (0, DATE_ADD(now(), INTERVAL 1 DAY), now(), ")
                .append(customerID)
                .append(", 'product', '")
                .append(alertType)
                .append("', '")
                .append(sku)
                .append("', NULL, now())").toString();
        executeUpdate(region, sqlQuery);
    }

    public String getDispatchedOrderItemStatus(RegionEnum region, String sku) {
        String sqlQuery = "SELECT status FROM dispatched_order_item WHERE sku = '"+sku+"';";
        List<Map> dispatchedOrderItemStatus = executeSelect(region, sqlQuery);
        return dispatchedOrderItemStatus.get(0).values().iterator().next().toString();
    }

    public void setSkUReturnability(RegionEnum regionEnum, String sku, int returnability) {
        String sqlQuery = "UPDATE order_item SET returnable = '"+returnability+"' WHERE sku = '"+sku+"';";
        executeUpdate(regionEnum, sqlQuery);
    }
}
