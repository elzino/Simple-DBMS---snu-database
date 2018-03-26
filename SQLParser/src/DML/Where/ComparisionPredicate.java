package DML.Where;

import DML.Messages;
import DML.Record;
import DML.ReferedTable;
import DML.Value;

import java.util.ArrayList;

/**
 * Created by 1997j on 2017-12-02.
 */
public class ComparisionPredicate extends Predicate {
    CompOperand LeftOp;
    String Op;
    CompOperand RightOp;

    public ComparisionPredicate(CompOperand Left, String Op, CompOperand Right){
        this.LeftOp = Left;
        this.Op = Op;
        this.RightOp = Right;
    }

    // | -1 error | 0 false | 1 true | 2 unknown |
    @Override
    public int execute(ArrayList<ReferedTable> RefTableList, ArrayList<Record> RecordList){
        Value LeftVal = this.LeftOp.getValue(RefTableList, RecordList);
        Value RightVal = this.RightOp.getValue(RefTableList, RecordList);
        //if CompOperand has error, then null
        if(LeftVal== null || RightVal == null){
            return -1;
        }
        if(LeftVal.getType()== 4){
            return 2;
        }
        if(RightVal.getType()==4){
            return 2;
        }
        if(LeftVal.getType() != RightVal.getType()){
            System.out.println(Messages.WhereIncomparableError);
            return -1;
        }

        //string.compareto   => 음수 앞이더 빠르다 /  양수 뒤가 더 빠르다
        if(Op.equals("<")){
            int result = LeftVal.compareTo(RightVal);
            if(result<0){
                return 1;
            }
            return 0;
        }
        else if(Op.equals("<=")){
            int result = LeftVal.compareTo(RightVal);
            if(result<=0){
                return 1;
            }
            return 0;
        }
        else if(Op.equals(">")){
            int result = LeftVal.compareTo(RightVal);
            if(result>0){
                return 1;
            }
            return 0;
        }
        else if(Op.equals(">=")){
            int result = LeftVal.compareTo(RightVal);
            if(result>=0){
                return 1;
            }
            return 0;
        }
        else if(Op.equals("=")){
            int result = LeftVal.compareTo(RightVal);
            if(result==0){
                return 1;
            }
            return 0;
        }
        //Op.equals !=
        else{
            int result = LeftVal.compareTo(RightVal);
            if(result!=0){
                return 1;
            }
            return 0;
        }
    }
}