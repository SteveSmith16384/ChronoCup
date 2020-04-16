package com.scs.splitscreenfps.game.systems.ql;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractEvent;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.BillBoardFPS_Main;
import com.scs.splitscreenfps.game.EventCollision;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.ql.IsBulletComponent;
import com.scs.splitscreenfps.game.components.ql.QLPlayerData;
import com.scs.splitscreenfps.game.entities.EntityFactory;
import com.scs.splitscreenfps.game.levels.QuantumLeagueLevel;

public class QLBulletSystem extends AbstractSystem {

	public QLBulletSystem(BasicECS ecs) {
		super(ecs, IsBulletComponent.class);
	}


	@Override
	public void processEntity(AbstractEntity entity) {
		PositionComponent pos = (PositionComponent)entity.getComponent(PositionComponent.class);

		List<AbstractEvent> colls = ecs.getEventsForEntity(EventCollision.class, entity);
		for (AbstractEvent evt : colls) {
			EventCollision coll = (EventCollision)evt;

			if (coll.hitEntity == null) { // Hit wall
				//Settings.p("Bullet removed after hitting wall");
				entity.remove();
				AbstractEntity expl = EntityFactory.createBlueExplosion(ecs, pos.position);
				ecs.addEntity(expl);
				BillBoardFPS_Main.audio.play("sfx/explosion_dull.wav");
				continue;
			}

			AbstractEntity[] ents = coll.getEntitiesByComponent(IsBulletComponent.class, QLPlayerData.class);
			if (ents != null) {
				IsBulletComponent bullet = (IsBulletComponent)entity.getComponent(IsBulletComponent.class);
				// Check if shooter is alive
				QLPlayerData shooterData = (QLPlayerData)bullet.shooter.getComponent(QLPlayerData.class);
				if (shooterData.health > 0) {
					QLPlayerData playerData = (QLPlayerData)ents[1].getComponent(QLPlayerData.class);
					if (playerData.side != bullet.side) {
						ents[0].remove(); // Remove bullet
						playerData.health -= 50;

						if (playerData.health <= 0) {
							/*HasModelComponent hasModel = (HasModelComponent)ents[1].getComponent(HasModelComponent.class);
							ModelInstance instance = hasModel.model;
							for (int i=0 ; i<instance.materials.size ; i++) {
								instance.materials.get(i).set(ColorAttribute.createDiffuse(Color.WHITE));
								instance.materials.get(i).set(ColorAttribute.createAmbient(Color.WHITE));
							}*/
							QuantumLeagueLevel.setAvatarColour(ents[1], false);
						}

						AbstractEntity expl = EntityFactory.createNormalExplosion(ecs, pos.position);
						ecs.addEntity(expl);

						return;
					}
				}
			}
		}
	}

}
