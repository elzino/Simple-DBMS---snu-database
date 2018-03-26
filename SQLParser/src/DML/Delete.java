package DML;

import java.io.File;
import java.util.ArrayList;

import DBQuery.DBQuery;
import DDL.ColumnDefinition;
import DDL.ForeignKeyDefinition;
import DDL.TableSchema;
import DML.Where.Where;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 * Created by 1997j on 2017-12-01.
 */
public class Delete {
    String tableName;
    public Where WhereClause = new Where();

    private int deleteNum;
    private int deleteError;

    private String key;


    public Delete (String tableName){
        this.tableName = tableName;
    }

    public void execute(){
        boolean whereError = false;

        //check table existence
        String schema = DBQuery.findTableSchemaByKey(this.tableName);
        if(schema==null){
            // NoSuchTable
            System.out.println(Messages.NoSuchTable);
            return;
        }

        this.deleteNum = 0;
        this.deleteError = 0;

        String result = null;

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
        myDatabase = myDbEnvironment.openDatabase(null, "data#" + this.tableName, dbConfig);

        Cursor cursor = null;
        try {
            cursor = myDatabase.openCursor(null, null);
            DatabaseEntry foundKey = new DatabaseEntry();
            DatabaseEntry foundData = new DatabaseEntry();
            if (cursor.getFirst(foundKey, foundData, LockMode.DEFAULT) != OperationStatus.SUCCESS) {
            }
            else {
                do {
                    key = new String(foundKey.getData(), "UTF-8");
                    result = new String(foundData.getData(), "UTF-8");
                    Record Record = new Record(result);

                    //check where
                    ArrayList<ReferedTable> RefTableList = new ArrayList<>();
                    RefTableList.add(new ReferedTable(this.tableName, this.tableName));
                    ArrayList<Record> RecordList = new ArrayList<>();
                    RecordList.add(Record);
                    int whereResult = WhereClause.execute(RefTableList, RecordList);
                    if(whereResult == -1){
                        whereError = true;
                        break;
                    }
                    if (whereResult == 1){
                        this.deleteRecord(Record, cursor);
                    }
                }
                while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        myDbEnvironment.sync();
        if (myDatabase != null) myDatabase.close();
        if (myDbEnvironment != null) myDbEnvironment.close();

        if(!whereError) {
            System.out.println(Messages.DeleteResult(this.deleteNum));
            if (this.deleteError != 0) {
                System.out.println(Messages.DeleteReferentialIntegrityPassed(this.deleteError));
            }
        }
    }

    private void deleteRecord(Record Record, Cursor cursor){
        // referenced
        if(Record.isReferenced()){
            if(Record.isNotNullReferenced()){
                //DeleteReferentialIntegrityPassed
                this.deleteError++;
            }
            else{
                for(ForeignRecordData ForeignData : Record.ReferencedData){
                    DBQuery.upDateRecordNullByKeyIndex(
                            ForeignData.referencingTableName, ForeignData.primaryKey, ForeignData.ForeignColumnIndexToDelete
                    );
                }
                this.deleteNum++;
                this.updateReferencingData(Record);
                cursor.delete();
            }
        }
        // not referenced
        else{
            this.deleteNum++;
            this.updateReferencingData(Record);
            cursor.delete();
        }
    }

    private void updateReferencingData(Record Record){
        // referencing 하는 것들 찾아가서  referenced 되는 데이타 다 지운다
        ArrayList<Integer> IndexList;
        TableSchema Schema = new TableSchema(this.tableName, DBQuery.findTableSchemaByKey(this.tableName));
        for(ForeignKeyDefinition ForDef : Schema.ForeignDefList){
            IndexList = this.findReferedPriKeyIndex(this.tableName, ForDef.referencedTable);
            String foreignkey = Record.convertIndexToKey(IndexList);
            if(foreignkey==null){
                continue;
            }
            Record ReferedRecord = new Record(DBQuery.findRecordByKey(ForDef.referencedTable, foreignkey));
            ArrayList<ForeignRecordData> ReferencedData = ReferedRecord.ReferencedData;
            int toDelete = -1;
            for(int i = 0; i<ReferencedData.size(); i++){
                ForeignRecordData ForeignData = ReferencedData.get(i);
                if(ForeignData.referencingTableName.equals(this.tableName)&&ForeignData.primaryKey.equals(this.key)){
                    toDelete = i;
                    break;
                }
            }
            ReferencedData.remove(toDelete);
            if(ReferencedData.size() == 0){
                ReferedRecord.setIsReferenced(false);
            }
            DBQuery.writeRecordByKey(ForDef.referencedTable, foreignkey, ReferedRecord.toString());
        }
    }

    private ArrayList<Integer> findReferedPriKeyIndex(String referencingTable, String referencedTable){
    	//find indexes using ForeignKeyDefinition
        ArrayList<Integer> IndexList = new ArrayList<>();
        TableSchema Referencing = new TableSchema (referencingTable, DBQuery.findTableSchemaByKey(referencingTable));
        TableSchema Referenced = new TableSchema (referencedTable, DBQuery.findTableSchemaByKey(referencedTable));
        ForeignKeyDefinition ReferencingForDef = null;

        for(ForeignKeyDefinition ForDef : Referencing.ForeignDefList){
            if(ForDef.referencedTable.equals(referencedTable)){
                ReferencingForDef = ForDef;
                break;
            }
        }
        for(String primaryColumn : Referenced.PrimaryDef){
            int colIndex = ReferencingForDef.referencedColumns.indexOf(primaryColumn);
            String referencingColName = ReferencingForDef.referencingColumns.get(colIndex);
            for(int i= 0 ; i<Referencing.ColDefList.size() ; i++){
                ColumnDefinition Coldef = Referencing.ColDefList.get(i);
                if(Coldef.columnName.equals(referencingColName)){
                    IndexList.add(i);
                    break;
                }
            }
        }
        return IndexList;
    }
}
