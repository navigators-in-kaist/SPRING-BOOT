package com.navigators.demo.adapter.api.keycloak;

import lombok.extern.log4j.Log4j2;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class KeycloakAPI {

    /** role names */
    private static final String PROVEN_USER = "proven_user";
    private static final String UNPROVEN_USER = "unproven_user";

    private final String apiBaseUrl = System.getenv("KEYCLOAK_URL") + ":" + System.getenv("KEYCLOAK_PORT");
    private String appClientUuid = "none";
    private final String adminId = System.getenv("KEYCLOAK_ADMIN_ID");
    private final String adminPw = System.getenv("KEYCLOAK_ADMIN_PW");
    private String accessToken = "none";

    private String getUuidOfAppRole(String roleName) throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/clients/" + appClientUuid + "/roles/" + roleName,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return (String) response.getBody().get("id");
            } else {
                throw new Exception("The GET response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void updateClientUuids() throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        /* for APP */
        try {
            ResponseEntity<String> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/clients?clientId=" + "APP",
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                JSONArray responseList = new JSONArray(response.getBody());
                JSONObject responseMap = (JSONObject) responseList.get(0);
                this.appClientUuid = (String) responseMap.get("id");
            } else {
                throw new Exception("The GET response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.info("failed to update client uuid.");
            throw e;
        }

        log.info("successfully update client uuid!");
    }

    /** login */
    public Map<String, String> loginUser(String userId, String password) throws Exception {
        log.info("login start...");
        Map<String, String> resultMap;

        RestTemplate rt = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        params.add("client_id", "APP");
        params.add("username", userId);
        params.add("password", password);
        params.add("grant_type", "password");
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map> response = rt.exchange(
                    apiBaseUrl + "/realms/NAV/protocol/openid-connect/token",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                resultMap = (HashMap) response.getBody();
            } else {
                throw new Exception("The POST response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw e;
        }

        return resultMap;
    }


    public String getAccessToken() {
        return this.accessToken;
    }


    /** init method */
    public void refreshAccessToken() throws Exception {
        log.info("keycloak init...");

        RestTemplate rt = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        params.add("client_id", "admin-cli");
        params.add("username", this.adminId);
        params.add("password", this.adminPw);
        params.add("grant_type", "password");
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map> response = rt.exchange(
                    apiBaseUrl + "/realms/master/protocol/openid-connect/token",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                this.accessToken = (String) response.getBody().get("access_token");
            } else {
                throw new Exception("The POST response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.info("failed to get admin-cli token");
            throw e;
        }

        log.info("successfully get admin-cli token!");
        /* get client uuid */
        this.updateClientUuids();
    }

    public void addUser(String userName, String password) throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String requestBody = "{\"username\": \""
                + userName
                + "\", \"enabled\": true, \"credentials\": [{\"type\": \"password\", \"value\": \""
                + password
                + "\", \"temporary\": false}]}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + this.accessToken);
        try {
            ResponseEntity<Map> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/users",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
                throw new Exception("The POST response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private String getCurrentClientRole(String userUuid) throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + this.accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/users/" + userUuid + "/role-mappings/clients/" + this.appClientUuid,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new Exception("The GET response status code is " + response.getStatusCode());
            } else {
                return response.getBody();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String getUserUuid(String userName) throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.accessToken);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<List> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/users?username=" + userName,
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return (String) ((Map<String, Object>) response.getBody().get(0)).get("id");
            } else {
                throw new Exception("The GET response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteClientRoleMapping(String userUuid) throws Exception {
        String prevRoles = getCurrentClientRole(userUuid);
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + this.accessToken);
        HttpEntity<String> entity = new HttpEntity<>(prevRoles, headers);
        try {
            ResponseEntity<Map> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/users/" + userUuid + "/role-mappings/clients/" + this.appClientUuid,
                    HttpMethod.DELETE,
                    entity,
                    Map.class
            );
            if (!response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                throw new Exception("The DELETE response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void addAppClientRoleMappingForProven(String userUuid) throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String appUserRoleObj = "[{"
                + "\"id\" :"
                + "\"" + this.getUuidOfAppRole(PROVEN_USER) + "\""
                + ",\"name\" :"
                + "\"" + PROVEN_USER + "\""
                + "}]";
        String requestBody = appUserRoleObj;
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + this.accessToken);
        try {
            ResponseEntity<Map> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/users/" + userUuid + "/role-mappings/clients/" + appClientUuid,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            if (!response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                throw new Exception("The POST response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void changePassword(String adminUuid, String newPassword) throws Exception {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String requestBody = "{"
                + "\"credentials\": [{\"type\": \"password\", \"value\": \""
                + newPassword
                + "\", \"temporary\": false}]"
                + "}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + this.accessToken);
        try {
            ResponseEntity<Map> response = rt.exchange(
                    apiBaseUrl + "/admin/realms/NAV/users/" + adminUuid,
                    HttpMethod.PUT,
                    entity,
                    Map.class
            );
            if (!response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                throw new Exception("The PUT response status code is " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
