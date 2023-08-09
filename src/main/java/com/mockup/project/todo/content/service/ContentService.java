package com.mockup.project.todo.content.service;

import com.mockup.project.todo.content.entity.Content;
import com.mockup.project.todo.content.exception.ContentException;
import com.mockup.project.todo.content.repository.ContentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentResponse createContent(ContentRequest contentRequest){
        Content save = contentRepository.save(contentRequest.toContent());
        return save.toContentResponse();
    }

    public List<ContentResponse> getAllContent(){
        List<Content> all = contentRepository.findAll();
        return all.stream().map(Content::toContentResponse).toList();
    }

    public ContentResponse getContent(Long id){
        Content content = contentRepository.findById(id).orElseThrow(() -> new ContentException("해당하는 id가 없습니다."));
        return content.toContentResponse();
    }

    public ContentResponse updateContent(Long id, ContentRequest contentRequest){
        Content content = contentRepository.findById(id).orElseThrow(() -> new ContentException("해당하는 id가 없습니다."));
        content.updateContent(contentRequest);
        contentRepository.save(content);
        return content.toContentResponse();
    }

    public void deleteContent(Long id){
        contentRepository.deleteById(id);
    }

    public void deleteAllContent(){
        contentRepository.deleteAll();
    }

}
