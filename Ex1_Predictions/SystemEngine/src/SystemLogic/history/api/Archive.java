package SystemLogic.history.api;

import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;
import SystemLogic.history.imp.SimulationHistory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Archive extends Serializable {
    public void addSimulation(Map<String, List<Context>>entities);
    public StringBuilder getSimulationsListByDates();
    public SimulationHistory getSimulationHistory(int number);

}
