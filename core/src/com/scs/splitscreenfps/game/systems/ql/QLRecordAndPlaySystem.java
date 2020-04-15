package com.scs.splitscreenfps.game.systems.ql;

import java.util.Iterator;
import java.util.LinkedList;

import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.Settings;
import com.scs.splitscreenfps.game.components.AnimatedComponent;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.ql.IsRecordable;
import com.scs.splitscreenfps.game.entities.ql.QuantumLeagueEntityFactory;
import com.scs.splitscreenfps.game.levels.QuantumLeagueLevel;
import com.scs.splitscreenfps.game.systems.ql.recorddata.AbstractRecordData;
import com.scs.splitscreenfps.game.systems.ql.recorddata.BulletFiredRecordData;
import com.scs.splitscreenfps.game.systems.ql.recorddata.EntityMovedRecordData;

public class QLRecordAndPlaySystem extends AbstractSystem {

	private QuantumLeagueLevel level;
	private LinkedList<AbstractRecordData> dataBeingRecorded = new LinkedList<AbstractRecordData>();
	private LinkedList<AbstractRecordData> dataBeingPlayedBack = new LinkedList<AbstractRecordData>();
	private Iterator<AbstractRecordData> revIt;

	public QLRecordAndPlaySystem(BasicECS _ecs, QuantumLeagueLevel _level) {
		super(_ecs, IsRecordable.class);

		level = _level;
	}


	public void loadNewRecordData() {
		this.dataBeingPlayedBack.addAll(this.dataBeingRecorded);
		this.dataBeingRecorded.clear();
	}


	@Override
	public void process() {
		super.process();

		if (level.isGamePhase()) {
			// Play from prev recording
			if (this.level.qlPhaseSystem.getPhaseNum012() > 0) {
				if (this.dataBeingPlayedBack.size() > 0) {
					AbstractRecordData next = this.dataBeingPlayedBack.getFirst();
					float currentPhaseTime = level.getCurrentPhaseTime();
					while (next.time < currentPhaseTime) {
						next = this.dataBeingPlayedBack.removeFirst();
						dataBeingRecorded.add(next); // Re-add ready for next time
						processForwardEvent(next);
						if (this.dataBeingPlayedBack.size() == 0) {
							break;
						}
						next = this.dataBeingPlayedBack.getFirst();
					}
				}
			}
		} else {
			if (revIt.hasNext()) {
				AbstractRecordData data = revIt.next();
				processReverseEvent(data);
			} else {
				revIt = null;
				level.nextGamePhase();
			}
		}
	}


	public void startRewind() {
		this.revIt = this.dataBeingRecorded.descendingIterator();
	}	


	private void processForwardEvent(AbstractRecordData abstract_data) {
		if (abstract_data.cmd == AbstractRecordData.CMD_MOVED) {
			EntityMovedRecordData data = (EntityMovedRecordData)abstract_data;
			AbstractEntity entity = data.entity_for_playback;//ecs.get(data.entityId);
			PositionComponent posData = (PositionComponent)entity.getComponent(PositionComponent.class);
			AnimatedComponent anim = (AnimatedComponent)entity.getComponent(AnimatedComponent.class);
			if (posData.position.equals(data.position)) {
				anim.next_animation = anim.idle_anim_name;
				//Settings.p("Shadow Idle");
			} else {
				anim.next_animation = anim.walk_anim_name;
				//Settings.p("Shadow walking");
			}
			posData.position.set(data.position);
			posData.angle_degs = data.direction;
		} else if (abstract_data.cmd == AbstractRecordData.CMD_BULLET_FIRED) {
			BulletFiredRecordData data = (BulletFiredRecordData)abstract_data;
			AbstractEntity bullet = QuantumLeagueEntityFactory.createBullet(ecs, data.shooter, data.start, data.offset);
			ecs.addEntity(bullet);
		} else if (abstract_data.cmd == AbstractRecordData.CMD_REMOVED) {
			// Needed?
		} else {
			throw new RuntimeException("Todo");
		}
	}


	private void processReverseEvent(AbstractRecordData abstract_data) {
		if (abstract_data.cmd == AbstractRecordData.CMD_MOVED) {
			EntityMovedRecordData data = (EntityMovedRecordData)abstract_data;
			AbstractEntity entity = data.entity_for_rewind;
			PositionComponent posData = (PositionComponent)entity.getComponent(PositionComponent.class);
			AnimatedComponent anim = (AnimatedComponent)entity.getComponent(AnimatedComponent.class);
			if (posData.position.equals(data.position)) {
				anim.next_animation = anim.idle_anim_name;
			} else {
				anim.next_animation = anim.walk_anim_name;
			}
			posData.position.set(data.position);
			posData.angle_degs = data.direction;
			
			Settings.p("Putting " + entity + " at pos " + data.position);
		}
	}


	@Override
	public void processEntity(AbstractEntity entity) {
		// Record entities position etc...
		if (level.isGamePhase()) {
			Settings.p("Recording " + entity);
			if (level.qlPhaseSystem.getPhaseNum012() < 2) {
				float currentPhaseTime = level.getCurrentPhaseTime();
				IsRecordable isRecordable = (IsRecordable)entity.getComponent(IsRecordable.class);
				PositionComponent posData = (PositionComponent)entity.getComponent(PositionComponent.class);
				EntityMovedRecordData data = new EntityMovedRecordData(entity, isRecordable.entity, currentPhaseTime, posData.position, posData.angle_degs);
				this.dataBeingRecorded.add(data);
			}
		}
	}


	public void addEvent(AbstractRecordData data) {
		this.dataBeingRecorded.add(data);
	}

}
