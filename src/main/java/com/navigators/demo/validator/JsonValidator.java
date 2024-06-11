package com.navigators.demo.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.navigators.demo.codes.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    /* Import schema .json file from resource directory */
    private String getJsonSchemaFromFile(String path) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    /* Convert the given object into JsonNode object. */
    private JsonNode convertObjToJsonNode(Object object) throws IllegalArgumentException {
        return mapper.valueToTree(object);
    }

    /* Convert the given string into JsonNode object. */
    private JsonNode convertStringToJsonNode(String string) throws JsonProcessingException {
        return mapper.readTree(string);
    }

    /* validator end point */
    public Map<String, String> validate(String path, Object targetObj) {
        /* initialize the result map */
        Map<String, String> resultMap = new HashMap<>();

        try {
            /* JsonNode for the json schema */
            String baseUri = "static/jsonSchema/";
            JsonNode schemaNode = convertStringToJsonNode(getJsonSchemaFromFile(baseUri + path));

            /* schema validator initialization */
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

            /* Schema object */
            JsonSchema schema = factory.getJsonSchema(schemaNode);

            /* convert the validation target object into JsonNode object */
            JsonNode targetJson = convertObjToJsonNode(targetObj);

            /* the validation result object */
            ProcessingReport report = schema.validate(targetJson);

            /* generate the resultMap */
            if (report.isSuccess()) {
                /* validation success */
                resultMap.put("status", "success");
                return resultMap;
            } else {
                /* validation fail */
                resultMap.put("status", "fail");
                resultMap.put("reason", report.toString());
                return resultMap;
            }
        } catch (JsonProcessingException e) {
            resultMap.put("status", "error");
            resultMap.put("reason", "The given json schema is invalid, failed to parse the json schema.");
            resultMap.put("errorCode", "1");
            resultMap.put("message", e.getMessage());
            return resultMap;
        } catch (IOException e) {
            resultMap.put("status", "error");
            resultMap.put("reason", "IO exception occurs while reading the schema file.");
            resultMap.put("errorCode", "2");
            resultMap.put("message", e.getMessage());
            return resultMap;
        } catch (ProcessingException e) {
            resultMap.put("status", "error");
            resultMap.put("reason", "Error occurs while validating process. " + e.getMessage());
            resultMap.put("errorCode", "3");
            resultMap.put("message", e.getMessage());
            return resultMap;
        } catch (IllegalArgumentException e) {
            resultMap.put("status", "error");
            resultMap.put("reason", "The given target object is invalid, failed to convert the object into JsonNode.");
            resultMap.put("errorCode", "4");
            resultMap.put("message", e.getMessage());
            return resultMap;
        } catch (Exception e) {
            resultMap.put("status", "error");
            resultMap.put("reason", "unspecified reason");
            resultMap.put("errorCode", "5");
            resultMap.put("message", e.getMessage());
            return resultMap;
        }
    }

    public ResponseEntity<Map<String, Object>> formatResponseBody(Map<String, String> validateResultMap) {
        Map<String, Object> responseBody = new HashMap<>();
        if (validateResultMap.get("status").equals("fail")) {
            responseBody.put("error_code", ErrorCode.JSON_VALIDATE_FAIL);
            responseBody.put("reason", validateResultMap.get("reason"));
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        } else {
            if (validateResultMap.get("errorCode").equals("0")) {
                responseBody.put("error_code", ErrorCode.SERVER_FILE_URI_ERROR);
                responseBody.put("reason", validateResultMap.get("reason"));
                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (validateResultMap.get("errorCode").equals("1")) {
                responseBody.put("error_code", ErrorCode.SERVER_FILE_INVALID_FORMAT);
                responseBody.put("reason", validateResultMap.get("reason"));
                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (validateResultMap.get("errorCode").equals("2")) {
                responseBody.put("error_code", ErrorCode.SERVER_FILE_READ_ERROR);
                responseBody.put("reason", validateResultMap.get("reason") + " Because : " + validateResultMap.get("message"));
                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (validateResultMap.get("errorCode").equals("3")) {
                responseBody.put("error_code", ErrorCode.SERVER_JSON_VALIDATE_FAIL);
                responseBody.put("reason", validateResultMap.get("reason"));
                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (validateResultMap.get("errorCode").equals("4")) {
                responseBody.put("error_code", ErrorCode.PUT_INVALID_PARAM);
                responseBody.put("reason", validateResultMap.get("reason"));
                return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
            } else {
                responseBody.put("error_code", ErrorCode.UNKNOWN_ERROR);
                responseBody.put("reason", validateResultMap.get("message"));
                return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
