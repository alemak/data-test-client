package com.netaporter.productservice.solr.client.pojos;

import com.netaporter.test.utils.assertion.objectcomparison.BaseClassForAssertingFields;
import com.netaporter.test.utils.enums.SalesChannelEnum;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: c.dawson@london.net-a-porter.com
 * Date: 11/09/2012
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
public class SolrProductServiceResults extends BaseClassForAssertingFields {

    private ResponseHeader responseHeader;
    private Response response;
    private Facet_Counts facet_counts;


    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Facet_Counts getFacet_counts() {
        return facet_counts;
    }

    public void setFacet_counts(Facet_Counts facet_counts) {
        this.facet_counts = facet_counts;
    }



    public static class ResponseHeader extends BaseClassForAssertingFields {

        private Number status;
        private Number QTime;


        public Number getStatus() {
            return status;
        }

        public void setStatus(Number status) {
            this.status = status;
        }

        public Number getQTime() {
            return QTime;
        }

        public void setQTime(Number QTime) {
            this.QTime = QTime;
        }



        public static class Params{


        }

    }




    public static class Response {

        private Number numFound;
        private Number start;
        private List<Doc> docs;


        public Number getNumFound() {
            return numFound;
        }

        public void setNumFound(Number numFound) {
            this.numFound = numFound;
        }

        public Number getStart() {
            return start;
        }

        public void setStart(Number start) {
            this.start = start;
        }

        public List<Doc> getDocs() {
            return docs;
        }

        public void setDocs(List<Doc> docs) {
            this.docs = docs;
        }

        public boolean containsChannel(SalesChannelEnum channel) {
            return getDocForChannel(channel) != null;
        }

        public Doc getDocForChannel(SalesChannelEnum channel) {
            for(Doc doc: docs) {
                if(doc.channel_id == channel.getId()) {
                    return doc;
                }
            }

            return null;
        }


        public class Doc {

            private Number product_id;
            private String channel_name;
            private Integer channel_id;
            private Boolean visible;
            private List<String> sku_ids;
            private Integer stock_level_saleable;
            private List<Number> sku_stock_levels_saleable;

            public Integer getStock_level_saleable() {
                return stock_level_saleable;
            }

            public void setStock_level_saleable(Integer stock_level_saleable) {
                this.stock_level_saleable = stock_level_saleable;
            }

            public Number getProduct_id() {
                return product_id;
            }

            public void setProduct_id(Number product_id) {
                this.product_id = product_id;
            }

            public String getChannel_name() {
                return channel_name;
            }

            public void setChannel_name(String channel_name) {
                this.channel_name = channel_name;
            }

            public List<String> getSku_ids() {
                return sku_ids;
            }

            public void setSku_ids(List<String> sku_ids) {
                this.sku_ids = sku_ids;
            }

            public List<Number> getSku_stock_levels_saleable() {
                return sku_stock_levels_saleable;
            }

            public void setSku_stock_levels_saleable(List<Number> sku_stock_levels_saleable) {
                this.sku_stock_levels_saleable = sku_stock_levels_saleable;
            }

            public Integer getChannel_id() {
                return channel_id;
            }

            public void setChannel_id(Integer channel_id) {
                this.channel_id = channel_id;
            }

            public boolean skuExists(String sku) {
                Integer index = sku_ids.indexOf(sku);
                return index > -1;
            }

            public boolean isLowStock(String sku, SalesChannelEnum channel) {
                Integer index = sku_ids.indexOf(sku);
                if(index < 0 || index >= sku_stock_levels_saleable.size()) {
                    return false;
                }

                Integer solrStock = sku_stock_levels_saleable.get(index).intValue();
                //now check db to ensure stock levels match
//                if(solrStock > 0 && solrStock < 3){
//                    Region region = convertProductChannel(channel);
//                    Integer dbStock = getDbHelper().getStockLevelForProductSku(region, sku);
//                    if(!solrStock.equals(dbStock)){
//                        return false;
//                    }
//                }

                return solrStock > 0 && solrStock < 3;
            }

            public int getSolrStockLevel(String sku) {
                Integer index = sku_ids.indexOf(sku);
                if(index < 0 || index >= sku_stock_levels_saleable.size()) {
                    return Integer.MIN_VALUE;
                }

                return sku_stock_levels_saleable.get(index).intValue();
            }

            public  boolean isPidInStock(String pid, SalesChannelEnum channel){
                return stock_level_saleable > 0;
            }

            public boolean isInStock(String sku, SalesChannelEnum channel) {
                Integer index = sku_ids.indexOf(sku);
                if(index < 0 || index >= sku_stock_levels_saleable.size()) {
                    return false;
                }

                //now check db to ensure stock levels match
//                if(sku_stock_levels_saleable.get(index).intValue() > 0){
//                    Region region = convertProductChannel(channel);
//                    Integer solrStock = sku_stock_levels_saleable.get(index).intValue();
//                    Integer dbStock = getDbHelper().getStockLevelForProductSku(region, sku);
//                    if(!solrStock.equals(dbStock)){
//                        return false;
//                    }
//                }

                return sku_stock_levels_saleable.get(index).intValue() > 0;
            }

            public Boolean isVisible() {
                return visible;
            }

            public void setVisible(Boolean visible) {
                this.visible = visible;
            }
        }

    }




    public static class Facet_Counts {

        private Facet_Queries facet_queries;
        private Facet_Fields facet_fields;
        private Facet_Dates facet_dates;
        private Facet_Ranges facet_ranges;


        public Facet_Queries getFacet_queries() {
            return facet_queries;
        }

        public void setFacet_queries(Facet_Queries facet_queries) {
            this.facet_queries = facet_queries;
        }

        public Facet_Fields getFacet_fields() {
            return facet_fields;
        }

        public void setFacet_fields(Facet_Fields facet_fields) {
            this.facet_fields = facet_fields;
        }

        public Facet_Dates getFacet_dates() {
            return facet_dates;
        }

        public void setFacet_dates(Facet_Dates facet_dates) {
            this.facet_dates = facet_dates;
        }

        public Facet_Ranges getFacet_ranges() {
            return facet_ranges;
        }

        public void setFacet_ranges(Facet_Ranges facet_ranges) {
            this.facet_ranges = facet_ranges;
        }

        public static class Facet_Queries {

        }


        public static class Facet_Fields {

            private List<Object> sku_ids;

            public List<Object> getSku_ids() {
                return sku_ids;
            }

            public void setSku_ids(List<Object> sku_ids) {
                this.sku_ids = sku_ids;
            }
        }


        public static class Facet_Dates {

        }

        public static class Facet_Ranges {

        }

    }


}
