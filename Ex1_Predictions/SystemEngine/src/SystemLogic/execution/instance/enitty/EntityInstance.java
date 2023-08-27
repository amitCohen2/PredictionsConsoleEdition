package SystemLogic.execution.instance.enitty;

import SystemLogic.execution.instance.property.PropertyInstance;

import java.io.Serializable;
import java.util.Map;

public interface EntityInstance extends Serializable {
    int getId();
    Map<String, PropertyInstance> getPropertyMap();
    PropertyInstance getPropertyByName(String name);
    void addPropertyInstance(PropertyInstance propertyInstance);
}
