package com.netaporter.productservice.api;

import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 03/07/2013
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */

public interface RESTClient {
      String getResponse(APIRequest request);
}
