package com.scs.splitscreenfps.game.systems.ql.recorddata;

import com.badlogic.gdx.math.Vector3;
import com.scs.basicecs.AbstractEntity;

public class EntityMovedRecordData extends AbstractRecordData {

	public AbstractEntity entity_for_rewind; // E.g. player themselves in phase 0
	public AbstractEntity entity_for_playback;
	public Vector3 position = new Vector3();
	public float direction;
	
	public EntityMovedRecordData(AbstractEntity _rewindEntity, AbstractEntity _playbackEntity, float _time, Vector3 pos, float dir) {
		super(CMD_MOVED, _time);

		entity_for_rewind = _rewindEntity;
		entity_for_playback =_playbackEntity;
		
		position.set(pos);
		direction = dir;
	}
	

}
