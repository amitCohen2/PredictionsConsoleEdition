package SystemLogic.Expression.api;

import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.execution.context.Context;

import java.io.Serializable;

public interface Function extends Serializable {
    Object environment(Expression expression, Context context) throws NoFunctionExpressionsException,
            FunctionNotOccurException, TooManyExpressionsException ;
    public int random(Expression expression, Context context) throws TooManyExpressionsException,
            FunctionNotOccurException, NoFunctionExpressionsException;
}
