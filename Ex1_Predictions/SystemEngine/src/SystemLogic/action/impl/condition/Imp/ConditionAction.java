package SystemLogic.action.impl.condition.Imp;

import SystemLogic.Expression.api.Expression;
import SystemLogic.action.api.Rangeable;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.Action;
import SystemLogic.action.api.ActionType;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.action.impl.condition.api.Condition;
import SystemLogic.action.impl.condition.api.OperationType;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;

import java.util.ArrayList;
import java.util.List;

public class ConditionAction extends AbstractAction {

    private final Condition condition;
    private final EntityDefinition entityDefinition;
    private  Expression expression;

    public ConditionAction(ActionType actionType, EntityDefinition entityDefinition, String byExpression, Condition condition) {
        super(actionType, entityDefinition, byExpression);
        this.entityDefinition = entityDefinition;
        this.condition = condition;
        this.expression = initExpression(byExpression);
    }


    @Override
    public void invoke(Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException {


        int result = isConditionApply(this.condition, context);

        // if the condition apply
        if (result > 0) {
            ArrayList<Action> actions = condition.getThenActions();
            if (actions != null) {
                for (Action action : actions) {
                    action.invoke(context);
                }
            }
        }
        else {
            ArrayList<Action> actions = condition.getElseActions();
            if (actions != null) {
                for (Action action : actions) {
                    action.invoke(context);
                }
            }

        }
    }
    public void setExpression(String byExpression){
        this.expression = initExpression(byExpression);
    }

    private int isConditionApply(Condition condition, Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException {
        //check if this is single condition

        if (condition.isSingle()) {
            String propertyName;

            //check if this the root
            if (condition.getPropertyName() == null && condition.getConditions().size() > 0){
                propertyName = condition.getConditions().get(0).getPropertyName();
                setExpression(condition.getConditions().get(0).gerByExpression());
            }
            else { // not the root
                 propertyName =condition.getPropertyName();
                 setExpression(condition.gerByExpression());
            }

            switch (condition.getOperationType()) {
                case EQUAL:
                    return OperationType.EQUAL.activateOperation(
                            context.getPrimaryEntityInstance().getPropertyByName(propertyName).getValue(),
                            expression.getExpressionValue(context));
                case NOT_EQUAL:
                    return OperationType.NOT_EQUAL.activateOperation(
                            context.getPrimaryEntityInstance().getPropertyByName(propertyName).getValue(),
                            expression.getExpressionValue(context));
                case BT:
                    return OperationType.BT.activateOperation(
                            context.getPrimaryEntityInstance().getPropertyByName(propertyName).getValue(),
                            expression.getExpressionValue(context));
                case LT:
                    return OperationType.LT.activateOperation(
                            context.getPrimaryEntityInstance().getPropertyByName(propertyName).getValue(),
                            expression.getExpressionValue(context));
                default:
                    throw new IllegalArgumentException("Unsupported operation type");
            }
        } else {
            int result = condition.getLogical().equals("and") ? 1 : 0;
            if (condition.getConditions() != null) {
                for (Condition cond : condition.getConditions()) {
                    try {
                        if (condition.getLogical().equals("and")) {
                            result *= isConditionApply(cond, context);
                        } else if (condition.getLogical().equals("or")) {
                            result += isConditionApply(cond, context);
                        }
                    } catch (TooManyExpressionsException | FunctionNotOccurException | NoFunctionExpressionsException | NullPointerException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return result;
        }}}


