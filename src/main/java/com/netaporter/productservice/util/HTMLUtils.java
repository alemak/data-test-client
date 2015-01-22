package com.netaporter.productservice.util;

import org.jsoup.Jsoup;

/**
 * Created by a.makarenko on 7/3/14.
 */
public class HTMLUtils {
    private HTMLUtils() {}

    public static String extractText(String htmlText)  {
        String line;
        String textOnly = Jsoup.parse(htmlText).text();
        return textOnly;
    }

}
