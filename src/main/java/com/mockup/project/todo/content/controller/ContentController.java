package com.mockup.project.todo.content.controller;

import com.mockup.project.todo.content.scheduler.CreateContentScheduler;
import com.mockup.project.todo.content.service.ContentResponse;
import com.mockup.project.todo.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/content")
public class ContentController {

    private final ContentService contentService;
    private final CreateContentScheduler createContentScheduler;

    @GetMapping("/{id}")
    public ContentAPI.ContentResponse getContent(@PathVariable Long id){
        return contentService.getContent(id).toContentAPIResponse();
    }

    @GetMapping("/all")
    public List<ContentAPI.ContentResponse> getAllContent(){
        return contentService.getAllContent().stream().map(ContentResponse::toContentAPIResponse).toList();
    }

    // TODO 스케쥴러 api를 따로 뺴서 사용하는게 좋은지, 아니면 하나의 api로 사용하는게 좋은지 고민해보기
    @PostMapping("/")
    public ContentAPI.ContentResponse createContent(@RequestBody @Valid ContentAPI.ContentRequest contentRequest){
        if(contentRequest.getReservationDateTime() != null)
            return createContentScheduler.addTask(contentRequest);
        return contentService.createContent(contentRequest.toContentRequest()).toContentAPIResponse();
    }

    @PutMapping("/{id}")
    public ContentAPI.ContentResponse updateContent(@PathVariable("id") Long id,@RequestBody @Valid ContentAPI.ContentRequest contentRequest){
        return contentService.updateContent(id, contentRequest.toContentRequest()).toContentAPIResponse();
    }

    @DeleteMapping("/{id}")
    public void deleteContent(@PathVariable("id") Long id){
        contentService.deleteContent(id);
    }

    @DeleteMapping("/all")
    public void deleteAllContent(){
        contentService.deleteAllContent();
    }
}
