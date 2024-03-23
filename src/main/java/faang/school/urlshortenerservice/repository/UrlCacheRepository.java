package faang.school.urlshortenerservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisUrlTemplate;
    private final ObjectMapper objectMapper;
    private final UrlMapper urlMapper;

    public void save(Url url) {
        try {
            UrlDto urlDto = urlMapper.toDto(url);
            redisUrlTemplate.opsForValue().set(urlDto.getHash(), objectMapper.writeValueAsString(urlDto));
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Optional<Url> get(String hash) {
        try {
            String json = redisUrlTemplate.opsForValue().get(hash);
            UrlDto urlDto = objectMapper.readValue(json, UrlDto.class);
            return Optional.of(urlMapper.toEntity(urlDto));
        } catch (JsonProcessingException exception) {
            return Optional.empty();
        }
    }
}
