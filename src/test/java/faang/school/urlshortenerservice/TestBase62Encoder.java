package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TestBase62Encoder {

    private ExecutorService executorService;
    private Base62Encoder base62Encoder;
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @BeforeEach
    public void init() {
        executorService = Executors.newSingleThreadExecutor();
        base62Encoder = new Base62Encoder(executorService);
    }

    @Test
    public void testEncode() {
        List<Long> numbers = Arrays.asList(123L, 456L, 789L);
        List<String> hashes = List.of("z1", "M7", "jC");
        List<String> result = base62Encoder.encode(numbers);
        assertEquals(3, result.size());
        assertEquals(hashes.get(0), result.get(0));
        assertEquals(hashes.get(1), result.get(1));
        assertEquals(hashes.get(2), result.get(2));
    }


    @Test
    void testEncodeEmptyList() {
        List<Long> numbers = Arrays.asList();
        List<String> result = base62Encoder.encode(numbers);
        assertTrue(result.isEmpty());
    }

    private String encodeBase62(long number) {
        StringBuilder str = new StringBuilder();
        while (number > 0) {
            str.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return str.toString();
    }
}
