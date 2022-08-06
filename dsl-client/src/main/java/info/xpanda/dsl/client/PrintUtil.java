package info.xpanda.dsl.client;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @author Paul Jiang
 */
public class PrintUtil {
    public static void printResultSet(ResultSet resultSet) throws Exception {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int count = rsmd.getColumnCount();

        StringBuilder sbHeader = new StringBuilder();
        for (int i = 1; i <= count; i++) {
            sbHeader.append(rsmd.getColumnName(i) + "\t");
        }
        System.out.println(sbHeader.toString());

        while (resultSet.next()) {
            StringBuilder sbRow = new StringBuilder();
            for (int i = 1; i <= count; i++) {
                sbRow.append(resultSet.getString(i) + "\t");
            }
            System.out.println(sbRow.toString());
        }
    }
}
