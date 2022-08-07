package info.xpanda.dsl.client.config;

import com.mongodb.MongoClient;
import info.xpanda.dsl.client.DbUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Properties;

/**
 * @author Paul Jiang
 */
@Configuration
public class JdbcConfig {
    @Value("${mysql.driver-class-name}")
    private String mysqlDriverClassName;
    @Value("${mysql.url}")
    private String mysqlUrl;
    @Value("${mysql.username}")
    private String mysqlUsername;
    @Value("${mysql.password}")
    private String mysqlPassword;

    @Value("${avatica.driver-class-name}")
    private String avaticaDriverClassName;
    @Value("${avatica.url}")
    private String avaticaUrl;
    @Value("${avatica.username}")
    private String avaticaUsername;
    @Value("${avatica.password}")
    private String avaticaPassword;
    @Value("${mongodb.host}")
    private String mongodbHost;
    @Value("${mongodb.port}")
    private int mongodbPort;

    @Bean
    public MongoClient mongoClient() {
        MongoClient client = new MongoClient(mongodbHost, mongodbPort);
        return client;
    }

    @Primary
    @Bean(name = "mysqlDbUtil")
    public DbUtil mysqlDbUtil() {
        Properties properties = new Properties();
        properties.put("username", mysqlUsername);
        properties.put("password", mysqlPassword);
        return new DbUtil(mysqlUrl, properties);
    }

    @Bean(name = "avaticaDbUtil")
    public DbUtil avaticaDbUtil() {
        Properties properties = new Properties();
        properties.put("Content-Type", "application/json; charset=utf-8");
        return new DbUtil(avaticaUrl, properties);
    }
}
