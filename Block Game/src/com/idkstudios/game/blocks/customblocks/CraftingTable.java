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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.DefaultBlock;
import com.idkstudios.game.game.Game;
import com.idkstudios.game.inventory.CraftingTableInventory;
import com.idkstudios.game.inventory.InventoryIO;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;

public class CraftingTable extends DefaultBlock {

	static {
		try {
			Class.forName("com.idkstudios.game.inventory.CraftingTableInventory");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private CraftingTableInventory inventory;

	public CraftingTable(Chunk chunk, Vec3i pos) {
		super(BlockManager.getInstance().getBlockType(BlockManager.getInstance().blockID("crafting_table")), chunk, pos);
		inventory = new CraftingTableInventory();
		inventory.setSharedContent(Game.getInstance().getWorld().getActivePlayer().getSharedInventoryContent());
	}

	@Override
	public void performSpecialAction() {
		Game.getInstance().getWorld().setActivatedInventory(inventory);
	}

	@Override
	public void saveSpecialSaveData(DataOutputStream dos) throws IOException {
		int offset = CraftingTableInventory.CraftingTableInventoryRaster.CRAFTING_OFFSET;

		InventoryIO.writeInventory(inventory, dos, offset, 9);
	}

	@Override
	public void readSpecialSaveData(DataInputStream dis) throws IOException {
		System.out.println("Read crafting table inventory");
		int offset = CraftingTableInventory.CraftingTableInventoryRaster.CRAFTING_OFFSET;

		InventoryIO.readInventory(dis, inventory, offset);
		inventory.checkForRecipe();
	}

}
