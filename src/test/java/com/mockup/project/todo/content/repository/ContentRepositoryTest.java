package com.mockup.project.todo.content.repository;

import com.mockup.project.todo.content.entity.Content;
import com.mockup.project.todo.content.service.ContentRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ContentRepository 테스트")
@DataJpaTest
@Slf4j
class ContentRepositoryTest {

    @Autowired
    private ContentRepository contentRepository;

    @BeforeEach
    void setUp() {
        contentRepository.deleteAll();
    }


    @Nested
    @DisplayName("saveTest")
    class saveTest{
        @Test
        @DisplayName("데이터가 정상적으로 저장되어야 한다.")
        void saveTest(){
            //given
            Content content = new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            //when
            Content save = contentRepository.save(content);
            //then
            assertEquals(content.getId(), save.getId());
            assertEquals(content.getContent(), save.getContent());
            assertEquals(content.getContentDetail(), save.getContentDetail());
        }
    }

    @Nested
    @DisplayName("findTest")
    class findTest{
        @Test
        @DisplayName("id에 해당하는 데이터가 존재할 때에는 데이터가 조회되어야한다.")
        void findByIdTest() {
            // given
            Content save = contentRepository.save(new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
            // when
            Content content = contentRepository.findById(save.getId()).orElseThrow(() -> new IllegalArgumentException("해당하는 id가 없습니다."));
            // then
            assertEquals(save.getId(), content.getId());
        }

        @Test
        @DisplayName("id에 해당하는 데이터가 존재하지 않을 때에는 에러가 발생해야한다.")
        void findByIdErrorTest(){
            //given
            Long id = 100L;
            //when then
            assertThrows(IllegalArgumentException.class, () -> contentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 id가 없습니다.")));
        }
    }

    @Nested
    @DisplayName("updateTest")
    class updateTest{
        @Test
        @DisplayName("데이터가 정상적으로 수정되어야 한다.")
        void updateOneTest(){
            //given
            Content save = contentRepository.save(new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
            //when
            Content content = contentRepository.findById(save.getId()).orElseThrow(() -> new IllegalArgumentException("해당하는 id가 없습니다."));

            ContentRequest contentRequest = new ContentRequest("수정된 내용", "수정된 내용 디테일", save.getStartDateTime(),save.getEndDateTime());
            content.updateContent(contentRequest);
            Content updateContent = contentRepository.save(content);
            //then
            assertEquals(updateContent.getContent(), contentRequest.getContent());
            assertEquals(updateContent.getContentDetail(), contentRequest.getContentDetail());
        }
    }

    @Nested
    @DisplayName("deleteTest")
    class deleteTest{

        @Test
        @DisplayName("데이터가 정상적으로 삭제되어야 한다.")
        void deleteOneTest(){
            //given
            Content save = contentRepository.save(new Content("내용", "내용 디테일", LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
            //when
            contentRepository.deleteById(save.getId());
            //then
            assertEquals(contentRepository.findAll().size(), 0);
        }

        @Test
        @DisplayName("데이터 전체가 정상적으로 삭제되어야 한다.")
        void deleteAllTest(){
            //given
            for(int i = 0; i < 10; i++){
                contentRepository.save(new Content("내용" + i, "내용 디테일" + i, LocalDateTime.now(), LocalDateTime.now().plusHours(i)));
            }
            //when
            contentRepository.deleteAll();
            //then
            assertEquals(contentRepository.findAll().size(), 0);
        }
    }


}