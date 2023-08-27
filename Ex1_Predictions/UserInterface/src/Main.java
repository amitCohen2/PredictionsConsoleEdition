
import SystemLogic.Expression.exceptions.SimulationHistoryNotFoundException;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.context.ContextImpl;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManagerImpl;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.history.imp.HistoryArchive;
import SystemLogic.history.imp.SimulationHistory;
import SystemLogic.termination.TerminationImpl;
import XmlLoader.XmlLoader;
import XmlLoader.schema.*;
import SystemLogic.rule.Rule;
import SystemLogic.Timer.*;
import java.util.*;

import SystemLogic.definition.entity.EntityDefinition;
import convertor.Convertor;

//import SystemLogic.action.*;
//import SystemLogic.rule.*;
//import SystemLogic.execution.*;
//import SystemLogic.termination.*;
import Menu.*;
public class Main {


    public static void main(String[] args) throws SimulationHistoryNotFoundException {

        Menu.MenuRunner();

}}