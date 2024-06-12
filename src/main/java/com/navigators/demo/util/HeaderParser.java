package com.navigators.demo.util;

import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;


@NoArgsConstructor
public class HeaderParser {

    public String getRoleNameByAccessToken(String accessTokenString) {
        if (accessTokenString == null) {
            return "None";
        }

        /* get role from the header string */
        String roleName = "None";

        /* slice JWT token */
        String[] chunks = accessTokenString.split("\\.");
        /* decode the payload part */
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        /* String to Json object */
        JSONObject jsonObject = new JSONObject(payload);
        /* get preferred_username */
        roleName = (String) ((JSONArray) ((JSONObject) ((JSONObject) jsonObject.get("resource_access")).get("APP")).get("roles")).get(0);

        return roleName;
    }

    public String getUserIdByAccessToken(String accessTokenString) {
        if (accessTokenString == null) {
            return "None";
        }

        String userId = "None";

        /* get userId from the header string */

        /* slice JWT token */
        String[] chunks = accessTokenString.split("\\.");
        /* decode the payload part */
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        /* String to Json object */
        JSONObject jsonObject = new JSONObject(payload);
        /* get preferred_username */
        userId = jsonObject.getString("preferred_username");

        return userId;
    }

}
