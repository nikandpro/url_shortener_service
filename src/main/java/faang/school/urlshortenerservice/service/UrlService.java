package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    @Value("${URL.static_address}")
    private String staticAddress;

    private final HashCache hashCache;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlRepository urlRepository;

    public UrlDto findUrl(String hash) {
        UrlDto urlDto = new UrlDto();
        try {
            Optional<String> originalUrl = urlCacheRepository.getAssociation(hash);
            if (originalUrl.isPresent()) {
                urlDto.setUrl(originalUrl.get());
                log.info("Got original url {} from redis", urlDto.getUrl());
            } else {
                urlDto.setUrl(urlRepository.findByHash(hash));
                log.info("Got original url {} from database", urlDto.getUrl());
                urlCacheRepository.saveAssociation(urlDto.getUrl(), hash);
                log.info("Saved original url {} in cache", urlDto.getUrl());
            }
        } catch (RedisConnectionFailureException ex) {
            log.error("Redis connection failure", ex);
            urlDto.setUrl(urlRepository.findByHash(hash));
            log.info("Got original url {} from database", urlDto.getUrl());
        }
        return urlDto;
    }

    public UrlDto convertToShortUrl(UrlDto urlDto) {
        String existedHash = containsUrl(urlDto.getUrl());
        try {
            if (existedHash.isEmpty()) {
                String hash = hashCache.getHash();
                return saveHashUrlConvertToShortUrl(existedHash, hash
                        , (redisCache) -> redisCache.saveAssociation(urlDto.getUrl(), hash)
                        , (dbCache) -> dbCache.saveAssociation(urlDto.getUrl(), hash));
            } else {
                log.info("Found url in database {} so hash will not be generated", urlDto.getUrl());
                urlDto.setUrl(staticAddress.concat(existedHash));
                log.info("configured shortUrl is {}", urlDto.getUrl());
                return urlDto;
            }
        } catch (RedisConnectionFailureException ex) {
            log.error("Redis connection failure", ex);
            String hash = hashCache.getHash();
            return saveHashUrlConvertToShortUrl(existedHash, hash
                    , null
                    , (dbCache) -> dbCache.saveAssociation(urlDto.getUrl(), hash));
        }
    }

    private UrlDto saveHashUrlConvertToShortUrl(String existedHash, String hash, Consumer<UrlCacheRepository> redisConsumer, Consumer<UrlRepository> dBConsumer) {
        log.info("got hash {} from cache", hash);
        if (redisConsumer != null) {
            redisConsumer.accept(urlCacheRepository);
            log.info("hash {} were saved to redis", hash);
        }
        dBConsumer.accept(urlRepository);
        log.info("hash {} were saved to DB \"URL\"", hash);
        UrlDto shortUrl = new UrlDto();
        shortUrl.setUrl(staticAddress.concat(hash));
        log.info("configured shortUrl is {}", shortUrl.getUrl());
        return shortUrl;
    }

    private String containsUrl(String url) {
        Optional<String> hash = urlRepository.findByUrl(url);
        return hash.orElse("");
    }
}
