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

import java.nio.FloatBuffer;

import com.idkstudios.game.Side;
import com.idkstudios.game.datastructures.AABB;
import com.idkstudios.game.game.Game;
import com.idkstudios.game.inventory.InventoryItem;
import com.idkstudios.game.math.MathHelper;
import com.idkstudios.game.math.Vec3f;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;
import com.idkstudios.game.world.LightBuffer;

public class DefaultBlock extends Block {

	public DefaultBlockBrush defaultBlockBrush;
	
	public static final Vec3f HALF_BLOCK_SIZE = new Vec3f(0.5f, 0.5f, 0.5f);
	public static final Vec3f BLOCK_SIZE = new Vec3f(1.0f, 1.0f, 1.0f);

	public static final byte ALL_FACES = 0x3f; // 0011 1111

	private boolean needVisibilityCheck;
	private byte faceMask;
	private boolean visible;

	private BlockMovementPlugin _movement;

	public DefaultBlock(BlockType type, Chunk chunk, Vec3i pos) {
		super(type, chunk, pos);
		aabb = null;
		needVisibilityCheck = true;
		defaultBlockBrush = new DefaultBlockBrush();
	}

	private void createMovementPlugin() {
		_movement = new BlockMovementPlugin(this);
	}

	private void destoryMovementPlugin() {
		_movement = null;
	}

	public boolean hasMovementPlugin() {
		return _movement != null;
	}

	@Override
	public void update() {
		if (!blockType.isFixed()) {
			byte supportingBlock = chunk.getBlockTypeAbsolute(getX(),
					getY() - 1, getZ(), false, false, false);
			if (supportingBlock == 0) {
				if (!isFalling()) {
					/* Start falling */
					if (!hasMovementPlugin()) {
						createMovementPlugin();
						addToManualRenderList();
						needVisibilityCheck = true;
						chunk.needsNewVBO();
					}
					_movement.setFalling(true);
					chunk.updateVisibilityFor(getX(), getY(), getZ());
					chunk.updateVisibilityForNeigborsOf(getX(), getY(), getZ());
					chunk.notifyNeighborsOf(getX(), getY(), getZ());
				}
			} else {
				if (isFalling()) {
					/* Stop falling */
					_movement.setFalling(false);
					chunk.updateVisibilityFor(getX(), getY(), getZ());
					chunk.updateVisibilityForNeigborsOf(getX(), getY(), getZ());
					chunk.notifyNeighborsOf(getX(), getY(), getZ());
					removeFromManualRenderList();
					needVisibilityCheck = true;
					chunk.needsNewVBO();
				}
			}

			if (isFalling()) {
				float step = Game.getInstance().getStep();
				_movement.getMotion().setY(
						_movement.getMotion().y() - (9.81f * step));
			} else if (hasMovementPlugin()) {
				_movement.getAdditionCoordinates().setY(0.0f);
				_movement.getMotion().setY(0.0f);
				/* Update pos */
				aabb.getPosition().set(postion).add(HALF_BLOCK_SIZE)
						.add(_movement.getAdditionCoordinates());
				aabb.recalcVertices();
			}
		}

		if (isMoving()) {
			_movement.solveMotion();
		} else {
			destoryMovementPlugin();

			/* Remove this block from the update list */
			removeFromUpdateList();
			removeFromManualRenderList();

			/* Un-specialize this block */
			unspecialize();
		}
	}

	private void unspecialize() {
		chunk.setDefaultBlockAbsolute(getX(), getY(), getZ(), blockType,
				(byte) 0, false, false, false);
	}

	private boolean isFalling() {
		return hasMovementPlugin() && _movement.isFalling();
	}

	/**
	 * Sets the bit in the face mask which represents the visibility of the
	 * face.
	 * 
	 * @param face
	 * @param flag
	 */
	public void setFaceVisible(Side face, boolean flag) {
		if (flag) {
			faceMask |= 1 << face.ordinal();
		} else {
			faceMask &= ~(1 << face.ordinal());
		}
	}

	@Override
	public void render(LightBuffer lightBuffer) {
		renderManually = true;
		rendering = true;
		if (isVisible()) {
			blockType.getBrush().setPosition(getAABB().getPosition());
			blockType.getDefaultBlockBrush().renderFaces(faceMask,
					lightBuffer);
		}
	}

	@Override
	public boolean isVisible() {
		if (needVisibilityCheck) {
			checkVisibility();
		}
		return visible;
	}

	public void checkVisibility() {

		boolean preVisibility = faceMask != 0;
		byte preMask = faceMask;
		if (isMoving()) {
			faceMask = ALL_FACES;
		} else {
			if (chunk != null) {
				for (int i = 0; i < 6; ++i) {
					Side side = Side.getSide(i);
					Vec3i normal = side.getNormal();
					Chunk c = chunk.getChunkContaining(
							getX() + normal.x(), getY() + normal.y(), getZ()
									+ normal.z(), false, false, false);
					if (c == null) {
						setFaceVisible(side, false);
					} else {
						byte block = c.getBlockTypeAbsolute(
								getX() + normal.x(), getY() + normal.y(),
								getZ() + normal.z(), false, false, false);
						if (block != 0) {
							if (false || !BlockManager.getInstance()
									.getBlockType(block).hasNormalAABB()) {
								setFaceVisible(side, true);
							} else {
								setFaceVisible(side, false);
							}
						} else {
							setFaceVisible(side, true);
						}
					}
				}
			}
			/* Make the bottom layer invisible */
			if (getY() == 0) {
				setFaceVisible(Side.BOTTOM, false);
			}
		}
		needVisibilityCheck = false;

		visible = faceMask != 0;

		if (visible != preVisibility) {
			if (visible) {
				addToVisibilityList();
			} else {
				removeFromVisibilityList();
			}

			chunk.needsNewVBO();
		} else if (preMask != faceMask) {
			chunk.needsNewVBO();
		}
	}

	@Override
	public synchronized AABB getAABB() {
		if (aabb == null) {
			aabb = new AABB(new Vec3f(getPosition()).add(HALF_BLOCK_SIZE),
					new Vec3f(HALF_BLOCK_SIZE));
		}
		return aabb;
	}

	@Override
	public void smash(InventoryItem item) {
	}

	@Override
	public synchronized void neighborChanged(Side side) {
		needVisibilityCheck = true;
		if (!rendering) {
			checkVisibility();
		}
		if (side == Side.BOTTOM && !blockType.isFixed()) {
			addToUpdateList();
		}
	}

	@Override
	public boolean isMoving() {
		return _movement != null && _movement.isFalling();
	}

	public byte getFaceMask() {
		if (needVisibilityCheck) {
			checkVisibility();
		}
		return faceMask;
	}

	@Override
	public int getVertexCount() {
		if (isRenderingManually()) {
			return 0;
		} else {
			return 4 * MathHelper.cardinality(faceMask);
		}

	}

	public DefaultBlockBrush getDefaultBlockBrush() {
		return defaultBlockBrush;
	}
	
	@Override
	public String toString() {
		return blockType.getName() + " " + postion.toString();
	}

	@Override
	public void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer) {
		if (!isRenderingManually()) {
			blockType.getDefaultBlockBrush().setFaceMask(faceMask);
			blockType.getDefaultBlockBrush().storeInVBO(vbo, getX() + 0.5f,
					getY() + 0.5f, getZ() + 0.5f, lightBuffer);
		}
	}

}
