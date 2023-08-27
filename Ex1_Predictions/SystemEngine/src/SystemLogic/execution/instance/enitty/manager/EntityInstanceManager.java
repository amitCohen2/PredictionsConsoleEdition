package SystemLogic.execution.instance.enitty.manager;

import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.instance.enitty.EntityInstance;

import java.io.Serializable;
import java.util.List;

public interface EntityInstanceManager extends Serializable {

    EntityInstance create(EntityDefinition entityDefinition);
    List<EntityInstance> getInstances();

    void killEntity(int id);
}
