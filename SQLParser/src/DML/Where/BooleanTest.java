package DML.Where;

import DML.Record;
import DML.ReferedTable;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-12-02.
 */
public abstract class BooleanTest {
    // | -1 error | 0 false | 1 true | 2 unknown |
    public abstract int execute(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList);
}