package SystemLogic.Expression.Imp;

import SystemLogic.Expression.api.AbstractExpression;
import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.api.Function;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.Expression.api.enums.FunctionType;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.environment.impl.ActiveEnvironmentImpl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FunctionExpression extends AbstractExpression implements Function {

    private final String functionName;
    private final FunctionType functionType;
    private final List<Expression> expressions;

    private ActiveEnvironmentImpl activeEnvironment;
    public FunctionExpression(ExpressionType expressionType, EntityDefinition entityDefinition,
                              String functionName, FunctionType functionType, List<Expression> expressions) {
        super(expressionType, entityDefinition);
        this.functionName = functionName;
        this.functionType = functionType;
        this.expressions = new ArrayList<>(expressions);


    }

    public void addExpression(Expression expression) {
        expressions.add(expression);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.FUNCTION;
    }


    @Override
    public Object getExpressionValue(Context context) throws NoFunctionExpressionsException, FunctionNotOccurException, TooManyExpressionsException {
        if (expressions.size() == 0) {
            throw new NoFunctionExpressionsException();
        }

        switch (functionType) {
            case ENVIRONMENT:

                return environment(expressions.get(0), context);
            case RANDOM:
                return random(expressions.get(0), context);
            case EVALUATE:
                // Code for EVALUATE function type
                // break;
            case PERCENT:
                // Code for PERCENT function type
                // break;
            case TICKS:
                // Code for TICKS function type
                // break;
            default:
                throw new FunctionNotOccurException();
        }
    }


    @Override
    public Object environment(Expression expression, Context context) throws
            TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException {
        if (expressions.size() > 1) {
            throw new TooManyExpressionsException();
        }

        return context.getEnvironmentVariable((String) expression.getExpressionValue(context)).getValue();

//        EnvVariableManagerImpl envVariablesManager = EnvVariableManagerImpl.getInstance();
//        return envVariablesManager.getEnvironmentValues().get(expression.getExpressionValue(context));
    }

    @Override
    public int random(Expression expression, Context context) throws TooManyExpressionsException, FunctionNotOccurException, NoFunctionExpressionsException {
        if (expressions.size() > 1) {
            throw new TooManyExpressionsException();
        }
        else if (!expression.getExpressionType().equals(ExpressionType.ANOTHER)) {
            throw new InvalidParameterException("This Parameter does not support this ExpressionType! ");
        }
        int expressionValue;
        try {
            Number numericValue = (Number) expression.getExpressionValue(context);
            float tmpValue = numericValue.floatValue();
            expressionValue = (int) Math.round(tmpValue);
        }   catch (InvalidParameterException e) {
            throw new InvalidParameterException("This Parameter" + expression.getExpressionValue(context) +
                    " does not support this ExpressionType! ");
        }

        Random rand = new Random();
        if (expressionValue <= 1) {
            throw new IllegalArgumentException("Max value should be greater than 1.");
        }

        return rand.nextInt(expressionValue + 1);

    }

}

