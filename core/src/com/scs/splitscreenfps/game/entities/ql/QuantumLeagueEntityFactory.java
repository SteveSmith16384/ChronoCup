package com.scs.splitscreenfps.game.entities.ql;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.AnimatedComponent;
import com.scs.splitscreenfps.game.components.AutoMoveComponent;
import com.scs.splitscreenfps.game.components.CollidesComponent;
import com.scs.splitscreenfps.game.components.HasDecal;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.MovementData;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.ql.IsBulletComponent;
import com.scs.splitscreenfps.game.components.ql.QLPlayerData;

import ssmith.libgdx.GraphicsHelper;
import ssmith.libgdx.ModelFunctions;

public class QuantumLeagueEntityFactory {

	public static AbstractEntity createShadow(BasicECS ecs, int side, int phase, float x, float z) {
		AbstractEntity e = new AbstractEntity(ecs, "P" + side + "_Phase" + phase + "_Shadow");

		PositionComponent pos = new PositionComponent(x+0.5f, 0, z+0.5f);
		e.addComponent(pos);

		if (side == 0) {
			ModelInstance instance = ModelFunctions.loadModel("shared/models/quaternius/Smooth_Male_Shirt.g3db", false);
			float scale = ModelFunctions.getScaleForHeight(instance, .8f);
			instance.transform.scl(scale);		
			Vector3 offset = ModelFunctions.getOrigin(instance);
			HasModelComponent hasModel = new HasModelComponent("Smooth_Male_Shirt", instance, offset, 90, scale);
			e.addComponent(hasModel);

			AnimationController animation = new AnimationController(instance);
			AnimatedComponent anim = new AnimatedComponent(animation, "HumanArmature|Man_Walk", "HumanArmature|Man_Idle");
			anim.animationController = animation;
			e.addComponent(anim);

		} else if (side == 1) {
			ModelInstance instance = ModelFunctions.loadModel("quantumleague/models/Alien.g3db", false);
			float scale = ModelFunctions.getScaleForHeight(instance, .8f);
			instance.transform.scl(scale);		
			Vector3 offset = ModelFunctions.getOrigin(instance);
			HasModelComponent hasModel = new HasModelComponent("Alien", instance, offset, 0, scale);
			e.addComponent(hasModel);

			AnimationController animation = new AnimationController(instance);
			AnimatedComponent anim = new AnimatedComponent(animation, "AlienArmature|Alien_Walk", "AlienArmature|Alien_Idle");
			anim.animationController = animation;
			e.addComponent(anim);
		} else {
			throw new RuntimeException("Invalid side: " + side);
		}

		e.addComponent(new MovementData());
		e.addComponent(new CollidesComponent(false, 0.3f));
		e.addComponent(new QLPlayerData(side));

		return e;
	}


	public static AbstractEntity createBullet(BasicECS ecs, AbstractEntity shooter, Vector3 start, Vector3 offset) {
		AbstractEntity e = new AbstractEntity(ecs, "Bullet");

		PositionComponent pos = new PositionComponent(start);
		e.addComponent(pos);

		HasDecal hasDecal = new HasDecal();
		hasDecal.decal = GraphicsHelper.DecalHelper("quantumleague/laser_bolt.png", .4f);
		hasDecal.decal.setPosition(pos.position);
		hasDecal.faceCamera = true;
		hasDecal.dontLockYAxis = false;
		e.addComponent(hasDecal);

		MovementData md = new MovementData();
		md.must_move_x_and_z = true;
		e.addComponent(md);

		e.addComponent(new AutoMoveComponent(offset));

		CollidesComponent cc = new CollidesComponent(false, .1f);
		cc.dont_collide_with = shooter;
		e.addComponent(cc);

		QLPlayerData playerData = (QLPlayerData)shooter.getComponent(QLPlayerData.class);
		e.addComponent(new IsBulletComponent(shooter, playerData.side));

		//No! Fire and forget e.addComponent(new IsRecordable("Bullet", e));

		return e;
	}

}
