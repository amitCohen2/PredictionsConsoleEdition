package convertor;

import SystemLogic.Expression.api.Expression;
import SystemLogic.Expression.api.enums.ExpressionType;
import SystemLogic.Expression.api.enums.FunctionType;
import SystemLogic.Expression.exceptions.FunctionNotOccurException;
import SystemLogic.Expression.exceptions.NoFunctionExpressionsException;
import SystemLogic.Expression.exceptions.TooManyExpressionsException;
import SystemLogic.action.api.Action;
import SystemLogic.action.api.ActionType;
import SystemLogic.action.impl.DecreaseAction;
import SystemLogic.action.impl.IncreaseAction;
import SystemLogic.action.impl.KillAction;
import SystemLogic.action.impl.SetAction;
import SystemLogic.action.impl.calculation.imp.CalculationAction;
import SystemLogic.action.impl.condition.Imp.ConditionAction;
import SystemLogic.action.impl.condition.api.Condition;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.definition.entity.EntityDefinitionImpl;
import SystemLogic.definition.environment.impl.EnvVariableManagerImpl;
import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.definition.property.impl.BooleanPropertyDefinition;
import SystemLogic.definition.property.impl.FloatPropertyDefinition;
import SystemLogic.definition.property.impl.IntegerPropertyDefinition;
import SystemLogic.definition.property.impl.StringPropertyDefinition;
import SystemLogic.definition.value.generator.api.ValueGeneratorFactory;
import SystemLogic.execution.context.Context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.environment.api.ActiveEnvironment;
import SystemLogic.execution.instance.property.PropertyInstanceImpl;
import SystemLogic.rule.Rule;
import SystemLogic.rule.RuleImpl;
import SystemLogic.termination.TerminationImpl;
import XmlLoader.schema.*;
import SystemLogic.action.impl.condition.*;

import java.io.Serializable;
import java.util.*;

public class Convertor implements Serializable {


    public void initTermination(PRDTermination prdTermination, TerminationImpl termination) {

        for (Object ter : prdTermination.getPRDByTicksOrPRDBySecond()) {
            if (ter instanceof PRDByTicks) {
                termination.setTerminationByTicks(((PRDByTicks) ter).getCount());
            } else if (ter instanceof PRDBySecond) {
                termination.setTerminationBySec(((PRDBySecond) ter).getCount());
            }
        }
    }


