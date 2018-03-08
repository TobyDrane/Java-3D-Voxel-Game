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
package com.idkstudios.game.blocks.customblocks;

import java.nio.FloatBuffer;

import com.idkstudios.game.Side;
import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.CrossedBlock;
import com.idkstudios.game.blocks.CrossedBlockBrush;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;
import com.idkstudios.game.world.LightBuffer;

public class TallGrass extends CrossedBlock {

	private static final int LENGHT_COUNT;
	private static final CrossedBlockBrush[] BLOCK_BRUSHES;

	static {
		LENGHT_COUNT = 6;
		BLOCK_BRUSHES = new CrossedBlockBrush[LENGHT_COUNT];
		for (int i = 0; i < LENGHT_COUNT; ++i) {
			BLOCK_BRUSHES[i] = new CrossedBlockBrush();
			BLOCK_BRUSHES[i].setTexturePosition(10 + i, 11);
			BLOCK_BRUSHES[i].create();
		}
	}

	public static void RELEASE_STATIC_CONTENT() {
		for (int i = 0; i < LENGHT_COUNT; ++i) {
			BLOCK_BRUSHES[i].release();
		}
	}

	private int length;

	public TallGrass(Chunk chunk, Vec3i pos, int length) {
		super(BlockManager.getInstance().getBlockType("tallgrass"), chunk, pos);

		this.length = length;
	}

	@Override
	public void render(LightBuffer lightBuffer) {
		BLOCK_BRUSHES[length].setPosition(getX() + 0.5f, getY() + 0.5f,
				getZ() + 0.5f);
		BLOCK_BRUSHES[length].render(lightBuffer);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public void neighborChanged(Side side) {
		if (side == Side.BOTTOM) {
			if (chunk.getBlockTypeAbsolute(getX(), getY() - 1, getZ(), false,
					false, false) <= 0) {
				destroy();
			}
		}
	}

	@Override
	public void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer) {
		BLOCK_BRUSHES[length].storeInVBO(vbo, getX() + 0.5f, getY() + 0.5f,
				getZ() + 0.5f, lightBuffer);
	}

	@Override
	public byte getMetaData() {
		return (byte) length;
	}

}
