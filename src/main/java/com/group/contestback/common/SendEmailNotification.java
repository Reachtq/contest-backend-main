package com.group.contestback.common;

import com.group.contestback.models.AppUser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;


import java.io.IOException;

public class SendEmailNotification {


    public void sendGradeNotification(String fio, String email, Integer score, String review) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://185.173.93.16:5000/notifyForGrade");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String json = "{\"type\" : \"GRADE\"," + "" +
                "       \"email\": \"" + email + "\"," +
                "       \"student_name\": \"" + fio + "\"," +
                "       \"grade\": \"" + score + "\"," +
                "       \"review\":\"" + review + "\"" +
                "       }";
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            ResponseEntity.ok().body(responseEntity.getContent());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseEntity.badRequest().body("Server not available");
    }

    public void sendRegistrationNotification(AppUser user, String password) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://185.173.93.16:5000/notifyForRegistrtaion");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String json = "{" +
                "    \"type\": \"REGISTRATION\"," +
                "    \"email\": \"" + user.getEmail() + "\"," +
                "    \"student_name\": \"" + user.getFio() + "\"," +
                "    \"login\": \"" + user.getLogin() + "\",\n" +
                "    \"password\": \"" + password + "\"" +
                "}";
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            ResponseEntity.ok().body(responseEntity.getContent());
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseEntity.badRequest().body("Server not available");
    }
}
