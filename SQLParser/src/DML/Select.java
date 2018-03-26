package DML;

import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import DBQuery.DBQuery;
import DDL.ColumnDefinition;
import DDL.TableSchema;
import DML.Where.*;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 * Created by 1997j on 2017-11-30.
 */
public class Select {
    public ArrayList<SelectedColumn> SelectedColList = new ArrayList<>();
    public ArrayList<ReferedTable> ReferedTableList = new ArrayList<>();
    public Where Where = new Where();

    private ArrayList<Record> RecordList;
    private ArrayList<ArrayList<Record>> resultRecordLists = new ArrayList<>();
    private ArrayList<TableSchema> SchemaList = new ArrayList<>();
    private ArrayList<String> IndexList = new ArrayList<>();
    private ArrayList<String> printColList = new ArrayList<>();

    public void execute(){
        // check SelectColumnResolveError
        if(!this.updateSchemaCheckWhere()){
            return;
        }
        if(SelectedColList == null){
            SelectedColList = new ArrayList<>();
            for(int i = 0; i<SchemaList.size(); i++){
                TableSchema Schema = SchemaList.get(i);
                ReferedTable RefTable = ReferedTableList.get(i);
                for(ColumnDefinition ColDef : Schema.ColDefList){
                    SelectedColList.add(new SelectedColumn(RefTable.newTableName, ColDef.columnName, ColDef. columnName));
                }
            }
        }

        //find index of columns
        for(SelectedColumn selectedColumn : SelectedColList){
            if(selectedColumn.newTableName!=null){
                int preSize = IndexList.size();

                for(int i = 0; i<SchemaList.size(); i++){
                    TableSchema Schema = SchemaList.get(i);

                    if(Schema.tableName.equals(selectedColumn.newTableName)){
                        for(int j =0; j<Schema.ColDefList.size(); j ++){
                            ColumnDefinition ColDef = Schema.ColDefList.get(j);
                            if(ColDef.columnName.equals(selectedColumn.columnName)){
                                String index = i + "#" + j;
                                IndexList.add(index);
                                this.printColList.add(selectedColumn.newColumnName);
                            }
                        }
                    }
                }
                int afterSize = IndexList.size();
                //SelectColumnResolveError
                if(afterSize-preSize!=1){
                    System.out.println(Messages.SelectColumnResolveError(selectedColumn.columnName));
                    return;
                }
            }
            else{
                int preSize = IndexList.size();
                for(int i = 0; i<SchemaList.size(); i++){
                    TableSchema Schema = SchemaList.get(i);
                    for(int j =0; j<Schema.ColDefList.size(); j ++){
                        ColumnDefinition ColDef = Schema.ColDefList.get(j);
                        if(ColDef.columnName.equals(selectedColumn.columnName)){
                            String index = i + "#" + j;
                            IndexList.add(index);
                            this.printColList.add(selectedColumn.newColumnName);
                        }
                    }
                }
                int afterSize = IndexList.size();
                //SelectColumnResolveError
                if(afterSize-preSize!=1){
                    System.out.println(Messages.SelectColumnResolveError(selectedColumn.columnName));
                    return;
                }
            }
        }
        HashSet<String> toPrintdata = new HashSet<>();
        for(ArrayList<Record> RecList : this.resultRecordLists){
            String result = "";
            for(String index : this.IndexList){
                String[] indexs = index.split("#");
                int i = Integer.parseInt(indexs[0]);
                int j = Integer.parseInt(indexs[1]);
                result += RecList.get(i).ValueList.get(j).getValue();
                result +="'";
            }
            toPrintdata.add(result);
        }
        printSelect(toPrintdata);
    }

