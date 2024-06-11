package com.navigators.demo.adapter.api.tmap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

public class TMapAPI {

    private final String API_URL = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1";
    private final String APP_KEY = "itsofqhALtw74omtS4EQ1eLvWgXfkR64p4wOzo0e";

    /* In ms, 5000 = 5 sec */
    private final Integer MAX_CONNECTION_TIMEOUT = 10000;
    private final Integer MAX_READ_TIMEOUT = 10000;
    private final Integer MAX_CONNECTION = 50;
    private final Integer MAX_CONNECTION_PER_ROUTE = 20;

    /* private method for HTTP */
    private Map<String, Object> sendHttpRequestAndGetResponse(JSONObject requestBody) throws Exception {
        /* time out configuration */
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(MAX_CONNECTION_TIMEOUT);
        factory.setReadTimeout(MAX_READ_TIMEOUT);

        /* socket usage limitation */
        /* apache HTTP client build */
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(MAX_CONNECTION)
                .setMaxConnPerRoute(MAX_CONNECTION_PER_ROUTE)
                .build();

        /* HTTP client configuration */
        factory.setHttpClient(httpClient);

        /* make rest template object */
        RestTemplate restTemplate = new RestTemplate(factory);

        /* make HTTP header */
        HttpHeaders headers = new HttpHeaders();

        /* header config */
        headers.add("Content-Type", "application/json");
        headers.add("appKey", APP_KEY);

        /* HTTP request */
        ResponseEntity<Map> response;

            /* craft HTTP entity */
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            /* craft HTTP request */
            String serverDomain = API_URL;
            UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl(serverDomain)
                    .build(false);
            /* response */
            response = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Map.class);

        /* extract responseBody */
        Map<String, Object> responseBody = response.getBody();
        return responseBody;
    }


    /** Note. (x,y) = (lon, lat) */
    public Map<String, Object> getRoute(Double startLat, Double startLon, Double endLat, Double endLon) {
        Map<String, Object> resultMap = new HashMap<>();

        /* setup JSON body */
        JSONObject requestBody = new JSONObject();
        requestBody.put("startX", startLon);
        requestBody.put("startY", startLat);
        requestBody.put("endX", endLon);
        requestBody.put("endY", endLat);
        requestBody.put("startName", "KAIST");
        requestBody.put("endName", "KAIST");

        /* send api call */
        try {
            Map<String, Object> apiResult = this.sendHttpRequestAndGetResponse(requestBody);
            resultMap.put("result", "success");
            resultMap.put("item", apiResult);
            return resultMap;
        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("reason", e.getMessage());
            return resultMap;
        }
    }

}
