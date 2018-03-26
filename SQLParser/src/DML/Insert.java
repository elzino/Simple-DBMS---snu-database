package DML;

import java.util.ArrayList;
import java.util.Collections;

import DBQuery.DBQuery;
import DDL.ColumnDefinition;
import DDL.ForeignKeyDefinition;
import DDL.TableSchema;


public class Insert {
	public String tableName;
	public ArrayList<String> ColumnNameList = null;
	public ArrayList<Value> ValueList;

	// used in checkForeginError method
	private ArrayList<ForeignRecordData> ForeignList = new ArrayList<>();

	public Insert(String tableName){
		this.tableName = tableName;
	}

	//for debugging convert return type from void to String
	public void execute(){
		String schema = DBQuery.findTableSchemaByKey(this.tableName);
		if(schema==null){
			// NoSuchTable
			System.out.println(Messages.NoSuchTable);
			return;
		}
		TableSchema TSchema = new TableSchema(this.tableName, schema);
		ArrayList<ColumnDefinition> ColDefList = TSchema.ColDefList;

		if(ColumnNameList == null){
			if(ValueList.size() != ColDefList.size()){
				// InsertTypeMismatchError
				System.out.println(Messages.InsertTypeMismatchError);
				return;
			}
			for(int i = 0; i<ValueList.size(); i++){
				Value value = ValueList.get(i);
				value.upDateColDef(ColDefList.get(i));
				//check InsertTypeMismatchError InsertColumnNonNullableError
				if(value.hasTypeError()){
					return;
				}
			}
			//check primary constraint
			String primaryTypeValue = valueToPrimaryKey(ValueList);
			if(hasPrimaryDuplicateError(primaryTypeValue)){
				return;
			}
			//check foreign constraint
			if(checkForeginError(ColDefList ,TSchema.ForeignDefList,this.ValueList, primaryTypeValue)){
				return;
			}
			write(primaryTypeValue);
		}
		// has ColumnNameList
		else{
			if(ValueList.size() != ColumnNameList.size()){
				System.out.println(Messages.InsertTypeMismatchError);
				return;
			}

			//update ValueList to fill with null Value
			ArrayList<Value> newValueList = new ArrayList<>();
			for(ColumnDefinition ColDef : ColDefList){
				int index = this.ColumnNameList.indexOf(ColDef.columnName);
				if(index==-1){
					newValueList.add(new Value(null,4));
				}
				else{
					newValueList.add(ValueList.get(index));
					this.ValueList.remove(index);
					this.ColumnNameList.remove(index);
				}
			}
			if(ColumnNameList.size()!=0){
				for(String s : ColumnNameList) {
					System.out.println(Messages.InsertColumnExistenceError(s));
				}
				return;
			}
			this.ValueList = newValueList;

			//identical with above code
			for(int i = 0; i<ValueList.size(); i++){
				Value value = ValueList.get(i);
				value.upDateColDef(ColDefList.get(i));
				//check InsertTypeMismatchError InsertColumnNonNullableError
				if(value.hasTypeError()){
					return;
				}
			}
			//check primary constraint
			String primaryTypeValue = valueToPrimaryKey(ValueList);
			if(hasPrimaryDuplicateError(primaryTypeValue)){
				return;
			}
			//check foreign constraint
			if(checkForeginError(ColDefList, TSchema.ForeignDefList,this.ValueList ,primaryTypeValue)){
				return;
			}
			write(primaryTypeValue);

		}
	}

	String valueToPrimaryKey(ArrayList<Value> ValueList){
		String primaryTypeValue = "";
		for(Value value : ValueList){
			if(value.primaryKey){
				primaryTypeValue += value.toString();
			}
		}
		return primaryTypeValue;
	}

	boolean hasPrimaryDuplicateError(String primaryTypeValue){
		String record = DBQuery.findRecordByKey(this.tableName, primaryTypeValue);
		if(record != null){
			System.out.println(Messages.InsertDuplicatePrimaryKeyError);
			return true;
		}
		return false;
	}

	boolean checkForeginError(ArrayList<ColumnDefinition> ColDefList, ArrayList<ForeignKeyDefinition> ForeignDefList, ArrayList<Value> ValueList, String referencingPrimaryKey){
		for(ForeignKeyDefinition ForeignDef : ForeignDefList){
			String referencedPrimaryTypeValue = "";
			boolean notNull = false;
			ArrayList<Integer> IndexList = new ArrayList<>();

			TableSchema Schema = new TableSchema(ForeignDef.referencedTable, DBQuery.findTableSchemaByKey(ForeignDef.referencedTable));
			for(String primary : Schema.PrimaryDef){
				int colIndex = ForeignDef.referencedColumns.indexOf(primary);
				String referencingColName = ForeignDef.referencingColumns.get(colIndex);
				for(int i = 0; i< ValueList.size() ; i++){
					Value value = ValueList.get(i);
					ColumnDefinition ColDef = ColDefList.get(i);
					if(ColDef.columnName.equals(referencingColName)){
						referencedPrimaryTypeValue += value.toString();
						if(value.notNull){
							notNull = true;
						}
						IndexList.add(i);
						break;
					}
				}
			}
			Collections.sort(IndexList);

			String record = DBQuery.findRecordByKey(ForeignDef.referencedTable, referencedPrimaryTypeValue);

			// 해당 foreign key 를 가지는 레코드 없는 것 -> 오류
			if(record == null){
				System.out.println(Messages.InsertReferentialIntegrityError);
				return true;
			}
			this.ForeignList.add(
					new ForeignRecordData(ForeignDef.referencedTable, referencedPrimaryTypeValue, this.tableName, referencingPrimaryKey , notNull, IndexList)
			);
		}
		return false;
	}

	private void write(String key){
		String record = "";
		for(Value value : this.ValueList){
			record += value.toString();
		}
		DBQuery.writeRecordByKey(this.tableName, key, record);
		for(ForeignRecordData ForData : this.ForeignList){
			ForData.writeDB();
		}
		System.out.println(Messages.InsertResult);
	}
}
