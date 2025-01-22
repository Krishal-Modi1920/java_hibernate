package org.baps.api.vtms.services;

import org.baps.api.vtms.enumerations.RedisEventEnum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Service
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class RedisService {
    

    @Value("${redis.enable}")
    private boolean isRedisEnable;

    private final RedisTemplate<String, String> redisTemplate;

    public void save(final String key, final String value, final RedisEventEnum redisEventEnum, final Date expireDateTime) {
        if (isRedisEnable && redisEventEnum.equals(RedisEventEnum.SAVE_TOKEN) && value != null) {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expireAt(key, expireDateTime);
        }
    }

    public String get(final String key, final RedisEventEnum redisEventEnum) {
        if (isRedisEnable && redisEventEnum.equals(RedisEventEnum.GET_TOKEN)) {
            return redisTemplate.opsForValue().get(key);
        }
        return null;
    }
}
