package com.mattermost.integration.figma;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"encryption.key = mySecretKey"})
class FigmaApplicationTests {

	@Test
	void contextLoads() {
	}

}
