package DML;

/**
 * Created by 1997j on 2017-11-28.
 */
public class Messages {

    public static String InsertColumnExistenceError(String colName){
        return "Insertion has failed: '[" + colName + "]' does not exist";
    }

    public static String InsertColumnNonNullableError(String colName){
        return "Insertion has failed: '[" + colName + "]' is not nullable";
    }

    public static String DeleteResult(int count){
        return "[" + count + "] row(s) are deleted";
    }

    public static String DeleteReferentialIntegrityPassed(int count){
        return "[" + count + "] row(s) are not deleted due to referential integrity";
    }

    public static String SelectTableExistenceError(String tableName){
        return "Selection has failed: '[" + tableName + "]' does not exist";
    }

    public static String SelectColumnResolveError(String colName){
        return "Selection has failed: fail to resolve '[" + colName + "]'";
    }

    public static final String InsertResult = "The row is inserted";
    public static final String InsertDuplicatePrimaryKeyError = " Insertion has failed: Primary key duplication";
    public static final String InsertReferentialIntegrityError = "Insertion has failed: Referential integrity violation";
    public static final String InsertTypeMismatchError = "Insertion has failed: Types are not matched";
    public static final String NoSuchTable = "No such table";
    public static final String WhereIncomparableError = "Where clause try to compare incomparable values";
    public static final String WhereTableNotSpecified = "Where clause try to reference tables which are not specified";
    public static final String WhereColumnNotExist = "Where clause try to reference non existing column";
    public static final String WhereAmbiguousReference = "Where clause contains ambiguous reference";
}
