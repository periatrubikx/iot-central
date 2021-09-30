package io.rubikx.rubikxcore.repository;

import io.rubikx.rubikxcore.RubikXConfig;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = RubikXConfig.class)
@TestPropertySource(properties = {"spring.config.location=classpath:rubikx-core.yml"})
public class BaseTest {
    @Before
    public void contextLoads() {

    }
}
