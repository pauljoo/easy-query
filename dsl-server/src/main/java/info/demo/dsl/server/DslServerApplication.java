package info.demo.dsl.server;

import info.demo.dsl.server.mongodb.MongoSchema;
import org.apache.calcite.avatica.jdbc.JdbcMeta;
import org.apache.calcite.avatica.remote.Driver;
import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.avatica.server.HttpServer;
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * https://github.com/sirensolutions/avatica-tls-server/blob/main/src/main/java/io/siren/avatica/TlsServer.java
 *
 * @author Paul Jiang
 */
public class DslServerApplication {
    private static final Logger log = LoggerFactory.getLogger(DslServerApplication.class);
    private static Driver.Serialization serialization = Driver.Serialization.JSON;

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option opt1 = new Option("m", "model", true, "model path");
        opt1.setRequired(true);
        options.addOption(opt1);

        Option opt2 = new Option("s", "mongo-schema", true, "mongo-schema");
        opt2.setRequired(true);
        options.addOption(opt2);

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        String mongoSchema = commandLine.getOptionValue("s");
        MongoSchema.load(new File(mongoSchema));

        String model = commandLine.getOptionValue("m");
        Properties config = new Properties();
        config.put("model", model);
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
