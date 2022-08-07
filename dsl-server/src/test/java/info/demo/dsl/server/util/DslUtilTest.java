package info.demo.dsl.server.util;

import org.junit.Test;

/**
 * @author Paul Jiang
 */
public class DslUtilTest {
    @Test
    public void select1() throws Exception{
        String model = "{\n" +
                "  \"defaultSchema\": \"db_mongo\",\n" +
                "  \"schemas\": [\n" +
                "    {\n" +
                "      \"factory\": \"info.demo.dsl.server.mongodb.MongoSchemaFactory\",\n" +
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
        String sql =
                "SELECT N_NATIONKEY,N_NAME,N_REGIONKEY,N_COMMENT FROM db_mongo.NATION limit 10";
        DslUtil dslUtil = new DslUtil(model);
        dslUtil.init();

        dslUtil.select(sql);
        dslUtil.destroy();
    }

    @Test
    public void select2() throws Exception{
        String model = "{\n" +
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
                "      \"factory\": \"info.demo.dsl.server.mongodb.MongoSchemaFactory\",\n" +
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
        DslUtil dslUtil = new DslUtil(model);
        dslUtil.init();

        String sql1 = "Select * from db_mysql.ORDERS where O_ORDERKEY = 2 AND O_ORDERDATE >= '1996-12-01' AND O_ORDERDATE <= '1996-12-01'";
//        DslUtil.printLogicPlan(model, sql1);
        dslUtil.select(sql1);

        String sql2 = "Select O_ORDERKEY,count(1) as cnt from db_mysql.ORDERS group by O_ORDERKEY having count(1) > 0 order by cnt limit 10";
//        String sql2 = "Select O_ORDERKEY,count(1) as cnt from db_mysql.ORDERS group by O_ORDERKEY having count(1) > 10";
//        DslUtil.printLogicPlan(model, sql2);
        dslUtil.select(sql2);

        String sql3 = "Select * from db_mysql.ORDERS o,db_mysql.CUSTOMER c,db_mongo.NATION n " +
                "where o.O_CUSTKEY = c.C_CUSTKEY and c.C_NATIONKEY = n.N_NATIONKEY and c.C_NAME = 'Customer#000000002'";
//        DslUtil.printLogicPlan(model, sql3);
        dslUtil.select(sql3);

        String sql4 = "Select C_NAME,Count(O_ORDERKEY) as cnt " +
                "From " +
                "( " +
                "Select o.O_ORDERKEY,c.C_CUSTKEY,c.C_NAME " +
                "from db_mysql.ORDERS o " +
                "Left join db_mysql.CUSTOMER c " +
                "On o.O_CUSTKEY = c.C_CUSTKEY " +
                ")t " +
                "Group by C_NAME " +
                "Having Count(O_ORDERKEY) > 0 " +
                "order by cnt desc " +
                "limit 10";
//        DslUtil.printLogicPlan(model, sql4);
        dslUtil.select(sql4);

        dslUtil.destroy();
    }

    @Test
    public void printLogicPlan() throws Exception{
        String model = "{\n" +
                "  \"defaultSchema\": \"db_mongo\",\n" +
                "  \"schemas\": [\n" +
                "    {\n" +
                "      \"factory\": \"info.demo.dsl.server.mongodb.MongoSchemaFactory\",\n" +
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
        String sql =
                "SELECT exchange, ts_code FROM db_mongo.ods_tushare_stock_basic limit 10";
        DslUtil.printLogicPlan(model, sql);
    }

}