    private boolean updateSchemaCheckWhere(){
        boolean noError = true;
        // Environment & Database define
        Environment myDbEnvironment = null;
        ArrayList<Database> myDBList = new ArrayList<Database>();
        ArrayList<Cursor> cursorList = new ArrayList<Cursor>();

        /* OPENING DB */

        // Open Database Environment or if not, create one.
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        myDbEnvironment = new Environment(new File("db/"), envConfig);

        // Open Database or if not, create one.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setSortedDuplicates(true);

        //read schemas
        for(ReferedTable RefTable : ReferedTableList){
            String tableName = RefTable.tableName;
            String schema = DBQuery.findTableSchemaByKey(tableName);
            if(schema ==null){
                System.out.println(Messages.SelectTableExistenceError(tableName));
                noError = false;
                break;
            }
            SchemaList.add(new TableSchema(RefTable.newTableName, schema));
            Database myDB =  myDbEnvironment.openDatabase(null, "data#"+tableName, dbConfig);
            myDBList.add(myDB);
            cursorList.add(myDB.openCursor(null, null));
        }
        if(noError) {
            try {
                ArrayList<DatabaseEntry> foundKeyList = new ArrayList<DatabaseEntry>();
                ArrayList<DatabaseEntry> foundDataList = new ArrayList<DatabaseEntry>();
                for (int i = 0; i < cursorList.size(); i++) {
                    foundKeyList.add(new DatabaseEntry());
                    foundDataList.add(new DatabaseEntry());
                }
                if (this.cursorListGetFirst(foundKeyList, foundDataList, cursorList)) {
                    do {
                        RecordList = new ArrayList<>();
                        for (DatabaseEntry foundData : foundDataList) {
                            RecordList.add(new Record(new String(foundData.getData(), "UTF-8")));
                        }
                        int result = Where.execute(ReferedTableList, RecordList);
                        // if -1 then it has error
                        if (result == -1) {
                            noError = false;
                            break;
                        } else if (result == 1) {
                            resultRecordLists.add(RecordList);
                        }

                        if(!noError){
                            break;
                        }
                    } while (this.cursorListGetNext(foundKeyList, foundDataList, cursorList));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        for(Cursor cursor : cursorList){
            cursor.close();
        }
        for(Database DB : myDBList){
            if(DB != null) DB.close();
        }
        myDbEnvironment.sync();
        if(myDbEnvironment != null) myDbEnvironment.close();

        return noError;
    }

    
    private void printSelect(HashSet<String> RecordDatas){
        ArrayList<Integer> lengthList = new ArrayList<>();
        String line = "";
        for(String newColName : this.printColList){
            lengthList.add(newColName.length());
        }
        ArrayList<ArrayList<String>> toPrintdata = new ArrayList<>();
        //if value's length is longer than priors, then update length
        for(String record : RecordDatas){
            String[] colDatas = record.split("'");
            ArrayList<String> colDataList = new ArrayList<>(Arrays.asList(colDatas));
            for(int i = 0; i< colDataList.size(); i++){
                String colData = colDataList.get(i);
                if(lengthList.get(i)<colData.length()){
                    lengthList.set(i, colData.length());
                }
            }
            toPrintdata.add(colDataList);
        }

        for(Integer length : lengthList){
            line += "+-";
            for(int i =0; i< length; i++){
                line += "-";
            }
            line += "-";
        }
        line += "+";
        System.out.println(line);


        printRecord(lengthList, this.printColList);

        System.out.println(line);
        for(ArrayList<String> colDatas : toPrintdata){
            printRecord(lengthList, colDatas);
        }
        System.out.println(line);
    }
    
    
    private void printRecord(ArrayList<Integer> lengthList, ArrayList<String> colDatas){
        String record = "";
        for(int i = 0; i<colDatas.size(); i++){
            String colData = colDatas.get(i);
            int length = lengthList.get(i);
            record +="| ";
            record += colData;
            int colLength = colData.length();
            for(int j = 0; j< length - colLength; j++){
                record += " ";
            }
            record += " ";
        }
        record += "|";
        System.out.println(record);
    }

    private boolean cursorListGetFirst(ArrayList<DatabaseEntry> foundKeyList, ArrayList<DatabaseEntry> foundDataList, ArrayList<Cursor> cursorList){
        int i = 0;
        for(Cursor cursor :cursorList){
            if (cursor.getFirst(foundKeyList.get(i), foundDataList.get(i), LockMode.DEFAULT) != OperationStatus.SUCCESS) {
                return false;
            }
            i++;
        }
        return true;
    }

    private boolean cursorListGetNext(ArrayList<DatabaseEntry> foundKeyList, ArrayList<DatabaseEntry> foundDataList, ArrayList<Cursor> cursorList ){
        int i = 0;
        for(Cursor cursor :cursorList){
            if (cursor.getNext(foundKeyList.get(i), foundDataList.get(i), LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                return true;
            }
            else{
                cursor.getFirst(foundKeyList.get(i), foundDataList.get(i), LockMode.DEFAULT);
            }
            i++;
        }
        return false;
    }


}
