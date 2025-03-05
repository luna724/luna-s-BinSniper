/*
 * Decompiled with CFR 0.151.
 */
package luna724.iloveichika.binsniper.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class Analytics {
    public static void requestWeb(String jsonData, String url) {
        try {
            URL endpoint = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) endpoint.openConnection();
            connection.addRequestProperty("Content-Type", "application/JSON; charset=utf-8");
            connection.addRequestProperty("User-Agent", "DiscordBot");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", String.valueOf(jsonData.length()));
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonData.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200 && responseCode != 204) {
                System.out.println("error:" + responseCode);
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String setJsonObj(String content, String username, String avatarUrl) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("content", content);
        if (username != null && username.length() > 0) {
            objectNode.put("username", username);
        }
        if (avatarUrl != null && avatarUrl.length() > 0) {
            objectNode.put("avatar_url", avatarUrl);
        }
        try {
            return objectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public Analytics() {
        Analytics analytics;
    }
}