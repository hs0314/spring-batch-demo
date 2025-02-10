package springbatchdemo;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@TestConfiguration
@EnableAutoConfiguration
@Configuration
public class TestBatchConfig {

    @DynamicPropertySource
    static void batchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.batch.jdbc.initialize-schema", () -> "always");  // ✅ 테스트 환경에서만 자동 생성
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void testH2Database() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println("Connected to: " + connection.getMetaData().getURL());
        connection.close();
    }
}
