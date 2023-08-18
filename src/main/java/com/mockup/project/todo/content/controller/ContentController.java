package com.mockup.project.todo.content.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mockup.project.todo.content.service.ContentResponse;
import com.mockup.project.todo.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/content")
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/{id}")
    public ContentAPI.ContentResponse getContent(@PathVariable Long id) {
        return contentService.getContent(id).toContentAPIResponse();
    }

    @GetMapping("/all")
    public List<ContentAPI.ContentResponse> getAllContent() {
        return contentService.getAllContent().stream().map(ContentResponse::toContentAPIResponse).toList();
    }

    // TODO 스케쥴러 api를 따로 뺴서 사용하는게 좋은지, 아니면 하나의 api로 사용하는게 좋은지 고민해보기

    // TODO 예약 로직을 어떻게 해야하는지 고민해보기. 스케쥴러로 post 요청 보내기 vs 미리 저장해놓고, 보여줄때 reservationDateTime이 현재보다 작은 것만 보여주기.
    @PostMapping("/")
    @ExceptionHandler({UnsupportedEncodingException.class, NoSuchAlgorithmException.class, URISyntaxException.class, InvalidKeyException.class, JsonProcessingException.class})
    public ContentAPI.ContentResponse createContent(@RequestBody @Valid ContentAPI.ContentRequest contentRequest) {

        ContentAPI.ContentResponse contentResponse;

        // 등록 or 예약 등록
        if (contentRequest.getReservationDateTime() != null &&
                contentRequest.getReservationDateTime().isAfter(contentRequest.getStartDateTime())) {
            // 예약
            contentResponse = contentService.createReservation(contentRequest.toContentRequest()).toContentAPIResponse();
        } else {
            // 등록
            contentResponse = contentService.createContent(contentRequest.toContentRequest()).toContentAPIResponse();
            contentService.setAlarm(contentRequest);
        }
        return contentResponse;
    }

    @PutMapping("/{id}")
    public ContentAPI.ContentResponse updateContent(@PathVariable("id") Long id, @RequestBody @Valid ContentAPI.ContentRequest contentRequest) {
        return contentService.updateContent(id, contentRequest.toContentRequest()).toContentAPIResponse();
    }

    @DeleteMapping("/{id}")
    public void deleteContent(@PathVariable("id") Long id) {
        contentService.deleteContent(id);
    }

    @DeleteMapping("/all")
    public void deleteAllContent() {
        contentService.deleteAllContent();
    }
}
