package com.mockup.project.todo.content.service;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.content.entity.Content;
import com.mockup.project.todo.content.exception.ContentException;
import com.mockup.project.todo.content.repository.ContentRepository;
import com.mockup.project.todo.content.scheduler.ContentScheduler;
import com.mockup.project.todo.util.MessageUtil;
import com.mockup.project.todo.util.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentScheduler createContentScheduler;
    private final MessageUtil messageUtil;
    private final RedisService redisService;

    @Transactional
    public ContentResponse createContent(ContentRequest contentRequest) {
        Content save = contentRepository.save(contentRequest.toContent());
        return save.toContentResponse();
    }

    @Transactional(readOnly = true)
    public List<ContentResponse> getAllContent() {
        List<Content> all = contentRepository.findAll();
        return all.stream().map(Content::toContentResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContentResponse getContent(Long id) {
        Content content = contentRepository.findById(id).orElseThrow(() -> new ContentException("해당하는 id가 없습니다."));
        return content.toContentResponse();
    }

    @Transactional
    public ContentResponse updateContent(Long id, ContentRequest contentRequest) {
        Content content = contentRepository.findById(id).orElseThrow(() -> new ContentException("해당하는 id가 없습니다."));
        content.updateContent(contentRequest);
        contentRepository.save(content);
        return content.toContentResponse();
    }

    @Transactional
    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllContent() {
        contentRepository.deleteAll();
    }

    public void setAlarm(ContentAPI.ContentRequest contentRequest) {
        log.info("남은 시간 : {}", Duration.between(LocalDateTime.now(), contentRequest.getEndDateTime()).toMinutes());
        // send message or reserve message
        if (Duration.between(LocalDateTime.now(), contentRequest.getEndDateTime()).toMinutes() < 60) {
            messageUtil.sendMessages(contentRequest);
        } else {
            log.info("남은 시간이 60분 이상이므로 스케쥴러에 등록합니다.");
            redisService.saveDueToAlarmTask(contentRequest);
            createContentScheduler.dueToAlarmAddTask(contentRequest);
        }
    }

}
