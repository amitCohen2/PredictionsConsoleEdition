package SystemLogic.termination;

import java.io.Serializable;

public class TerminationImpl implements Serializable {
    Integer TerminationBySec;
    Integer TerminationByTicks;

    public TerminationImpl(Integer TerminationBySec,Integer   TerminationByTicks )
    {
        this.TerminationBySec =TerminationBySec;
        this.TerminationByTicks = TerminationByTicks;
    }
    public Integer getNumOfSec() {return TerminationBySec;}

    public Integer getNumOfTicks() {return TerminationByTicks;}
    public void setTerminationBySec(Integer TerminationBySec){
        this.TerminationBySec= TerminationBySec;
    }
     public void setTerminationByTicks(Integer TerminationByTicks){
        this.TerminationByTicks= TerminationByTicks;
    }
}
