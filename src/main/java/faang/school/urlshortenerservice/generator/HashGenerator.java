package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${hash.batch-size}")
    private int batchSize;

    @PostConstruct
    private void init(){
        generateBatch();
    }

    @Async("hashExecutor")
    public void generateBatch(int customBatchSize) {
        List<Long> uniqueNumbers = hashRepository.getFollowingRangeUniqueNumbers(customBatchSize);
        List<Hash> hashes = baseEncoder.encode(uniqueNumbers).stream().map(Hash::new).toList();
        hashRepository.saveAll(hashes);
    }

    @Async("hashExecutor")
    public void generateBatch() {
        generateBatch(batchSize);
    }
}
