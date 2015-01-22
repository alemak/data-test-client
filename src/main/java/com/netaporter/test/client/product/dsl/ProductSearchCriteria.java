package com.netaporter.test.client.product.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 28/09/12
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class ProductSearchCriteria {

    private List<String> ignoreSkus = new ArrayList();
    private ProductDsl.ProductCategory category;
    private List<ProductDsl.ProductChannelAvailability> channelAvails = new ArrayList();

    private Boolean ensureDbAndPsStockAgree = true;

    private Integer maxResults = Integer.MAX_VALUE;

    public static ProductSearchCriteria availabilities(ProductDsl.ProductChannelAvailability... avails) {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        criteria.channelAvails.addAll(Arrays.asList(avails));
        return criteria;
    }

    public ProductSearchCriteria inCategory(ProductDsl.ProductCategory category) {
        this.category = category;
        return this;
    }

    public ProductSearchCriteria ensureDbAndPsStockAgree(Boolean b) {
        this.ensureDbAndPsStockAgree = b;
        return this;
    }

    public ProductSearchCriteria ignoreSkus(Collection<String> skus) {
        this.ignoreSkus.addAll(skus);
        return this;
    }

    public ProductSearchCriteria ignoreSkus(String... skus) {
        this.ignoreSkus.addAll(Arrays.asList(skus));
        return this;
    }

    public ProductSearchCriteria maxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public List<String> getIgnoreSkus() {
        return ignoreSkus;
    }

    public ProductDsl.ProductCategory getCategory() {
        return category;
    }

    public List<ProductDsl.ProductChannelAvailability> getChannelAvails() {
        return channelAvails;
    }

    public Boolean getEnsureDbAndPsStockAgree() {
        return ensureDbAndPsStockAgree;
    }

    public int getMaxResults() { return maxResults; }
}
