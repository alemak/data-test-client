package com.netaporter.productservice.api;
import com.netaporter.productservice.api.APIClientUtils.APIRequests.APIRequest;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: a.makarenko@london.net-a-porter.com
 * Date: 02/07/2013
 * Time: 12:32
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ApiClient implements RESTClient {
    @Value("${lad.api.base.url}")
    private String baseUri;

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        URI uri;
        WebTarget service;

    public void init(){
        uri = URI.create(baseUri);
    }
    public String getBaseUri() {
        return baseUri;
    }
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getResponse(APIRequest r){
        service = client.target(uri);
        for(String pathentry: r.getPath()){
            service = service.path(pathentry);
        }
        List<String> keyset = new ArrayList<String>(r.getQuery().keySet());
        Collections.sort(keyset);
        for(String key: keyset){
            service = service.queryParam(key, r.getQuery().get(key));
        }
       System.out.println("->->->" + service.getUri().toString());
       return service.request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
    }


}
