package com.scs.splitscreenfps.game.systems.ql;

import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.ql.QLPlayerData;

public class StandOnPointSystem extends AbstractSystem {

	public StandOnPointSystem(BasicECS ecs) {
		super(ecs, QLPlayerData.class);
	}
	
	
	public void processEntity(AbstractEntity entity) {
		// todo
	}

}
