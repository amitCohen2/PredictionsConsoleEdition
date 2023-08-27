package SystemLogic.execution.context;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.manager.EntityInstanceManager;
import SystemLogic.execution.instance.property.PropertyInstance;

import java.io.Serializable;

public interface Context  extends Serializable {
    EntityInstance getPrimaryEntityInstance();
    void removeEntity(EntityInstance entityInstance);
    PropertyInstance getEnvironmentVariable(String name);
    EntityInstanceManager getEntityInstanceManager();

}
