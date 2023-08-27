package SystemLogic.action.impl;

import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.ActionType;
import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.action.api.Rangeable;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.property.PropertyInstance;

public class SetAction extends AbstractAction implements Rangeable {

    private final String property;
    private final Expression expression;

    public SetAction(EntityDefinition entityDefinition, String property,
                          String byExpression) {
        super(ActionType.SET, entityDefinition, byExpression);
        this.property = property;
        this.expression = getExpression();
    }

    @Override
    public void invoke(Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException, ValueOutOfRangeException {
        PropertyInstance propertyInstance = context.getPrimaryEntityInstance().getPropertyByName(property);
        Object newValue = expression.getExpressionValue(context);

        Double currentChangeValue = null;

        if (newValue instanceof Integer) {
            currentChangeValue = ((Integer) newValue).doubleValue();
        } else if (newValue instanceof Double) {
            currentChangeValue = (Double) newValue;
        }
        if (newValue instanceof Number) {
            if (!isInsideRange(propertyInstance, currentChangeValue)) {
                throw new ValueOutOfRangeException(ActionType.INCREASE, ((Number) propertyInstance.getPropertyDefinition().getRange().getKey()).doubleValue()
                        , ((Number) propertyInstance.getPropertyDefinition().getRange().getValue()).doubleValue(), currentChangeValue, propertyInstance.getPropertyDefinition().getName());
            }
        }

        propertyInstance.updateValue(newValue);
    }
}