    public void PrdEnvProperty2EvironmentManager(List<PRDEnvProperty> prdEnvProperties,
                                                 EnvVariableManagerImpl envVariablesManager,
                                                 ActiveEnvironment activeEnvironment,
                                                 Map<String, EnvironmentValuesFromUser> userEnvInput) {

        Set<String> envPropertyNames = new HashSet<>();
        for (PRDEnvProperty envProperty : prdEnvProperties) {
            String propName = envProperty.getPRDName();
            if (envPropertyNames.contains(propName)) {
                throw new IllegalArgumentException("Property with name '" + propName + "' is duplicated.");
            }
            envPropertyNames.add(propName);
        }

        prdEnvProperties.forEach(envprop -> {
            //TODO: add option to user enter an environment values and use FixedValueGenerator instead
            PropertyDefinition intProp = null;
            Object valueGenerated = null;

            if (userEnvInput.get(envprop.getPRDName()).isValueFromUser()) { // get the value from user
                if (envprop.getType().equals("decimal")) {
                    intProp = new IntegerPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createFixed((Integer) userEnvInput.get(envprop.getPRDName()).getValue()),
                            (int) envprop.getPRDRange().getFrom(), (int) envprop.getPRDRange().getTo());
                }
                else if (envprop.getType().equals("float")) {
                    intProp = new FloatPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createFixed((Double) userEnvInput.get(envprop.getPRDName()).getValue()),
                            envprop.getPRDRange().getFrom(), envprop.getPRDRange().getTo());
                }
                else if (envprop.getType().equals("boolean")) {
                    intProp = new BooleanPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createFixed((Boolean) userEnvInput.get(envprop.getPRDName()).getValue()));
                }
                else if (envprop.getType().equals("string")) {
                    intProp = new StringPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createFixed((String) userEnvInput.get(envprop.getPRDName()).getValue()));
                }
                else {
                    throw new IllegalArgumentException("This argument type: " + envprop.getType() + "does not supported!");
                }
            }
            else {
                if (envprop.getType().equals("decimal")) {
                    intProp = new IntegerPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createRandomInteger((int) envprop.getPRDRange().getFrom(), (int) envprop.getPRDRange().getTo()),
                            (int) envprop.getPRDRange().getFrom(), (int) envprop.getPRDRange().getTo());
                }
                else if (envprop.getType().equals("float")) {
                    intProp = new FloatPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createRandomFloat(envprop.getPRDRange().getFrom(), envprop.getPRDRange().getTo()),
                            envprop.getPRDRange().getFrom(), envprop.getPRDRange().getTo());
                }
                else if (envprop.getType().equals("boolean")) {
                    intProp = new BooleanPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createRandomBoolean());
                }
                else if (envprop.getType().equals("string")) {
                    intProp = new StringPropertyDefinition(envprop.getPRDName(),
                            ValueGeneratorFactory.createRandomString());
                }
                else {
                    throw new IllegalArgumentException("This argument type: " + envprop.getType() + "does not supported!");
                }
            }

            valueGenerated = intProp.generateValue();
            activeEnvironment.addPropertyInstance(new PropertyInstanceImpl(intProp, valueGenerated));
            envVariablesManager.addEnvironmentVariable(intProp);
            envVariablesManager.addEnvironmentValues(envprop.getPRDName(), valueGenerated);
        });
    }

    //TODO: כן
    public  void PrdEntity2EntityMap(List<PRDEntity> prdEntity, Map<String, EntityDefinition> EntityMap) {
        prdEntity.stream()
                .map(entity -> {
                    int population = entity.getPRDPopulation();
                    String name = entity.getName();
                    if (EntityMap.containsKey(name)) {
                        throw new IllegalArgumentException("Entity with name '" + name + "' already exists.");
                    }
                    // System.out.println("Population: " + population);
                    // System.out.println("name: " + name);
                    EntityDefinition Entity = new EntityDefinitionImpl(name, population);
                    entity.getPRDProperties().getPRDProperty().forEach(prop -> {

                        switch (prop.getType()) {
                            case "decimal":
                                try {
                                    if (prop.getPRDValue().isRandomInitialize()) {
                                        Entity.getProps().add(new IntegerPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createRandomInteger((int) prop.getPRDRange().getFrom(), (int) prop.getPRDRange().getTo()),
                                                (int) prop.getPRDRange().getFrom(), (int) prop.getPRDRange().getTo()));
                                    } else {
                                        Entity.getProps().add(new IntegerPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createFixed(Integer.parseInt(prop.getPRDValue().getInit())),
                                        (int) prop.getPRDRange().getFrom(), (int) prop.getPRDRange().getTo()));
                                    }
                                } catch (Exception e) {
                                    throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getPRDValue().getInit() + "to decimal");
                                }
                                break;
                            case "float":
                                try {
                                    if (prop.getPRDValue().isRandomInitialize()) {
                                        Entity.getProps().add(new FloatPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createRandomFloat(prop.getPRDRange().getFrom(), prop.getPRDRange().getTo()),
                                                prop.getPRDRange().getFrom(), prop.getPRDRange().getTo()));
                                    } else {
                                        Entity.getProps().add(new FloatPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createFixed(Double.parseDouble(prop.getPRDValue().getInit())),
                                                prop.getPRDRange().getFrom(), prop.getPRDRange().getTo()));
                                    }
                                } catch (Exception e) {
                                    throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getPRDValue().getInit() + "to float");
                                }

                                break;
                            case "boolean":
                                try {
                                    if (prop.getPRDValue().isRandomInitialize()) {
                                        Entity.getProps().add(new BooleanPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createRandomBoolean()));
                                    } else {
                                        Entity.getProps().add(new BooleanPropertyDefinition(prop.getPRDName(),
                                                ValueGeneratorFactory.createFixed(Boolean.parseBoolean(prop.getPRDValue().getInit()))));
                                    }
                                } catch (Exception e) {
                                    throw new IllegalArgumentException("Cannot do casting from this initial value type: " + prop.getPRDValue().getInit() + "to boolean");
                                }
                                break;
                            case "string":
                                if (prop.getPRDValue().isRandomInitialize()) {
                                    Entity.getProps().add(new StringPropertyDefinition(prop.getPRDName(),
                                            ValueGeneratorFactory.createRandomString()));
                                } else {
                                    Entity.getProps().add(new StringPropertyDefinition(prop.getPRDName(),
                                            ValueGeneratorFactory.createFixed(prop.getPRDValue().getInit())));
                                }
                                break;
                        }
                    });
                    Set<String> propertyNames = new HashSet<>();
                    for (PropertyDefinition prop : Entity.getProps()) {
                        String propName = prop.getName();
                        if (propertyNames.contains(propName)) {
                            throw new IllegalArgumentException("Property with name '" + propName + "' is duplicated.");
                        }
                        propertyNames.add(propName);
                    }
                    return Entity;
                })
                .forEach(entity -> EntityMap.put(entity.getName(), entity));
    }

    public  void PrdRule2RuleMap(List<PRDRule> prdRules, Map<String, Rule> RuleMap, Map<String , EntityDefinition> entity) {
        prdRules.forEach(prdRule ->
        {
            RuleImpl rule = new RuleImpl(prdRule.getName());

            prdRule.getPRDActions().getPRDAction().forEach(prdAction -> {

                if(!entity.containsKey(prdAction.getEntity())){
                    throw new IllegalArgumentException("In rule " + prdRule.getName() +" you are referring to entity " + prdAction.getEntity()
                            +" and this entity is not exist!");
                }
                //set activations
                Integer ticks = getTicks(prdRule.getPRDActivation());
                Double prob = getProbability(prdRule.getPRDActivation());
                rule.setActivation(ticks,prob);

                checkGetBy(prdAction.getBy());

                if(prdAction.getType().equals("increase"))
                {
                    if(!isPropertyExist(prdAction.getProperty(),entity.get(prdAction.getEntity()))){
                        throw new IllegalArgumentException("Property with name '" + prdAction.getProperty() + "' is not present in entity " + prdAction.getEntity());
                    }
                    rule.addAction(new IncreaseAction(entity.get(prdAction.getEntity()),prdAction.getProperty(),prdAction.getBy()));
                } else if (prdAction.getType().equals("kill")) {
                    rule.addAction(new KillAction(entity.get(prdAction.getEntity())));
                } else if (prdAction.getType().equals("condition")) {
                    //init then and Else conditions lists
                    ArrayList<Action> thenActions = null;
                    ArrayList<Action> elseActions = null;
                    if (prdAction.getPRDThen() != null) {
                        checkPRDinerConditionAction(prdAction.getPRDThen().getPRDAction());

                        thenActions =  prdToActionList(prdAction,entity, prdAction.getPRDThen().getPRDAction());
                    }
                    if (prdAction.getPRDElse() != null) {
                        checkPRDinerConditionAction(prdAction.getPRDElse().getPRDAction());
                        elseActions =  prdToActionList(prdAction,entity, prdAction.getPRDElse().getPRDAction());
                    }
                    ArrayList<Condition> conditions = prdCondition2ConditionList(prdAction.getPRDCondition());
                    Condition condition = new Condition(prdAction.getEntity(),prdAction.getProperty(), prdAction.getBy(),prdAction.getPRDCondition().getOperator(),prdAction.getPRDCondition().getSingularity(),prdAction.getPRDCondition().getLogical()
                    ,thenActions, elseActions, conditions);
                    rule.addAction(new ConditionAction(ActionType.CONDITION,entity.get(prdAction.getEntity()),"condition",condition));
                } else if (prdAction.getType().equals("decrease")) {
                    if(!isPropertyExist(prdAction.getProperty(),entity.get(prdAction.getEntity()))){
                        throw new IllegalArgumentException("Property with name '" + prdAction.getProperty() + "' is not present in entity " + prdAction.getEntity());
                    }

                    rule.addAction(new DecreaseAction(entity.get(prdAction.getEntity()),prdAction.getProperty(),prdAction.getBy()));
                } else if (prdAction.getType().equals("set")) {
                    if(!isPropertyExist(prdAction.getProperty(),entity.get(prdAction.getEntity()))){
                        throw new IllegalArgumentException("Property with name '" + prdAction.getProperty() + "' is not present in entity " + prdAction.getEntity());
                    }
                    rule.addAction(new SetAction(entity.get(prdAction.getEntity()),prdAction.getProperty(),prdAction.getBy()));
                }

            });
            RuleMap.put(prdRule.getName(),rule);
        });
    }

    private void checkPRDinerConditionAction(List<PRDAction> condList){
        for(PRDAction cond : condList){
            checkGetBy(cond.getBy());
        }

    }
    private void checkGetBy(String by) {
        if (by != null) {
            if (isNumeric(by)) {
                return;
            }
            else if(!isFunction(by)) {
                throw new IllegalArgumentException("This By expression: " + by + " is not valid!");
            }
        }
    }
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean ifByIsFunction(String by) {

        int openParenthesisIndex = by.indexOf('(');

        if (openParenthesisIndex != -1 && isFunction(by.substring(0, openParenthesisIndex))) {
            return true;
        }

        return false;
    }

    public boolean isFunction(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        input =extractSubstring(input);
        for (FunctionType func : FunctionType.values()) {
            if (func.name().equals(input.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public  String extractSubstring(String input) {
        int indexOfParenthesis = input.indexOf("(");
        if (indexOfParenthesis != -1) {
            return input.substring(0, indexOfParenthesis);
        } else {
            return input;
        }
    }

    public boolean isPropertyExist(String prop , EntityDefinition entity )
    {

        for(PropertyDefinition property : entity.getProps())
        {
            if(property.getName().equals(prop)){

                return true;
            }
        }
            return false;

    }



    public Integer getTicks(PRDActivation prdActivation) {
        Integer result = 1;
        if (prdActivation != null) {
            if (prdActivation.getTicks() != null) {
                result = prdActivation.getTicks();
            }
        }
        return result;
    }
    public Double getProbability(PRDActivation prdActivation) {
        Double result = new Double(1);
        if (prdActivation != null) {
            if (prdActivation.getProbability() != null)
            {
                result = prdActivation.getProbability();
            }
        }
        return result;
    }


    ArrayList<Action> prdToActionList(PRDAction prdAction,Map<String , EntityDefinition> entity, List<PRDAction> predActionList){ // TODO: check if the entity map is good here to save this
        ArrayList<Action> resArrayList = null;

        if (predActionList != null) {
            resArrayList = new ArrayList<>();

            for(PRDAction action : predActionList) {
                if(action.getType().equals("increase"))
                {
                    resArrayList.add(new IncreaseAction(entity.get(action.getEntity()),action.getProperty(),action.getBy()));
                }
                else if(action.getType().equals("decrease"))
                {
                    resArrayList.add(new DecreaseAction(entity.get(action.getEntity()),action.getProperty(),action.getBy()));
                }
                else if(action.getType().equals("set"))
                {
                    resArrayList.add(new SetAction(entity.get(action.getEntity()),action.getProperty(),action.getValue()));
                }
                else if(action.getType().equals("kill"))
                {
                    resArrayList.add(new KillAction(entity.get(action.getEntity())));
                }
            }
        }
          return  resArrayList;
    }

    //TODO: understand what is going on here
    ArrayList<Condition> prdCondition2ConditionList(PRDCondition prdCondition) {
        ArrayList<Condition> conditionArrayList = new ArrayList<>();

    if(prdCondition.getPRDCondition().size() == 0){
        Condition condition = new Condition(prdCondition.getEntity(), prdCondition.getProperty(), prdCondition.getValue(), prdCondition.getOperator(),
                prdCondition.getSingularity(), prdCondition.getLogical(), null,null, null);
        conditionArrayList.add(condition);
    }
    else{
       // ArrayList<Condition> subConditionArrayList = new ArrayList<>();
        for(PRDCondition prdCondition1 : prdCondition.getPRDCondition()){
            Condition condition = new Condition(prdCondition1.getEntity(), prdCondition1.getProperty(), prdCondition1.getValue(), prdCondition1.getOperator(),
                    prdCondition1.getSingularity(), prdCondition1.getLogical(), null,null, null);
            conditionArrayList.add(condition);
        }
    }
    return conditionArrayList;
    }
    public static double generateRandomNumber(double from, double to) {
        if (from >= to) {
            throw new IllegalArgumentException("The 'from' number must be less than the 'to' number.");
        }

        Random random = new Random();
        return random.nextDouble() * (to - from) + from;
    }

}
