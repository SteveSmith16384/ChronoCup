package com.scs.splitscreenfps.game.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.scs.splitscreenfps.Settings;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.PersonCameraController;
import com.scs.splitscreenfps.game.ViewportData;
import com.scs.splitscreenfps.game.components.AnimatedComponent;
import com.scs.splitscreenfps.game.components.CollidesComponent;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.MovementData;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.input.IInputMethod;
import com.scs.splitscreenfps.game.levels.QuantumLeagueLevel;

import ssmith.libgdx.ModelFunctions;

public class PlayersAvatar_Person extends AbstractPlayersAvatar {

	private static final float MOVE_SPEED = 1.5f;

	public PersonCameraController cameraController;
	private Vector3 tmpVector = new Vector3();
	private Vector2 tmpVec2 = new Vector2();

	public PlayersAvatar_Person(Game _game, int playerIdx, ViewportData _viewportData, IInputMethod _inputMethod, int modelType) {
		super(_game.ecs, playerIdx, PlayersAvatar_Person.class.getSimpleName() + "_" + playerIdx);

		game = _game;
		inputMethod = _inputMethod;

		this.addComponent(new MovementData());
		this.addComponent(new PositionComponent());

		// Model stuff
		this.addModel(playerIdx, modelType);

		this.addComponent(new CollidesComponent(false, .3f));

		camera = _viewportData.camera;
		cameraController = new PersonCameraController(camera, inputMethod);
		
		QuantumLeagueLevel.setAvatarColour(this, true);
	}


	private ModelInstance addModel(int playerIdx, int modelType) {
		AssetManager am = game.assetManager;

		switch (modelType) {
		case 0:
		{
			am.load("shared/models/quaternius/Smooth_Male_Shirt.g3db", Model.class);
			am.finishLoading();
			Model model = am.get("shared/models/quaternius/Smooth_Male_Shirt.g3db");
			ModelInstance instance = new ModelInstance(model);
			
			HasModelComponent hasModel = new HasModelComponent("SmoothMale", instance, -.3f, 90, 0.0016f);
			hasModel.dontDrawInViewId = playerIdx;
			this.addComponent(hasModel);

			AnimationController animation = new AnimationController(instance);
			AnimatedComponent anim = new AnimatedComponent(animation, "HumanArmature|Man_Walk", "HumanArmature|Man_Idle");
			anim.animationController = animation;
			this.addComponent(anim);

			return instance;
		}
		case 1:
		{
			ModelInstance instance = ModelFunctions.loadModel("quantumleague/models/Alien.g3db", false);
			float scale = ModelFunctions.getScaleForHeight(instance, .8f);
			instance.transform.scl(scale);		
			Vector3 offset = ModelFunctions.getOrigin(instance);
			offset.y -= .3f; // Hack since model is too high

			HasModelComponent hasModel = new HasModelComponent("Alien", instance, offset, 90, scale);
			hasModel.dontDrawInViewId = playerIdx;
			this.addComponent(hasModel);

			AnimationController animation = new AnimationController(instance);
			AnimatedComponent anim = new AnimatedComponent(animation, "AlienArmature|Alien_Walk", "AlienArmature|Alien_Idle");
			anim.animationController = animation;
			this.addComponent(anim);
			
			/*
			QuantumLeagueLevel.setAvatarColour(e, alive);

			for (int i=0 ; i<instance.materials.size ; i++) {
				instance.materials.get(i).set(ColorAttribute.createDiffuse(Color.BLACK));
				instance.materials.get(i).set(ColorAttribute.createAmbient(Color.BLACK));
			}
			*/
			return instance;
		}
		}
		throw new RuntimeException("Unknown modelType:" + modelType);
	}


	public void update() {
		checkMovementInput();
		cameraController.update();

		// Rotate model to direction of camera
		HasModelComponent hasModel = (HasModelComponent)this.getComponent(HasModelComponent.class);
		if (hasModel != null) {
			PositionComponent pos = (PositionComponent)getComponent(PositionComponent.class);
			tmpVec2.set(camera.direction.x, camera.direction.z);
			pos.angle_degs = -tmpVec2.angle();
		}

	}


	private void checkMovementInput() {
		MovementData movementData = (MovementData)this.getComponent(MovementData.class);

		if (this.inputMethod.getForwards() > Settings.MIN_AXIS) {
			//Settings.p("Fwd:" + this.inputMethod.isForwardsPressed());
			tmpVector.set(camera.direction);
			tmpVector.y = 0;
			movementData.offset.add(tmpVector.nor().scl(this.inputMethod.getForwards() * MOVE_SPEED));
		} else if (this.inputMethod.getBackwards() > Settings.MIN_AXIS) {
			//Settings.p("Back:" + this.inputMethod.isBackwardsPressed());
			tmpVector.set(camera.direction);
			tmpVector.y = 0;
			movementData.offset.add(tmpVector.nor().scl(-MOVE_SPEED * this.inputMethod.getBackwards()));
		}
		if (this.inputMethod.getStrafeLeft() > Settings.MIN_AXIS) {
			tmpVector.set(camera.direction).crs(camera.up);
			tmpVector.y = 0;
			movementData.offset.add(tmpVector.nor().scl(-MOVE_SPEED * this.inputMethod.getStrafeLeft()));
		} else if (this.inputMethod.getStrafeRight() > Settings.MIN_AXIS) {
			tmpVector.set(camera.direction).crs(camera.up);
			tmpVector.y = 0;
			movementData.offset.add(tmpVector.nor().scl(MOVE_SPEED * this.inputMethod.getStrafeRight()));
		}

		PositionComponent posData = (PositionComponent)this.getComponent(PositionComponent.class);
		camera.position.set(posData.position.x, posData.position.y + (Settings.PLAYER_HEIGHT/2)+Settings.CAM_OFFSET, posData.position.z);

	}

}

