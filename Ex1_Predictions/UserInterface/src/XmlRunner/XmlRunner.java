package XmlRunner;

import Menu.Menu;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.SimulationHistoryNotFoundException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.Timer.TimeMeasurement;
import SystemLogic.action.api.ActionType;
import SystemLogic.action.impl.Exceptions.ValueOutOfRangeException;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.context.ContextImpl;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.EntityInstanceImpl;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManagerImpl;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.history.imp.HistoryArchive;
import SystemLogic.history.imp.SimulationHistory;
import SystemLogic.rule.Rule;
import SystemLogic.termination.TerminationImpl;
import XmlLoader.XmlLoader;
import XmlLoader.schema.PRDEnvProperty;
import XmlLoader.schema.PRDEvironment;
import XmlLoader.schema.PRDWorld;
import convertor.Convertor;
import convertor.EnvironmentValuesFromUser;

import java.io.Serializable;
import java.util.*;

public class XmlRunner  implements Serializable {
    Convertor convertor;
    Map<String, Rule> ruleMap;
    Map<String, EntityDefinition> entityMap;
    PRDWorld world;
    EnvVariableManagerImpl envVariablesManager;
    String xmlFilePath;
    Map<String, List<Context>> contexts;
    HistoryArchive historyArchive;
    int lastSimulationId;
    TerminationImpl termination;
    ActiveEnvironment activeEnvironment;

    SimulationHistory simulationHistory;
    boolean isInit = false;

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public int getLastSimulationId() {
        return this.lastSimulationId;
    }



    public void initRunSimulation(String xmlFilePath, int simulationsCounter, HistoryArchive historyArchive) {
        this.historyArchive = historyArchive;
        setXmlFilePath(xmlFilePath);
        initXml(simulationsCounter);
        InitSimulation(simulationsCounter);

    }
    public HistoryArchive getHistoryArchive(){
        return this.historyArchive;
    }
    public void initXml(int lastSimulationId)
    {
        this.envVariablesManager =EnvVariableManagerImpl.getInstance();
        this.convertor = new Convertor();
        this.ruleMap = new HashMap<>();
        this.entityMap = new HashMap<>();
        XmlLoader xmlLoader = new XmlLoader(this.xmlFilePath);
        this.world = xmlLoader.loadXmlData();
        this.isInit = true;
        this. contexts = new HashMap<>();
        this.lastSimulationId = lastSimulationId;
        this.termination = new TerminationImpl(null, null);
        this.activeEnvironment = null ;
        this.simulationHistory =null;
    }

    public void InitSimulation(int counterSimulation)
    {
        if(!isInit){
            initXml(counterSimulation);
        }
        try {
            activeEnvironment = envVariablesManager.createActiveEnvironment();
            convertor.initTermination(world.getPRDTermination(), termination);
            convertor.PrdEntity2EntityMap(world.getPRDEntities().getPRDEntity(), entityMap);
            convertor.PrdRule2RuleMap(world.getPRDRules().getPRDRule(), ruleMap, entityMap);
//            convertor.PrdEnvProperty2EvironmentManager(world.getPRDEvironment().getPRDEnvProperty(), envVariablesManager, activeEnvironment);
        }
        catch (Exception e){
            throw new IllegalArgumentException("There is a problem with the xml that was provided: " + e.getMessage());
        }
    }

    public void printPreviousSimulationHistory() throws SimulationHistoryNotFoundException {
        if (this.historyArchive.getSimulations().size() == 0) {
            throw new SimulationHistoryNotFoundException("There is no history to show");
        }

        System.out.println(this.historyArchive.getSimulationsListByDates());

    }
    public SimulationHistory getSimulationById(int id){
       return  this.historyArchive.getSimulationHistory(id);
    }
    public void Run()
    {
        Map<String, EnvironmentValuesFromUser> userEnvInput = showEvironment(world.getPRDEvironment().getPRDEnvProperty());
        convertor.PrdEnvProperty2EvironmentManager(world.getPRDEvironment().getPRDEnvProperty(), envVariablesManager, activeEnvironment, userEnvInput);
        for (Map.Entry<String, EntityDefinition> entry : entityMap.entrySet()) {
            //init the instanceManager and the list of the current entity contexts
            EntityInstanceManager entityInstanceManager = new EntityInstanceManagerImpl();
            List<Context> entityContexts = new ArrayList<>();

            for (EntityDefinition entity : entityMap.values()) {
                for (int i = 0; i < entity.getPopulation(); i++) {
                    EntityInstance currEntityInstance = entityInstanceManager.create(entity);
                    Context currentContext = new ContextImpl(currEntityInstance, entityInstanceManager, activeEnvironment);
                    entityContexts.add(currentContext);
                }
            }
            // add to each entity string list of contexts
            contexts.put(entry.getKey(), entityContexts);
        }

        historyArchive.addSimulation(contexts);
        Random random = new Random();
        TimeMeasurement timer = new TimeMeasurement();
        String prefix = "\t";
        int ticksCounter = 1;
        timer.start();
        int terminationInTicks = termination.getNumOfTicks();
        int terminationInSec = termination.getNumOfSec();
        while (timer.checkElapsedTime() < terminationInSec && ticksCounter < terminationInTicks) {
            for (Map.Entry<String, Rule> ruleEntry : ruleMap.entrySet()) {
                Rule rule = ruleEntry.getValue();
                if (rule.isActive(ticksCounter, random.nextDouble())) {
                    for (Map.Entry<String, List<Context>> entry : contexts.entrySet()) {
                        entry.getValue().forEach(context -> {
                            rule.getActionsToPerform().forEach(action -> {
                                try {
                                    if(isAlive(context.getEntityInstanceManager().getInstances(),context.getPrimaryEntityInstance().getId())){
                                        action.invoke(context);
                                    }

                                } catch (TooManyExpressionsException | FunctionNotOccurException |
                                         IllegalArgumentException | NoFunctionExpressionsException e) {
                                    System.out.println(e.getMessage());
                                } catch (ValueOutOfRangeException e) {
                                    System.out.println(e.getMessage());
                                }
                            });
                        });
                    }
                }
            }
            ticksCounter++;
        }

        System.out.print("Simulation number: " + lastSimulationId + " was ended Successfully! End by: ");
        if(ticksCounter == terminationInTicks){
            System.out.println( ticksCounter + " ticks.\n");
        } else {
            System.out.println( terminationInSec + " Seconds.\n");
        }
        simulationHistory = historyArchive.getSimulationHistory(lastSimulationId);
        simulationHistory.updateSimulationHistory(contexts);
        setLastSimulationId(1);

    }
    public void setHistoryArchive(HistoryArchive historyArchive){
        this.historyArchive = historyArchive;
    }

