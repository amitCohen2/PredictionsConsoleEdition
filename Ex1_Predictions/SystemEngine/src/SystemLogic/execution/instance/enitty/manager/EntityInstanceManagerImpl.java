package SystemLogic.execution.instance.enitty.manager;
import SystemLogic.definition.entity.EntityDefinition;

import SystemLogic.definition.property.api.PropertyDefinition;
import SystemLogic.execution.instance.enitty.EntityInstance;
import SystemLogic.execution.instance.enitty.EntityInstanceImpl;
import SystemLogic.execution.instance.property.PropertyInstance;
import SystemLogic.execution.instance.property.PropertyInstanceImpl;

import java.util.ArrayList;
import java.util.List;

public class EntityInstanceManagerImpl implements EntityInstanceManager {

    private int count;
    private List<EntityInstance> instances;

    public int getInstancesSize() {
        return this.instances.size();
    }

    public EntityInstanceManagerImpl() {
        count = 0;
        instances = new ArrayList<>();
    }

    @Override
    public EntityInstance create(EntityDefinition entityDefinition) {

        count++;
        EntityInstance newEntityInstance = new EntityInstanceImpl(entityDefinition, count);
        instances.add(newEntityInstance);

        for (PropertyDefinition propertyDefinition : entityDefinition.getProps()) {
            Object value = propertyDefinition.generateValue();
            PropertyInstance newPropertyInstance = new PropertyInstanceImpl(propertyDefinition, value);
            newEntityInstance.addPropertyInstance(newPropertyInstance);
        }

        return newEntityInstance;
    }

    @Override
    public List<EntityInstance> getInstances() {
        return instances;
    }

    @Override
    public void killEntity(int id) {
        int idx;

        for(int i = 0; i <instances.size(); i++) {
         if(instances.get(i).getId() ==id) {
              idx = i;
             instances.remove(idx);
             break;
         }
        }


      }

    }

