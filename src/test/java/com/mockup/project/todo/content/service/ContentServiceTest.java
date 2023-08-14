package com.mockup.project.todo.content.service;

import com.mockup.project.todo.content.entity.Content;
import com.mockup.project.todo.content.exception.ContentException;
import com.mockup.project.todo.content.repository.ContentRepository;
import com.mockup.project.todo.content.scheduler.ContentScheduler;
import com.mockup.project.todo.util.MessageUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ContentService.class)
class ContentServiceTest {

    @Autowired
    private ContentService contentService;
    @MockBean
    private ContentRepository contentRepository;
    @MockBean
    private MessageUtil messageUtil;
    @MockBean
    private ContentScheduler createContentScheduler;

    @Nested
    @DisplayName("ContentService createContent 테스트")
    class createTest {
        @Test
        @DisplayName("ContentRequest를 받아서 정상적으로 content로 변환시켜 저장되어야 한다.")
        void createContentTest() {
            //given
            Content content = new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            when(contentRepository.save(any())).thenReturn(content);

            //when
            ContentResponse contentResponse = contentService.createContent(new ContentRequest("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

            //then
            assertEquals(content.getContent(), contentResponse.getContent());
            assertEquals(content.getContentDetail(), contentResponse.getContentDetail());
        }
    }

    @Nested
    @DisplayName("ContentService getContent 테스트")
    class getTest {
        @Test
        @DisplayName("id에 해당하는 데이터가 존재할 때에는 데이터가 조회되어야한다.")
        void getContentTest() {
            //given
            Content content = new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            when(contentRepository.findById(any())).thenReturn(java.util.Optional.of(content));

            //when
            ContentResponse contentResponse = contentService.getContent(1L);

            //then
            assertEquals(content.getContent(), contentResponse.getContent());
            assertEquals(content.getContentDetail(), contentResponse.getContentDetail());
        }

        @Test
        @DisplayName("id에 해당하는 데이터가 존재하지 않을 때에는 ContentException 발생해야한다.")
        void getContentErrorTest() {
            //given
            when(contentRepository.findById(any())).thenReturn(java.util.Optional.empty());
            //when
            //then
            assertThrows(ContentException.class, () -> contentService.getContent(1L));
        }

    }

    @Nested
    @DisplayName("ContentService updateContent 테스트")
    class updateTest {

        @Test
        @DisplayName("id에 해당하는 데이터가 존재할 때에는 데이터가 수정되어야한다.")
        void updateContentTest() {
            //given
            Content content = new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            ContentRequest contentRequest = new ContentRequest("변경된 내용", "변경된 내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            Content updatedContent = new Content("변경된 내용", "변경된 내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            when(contentRepository.findById(any())).thenReturn(java.util.Optional.of(content));
            when(contentRepository.save(any())).thenReturn(updatedContent);
            //when
            ContentResponse contentResponse = contentService.updateContent(1L, contentRequest);
            //then
            assertEquals(content.getContent(), contentResponse.getContent());
            assertEquals(content.getContentDetail(), contentResponse.getContentDetail());
        }
    }

    @Nested
    @DisplayName("ContentService deleteContent 테스트")
    class deleteTest {

        @Test
        @DisplayName("id에 해당하는 데이터가 존재할 때에는 데이터가 삭제되어야한다.")
        void deleteContentTest() {
            //given
            Long deleteId = 1L;
            when(contentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

            //when
            contentService.deleteContent(deleteId);

            //when
            assertEquals(contentRepository.findById(deleteId), java.util.Optional.empty());
            verify(contentRepository, Mockito.times(1)).deleteById(deleteId);
        }

        @Test
        @DisplayName("전체 삭제가 수행되어야 한다.")
        void deleteAllContentTest() {
            //given
            //when
            contentService.deleteAllContent();
            //then
            verify(contentRepository, Mockito.times(1)).deleteAll();
        }
    }


}