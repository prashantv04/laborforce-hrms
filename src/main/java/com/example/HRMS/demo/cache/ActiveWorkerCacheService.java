package com.example.HRMS.demo.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActiveWorkerCacheService {

    private static final String PREFIX = "active_worker:";

    private final RedisTemplate<String, ActiveWorkerCache> redisTemplate;

    public void save(ActiveWorkerCache worker) {

        String key = PREFIX + worker.getWorkerId();

        redisTemplate.opsForValue().set(
                key,
                worker,
                Duration.ofHours(16)
        );

        System.out.println("REDIS SAVE COMPLETED");
    }

    public void remove(Long workerId) {

        redisTemplate.delete(PREFIX + workerId);
    }

    public List<ActiveWorkerCache> getAllActiveWorkers() {

        Set<String> keys = redisTemplate.keys(PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());
    }
}