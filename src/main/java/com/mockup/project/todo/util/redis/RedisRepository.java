package com.mockup.project.todo.util.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveHash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void saveSet(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void deleteHash(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public void deleteSet(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public void deleteHashByKey(String key) {
        redisTemplate.delete(key);
    }

    public List<Object> getHashData(String key) {
        List<Object> list = new ArrayList<>();
        redisTemplate.opsForHash().entries(key).forEach((hashKey, value) -> {
            list.add(value);
        });
        return list;
    }
}
