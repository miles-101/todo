package com.mockup.project.todo.content.repository;

import com.mockup.project.todo.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// jpa 구현 후 jpa 사용하지않는 방식으로 변경
@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    Optional<Content> findById(Long id);

    <S extends Content> S save(S content);

    void deleteById(Long id);

    List<Content> findAll();

    void deleteAll();
}
