package com.scs.splitscreenfps.game.systems.ql;

import com.badlogic.gdx.Gdx;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.ql.QLPlayerData;
import com.scs.splitscreenfps.game.levels.QuantumLeagueLevel;

public class StandOnPointSystem extends AbstractSystem {

	private QuantumLeagueLevel level;

	public StandOnPointSystem(BasicECS ecs, QuantumLeagueLevel _level) {
		super(ecs, QLPlayerData.class);

		level = _level;
	}


	public void processEntity(AbstractEntity entity) {
		PositionComponent posData = (PositionComponent)entity.getComponent(PositionComponent.class);
		if ((int)posData.position.x == level.spot.x) {
			if ((int)posData.position.z == level.spot.y) {
				QLPlayerData playerData = (QLPlayerData)entity.getComponent(QLPlayerData.class); 
				level.timeOnPoint[playerData.side] += Gdx.graphics.getDeltaTime();
			}
		}
	}

}