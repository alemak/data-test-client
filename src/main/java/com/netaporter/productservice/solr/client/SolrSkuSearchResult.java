package com.netaporter.productservice.solr.client;

import com.google.common.collect.Lists;
import com.netaporter.productservice.solr.client.pojos.SolrProductServiceResults;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 19/09/12
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class SolrSkuSearchResult {
    private List<SolrProductServiceResults.Response.Doc> results;
    private String sku;

    public SolrSkuSearchResult(SolrProductServiceResults.Response.Doc result, String sku) {
        this.results = Lists.newArrayList(result);
        this.sku = sku;
    }

    public SolrSkuSearchResult(List<SolrProductServiceResults.Response.Doc> results, String sku) {
        this.results = results;
        this.sku = sku;
    }

    public List<SolrProductServiceResults.Response.Doc> getResults() {
        return results;
    }

    public void setResults(List<SolrProductServiceResults.Response.Doc> result) {
        this.results = result;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
