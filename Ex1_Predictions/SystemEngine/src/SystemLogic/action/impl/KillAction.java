package SystemLogic.action.impl;

import SystemLogic.action.api.AbstractAction;
import SystemLogic.action.api.ActionType;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.context.Context;

public class KillAction extends AbstractAction {


    public KillAction(EntityDefinition entityDefinition) {
        super(ActionType.KILL, entityDefinition, "kill");
    }

    @Override
    public void invoke(Context context) {
        context.removeEntity(context.getPrimaryEntityInstance());
    }

}
