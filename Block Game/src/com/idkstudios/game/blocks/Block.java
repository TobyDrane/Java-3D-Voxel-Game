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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import com.idkstudios.game.Side;
import com.idkstudios.game.datastructures.AABB;
import com.idkstudios.game.datastructures.AABBObject;
import com.idkstudios.game.game.Game;
import com.idkstudios.game.inventory.InventoryItem;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;
import com.idkstudios.game.world.ChunkData;
import com.idkstudios.game.world.LightBuffer;

public abstract class Block implements AABBObject {

	protected BlockType blockType;
	protected Vec3i postion;
	protected Chunk chunk;
	protected AABB aabb;
	protected float health;

	public int distanceID;
	public float distance;

	/* List facts */
	protected boolean updating;
	protected boolean rendering;
	protected boolean renderManually;
	protected int specialBlockPoolIndex;

	public Block(BlockType type, Chunk chunk, Vec3i pos) {
		postion = pos;
		blockType = type;
		this.chunk = chunk;
		health = type.getResistance();
	}

	public void setSpecialBlockPoolIndex(int specialBlockPoolIndex) {
		this.specialBlockPoolIndex = specialBlockPoolIndex;
	}

	public int getSpecialBlockPoolIndex() {
		return specialBlockPoolIndex;
	}

	public Vec3i getPosition() {
		return postion;
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public Chunk getBlockChunk() {
		return chunk;
	}

	public int getX() {
		return postion.x();
	}

	public int getY() {
		return postion.y();
	}

	public int getZ() {
		return postion.z();
	}

	public int getChunkDataIndex() {
		return ChunkData.positionToIndex(getX() - chunk.getAbsoluteX(),
				getY(), getZ() - chunk.getAbsoluteZ());
	}

	public boolean isMoving() {
		return false;
	}

	public boolean inflictDamage(float damage) {
		health -= damage;
		if (health <= 0) {
			destroy();
			return true;
		}
		return false;
	}

	public final void destroy() {
		Game.getInstance().getWorld().getChunkManager()
				.removeBlock(getX(), getY(), getZ());
		chunk.needsNewVBO();
	}

	public void destruct() {

	}

	public synchronized void removeFromVisibilityList() {
		if (rendering) {
			chunk.getVisibleBlocks().bufferRemove(getChunkDataIndex());
			rendering = false;
			removeFromManualRenderList();
		}
	}

	public synchronized void addToVisibilityList() {
		if (!rendering) {
			chunk.getVisibleBlocks().bufferAdd(getChunkDataIndex());
			rendering = true;
		}
	}

	public synchronized void addToUpdateList() {
		if (!updating) {
			chunk.getUpdatingBlocks().bufferAdd(getChunkDataIndex());
			updating = true;
		}
	}

	public synchronized void removeFromUpdateList() {
		if (updating) {
			chunk.getUpdatingBlocks().bufferRemove(getChunkDataIndex());
			updating = false;
		}
	}

	public synchronized void addToManualRenderList() {
		if (!renderManually) {
			chunk.getManualRenderingBlocks().bufferAdd(getChunkDataIndex());
			renderManually = true;
		}
	}

	public synchronized void removeFromManualRenderList() {
		if (renderManually) {
			chunk.getManualRenderingBlocks().bufferRemove(getChunkDataIndex());
			renderManually = false;
		}
	}

	public abstract void update();

	public abstract void render(LightBuffer lightBuffer);

	public abstract void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer);

	public abstract boolean isVisible();

	public abstract AABB getAABB();

	public abstract void smash(InventoryItem item);

	public abstract void neighborChanged(Side side);

	public abstract void checkVisibility();

	public abstract int getVertexCount();

	public void performSpecialAction() {

	}

	public void saveSpecialSaveData(DataOutputStream dos) throws IOException {

	}

	public void readSpecialSaveData(DataInputStream dis) throws IOException {

	}

	public synchronized void setUpdatingFlag(boolean u) {
		updating = u;

	}

	public synchronized void setRenderingFlag(boolean v) {
		rendering = v;
	}

	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}

	public synchronized boolean isRenderingManually() {
		return renderManually;
	}

	public synchronized boolean isUpdating() {
		return updating;
	}

	public byte getMetaData() {
		return 0;
	}

	public synchronized boolean isRendering() {
		return rendering;
	}

}
