package com.scs.splitscreenfps.game.entities;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.CollidesComponent;
import com.scs.splitscreenfps.game.components.HasModelComponent;

public class Wall extends AbstractEntity {

	public Wall(BasicECS ecs, String tex_filename, int mapPosX, float yPos, int mapPosZ, boolean add_collision) {
		super(ecs, Wall.class.getSimpleName() + "_" + mapPosX + "_" + mapPosZ);
		
		BlendingAttribute blendingAttribute = new BlendingAttribute();
		blendingAttribute.opacity = 1f;
		
		Material black_material = new Material(TextureAttribute.createDiffuse(new Texture(tex_filename)), blendingAttribute);
		ModelBuilder modelBuilder = new ModelBuilder();
		Model box_model = modelBuilder.createBox(1f, 1f, 1f, black_material, VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);

		ModelInstance instance = new ModelInstance(box_model, new Vector3(mapPosX+0.5f, yPos+0.5f, mapPosZ+0.5f));
		instance.transform.rotate(Vector3.Z, 90); // Rotates cube so textures are upright

		HasModelComponent model = new HasModelComponent(this.getClass().getSimpleName(), instance);
		this.addComponent(model);
		
		if (add_collision) {
			CollidesComponent cc = new CollidesComponent(true, instance);
			this.addComponent(cc);
		} else {
			// Probably uses mapData to check for collisions
		}
	}


	public Wall(BasicECS ecs, Pixmap pixmap, int mapPosX, int mapPosZ, boolean add_collision) {
		super(ecs, Wall.class.getSimpleName());
		
		Material black_material = new Material(TextureAttribute.createDiffuse(new Texture(pixmap)));
		ModelBuilder modelBuilder = new ModelBuilder();
		Model box_model = modelBuilder.createBox(1f, 1f, 1f, black_material, VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);

		ModelInstance instance = new ModelInstance(box_model, new Vector3(mapPosX+0.5f, 0.5f, mapPosZ+0.5f));
		instance.transform.rotate(Vector3.Z, 90); // Rotates cube so textures are upright
		//instance.calculateTransforms();

		HasModelComponent model = new HasModelComponent(this.getClass().getSimpleName(), instance);
		this.addComponent(model);
		
		if (add_collision) {
			CollidesComponent cc = new CollidesComponent(true, instance);
			this.addComponent(cc);
		} else {
			// Maybe uses mapData to check for collisions
		}
	}


	public Wall(BasicECS ecs, String name, String tex_filename, float posX, float posY, float posZ, float w, float h, float d, boolean add_collision) {
		super(ecs, name);
		
		Material black_material = new Material(TextureAttribute.createDiffuse(new Texture(tex_filename)));
		ModelBuilder modelBuilder = new ModelBuilder();
		Model box_model = modelBuilder.createBox(w, h, d, black_material, VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);

		ModelInstance instance = new ModelInstance(box_model, new Vector3(posX+(w/2), posY+(h/2), posZ+(d/2)));
		//ModelInstance instance = new ModelInstance(box_model, new Vector3(posX, posY, posZ));
		//instance.transform.rotate(Vector3.Z, 90); // Position textures upright

		HasModelComponent model = new HasModelComponent(this.getClass().getSimpleName(), instance);
		this.addComponent(model);
		
		if (add_collision) {
			CollidesComponent cc = new CollidesComponent(true, instance);
			this.addComponent(cc);
		}
	}

}
