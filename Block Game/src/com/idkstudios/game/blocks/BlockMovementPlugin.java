/*******************************************************************************
 * Copyright 2012 Martijn Courteaux <martijn.courteaux@skynet.be>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.idkstudios.game.blocks;

import com.idkstudios.game.game.Game;
import com.idkstudios.game.math.MathHelper;
import com.idkstudios.game.math.Vec3f;

public class BlockMovementPlugin {

	private Block block;
	private boolean falling;
	private Vec3f motion;
	private Vec3f additionalCoordinates;

	public BlockMovementPlugin(Block b) {
		block = b;
		motion = new Vec3f();
		additionalCoordinates = new Vec3f();
	}

	public void solveMotion() {
		/* First apply the motion */
		float delta = 0.001f;
		if (motion.lengthSquared() > delta) {
			additionalCoordinates.addFactor(motion, Game.getInstance().getStep());
			block.getAABB().getPosition().set(block.getPosition()).add(block.getAABB().getDimensions()).add(additionalCoordinates);
			block.getAABB().recalcVertices();
		}

		int moveX = 0;
		int moveY = 0;
		int moveZ = 0;
		if (additionalCoordinates.x() >= 1.0f || additionalCoordinates.x() <= -1.0f) {
			float addX = MathHelper.roundDelta(additionalCoordinates.x(), delta);
			moveX = MathHelper.roundToZero(addX);
			additionalCoordinates.setX(addX - moveX);
		}
		if (additionalCoordinates.y() >= 1.0f || additionalCoordinates.y() <= -1.0f) {
			float addY = MathHelper.roundDelta(additionalCoordinates.y(), delta);
			moveY = MathHelper.roundToZero(addY);
			additionalCoordinates.setY(addY - moveY);
		}
		if (additionalCoordinates.z() >= 1.0f || additionalCoordinates.z() <= -1.0f) {
			float addZ = MathHelper.roundDelta(additionalCoordinates.z(), delta);
			moveZ = MathHelper.roundToZero(addZ);
			additionalCoordinates.setZ(addZ - moveZ);
		}

		if (moveX != 0 || moveY != 0 || moveZ != 0) {
			Game.getInstance().getWorld().getChunkManager().rememberBlockMovement(block.getX(), block.getY(), block.getZ(), block.getX() + moveX, block.getY() + moveY, block.getZ() + moveZ);
			// _additionalCoordinates.sub(moveX, moveY, moveZ);
		}

		if (block.getY() < -40) {
			block.destroy();
		}
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean b) {
		falling = b;
	}

	public boolean isMoving() {
		return falling || motion.lengthSquared() > 0.0001f;
	}

	public Vec3f getMotion() {
		return motion;
	}

	public Vec3f getAdditionCoordinates() {
		return additionalCoordinates;
	}

}
