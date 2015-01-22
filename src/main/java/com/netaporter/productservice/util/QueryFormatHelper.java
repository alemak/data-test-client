package com.netaporter.productservice.util;

import java.util.Collections;
import java.util.List;

/**
 * Created by a.makarenko on 6/24/14.
 */
public class QueryFormatHelper {
    public static String listToCommaValues(List<String> list){
        StringBuilder commaSepValueBuilder = new StringBuilder();
        for(int i = 0; i < list.size(); i++){
            commaSepValueBuilder.append(list.get(i));
            if(i!= list.size()-1){
                commaSepValueBuilder.append("%2C");
            }
        }
        return commaSepValueBuilder.toString();
    }
}
