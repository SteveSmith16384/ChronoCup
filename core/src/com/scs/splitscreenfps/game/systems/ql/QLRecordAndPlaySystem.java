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
	private float currentPhaseTime;

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
		currentPhaseTime = level.getCurrentPhaseTime();

		super.process();

		if (level.isGamePhase()) {
			// Play from prev recording
			if (this.level.qlPhaseSystem.getPhaseNum012() > 0) {
				if (this.dataBeingPlayedBack.size() > 0) {
					AbstractRecordData next = this.dataBeingPlayedBack.getFirst();
					while (next.time < this.currentPhaseTime) {
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
			//if (ecs.containsEntity(data.entityId)) {
			AbstractEntity entity = data.entity;//ecs.get(data.entityId);
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
			/*} else {
				Settings.p("No entity!");
			}*/
		} else if (abstract_data.cmd == AbstractRecordData.CMD_BULLET_FIRED) {
			BulletFiredRecordData data = (BulletFiredRecordData)abstract_data;
			AbstractEntity bullet = QuantumLeagueEntityFactory.createBullet(ecs, data.shooter, data.start, data.offset);
			ecs.addEntity(bullet);
		} else if (abstract_data.cmd == AbstractRecordData.CMD_REMOVED) {
			/*AbstractEntity entity = ecs.get(data.entityId);
			IsRecordable isRecordable = (IsRecordable)entity.getComponent(IsRecordable.class);
			isRecordable.active = false;
			entity.remove();*/
		} else {
			throw new RuntimeException("Todo");
		}
	}


	private void processReverseEvent(AbstractRecordData abstract_data) {
		if (abstract_data.cmd == AbstractRecordData.CMD_MOVED) {
			EntityMovedRecordData data = (EntityMovedRecordData)abstract_data;
			//if (ecs.containsEntity(data.entityId)) {
			AbstractEntity entity = data.entity;//ecs.get(data.entityId);
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
			/*} else {
				//Settings.p("No entity!");
			}*/
		}
	}


	@Override
	public void processEntity(AbstractEntity entity) {
		if (level.isGamePhase()) {
			// Record entities position etc...
			if (level.qlPhaseSystem.getPhaseNum012() < 2) {
				IsRecordable isRecordable = (IsRecordable)entity.getComponent(IsRecordable.class);
				PositionComponent posData = (PositionComponent)entity.getComponent(PositionComponent.class);
				EntityMovedRecordData data = new EntityMovedRecordData(isRecordable.entity, currentPhaseTime, posData.position, posData.angle_degs);
				this.dataBeingRecorded.add(data);
			}
		}
	}


	public void addEvent(AbstractRecordData data) {
		this.dataBeingRecorded.add(data);
	}

}
