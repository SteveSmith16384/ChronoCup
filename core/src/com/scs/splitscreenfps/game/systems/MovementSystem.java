package com.scs.splitscreenfps.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.Settings;
import com.scs.splitscreenfps.game.EventCollision;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.MapData;
import com.scs.splitscreenfps.game.components.AnimatedComponent;
import com.scs.splitscreenfps.game.components.AutoMoveComponent;
import com.scs.splitscreenfps.game.components.CollidesComponent;
import com.scs.splitscreenfps.game.components.MovementData;
import com.scs.splitscreenfps.game.components.PositionComponent;

public class MovementSystem extends AbstractSystem {

	private Game game;

	public MovementSystem(Game _game, BasicECS ecs) {
		super(ecs, MovementData.class);

		game = _game;
	}


	@Override
	public void processEntity(AbstractEntity entity) {
		if (Settings.STRICT) {
			// Check if we're already colliding
			PositionComponent pos = (PositionComponent)entity.getComponent(PositionComponent.class);
			if (this.game.collCheckSystem.collided(entity, pos, true)) {
				Settings.p("Warning - already colliding!");
			}
		}

		MovementData movementData = (MovementData)entity.getComponent(MovementData.class);

		AutoMoveComponent auto = (AutoMoveComponent)entity.getComponent(AutoMoveComponent.class);
		if (auto != null) {
			movementData.offset.set(auto.dir);
		}
		movementData.offset.scl(Gdx.graphics.getDeltaTime());

		if (movementData.offset.x != 0 || movementData.offset.y != 0 || movementData.offset.z != 0) {
			if (movementData.frozenUntil < System.currentTimeMillis()) {
				CollidesComponent cc = (CollidesComponent)entity.getComponent(CollidesComponent.class);
				if (movementData.must_move_x_and_z) {
					this.tryMoveXAndZ(entity, game.mapData, movementData.offset, cc.rad*2);
				} else {
					this.tryMoveXOrZ(entity, game.mapData, movementData.offset, cc.rad*2);
				}
			}
		}

		// Animate
		AnimatedComponent anim = (AnimatedComponent)entity.getComponent(AnimatedComponent.class);
		if (anim != null) {
			if (movementData.offset.len2() > 0) {
				anim.next_animation = anim.walk_anim_name;
			} else {
				anim.next_animation = anim.idle_anim_name;
			}
		}

		movementData.offset.setZero(); // Ready for next loop
	}


	/**
	 * Returns true if entity moved successfully on either axis.
	 */
	private boolean tryMoveXAndZ(AbstractEntity mover, MapData world, Vector3 offset, float diameter) {
		PositionComponent pos = (PositionComponent)mover.getComponent(PositionComponent.class);
		pos.originalPosition.set(pos.position);
		Vector3 position = pos.position;

		position.x += offset.x;
		position.z += offset.z;

		boolean result = false;
		if (world.rectangleFree(position.x, position.z, diameter, diameter)) {
			if (this.game.collCheckSystem.collided(mover, pos, true) == false) {
				result = true;
			}
		} else {
			ecs.events.add(new EventCollision(mover, null));
		}

		if (!result) {
			position.set(pos.originalPosition);
		}

		return result;
	}


	private boolean tryMoveXOrZ(AbstractEntity mover, MapData world, Vector3 offset, float diameter) {
		PositionComponent pos = (PositionComponent)mover.getComponent(PositionComponent.class);
		pos.originalPosition.set(pos.position);
		Vector3 position = pos.position;

		position.x += offset.x;
		boolean resultX = false;
		if (world.rectangleFree(position.x, position.z, diameter, diameter)) {
			if (this.game.collCheckSystem.collided(mover, pos, true) == false) {
				resultX = true;
			}
		} else {
			ecs.events.add(new EventCollision(mover, null));
		}
		if (!resultX) {
			position.x -= offset.x;
		}

		position.z += offset.z;
		boolean resultZ = false;
		if (world.rectangleFree(position.x, position.z, diameter, diameter)) {
			if (this.game.collCheckSystem.collided(mover, pos, true) == false) {
				resultZ = true;
			}
		} else {
			ecs.events.add(new EventCollision(mover, null));
		}
		if (!resultZ) {
			position.z -= offset.z;
		}

		return resultX || resultZ;
	}

}
