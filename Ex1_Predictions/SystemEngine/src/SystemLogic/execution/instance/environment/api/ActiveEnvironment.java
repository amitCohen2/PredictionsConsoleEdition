package SystemLogic.execution.instance.environment.api;

import SystemLogic.execution.instance.property.PropertyInstance;

import java.io.Serializable;

public interface ActiveEnvironment extends Serializable {
    PropertyInstance getProperty(String name);
    void addPropertyInstance(PropertyInstance propertyInstance);
}
