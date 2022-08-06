package info.xpanda.dsl.server;

import org.apache.calcite.avatica.jdbc.JdbcMeta;
import org.apache.calcite.avatica.remote.Driver;
import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.avatica.server.HttpServer;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * https://github.com/sirensolutions/avatica-tls-server/blob/main/src/main/java/io/siren/avatica/TlsServer.java
 *
 * @author Paul Jiang
 */
public class DslServerApplication {
    private static final Logger log = LoggerFactory.getLogger(DslServerApplication.class);

    private static String model = "{\n" +
            "  \"defaultSchema\": \"db_mysql\",\n" +
            "  \"schemas\": [\n" +
            "    {\n" +
            "      \"factory\": \"org.apache.calcite.adapter.jdbc.JdbcSchema$Factory\",\n" +
            "      \"name\": \"db_mysql\",\n" +
            "      \"operand\": {\n" +
            "        \"jdbcDriver\": \"com.mysql.cj.jdbc.Driver\",\n" +
            "        \"jdbcUrl\": \"jdbc:mysql://127.0.0.1:3306/test\",\n" +
            "        \"jdbcUser\": \"root\",\n" +
            "        \"jdbcPassword\": \"123456\"\n" +
            "      },\n" +
            "      \"type\": \"custom\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"factory\": \"info.xpanda.dsl.server.mongodb.MongoSchemaFactory\",\n" +
            "      \"name\": \"db_mongo\",\n" +
            "      \"operand\": {\n" +
            "        \"host\": \"127.0.0.1:27017\",\n" +
            "        \"database\": \"test\"\n" +
            "      },\n" +
            "      \"type\": \"custom\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"version\": \"1.0\"\n" +
            "}";
    private static Driver.Serialization serialization = Driver.Serialization.JSON;

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        config.put("model", "inline:" + model);
        config.put("lex", "MYSQL");

        JdbcMeta meta = new JdbcMeta("jdbc:calcite:", config);

        LocalService service = new LocalService(meta);

        HttpServer.Builder<Server> builder = new HttpServer.Builder<Server>()
                .withHandler(service, serialization)
                .withPort(9090);

        HttpServer server = builder.build();
        server.start();
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.info("Stopping server");
                    server.stop();
                    log.info("Server stopped");
                }));

        try {
            server.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
