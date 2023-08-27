package SystemLogic.history.api;

import java.io.Serializable;

public interface History extends Serializable {
    public StringBuilder getBeforeAfterEntitiesAmount();
    public StringBuilder getPropertiesList(int userEntityChoice);
    public StringBuilder getPropertyHistory(int userEntityChoice, int userPropertyChoice);
}
