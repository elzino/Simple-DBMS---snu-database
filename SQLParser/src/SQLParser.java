/* Generated By:JavaCC: Do not edit this line. SQLParser.java */
//import
import java.io.IOException;
import java.util.ArrayList;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import java.io.File;


import DDL.*;
import DML.*;
import DBQuery.*;
import DML.Where.*;

public class SQLParser implements SQLParserConstants {
  public static final int PRINT_SYNTAX_ERROR = 0;
  public static final int CREATE_TABLE_QUERY = 1;
  public static final int DROP_TABLE_QUERY = 2;
  public static final int DESC_QUERY = 3;
  public static final int SHOW_TABLES_QUERY = 4;
  public static final int SELECT_QUERY = 5;
  public static final int INSERT_QUERY = 6;
  public static final int DELETE_QUERY = 7;

  //variables for create table query
  static String globalTableName = null; // 쿼리 바�? ?�� ?��?��?���?
  static TableSchema TableSchema = null;
  static ColumnDefinition ColDef = null;
  static ForeignKeyDefinition ForeignDef = null;
  static Insert Insert = null;
  static Delete Delete = null;
  static Select Select = null;

  public static void main(String args[]) throws ParseException
  {

    SQLParser parser = new SQLParser(System.in);
    System.out.print("DB_2016-12155> ");

    while (true)
    {
      try
      {
        parser.command();
      }
      catch (Exception e)
      {
        printMessage(PRINT_SYNTAX_ERROR);
        SQLParser.ReInit(System.in);
      }
    }
  }

  //?��?��미터�? ?��?��?��?�� int 값에 ?��?�� ?��?��?��?�� 문구�? 출력?��?��.
  public static void printMessage(int q)
  {
    switch(q)
    {
      case PRINT_SYNTAX_ERROR:
        System.out.println("Syntax error");
        break;
      case CREATE_TABLE_QUERY:
        DDLQuery.createTableQuery(TableSchema);
        TableSchema = null;
        break;
      case DROP_TABLE_QUERY:
        DDLQuery.dropTableQuery(globalTableName);
        globalTableName = null;
        break;
      case DESC_QUERY:
        DDLQuery.descQuery(globalTableName);
        globalTableName = null;
        break;
      case SHOW_TABLES_QUERY:
        DDLQuery.showTablesQuery();
        break;
      case INSERT_QUERY:
        Insert.execute();
        Insert = null;
        break;
      case DELETE_QUERY:
        Delete.execute();
        Delete = null;
        break;
      case SELECT_QUERY:
        Select.execute();
        Select = null;
        break;
    }
    System.out.print("DB_2016-12155> ");
  }

  static final public void command() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE_TABLE:
    case DROP_TABLE:
    case DESC:
    case SHOW_TABLES:
    case SELECT:
    case INSERT:
    case DELETE:
      queryList();
      break;
    case EXIT:
      jj_consume_token(EXIT);
      jj_consume_token(SEMICOLON);
      // Environment & Database define
      Environment myDbEnvironment = null;
      // Open Database Environment or if not, create one.
      EnvironmentConfig envConfig = new EnvironmentConfig();
      envConfig.setAllowCreate(true);
      myDbEnvironment = new Environment(new File("db/"), envConfig);
      myDbEnvironment.sync();
      if(myDbEnvironment != null) myDbEnvironment.close();

