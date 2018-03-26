package DDL;

// import
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import com.sleepycat.je.DatabaseEntry;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import DBQuery.DBQuery;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class TableSchema {
	public String tableName;
	public ArrayList<ColumnDefinition> ColDefList = new ArrayList<ColumnDefinition>();
	public ArrayList<String> PrimaryDef = new ArrayList<String>();
	public int numPrimary = 0;
	public ArrayList<ForeignKeyDefinition> ForeignDefList = new ArrayList<ForeignKeyDefinition>();


	// tableCheck
	private boolean isDuplicateColumnDefError = false;
	private boolean isCharLengthError = false;
	private boolean isTableExistenceError = false;

	// primaryCheck
	private boolean isNonPrimaryKeyDefError = false;
	private boolean isDuplicatePrimaryKeyDefError = false;
	private boolean isNonExistingColumnDefError = false;
	ArrayList<String> NonExistingColumnDefError = new ArrayList<>();

	// foreignCheck
	//private boolean isNonExistingColumnDefError =false;
	private boolean isReferenceTypeError = false;
	private boolean isReferenceNonPrimaryKeyError = false;
	private boolean isReferenceColumnExistenceError = false;
	private boolean isReferenceTableExistenceError = false;

	public TableSchema(String tableName){
		this.tableName = tableName;
	}

	//parse String data to construct Class TableSchema
	public TableSchema(String tableName, String data){
		this.tableName = tableName;
		String[] dataArray = data.split("#");
		String columnsData = dataArray[0];
		String primaryKeyData = dataArray[1];

		String[] columnArray = columnsData.split("@");
		for(String columnDef : columnArray){
			String[] specificDataArray = columnDef.split("/");
			ColumnDefinition ColDef = new ColumnDefinition(specificDataArray[0]);
			ColDef.type = Integer.parseInt(specificDataArray[1]);
			ColDef.charLength = Integer.parseInt(specificDataArray[2]);
			ColDef.notNull = specificDataArray[3].equals("1");
			ColDef.primaryKey = specificDataArray[4].equals("1");
			ColDef.foreignKey = specificDataArray[5].equals("1");

			this.ColDefList.add(ColDef);
		}

		Collections.addAll(this.PrimaryDef, primaryKeyData.split("@"));

		if(dataArray.length > 2) {
			String foreignKeyData = dataArray[2];
			String[] foreignDefArray = foreignKeyData.split("@");
			for(String foreignDef : foreignDefArray){
				ForeignKeyDefinition ForeignKeyDefinition = new ForeignKeyDefinition();
				String[] specificDataArray = foreignDef.split("%");
				Collections.addAll(ForeignKeyDefinition.referencingColumns , specificDataArray[0].split("/"));
				ForeignKeyDefinition.referencedTable = specificDataArray[1];
				Collections.addAll(ForeignKeyDefinition.referencedColumns , specificDataArray[2].split("/"));

				this.ForeignDefList.add(ForeignKeyDefinition);
			}
		}
	}

	public boolean hasError(){
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


		//columnNameSet initialize
		HashSet<String> columnNameSet = new HashSet<>();

		//add all columnName to Set in tableErrorCheck()
		tableErrorCheck(myDatabase, columnNameSet);
		primaryErrorCheck(columnNameSet);
		foreignErrorCheck(myDatabase, columnNameSet);

		myDbEnvironment.sync();
		if(myDatabase != null) myDatabase.close();
		if(myDbEnvironment != null) myDbEnvironment.close();

		if(isDuplicateColumnDefError|isCharLengthError|isTableExistenceError|isNonPrimaryKeyDefError|isDuplicatePrimaryKeyDefError
				|isNonExistingColumnDefError|isReferenceTypeError|isReferenceNonPrimaryKeyError
				|isReferenceColumnExistenceError|isReferenceTableExistenceError){
			return true;
		}
		else{
			updatePrimaryForeign();
		}

		return false;
	}

	private void tableErrorCheck(Database myDatabase, HashSet<String> columnNameSet){
		//column error check
		for(ColumnDefinition ColDef : ColDefList){
			// DuplicateColumnDefError check
			if(columnNameSet.add(ColDef.columnName) != true){
				isDuplicateColumnDefError = true;
			};

			// CharLengthError check
			if(ColDef.type==2){
				if(ColDef.charLength<1){
					isCharLengthError = true;
				}
			}
		}

		// TableExistenceError check
		try{
			Cursor cursor = myDatabase.openCursor(null,null);
			String data = DBQuery.findTableSchemaByKey(this.tableName, cursor);
			if(data != null){
				isTableExistenceError = true;
			}
			if(cursor != null){
				cursor.close();
			}
		}
		catch(DatabaseException de){
			de.printStackTrace();
		}
	}

	private void primaryErrorCheck(HashSet<String> columnNameSet){
		// DuplicatePrimaryKeyDefError check
		if(numPrimary>1){
			isDuplicatePrimaryKeyDefError = true;
		}
		if(numPrimary==0){
			isNonPrimaryKeyDefError = true;
		}

		// NonExistingColumnDefError check
		for(String column : PrimaryDef){
			if(!columnNameSet.contains(column)){
				isNonExistingColumnDefError = true;
				NonExistingColumnDefError.add(column);
			}
		}
	}

	private void foreignErrorCheck(Database myDatabase, HashSet<String> columnNameSet){
		for(ForeignKeyDefinition ForeignDef : ForeignDefList){
			Cursor cursor = null;
			DatabaseEntry foundKey;
			DatabaseEntry foundData;

			// NonExistingColumnDefError check
			for(String column : ForeignDef.referencingColumns){
				if(!columnNameSet.contains(column)){
					isNonExistingColumnDefError = true;
					NonExistingColumnDefError.add(column);
				}
			}
			if(isNonExistingColumnDefError){
				return;
			}
			// ReferenceTypeError(length) check
			if(ForeignDef.referencingColumns.size() != ForeignDef.referencedColumns.size()){
				isReferenceTypeError = true;
			}
			else{
				try{
					cursor = myDatabase.openCursor(null,null);
					String result = DBQuery.findTableSchemaByKey(ForeignDef.referencedTable, cursor);
					// ReferenceTableExistenceError check
					if(result==null){
						isReferenceTableExistenceError = true;
					}
					else{
						TableSchema ReferencedTable = new TableSchema(ForeignDef.referencedTable, result);
						for(int i = 0; i< ForeignDef.referencedColumns.size(); i++){
							String referencedColumn = ForeignDef.referencedColumns.get(i);
							ColumnDefinition referencedColDef = ReferencedTable.findColumn(referencedColumn);
							// ReferenceColumnExistenceError check
							if(referencedColDef == null){
								isReferenceColumnExistenceError = true;
							}
							else{
								// ReferenceNonPrimaryKeyError check
								if(!ReferencedTable.PrimaryDef.contains(referencedColumn)){
									isReferenceNonPrimaryKeyError = true;
								}
								// ReferenceTypeError check
								String referencingType = this.findColumn(ForeignDef.referencingColumns.get(i)).getType();
								String referencedType = referencedColDef.getType();
								if(!referencingType.equals(referencedType)){
									isReferenceTypeError = true;
								}
							}
						}
					}
					if(cursor != null){
						cursor.close();
					}
				}
				catch(DatabaseException de){
					de.printStackTrace();
				}
			}
		}
	}


	// update primaryKey/foreignKey in all ColumnDefinitions
	private void updatePrimaryForeign(){
		HashSet<String> referencingColumnSet = new HashSet<>();
		HashSet<String> primaryColumnSet = new HashSet<>();

		for(ForeignKeyDefinition ForeignDef : ForeignDefList){
			referencingColumnSet.addAll(ForeignDef.referencingColumns);
		}
		primaryColumnSet.addAll(PrimaryDef);

		for(ColumnDefinition ColDef : ColDefList){
			if(referencingColumnSet.contains(ColDef.columnName)){
				ColDef.foreignKey = true;
			}
			if(primaryColumnSet.contains(ColDef.columnName)){
				ColDef.primaryKey = true;
				ColDef.notNull = true;
			}
		}
	}

	//return a ColumnDefinition which is matched with the columnName
	private ColumnDefinition findColumn(String columnName){
		for(ColumnDefinition ColDef : ColDefList){
			if(ColDef.columnName.equals(columnName)){
				return ColDef;
			}
		}
		return null;
	}

	public boolean hasReferences(String tableName){
		for(ForeignKeyDefinition ForeignDef : ForeignDefList){
			if(tableName.equals(ForeignDef.referencedTable)){
				return true;
			}
		}
		return false;
	}

	//convert this class to String using delimiter
	public String toString(){
		String description = "";
		int cnt = 0;
		for(ColumnDefinition ColDef : ColDefList){
			if(cnt != 0){
				description+="@";
			}
			description+=ColDef.toString();
			cnt++;
		}
		description+="#";

		this.sortPrimary();
		cnt = 0;
		for(String primaryKey : PrimaryDef){
			if(cnt!=0){
				description+="@";
			}
			description+=primaryKey;
			cnt++;
		}
		description+="#";

		cnt = 0;
		for(ForeignKeyDefinition ForeignDef : ForeignDefList){
			if(cnt!=0){
				description +="@";
			}
			description += ForeignDef.toString();
			cnt++;
		}

		return description;
	}

	private void sortPrimary(){
		ArrayList<String> SortedPrimary = new ArrayList<>();
		for(ColumnDefinition Column : this.ColDefList){
			if(Column.primaryKey){
				SortedPrimary.add(Column.columnName);
			}
		}
		this.PrimaryDef = SortedPrimary;
	}

	//print a message which describes this class
	public void desc(){
		System.out.println("-------------------------------------------------");
		System.out.println("table_name ["+ this.tableName +"]");
		System.out.println("column_name\t\ttype\t\tnull\t\tkey");
		for(ColumnDefinition ColDef : this.ColDefList){
			String description = ColDef.columnName + "\t\t";
			switch(ColDef.type){
			case 1:
				description+="int\t\t";
				break;
			case 2:
				description+="char("+ColDef.charLength+")\t\t";
				break;
			case 3:
				description+="date\t\t";
				break;
			}
			description += (ColDef.notNull)? "N\t\t" : "T\t\t";
			int cnt = 0;
			if(ColDef.primaryKey){
				description+="PRI";
				cnt++;
			}
			if(ColDef.foreignKey){
				if(cnt == 1){
					description+="/";
				}
				description+="FOR";
			}
			System.out.println(description);
		}
		System.out.println("-------------------------------------------------");
	}

	void printErrorMessages(){
		// table check
		if(isDuplicateColumnDefError){
			System.out.println(Messages.DuplicateColumnDefError);
		}
		if(isCharLengthError){
			System.out.println(Messages.CharLengthError);
		}
		if(isTableExistenceError){
			System.out.println(Messages.TableExistenceError);
		}

		// primary check
		if(isNonPrimaryKeyDefError){
			System.out.println(Messages.NonPrimaryKeyDefError);
		}
		if(isDuplicatePrimaryKeyDefError){
			System.out.println(Messages.DuplicatePrimaryKeyDefError);
		}
		if(isNonExistingColumnDefError){
			for(String columnName : this.NonExistingColumnDefError){
				System.out.println(Messages.NonExistingColumnDefError(columnName));
			}
		}

		// foreign check
		if(isReferenceTypeError){
			System.out.println(Messages.ReferenceTypeError);
		}
		if(isReferenceNonPrimaryKeyError){
			System.out.println(Messages.ReferenceNonPrimaryKeyError);
		}
		if(isReferenceColumnExistenceError){
			System.out.println(Messages.ReferenceColumnExistenceError);
		}
		if(isReferenceTableExistenceError){
			System.out.println(Messages.ReferenceTableExistenceError);
		}
	}

}
