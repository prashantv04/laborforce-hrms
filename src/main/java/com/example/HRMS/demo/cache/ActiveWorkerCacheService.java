package com.example.HRMS.demo.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveWorkerCacheService {

    private static final String ACTIVE_WORKER_PREFIX =
            "active_worker:";

    private final RedisTemplate<String, ActiveWorkerCache>
            redisTemplate;

    public void save(
            ActiveWorkerCache activeWorker
    ) {

        try {

            redisTemplate.opsForValue().set(
                    ACTIVE_WORKER_PREFIX
                            + activeWorker.getWorkerId(),
                    activeWorker,
                    Duration.ofHours(16)
            );

        } catch (Exception ex) {

            log.warn(
                    "Redis unavailable while caching worker {}",
                    activeWorker.getWorkerId()
            );
        }
    }

    public void remove(Long workerId) {

        try {

            redisTemplate.delete(
                    ACTIVE_WORKER_PREFIX + workerId
            );

        } catch (Exception ex) {

            log.warn(
                    "Redis unavailable while removing worker {}",
                    workerId
            );
        }
    }

    public List<ActiveWorkerCache> getAllActiveWorkers() {

        try {

            Set<String> keys =
                    redisTemplate.keys(
                            ACTIVE_WORKER_PREFIX + "*"
                    );

            if (keys == null || keys.isEmpty()) {

                return List.of();
            }

            List<ActiveWorkerCache> workers =
                    redisTemplate.opsForValue()
                            .multiGet(keys);

            if (workers == null) {

                return List.of();
            }

            return workers;

        } catch (Exception ex) {

            log.warn(
                    "Redis unavailable while fetching active workers"
            );

            return List.of();
        }
    }
}