      System.exit(0);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void queryList() throws ParseException {
  int q;
    label_1:
    while (true) {
      q = query();
      jj_consume_token(SEMICOLON);
      //?��롬포?��?? ?��?��?�� 맞는 메세�?�? 출력?��?��.
      printMessage(q);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CREATE_TABLE:
      case DROP_TABLE:
      case DESC:
      case SHOW_TABLES:
      case SELECT:
      case INSERT:
      case DELETE:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
    }
  }

//?��?��?�� ?��?��?�� ?��?��?��?�� 쿼리�? ?��???��?�� int 값을 리턴?��?��.
  static final public int query() throws ParseException {
  int q;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE_TABLE:
      createTableQuery();
      q = CREATE_TABLE_QUERY;
      break;
    case DROP_TABLE:
      dropTableQuery();
           q = DROP_TABLE_QUERY;
      break;
    case DESC:
      descQuery();
           q = DESC_QUERY;
      break;
    case SHOW_TABLES:
      showTablesQuery();
           q = SHOW_TABLES_QUERY;
      break;
    case SELECT:
      selectQuery();
           q = SELECT_QUERY;
      break;
    case INSERT:
      insertQuery();
           q = INSERT_QUERY;
      break;
    case DELETE:
      deleteQuery();
           q = DELETE_QUERY;
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

  static final public void createTableQuery() throws ParseException {
    jj_consume_token(CREATE_TABLE);
    globalTableName = tableName();
    TableSchema = new TableSchema(globalTableName);
    globalTableName = null;
    tableElementList();
  }

  static final public void tableElementList() throws ParseException {
    jj_consume_token(LEFT_PAREN);
    tableElement();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_2;
      }
      jj_consume_token(COMMA);
      tableElement();
    }
    jj_consume_token(RIGHT_PAREN);
  }

  static final public void tableElement() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
      columnDefinition();
      break;
    case PRIMARY_KEY:
    case FOREIGN_KEY:
      tableConstraintDefinition();
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void columnDefinition() throws ParseException {
  String s;
  int i;
    s = columnName();
     ColDef = new ColumnDefinition(s);
    dataType();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT_NULL:
      jj_consume_token(NOT_NULL);
      ColDef.notNull = true;
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    TableSchema.ColDefList.add(ColDef);
    ColDef = null;
  }

  static final public void tableConstraintDefinition() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PRIMARY_KEY:
      primaryKeyConstraint();
      break;
    case FOREIGN_KEY:
      referentialConstraint();
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void primaryKeyConstraint() throws ParseException {
    jj_consume_token(PRIMARY_KEY);
    TableSchema.PrimaryDef = columnNameList();
    TableSchema.numPrimary++;
  }

  static final public void referentialConstraint() throws ParseException {
  ForeignDef = new ForeignKeyDefinition();
    jj_consume_token(FOREIGN_KEY);
    ForeignDef.referencingColumns = columnNameList();
    jj_consume_token(REFERENCES);
    ForeignDef.referencedTable = tableName();
    ForeignDef.referencedColumns = columnNameList();
    TableSchema.ForeignDefList.add(ForeignDef);
    ForeignDef = null;
  }

  static final public ArrayList<String> columnNameList() throws ParseException {
  String s;
  ArrayList<String> columnList = new ArrayList<String>();
    jj_consume_token(LEFT_PAREN);
    s = columnName();
    columnList.add(s);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      s = columnName();
      columnList.add(s);
    }
    jj_consume_token(RIGHT_PAREN);
    {if (true) return columnList;}
    throw new Error("Missing return statement in function");
  }

  static final public void dataType() throws ParseException {
  Token t;
  int i;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      jj_consume_token(INT);
    ColDef.type = 1;
      break;
    case CHAR:
      jj_consume_token(CHAR);
      jj_consume_token(LEFT_PAREN);
      t = jj_consume_token(INT_VALUE);
      jj_consume_token(RIGHT_PAREN);
    ColDef.type = 2;
    i = Integer.parseInt(t.image);
    ColDef.charLength = i;
      break;
    case DATE:
      jj_consume_token(DATE);
    ColDef.type = 3;
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public String tableName() throws ParseException {
  Token t;
    t = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return t.image.toLowerCase();}
    throw new Error("Missing return statement in function");
  }

  static final public String columnName() throws ParseException {
  Token t;
    t = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return t.image.toLowerCase();}
    throw new Error("Missing return statement in function");
  }

  static final public void dropTableQuery() throws ParseException {
    jj_consume_token(DROP_TABLE);
    globalTableName = tableName();
  }

  static final public void descQuery() throws ParseException {
    jj_consume_token(DESC);
    globalTableName = tableName();
  }

  static final public void showTablesQuery() throws ParseException {
    jj_consume_token(SHOW_TABLES);
  }

  static final public void selectQuery() throws ParseException {
  Select = new Select();
    jj_consume_token(SELECT);
    selectList();
    tableExpression();
  }

  static final public void selectList() throws ParseException {
  SelectedColumn SelectedColumn;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ASTERISK:
      jj_consume_token(ASTERISK);
    Select.SelectedColList = null;
      break;
    case LEGAL_IDENTIFIER:
      SelectedColumn = selectedColumn();
    Select.SelectedColList.add(SelectedColumn);
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
        SelectedColumn = selectedColumn();
      Select.SelectedColList.add(SelectedColumn);
      }
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public SelectedColumn selectedColumn() throws ParseException {
  SelectedColumn SelectedCol;
  String tableName = null;
  String columnName;
  String newColumnName;
    if (jj_2_1(2)) {
      tableName = tableName();
      jj_consume_token(PERIOD);
    } else {
      ;
    }
    columnName = columnName();
    newColumnName = columnName;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      newColumnName = columnName();
      break;
    default:
      jj_la1[11] = jj_gen;
      ;
    }
    SelectedCol = new SelectedColumn(tableName, columnName, newColumnName);
    {if (true) return SelectedCol;}
    throw new Error("Missing return statement in function");
  }

  static final public void tableExpression() throws ParseException {
  Where Where;
    fromClause();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      Where = whereClause();
     Select.Where = Where;
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
  }

  static final public void fromClause() throws ParseException {
    jj_consume_token(FROM);
    tableReferenceList();
  }

  static final public void tableReferenceList() throws ParseException {
    referedTable();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_5;
      }
      jj_consume_token(COMMA);
      referedTable();
    }
  }

  static final public void referedTable() throws ParseException {
  String tableName;
  String newTableName;
    tableName = tableName();
    newTableName = tableName;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      newTableName = tableName();
      break;
    default:
      jj_la1[14] = jj_gen;
      ;
    }
    Select.ReferedTableList.add(new ReferedTable(tableName, newTableName));
  }

  static final public Where whereClause() throws ParseException {
  Where Where = new Where();
  BoolValExp BoolValExp;
    jj_consume_token(WHERE);
    BoolValExp = booleanValueExpression();
    Where.BoolValExp = BoolValExp;
    {if (true) return Where;}
    throw new Error("Missing return statement in function");
  }

  static final public BoolValExp booleanValueExpression() throws ParseException {
  BoolValExp BoolValExp = new BoolValExp();
  BooleanTerm BTerm;
    BTerm = booleanTerm();
    BoolValExp.BTermList.add(BTerm);
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[15] = jj_gen;
        break label_6;
      }
      jj_consume_token(OR);
      BTerm = booleanTerm();
      BoolValExp.BTermList.add(BTerm);
    }
    {if (true) return BoolValExp;}
    throw new Error("Missing return statement in function");
  }

  static final public BooleanTerm booleanTerm() throws ParseException {
  BooleanTerm BTerm = new BooleanTerm();
  BooleanFactor BFactor;
    BFactor = booleanFactor();
    BTerm.BFactorList.add(BFactor);
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[16] = jj_gen;
        break label_7;
      }
      jj_consume_token(AND);
      BFactor = booleanFactor();
      BTerm.BFactorList.add(BFactor);
    }
    {if (true) return BTerm;}
    throw new Error("Missing return statement in function");
  }

  static final public BooleanFactor booleanFactor() throws ParseException {
  BooleanFactor BFactor = new BooleanFactor();
  BooleanTest BTest;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      BFactor.isNot = true;
      break;
    default:
      jj_la1[17] = jj_gen;
      ;
    }
    BTest = booleanTest();
    BFactor.BTest = BTest;
    {if (true) return BFactor;}
    throw new Error("Missing return statement in function");
  }

  static final public BooleanTest booleanTest() throws ParseException {
  BooleanTest BTest;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
    case LEGAL_IDENTIFIER:
    case DATE_VALUE:
    case CHAR_STRING:
      BTest = predicate();
    {if (true) return BTest;}
      break;
    case LEFT_PAREN:
      BTest = parenthesizedBooleanExpression();
    {if (true) return BTest;}
      break;
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public BoolValExp parenthesizedBooleanExpression() throws ParseException {
  BoolValExp BoolValExp;
    jj_consume_token(LEFT_PAREN);
    BoolValExp = booleanValueExpression();
    jj_consume_token(RIGHT_PAREN);
    {if (true) return BoolValExp;}
    throw new Error("Missing return statement in function");
  }

  static final public Predicate predicate() throws ParseException {
  Predicate Predicate;
    if (jj_2_2(4)) {
      Predicate = comparisonPredicate();
    {if (true) return Predicate;}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LEGAL_IDENTIFIER:
        Predicate = nullPredicate();
    {if (true) return Predicate;}
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public ComparisionPredicate comparisonPredicate() throws ParseException {
  ComparisionPredicate ComparisionPredicate;
  Token t;
  CompOperand Left;
  CompOperand Right;
    Left = compOperand();
    t = jj_consume_token(COMP_OP);
    Right = compOperand();
    ComparisionPredicate = new ComparisionPredicate(Left, t.image, Right);
    {if (true) return ComparisionPredicate;}
    throw new Error("Missing return statement in function");
  }

  static final public CompOperand compOperand() throws ParseException {
  CompOperand CompOperand;
  String tableName = null;
  String columnName;
  Value Value;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
    case DATE_VALUE:
    case CHAR_STRING:
      Value = comparableValue();
    CompOperand = new CompOperand(Value);
    {if (true) return CompOperand;}
      break;
    case LEGAL_IDENTIFIER:
      if (jj_2_3(2)) {
        tableName = tableName();
        jj_consume_token(PERIOD);
      } else {
        ;
      }
      columnName = columnName();
    CompOperand = new CompOperand(tableName, columnName);
    {if (true) return CompOperand;}
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Value comparableValue() throws ParseException {
  Token t;
  Value value;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
      t = jj_consume_token(INT_VALUE);
    value = new Value(t.image, 1);
    {if (true) return value;}
      break;
    case CHAR_STRING:
      t = jj_consume_token(CHAR_STRING);
    value = new Value(t.image.substring(1, t.image.length()-1), 2);
    {if (true) return value;}
      break;
    case DATE_VALUE:
      t = jj_consume_token(DATE_VALUE);
    value = new Value(t.image, 3);
    {if (true) return value;}
      break;
    default:
      jj_la1[21] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public NullPredicate nullPredicate() throws ParseException {
  NullPredicate NullPredicate;
  boolean isNot;
  String tableName = null;
  String columnName;
    if (jj_2_4(2)) {
      tableName = tableName();
      jj_consume_token(PERIOD);
    } else {
      ;
    }
    columnName = columnName();
    isNot = nullOperation();
    NullPredicate = new NullPredicate(tableName, columnName, isNot);
    {if (true) return NullPredicate;}
    throw new Error("Missing return statement in function");
  }

  static final public boolean nullOperation() throws ParseException {
    jj_consume_token(IS);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NULL:
      jj_consume_token(NULL);
      {if (true) return false;}
      break;
    case NOT_NULL:
      jj_consume_token(NOT_NULL);
      {if (true) return true;}
      break;
    default:
      jj_la1[22] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public void insertQuery() throws ParseException {
  String tableName;
    jj_consume_token(INSERT);
    tableName = tableName();
    Insert = new Insert(tableName);
    insertColumnAndSource();
  }

  static final public void insertColumnAndSource() throws ParseException {
  ArrayList<Value> ValueList;
  ArrayList<String> ColNameList;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEFT_PAREN:
      ColNameList = columnNameList();
    Insert.ColumnNameList = ColNameList;
      break;
    default:
      jj_la1[23] = jj_gen;
      ;
    }
    ValueList = valueList();
    Insert.ValueList = ValueList;
  }

  static final public ArrayList<Value> valueList() throws ParseException {
  ArrayList<Value> ValueList = new ArrayList<Value>();
  Value value;
    jj_consume_token(VALUES);
    jj_consume_token(LEFT_PAREN);
    value = value();
    ValueList.add(value);
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[24] = jj_gen;
        break label_8;
      }
      jj_consume_token(COMMA);
      value = value();
      ValueList.add(value);
    }
    jj_consume_token(RIGHT_PAREN);
    {if (true) return ValueList;}
    throw new Error("Missing return statement in function");
  }

  static final public Value value() throws ParseException {
  Value value;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NULL:
      jj_consume_token(NULL);
    value = new Value(null, 4);
    {if (true) return value;}
      break;
    case INT_VALUE:
    case DATE_VALUE:
    case CHAR_STRING:
      value = comparableValue();
    {if (true) return value;}
      break;
    default:
      jj_la1[25] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public void deleteQuery() throws ParseException {
  String tableName;
  Where Where;
    jj_consume_token(DELETE);
    tableName = tableName();
    Delete = new Delete(tableName);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      Where = whereClause();
      Delete.WhereClause = Where;
      break;
    default:
      jj_la1[26] = jj_gen;
      ;
    }
  }

  static private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  static private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  static private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  static private boolean jj_3_1() {
    if (jj_3R_9()) return true;
    if (jj_scan_token(PERIOD)) return true;
    return false;
  }

  static private boolean jj_3R_15() {
    if (jj_scan_token(LEGAL_IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3R_18() {
    if (jj_scan_token(DATE_VALUE)) return true;
    return false;
  }

  static private boolean jj_3_2() {
    if (jj_3R_10()) return true;
    return false;
  }

  static private boolean jj_3_3() {
    if (jj_3R_9()) return true;
    if (jj_scan_token(PERIOD)) return true;
    return false;
  }

  static private boolean jj_3R_13() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_3()) jj_scanpos = xsp;
    if (jj_3R_15()) return true;
    return false;
  }

  static private boolean jj_3R_10() {
    if (jj_3R_11()) return true;
    if (jj_scan_token(COMP_OP)) return true;
    if (jj_3R_11()) return true;
    return false;
  }

  static private boolean jj_3R_17() {
    if (jj_scan_token(CHAR_STRING)) return true;
    return false;
  }

  static private boolean jj_3R_9() {
    if (jj_scan_token(LEGAL_IDENTIFIER)) return true;
    return false;
  }

  static private boolean jj_3_4() {
    if (jj_3R_9()) return true;
    if (jj_scan_token(PERIOD)) return true;
    return false;
  }

  static private boolean jj_3R_16() {
    if (jj_scan_token(INT_VALUE)) return true;
    return false;
  }

  static private boolean jj_3R_14() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_16()) {
    jj_scanpos = xsp;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) return true;
    }
    }
    return false;
  }

  static private boolean jj_3R_11() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) return true;
    }
    return false;
  }

  static private boolean jj_3R_12() {
    if (jj_3R_14()) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public SQLParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[27];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x1f8220,0x1f8200,0x1f8200,0x0,0x3000,0x400,0x3000,0x0,0x1c0,0x0,0x400000,0x800000,0x2000000,0x0,0x800000,0x8000000,0x4000000,0x10000000,0x80000000,0x0,0x0,0x0,0xc00,0x80000000,0x0,0x800,0x2000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x2,0x40,0x0,0x0,0x2,0x0,0x2,0x40,0x0,0x0,0x2,0x0,0x0,0x0,0x0,0xa048,0x40,0xa048,0xa008,0x0,0x0,0x2,0xa008,0x0,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[4];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public SQLParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SQLParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SQLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 27; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 27; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public SQLParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SQLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 27; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 27; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public SQLParser(SQLParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 27; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(SQLParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 27; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[48];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 27; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 48; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 4; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}