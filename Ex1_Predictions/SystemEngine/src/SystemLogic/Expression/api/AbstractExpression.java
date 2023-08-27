package SystemLogic.Expression.api;

import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.definition.entity.EntityDefinition;

public abstract class AbstractExpression implements Expression {
    private final ExpressionType expressionType;
    private final EntityDefinition entityDefinition;

    protected AbstractExpression(ExpressionType expressionType, EntityDefinition entityDefinition) {
        this.expressionType = expressionType;
        this.entityDefinition = entityDefinition;
    }
    @Override
    public ExpressionType getExpressionType() {
        return expressionType;
    }

}
