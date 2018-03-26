package DDL;

// import
import DBQuery.DBQuery;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


import com.sleepycat.je.DatabaseEntry;


import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DDLQuery {
    public static void createTableQuery(TableSchema TableSchema){
        if(!TableSchema.hasError()){
            // Environment & Database define
            Environment myDbEnvironment = null;
            Database myDatabase = null;

            /* OPENING DB */

            // Open Database Environment or if not, create one.
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            myDbEnvironment = new Environment(new File("db/"), envConfig);

            // Open Database or if not, create one.
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            dbConfig.setSortedDuplicates(true);
            myDatabase = myDbEnvironment.openDatabase(null, "tableSchema", dbConfig);

            Cursor cursor = null;
            DatabaseEntry key;
            DatabaseEntry data;
            
            try{
            	cursor = myDatabase.openCursor(null, null);
            	key = new DatabaseEntry(TableSchema.tableName.getBytes("UTF-8"));
            	data = new DatabaseEntry(TableSchema.toString().getBytes("UTF-8"));
            	cursor.put(key, data);
            	cursor.close();
            	System.out.println(Messages.CreateTableSuccess(TableSchema.tableName));
            }catch(DatabaseException de){
            	de.printStackTrace();
            }catch(UnsupportedEncodingException e){
            	e.printStackTrace();
            }
            myDbEnvironment.sync();
            if(myDatabase != null) myDatabase.close();
            if(myDbEnvironment != null) myDbEnvironment.close();
           
        }
        else{
        	TableSchema.printErrorMessages();
        }
    }
    
    public static void dropTableQuery(String tableName){
        boolean noError = true;
      
        // Environment & Database define
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        myDatabase = myDbEnvironment.openDatabase(null, "tableSchema", dbConfig);

        try{
            Cursor cursor = myDatabase.openCursor(null, null);
            String result = DBQuery.findTableSchemaByKey(tableName, cursor);
            if(result == null){
                System.out.println(Messages.NoSuchTable);
                noError = false;
            }
            else{
            	// should implement DropReferencedTableError
                boolean isReferenced = false;
                Cursor cursor2 = myDatabase.openCursor(null,null);
                DatabaseEntry key = new DatabaseEntry();
                DatabaseEntry data = new DatabaseEntry();

                cursor2.getFirst(key, data, LockMode.DEFAULT);
                do{
                    TableSchema TableIterator = new TableSchema(new String(key.getData(), "UTF-8"), new String(data.getData(), "UTF-8"));
                    if(TableIterator.hasReferences(tableName)){
                        noError = false;
                        System.out.println(Messages.DropReferencedTableError(tableName));
                        break;
                    }
                }while( cursor2.getNext(key,data,LockMode.DEFAULT) == OperationStatus.SUCCESS);
                cursor2.close();

            	if(noError){
                    cursor.delete();
                    System.out.println(Messages.DropSuccess(tableName));
            	}
            }
            cursor.close();
        }catch(DatabaseException de){
        	de.printStackTrace();
        }catch(UnsupportedEncodingException e){
        	e.printStackTrace();
        }

        myDbEnvironment.sync();
        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();
    }


    public static void descQuery(String tableName){
        // Environment & Database define
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        myDatabase = myDbEnvironment.openDatabase(null, "tableSchema", dbConfig);

        try{
            Cursor cursor = myDatabase.openCursor(null, null);
            String result = DBQuery.findTableSchemaByKey(tableName, cursor);
            if(result == null){
                System.out.println(Messages.NoSuchTable);
            }
            else{
                TableSchema schema = new TableSchema(tableName,result);
                schema.desc();
            }
            cursor.close();
        }catch(DatabaseException de){
        	de.printStackTrace();
        }

        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();
    }

    public static void showTablesQuery(){
        // Environment & Database define
        Environment myDbEnvironment = null;
        Database myDatabase = null;

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);
        myDatabase = myDbEnvironment.openDatabase(null, "tableSchema", dbConfig);

        try{
            if(myDatabase.count()==0){
                System.out.println(Messages.ShowTablesNoTable);
            }
            else{
                Cursor cursor = myDatabase.openCursor(null, null);
                DatabaseEntry key = new DatabaseEntry();
                DatabaseEntry data = new DatabaseEntry();

                cursor.getFirst(key, data, LockMode.DEFAULT);

                System.out.println("----------------");
                do{
                    String tableName = new String(key.getData(), "UTF-8");
                    System.out.println(tableName);
                }while( cursor.getNext(key,data,LockMode.DEFAULT) == OperationStatus.SUCCESS);
                System.out.println("----------------");
                cursor.close();
            }
        }catch(DatabaseException de){
        	de.printStackTrace();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        if(myDatabase != null) myDatabase.close();
        if(myDbEnvironment != null) myDbEnvironment.close();
    }
    
}
