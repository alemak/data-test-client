package com.netaporter.productservice.solr.client.crosschannel;


import com.netaporter.productservice.solr.client.pojos.SolrProductServiceResults;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 19/09/12
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class SolrSkuChannelFilter {

    public boolean queryForProduct(String sku, Integer channelCount) {
        return true;
    }

    public abstract List<SolrProductServiceResults.Response.Doc> filter(String sku, SolrProductServiceResults results);
}
