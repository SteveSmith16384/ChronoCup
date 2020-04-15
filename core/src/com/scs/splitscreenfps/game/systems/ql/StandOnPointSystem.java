package com.scs.splitscreenfps.game.systems.ql;

import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.ql.CanStandOnPoint;

public class StandOnPointSystem extends AbstractSystem {

	public StandOnPointSystem(BasicECS ecs) {
		super(ecs, CanStandOnPoint.class);
	}
	
	
	public void processEntity(AbstractEntity entity) {
		// todo
	}

}
