/*
 * SchemaView.java
 * Created on 21-May-2005
 *
 */
package uk.co.alvagem.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SchemaView
 * 
 * @author rbp28668
 * Created on 21-May-2005
 */
public class SchemaView {

    /**
     * 
     */
    public SchemaView() {
        super();
    }
    
    public static void showTables(Connection con) throws SQLException{
        DatabaseMetaData meta = con.getMetaData();
        
        System.out.println("=======================================================");
        System.out.println("Database " + meta.getDatabaseProductName() + " version " + meta.getDatabaseProductVersion());
        System.out.println("Using JDBC " + meta.getDriverName() + " version " + meta.getDriverVersion());
        System.out.println("=======================================================");
        
        ResultSet rsTables = meta.getTables(null,null,null,new String[]{"TABLE"});
        while(rsTables.next()){
            String tableName = rsTables.getString("TABLE_NAME");
            String tableType = rsTables.getString("TABLE_TYPE");
            String catalog = rsTables.getString("TABLE_CAT");
            String schema = rsTables.getString("TABLE_SCHEM");
            System.out.println("Table " + tableName + " of type " + tableType);
            
            ResultSet rsColumns = meta.getColumns(catalog, schema, tableName, null );
            while(rsColumns.next()){
                String colName = rsColumns.getString("COLUMN_NAME");
                String colType = rsColumns.getString("TYPE_NAME");
                String colSize = rsColumns.getString("COLUMN_SIZE");
                String colNull = rsColumns.getString("IS_NULLABLE");
                
                System.out.println("  " + colName + "(" + colType + "," + colSize + "), nullable: " + colNull);
           }
            System.out.println();
            rsColumns.close();
        }
        rsTables.close();
        
    }

}
