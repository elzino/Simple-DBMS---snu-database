package DDL;

public class Messages {

	public static String CreateTableSuccess(String tableName){
		return "'" + tableName + "' table is created";
	}
	public static String NonExistingColumnDefError(String colName){
		return "Create table has failed: '"+ colName+ "' does not exists in column definition";
	}
	public static String DropSuccess(String tableName){
		return "'"+ tableName + "' table is dropped";
	}
	public static String DropReferencedTableError(String tableName){
		return "Drop table has failed: '" + tableName + "' is referenced by other table";
	}
	
	public static final String SyntaxError ="Syntax error";	
	public static final String DuplicateColumnDefError ="Create table has failed: column definition is duplicated";
	public static final String DuplicatePrimaryKeyDefError ="Create table has failed: primary key definition is duplicated";
	public static final String ReferenceTypeError ="Create table has failed: foreign key references wrong type";
	public static final String ReferenceNonPrimaryKeyError ="Create table has failed: foreign key references non primary key column";
	public static final String ReferenceColumnExistenceError ="Create table has failed: foreign key references non existing column";
	public static final String ReferenceTableExistenceError ="Create table has failed: foreign key references non existing table";
	public static final String TableExistenceError ="Create table has failed: table with the same name already exists";
	public static final String ShowTablesNoTable ="There is no table";
	public static final String NoSuchTable ="No such table";
	public static final String CharLengthError ="Char length should be over 0";
	public static final String NonPrimaryKeyDefError ="Create table has failed: primary key definition does not exists";


}
