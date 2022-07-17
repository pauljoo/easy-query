package info.xpanda.easy.query.dsl;

import org.apache.calcite.adapter.csv.CsvSchema;
import org.apache.calcite.adapter.csv.CsvTable;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DslApplication {
    public static void main(String[] args) throws Exception{
        String path = DslApplication.class.getClassLoader().getResource("csv").getPath();
        CsvSchema csvSchema = new CsvSchema(new File(path), CsvTable.Flavor.SCANNABLE);

        Properties info = new Properties();
        info.setProperty("caseSensitive", "false");
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();
        rootSchema.add("csv", csvSchema);

        Statement statement = calciteConnection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from csv.depts");
        while(resultSet.next()) {
            System.out.println(resultSet.getString(1));
            System.out.println(resultSet.getString(2));
        }
        resultSet.close();
        statement.close();
        connection.close();
    }
}