    public boolean isAlive(List<EntityInstance> entityInstanceArrayList, int id){
        for(EntityInstance entityInstance : entityInstanceArrayList){
            if(entityInstance.getId() == id){
                return true;
            }
        }
        return false;
    }
    public Map<String, EnvironmentValuesFromUser> showEvironment(List<PRDEnvProperty> envProperties)
    {
        Map<String, EnvironmentValuesFromUser> result = new HashMap<>();
        for (PRDEnvProperty property : envProperties) {
            String propName = property.getPRDName();
            String type = property.getType();
            double from = - Double.MAX_VALUE, to = Double.MAX_VALUE;;
            if (property.getPRDRange() != null) {
                to = property.getPRDRange().getTo();
                from = property.getPRDRange().getFrom();
                System.out.println("The Environment property name: " + propName + ", Type: " + type
                        + ", Range: [" + from + ", " + to + "].");
            }
            else {
                System.out.println("The Environment property name: " + propName + ", Type: " + type
                        + ", Range: no range");
            }



            System.out.println("Press 1 to change the Value, or press 2 to continue");
            System.out.print("Your choice: ");

            int userInput = Menu.getIntInput(1, 2);
            EnvironmentValuesFromUser currEnvPropertyFromUser = new EnvironmentValuesFromUser();
            if (userInput == 1) {
                System.out.println("Please enter a value: ");
                Object userValue = null;
                if (type.equals("decimal")) {
                    userValue = Menu.getIntInput((int) from, (int) to);
                }
                else if (type.equals("float")) {
                    userValue = Menu.getFloatInput(from, to);
                }
                else if (type.equals("boolean")) {
                    userValue =Menu.getBooleanInput();
                }
                else if (type.equals("string")) {
                    userValue = Menu.getStringInput();
                }

                currEnvPropertyFromUser.setType(type);
                currEnvPropertyFromUser.setValue(userValue);
                currEnvPropertyFromUser.setName(propName);
                currEnvPropertyFromUser.setValueFromUser(true);
            }

            result.put(propName, currEnvPropertyFromUser);
        }

        return result;
    }

    public  boolean isInt(double num) {
        if (num == (int) num) {
            return true; // The number is an integer
        } else {
            System.out.println("Invalid input! Input can be a decimal number only.");
            System.out.println("Try again.");
            return false; // The number is not an integer
        }
    }

    public void setLastSimulationId(int number){
        this.lastSimulationId += number;
    }
    public PRDWorld getWorld() {
        return this.world;
    }
    public void setIsInit(boolean isInit){
        this.isInit=isInit;
    }
    public boolean getIsInit(){
        return this.isInit;
    }

    public void setWorld(PRDWorld world) {
        this.world = world;
    }

    public Map<String, Rule> getRuleMap() {
        return this.ruleMap;
    }

    public void setRuleMap(Map<String, Rule> ruleMap) {
        this.ruleMap = ruleMap;
    }

    public Map<String, EntityDefinition> getEntityMap() {
        return this.entityMap;
    }

    public void setEntityMap(Map<String, EntityDefinition> entityMap) {
        this.entityMap = entityMap;
    }

    public EnvVariableManagerImpl getEnvVariablesManager() {
        return this.envVariablesManager;
    }

    public void setEnvVariablesManager(EnvVariableManagerImpl envVariablesManager) {
        this.envVariablesManager = envVariablesManager;
    }

    public Convertor getConvertor() {
        return this.convertor;
    }

    public void setConvertor(Convertor convertor) {
        this.convertor = convertor;
    }
}
