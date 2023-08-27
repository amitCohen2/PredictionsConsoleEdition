package SystemLogic.definition.property.impl;

import SystemLogic.definition.property.api.AbstractPropertyDefinition;
import SystemLogic.definition.property.api.PropertyType;
import SystemLogic.definition.value.generator.api.ValueGenerator;
import javafx.util.Pair;

public class IntegerPropertyDefinition extends AbstractPropertyDefinition<Integer>  {

    private final Pair<Integer, Integer> range;
    public IntegerPropertyDefinition(String name, ValueGenerator<Integer> valueGenerator, Integer from, Integer to) {
        super(name, PropertyType.DECIMAL, valueGenerator);
        this.range = new Pair<>(from, to);
    }

    @Override
    public Pair<Integer, Integer> getRange() {
        return this.range;
    }
}
