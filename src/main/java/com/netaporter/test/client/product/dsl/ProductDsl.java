package com.netaporter.test.client.product.dsl;

import com.netaporter.productservice.api.APIClientUtils.StockAvailability;
import com.netaporter.test.utils.enums.SalesChannelEnum;

import static com.netaporter.test.utils.enums.SalesChannelEnum.*;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * Date: 23/04/2013
 * Time: 12:05
 */
public class ProductDsl {

    public enum ProductCategory {
        CLOTHING(2),
        LINGERIE(5490),
        BAGS(3),
        SHOES(4),
        ACCESSORIES(5),
        BEAUTY(10103);
        private int categoryId;
         ProductCategory(int categoryId) {
             this.categoryId = categoryId;
        }
        public Integer getCategoryId(){
            return this.categoryId;
        }

        public String toSolrString() {
            return capitalize(lowerCase(this.toString()));
        }

    }

    public enum ProductAvailability {
        NOT_UPLOADED,
        LOW_STOCK,
        SOLD_OUT,
        IN_STOCK,
        ON_SALE,
        ONLY_ONE_LEFT;

        public String toAPIString(){
            switch(this){
                case NOT_UPLOADED:
                    return StockAvailability.NEVER_IN_STOCK.toString();
                case LOW_STOCK:
                    return StockAvailability.LOW_STOCK.toString();
                case  SOLD_OUT:
                    return StockAvailability.OUT_OF_STOCK.toString();
                case  IN_STOCK:
                    return StockAvailability.IN_STOCK.toString();
                case  ON_SALE:
                    return StockAvailability.IN_STOCK.toString();
                default:
                   return null;
            }
        }

    }

    public enum Visibility {
        VISIBLE,
        INVISIBLE
    }

    public static class ProductChannelAvailability {
        private SalesChannelEnum channel;
        private ProductAvailability availability;
        private Visibility visibility;

        public ProductChannelAvailability(SalesChannelEnum channel, ProductAvailability availability) {
            this(channel, availability, Visibility.VISIBLE);
        }

        public ProductChannelAvailability(SalesChannelEnum channel, ProductAvailability availability, Visibility visibility) {
            this.channel = channel;
            this.availability = availability;
            this.visibility = visibility;
        }

        public SalesChannelEnum getChannel() {
            return channel;
        }

        public ProductAvailability getAvailability() {
            return availability;
        }

        public Visibility getVisibility() {
            //If we request something not uploaded, only invisible makes sense.
            if(availability == ProductAvailability.NOT_UPLOADED) {
                return Visibility.INVISIBLE;
            }

            return visibility;
        }
    }

    public static ProductChannelAvailability intl(ProductAvailability availability, Visibility visibility) {
        return new ProductChannelAvailability(NAP_INTL, availability, visibility);
    }

    public static ProductChannelAvailability intl(ProductAvailability availability) {
        return new ProductChannelAvailability(NAP_INTL, availability);
    }

    public static ProductChannelAvailability am(ProductAvailability availability, Visibility visibility) {
        return new ProductChannelAvailability(NAP_AM, availability, visibility);
    }

    public static ProductChannelAvailability am(ProductAvailability availability) {
        return new ProductChannelAvailability(NAP_AM, availability);
    }

    public static ProductChannelAvailability apac(ProductAvailability availability, Visibility visibility) {
        return new ProductChannelAvailability(NAP_APAC, availability, visibility);
    }

    public static ProductChannelAvailability apac(ProductAvailability availability) {
        return new ProductChannelAvailability(NAP_APAC, availability);
    }
}
