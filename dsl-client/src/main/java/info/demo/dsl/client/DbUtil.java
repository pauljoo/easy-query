package info.demo.dsl.client;

import java.sql.*;
import java.util.*;

/**
 * @author Paul Jiang
 */
public class DbUtil {
    private String url;

    private Properties properties;

    public DbUtil(String url, Properties properties) {
        this.url = url;
        this.properties = properties;
    }

    public List<Map<String, Object>> select(String sql) throws Exception{
        try(Connection connection = DriverManager.getConnection(url, properties)){
            try(Statement statement = connection.createStatement()){
                ResultSet resultSet = statement.executeQuery(sql);
                return DbUtil.returnResultSet(resultSet);
            }
        }
    }

    public static List<Map<String, Object>> returnResultSet(ResultSet resultSet) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int count = rsmd.getColumnCount();

        StringBuilder sbHeader = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sbHeader.append(rsmd.getColumnName(i) + "|");
        }
        System.out.println(sbHeader.toString());

        while (resultSet.next()) {
            StringBuilder sbRow = new StringBuilder();
            Map<String, Object> result = new HashMap<>();
            for (int i = 1; i <= count; i++) {
                result.put(rsmd.getColumnName(i), resultSet.getString(i));
                sbRow.append(resultSet.getString(i) + "|");
            }
            results.add(result);
            System.out.println(sbRow.toString());
        }

        return results;
    }

    public static String printResultSet(ResultSet resultSet) throws Exception {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int count = rsmd.getColumnCount();

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sb.append(rsmd.getColumnName(i) + "|");
        }
        sb.append("\r\n");

        while (resultSet.next()) {
            for (int i = 1; i <= count; i++) {
                sb.append(resultSet.getString(i) + "|");
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
