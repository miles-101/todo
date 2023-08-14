package com.mockup.project.todo.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockup.project.todo.content.entity.Content;
import com.mockup.project.todo.content.exception.ContentException;
import com.mockup.project.todo.content.scheduler.ContentScheduler;
import com.mockup.project.todo.content.service.ContentResponse;
import com.mockup.project.todo.content.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {ContentController.class, ContentAPI.class})
@Slf4j
class ContentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContentService contentService;

    @MockBean
    private ContentScheduler createContentScheduler;

    @Nested
    @DisplayName("ContentController createContent 테스트")
    class createAPITest {

        @Test
        @DisplayName("ContentRequest를 받아서 정상적으로 content로 변환시켜 저장되어야 한다.")
        void createContentTest() throws Exception {
            //given
            // 기본 생성자 없으면 오류... why?
            ContentAPI.ContentRequest contentRequest = new ContentAPI.ContentRequest("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1), null);

            //when
            when(contentService.createContent(any())).thenReturn(new ContentResponse("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
            //then
            mockMvc.perform(post("/api/v1/content/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contentRequest)))
                    .andExpect(result -> {
                        status().isOk();
                        jsonPath("$.data.content").value("내용");
                        jsonPath("$.data.content").value("내용 디테일");
                    });
        }

        @Test
        @DisplayName("ContentRequest에 content가 없을 경우에는 400에러가 발생해야 한다.")
        void createContentErrorTest() throws Exception {
            //given
            ContentAPI.ContentRequest contentRequest = new ContentAPI.ContentRequest("", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            //then
            mockMvc.perform(post("/api/v1/content/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(contentRequest)))
                    .andExpect(result -> {
                        status().isBadRequest();
                    });

        }
    }

    @Nested
    @DisplayName("ContentController getContent 테스트")
    class getControllerTest {

        @Test
        @DisplayName("id에 해당하는 데이터가 존재할 때에는 데이터가 조회되어야한다.")
        void getContentTest() throws Exception {
            //given
            Long id = 1L;
            Content content = new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            //when
            when(contentService.getContent(id)).thenReturn(content.toContentResponse());
            //then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/content/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> {
                        status().isOk();
                        jsonPath("$.data.content").value("내용");
                        jsonPath("$.data.contentDetail").value("내용 디테일");
                    });
        }

        @Test
        @DisplayName("id에 해당하는 데이터가 존재하지 않을 때에는 not found 에러가 발생해야한다.")
        void getContentErrorTest() throws Exception {
            //given
            Long id = 1L;
            //when
            when(contentService.getContent(id)).thenThrow(new ContentException(400, "해당하는 id가 없습니다."));
            //then
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/content/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> {
                        status().isNotFound();
                    });

        }
    }

    @Nested
    @DisplayName("ContentController updateContent 테스트")
    class updateTest {

        @Test
        @DisplayName("파라미터가 정상적으로 들어올 때에는 데이터가 수정되어야 한다.")
        void updateContentTest() throws Exception {
            //given
            Long contentId = 1L;
            ContentResponse updatedContentResponse = new ContentResponse("변경된 내용", "변경된 내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            //when
            when(contentService.updateContent(anyLong(), any())).thenReturn(updatedContentResponse);
            //then
            mockMvc.perform(put("/api/v1/content/{id}", contentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedContentResponse)))
                    .andExpect(result -> {
                        status().isOk();
                        jsonPath("$.data.content").value("변경된 내용");
                        jsonPath("$.data.contentDetail").value("변경된 내용 디테일");
                    });
        }

        @Test
        @DisplayName("파라미터가 정상적으로 들어오지 않을 때에는 400에러가 발생해야 한다.")
        void updateContentErrorTest() throws Exception {
            //given
            Long contentId = 1L;
            ContentResponse updatedContentResponse = new ContentResponse("", "변경된 내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            //then
            mockMvc.perform(put("/api/v1/content/{id}", contentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedContentResponse)))
                    .andExpect(result -> {
                        status().isBadRequest();
                    });
        }

        @Test
        @DisplayName("존재하지않는 ID로 요청할 경우 404에러가 발생해야 한다.")
        void notExistIdUpdateContentErrorTest() throws Exception {
            //given
            Long contentId = 1L;
            ContentResponse updatedContentResponse = new ContentResponse("변경된 내용", "변경된 내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            //when
            when(contentService.updateContent(anyLong(), any())).thenThrow(new ContentException(400, "해당하는 id가 없습니다."));
            //then
            mockMvc.perform(put("/api/v1/content/{id}", contentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedContentResponse)))
                    .andExpect(result -> {
                        status().isNotFound();
                    });
        }
    }

    @Nested
    @DisplayName("ContentController deleteContent 테스트")
    class deleteTest {
        @Test
        @DisplayName("id에 해당하는 데이터가 존재할 때에는 문제없이 200이 나와야한다.")
        void deleteContentTest() throws Exception {
            //given
            Long id = 1L;
            //then
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/content/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> {
                        status().isOk();
                    });
        }

        @Test
        @DisplayName("id에 해당하는 데이터가 존재하지 않을 때에는 not found 에러가 발생해야한다.")
        void deleteContentErrorTest() throws Exception {
            //given
            Long id = 1L;
            //when
            when(contentService.getContent(id)).thenThrow(new ContentException(400, "해당하는 id가 없습니다."));
            //then
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/content/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result -> {
                        status().isNotFound();
                    });
        }
    }
}