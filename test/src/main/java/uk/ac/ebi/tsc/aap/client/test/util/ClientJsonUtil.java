package uk.ac.ebi.tsc.aap.client.test.util;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientJsonUtil {

    /**
     * Convert the data to a json-format string.
     * 
     * @param data Data to convert.
     * @return json representation.
     */
    public static String toJson(final Map<String, Object> data) {
        if (data == null) {
            throw new IllegalArgumentException("Please provide some data to convert to json!");
        }

        final JSONObject json = new JSONObject();
        data.entrySet().stream().forEach(entrySet -> {
            try {
                json.put(entrySet.getKey(), entrySet.getValue());
            } catch (JSONException e) {
                throw new UnsupportedOperationException("Error processing JSON " + e.getMessage());
            }
        });

        return json.toString();
    }

}