package io.github.hossensyedriadh.InvenTrackRESTfulService;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InvenTrackResTfulServiceApplicationTests {
    public Logger logger = Logger.getLogger(this.getClass());

    @Test
    void contextLoads() {
    }

    @Test
    void generate_random_string_only_letters() {
        String letters = "rvqhWdXHzgimbeZFMNYsSGfQTxCnJAROBaPoLUkKwEDpjucytV";

        StringBuilder randomString = new StringBuilder(8);

        for (int i = 0; i < 8; i += 1) {
            randomString.append(letters.toCharArray()[(int) (Math.random() * letters.length())]);
        }

        assert randomString.length() == 8;

        logger.info(randomString.toString());
    }

    @Test
    void generate_random_string_letters_and_numbers() {
        String letters = "hH1VT9wbzAXpi18gko0vK5OStG6WCxYLjJ42Ndn3mMFRaDZUuefBPyQrEsc7q";

        StringBuilder randomString = new StringBuilder(8);

        for (int i = 0; i < 8; i += 1) {
            randomString.append(letters.toCharArray()[(int) (Math.random() * letters.length())]);
        }

        assert randomString.length() == 8;

        logger.info(randomString.toString());
    }
}
