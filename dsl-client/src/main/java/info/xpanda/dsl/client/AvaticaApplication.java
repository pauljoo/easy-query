package info.xpanda.dsl.client;

import org.apache.calcite.avatica.AvaticaConnection;
import org.apache.calcite.avatica.AvaticaStatement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * @author Paul Jiang
 */
public class AvaticaApplication {
    public static void main(String[] args) throws Exception{
        String url = "jdbc:avatica:remote:url=http://127.0.0.1:9090/";
        Properties properties = new Properties();
        properties.put("Content-Type", "application/json; charset=utf-8");
        AvaticaConnection connection = (AvaticaConnection) DriverManager.getConnection(url, properties);
        AvaticaStatement statement = connection.createStatement();
        String sql = "Select * from db_mysql.ORDERS where O_ORDERKEY = 2 AND O_ORDERDATE >= '1996-12-01' AND O_ORDERDATE <= '1996-12-01'";
        ResultSet resultSet = statement.executeQuery(sql);
        PrintUtil.printResultSet(resultSet);
    }
}
