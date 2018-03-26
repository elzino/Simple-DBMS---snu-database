package DML.Where;

import DML.Record;
import DML.ReferedTable;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-12-02.
 */
public class BooleanTerm {
    public ArrayList<BooleanFactor> BFactorList = new ArrayList<>();

    //| -1 error | 0 false | 1 true | 2 unknown |
    public int execute(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList){
        int result = 1;
        for(BooleanFactor BFactor : this.BFactorList){
            int termResult = BFactor.execute(RefTableList, RecordList);
            if(termResult == -1){
                return -1;
            }
            else if(termResult == 0){
                return 0;
            }
            else if(termResult == 2){
                result = 2;
            }
        }
        return result;
    }

}
