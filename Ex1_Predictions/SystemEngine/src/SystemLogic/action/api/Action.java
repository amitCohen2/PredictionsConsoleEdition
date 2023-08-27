package SystemLogic.action.api;

import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;

import java.io.Serializable;

public interface Action extends Serializable {
    void invoke(Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException;
    ActionType getActionType();
    EntityDefinition getContextEntity();
}
