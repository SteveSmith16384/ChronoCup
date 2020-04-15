package com.scs.splitscreenfps.game.systems;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.PositionComponent;

public class DrawModelSystem extends AbstractSystem {

	private Game game;
	private ModelBatch modelBatch;
	private Environment environment;

	private Vector3 tmpOffset = new Vector3();

	public DrawModelSystem(Game _game, BasicECS ecs) {
		super(ecs);
		game = _game;

		this.modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

	}


	@Override
	public Class<?> getComponentClass() {
		return HasModelComponent.class;
	}


	//@Override
	public void process(Camera cam) {
		this.modelBatch.begin(cam);

		Iterator<AbstractEntity> it = entities.iterator();
		while (it.hasNext()) {
			AbstractEntity entity = it.next();
			this.processEntity(entity, cam);
		}

		this.modelBatch.end();
	}


	//@Override
	public void processEntity(AbstractEntity entity, Camera camera) {
		HasModelComponent model = (HasModelComponent)entity.getComponent(HasModelComponent.class);
		if (model.dontDrawInViewId == game.currentViewId) {
			return;
		}
		if (model.onlyDrawInViewId >= 0) {
			if (model.onlyDrawInViewId != game.currentViewId) {
				return;
			}
		}
		PositionComponent posData = (PositionComponent)entity.getComponent(PositionComponent.class) ;
		if (posData != null) {
			// Only draw if in frustum 
			if (model.always_draw == false && !camera.frustum.sphereInFrustum(posData.position, 1f)) {
				return;
			}

			Vector3 position = posData.position;
			tmpOffset.set(position);
			tmpOffset.add(model.offset);
			model.model.transform.setToTranslation(tmpOffset);
			model.model.transform.scl(model.scale);
			model.model.transform.rotate(Vector3.Y, posData.angle_degs+model.angleOffset);
		} else {
			if (model.always_draw == false) {
				// Only draw if in frustum 
				if (model.bb == null) {
					model.bb = new BoundingBox();
					model.model.calculateBoundingBox(model.bb);
					model.bb.mul(model.model.transform);
				}
				if (!camera.frustum.boundsInFrustum(model.bb)) {
					return;
				}
			}
		}
		modelBatch.render(model.model, environment);
	}

}
