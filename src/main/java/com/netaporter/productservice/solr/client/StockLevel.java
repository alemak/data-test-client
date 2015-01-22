package com.netaporter.productservice.solr.client;

import com.netaporter.productservice.util.ChannelConverter;
import com.netaporter.test.utils.dataaccess.database.LegacyWebAppProductDatabaseClient;
import com.netaporter.test.utils.enums.SalesChannelEnum;


/**
 * Date: 23/04/2013
 * Time: 12:47
 */
public class StockLevel {
    
    private Integer webappDbStock;
    private Integer productServiceStock;
    private LegacyWebAppProductDatabaseClient dbUtils;
    private String sku;
    private SalesChannelEnum channel;

    public StockLevel(LegacyWebAppProductDatabaseClient dbUtils, String sku, SalesChannelEnum channel, Integer productServiceStock) {
        this.dbUtils = dbUtils;
        this.productServiceStock = productServiceStock;
        this.sku = sku;
        this.channel = channel;
    }

    public Integer getProductServiceStockLevel() {
        return productServiceStock;
    }

    /**
     * We only run tests against products where the product service stock level matches the database stock level
     */
    public boolean dbAndPsAgree() {
        return getWebappDbStock() != null && getWebappDbStock().equals(productServiceStock);
    }

    private Integer getWebappDbStock() {
        // Lazily fetch this on demand
        if (webappDbStock == null ){
            webappDbStock = dbUtils.getStockLevelForProductSku(ChannelConverter.convertProductChannel(channel), sku);
        }

        return webappDbStock;
    }

}
