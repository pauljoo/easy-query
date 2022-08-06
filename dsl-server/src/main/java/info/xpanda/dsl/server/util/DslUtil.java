package info.xpanda.dsl.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Paul Jiang
 */
public class DslUtil {
    private String model;

    private Connection connection;

    public DslUtil(String model) {
        this.model = model;
    }

    public void init() throws Exception {
        if (this.connection == null) {
            Properties config = new Properties();
            config.put("model", "inline:" + model);
            config.put("lex", "MYSQL");
            connection = DriverManager.getConnection("jdbc:calcite:", config);
        }
    }

    public void destroy() throws Exception {
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
    }

    public void select(String sql) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                //打印查询结果
                PrintUtil.printResultSet(rs);
            }
        }
    }

    public static void printLogicPlan(String model, String sql) throws Exception {
        System.out.println(sql);
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> map = om.readValue(model, Map.class);
        List<Map<String, Object>> schemaModelList = (List<Map<String, Object>>) map.get("schemas");
        for (Map<String, Object> schemaModel : schemaModelList) {
            String factory = String.valueOf(schemaModel.get("factory"));
            String name = String.valueOf(schemaModel.get("name"));
            SchemaFactory schemaFactory = ReflectUtil.newInstance(factory);
            Schema schema = schemaFactory.create(rootSchema, name, (Map<String, Object>) schemaModel.get("operand"));
            rootSchema.add(name, schema);
        }

        SqlParser.Config insensitiveParser = SqlParser.configBuilder()
                .setCaseSensitive(false)
                .build();

        FrameworkConfig config = Frameworks.newConfigBuilder()
                .parserConfig(insensitiveParser)
                .defaultSchema(rootSchema)
                .build();

        Planner planner = Frameworks.getPlanner(config);
        SqlNode sqlNode = planner.parse(sql);
        SqlNode sqlNodeValidated = planner.validate(sqlNode);
        RelRoot relRoot = planner.rel(sqlNodeValidated);
        RelNode relNode = relRoot.project();

        System.out.println(sqlNode.toSqlString(MysqlSqlDialect.DEFAULT));
        System.out.println(relNode.explain());
    }
}
