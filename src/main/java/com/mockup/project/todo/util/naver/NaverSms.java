package com.mockup.project.todo.util.naver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NaverSms {

    private final JsonMapper jsonMapper;

    @Value("${naver.cloud-sms.accessKey}")
    private String accessKey;
    @Value("${naver.cloud-sms.secretKey}")
    private String secretKey;
    @Value("${naver.cloud-sms.serviceId}")
    private String serviceId;
    @Value("${naver.cloud-sms.senderPhoneNumber}")
    private String senderPhoneNumber;

    public String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String method = "POST";                    // method
        String url = "/sms/v2/services/" + this.serviceId + "/messages";    // url (include query string)
        String timestamp = time.toString();            // current timestamp (epoch)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(this.accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(this.secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        log.info("makeSignature 완료");
        return Base64.encodeBase64String(rawHmac);
    }

    public NaverSmsDTO.MessageResponse sendMessage(String toPoneNumber, ContentAPI.ContentRequest contentRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, URISyntaxException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", this.accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        log.info("makeSignature value : {}", makeSignature(time));

        log.info("Header 생성 완료");

        List<NaverSmsDTO.Message> messages = new ArrayList<>();
        messages.add(new NaverSmsDTO.Message(toPoneNumber, contentRequest.toMessage()));

        NaverSmsDTO.MessageRequest messageRequest = NaverSmsDTO.MessageRequest.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(this.senderPhoneNumber)
                .content(contentRequest.toMessage())
                .messages(messages)
                .build();

        log.info("messageRequest 생성 완료");
        String body = jsonMapper.objectToJson(messageRequest);
        log.info("body 생성 완료 : {}", body);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        log.info("httpEntity 생성 완료");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        NaverSmsDTO.MessageResponse messageResponse = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages"), httpEntity, NaverSmsDTO.MessageResponse.class);
        log.info("messageResponse 생성 완료 : {}", messageResponse);

        return messageResponse;
    }
}
