package com.netaporter.productservice.util;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by a.makarenko on 6/25/14.
 */
public class PidFromSkuExtractor {
    public static String getPidForSku(String skuId){
         return skuId.split("-")[0];
    }
}
