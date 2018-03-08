package com.idkstudios.game.blocks.customblocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.DefaultBlock;
import com.idkstudios.game.game.Game;
import com.idkstudios.game.inventory.DoubleContainerInventory;
import com.idkstudios.game.inventory.InventoryIO;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;

public class Container extends DefaultBlock {

	static {
		try {
			Class.forName("com.idkstudios.game.inventory.DoubleContainerInventory");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private DoubleContainerInventory inventory;

	public Container(Chunk chunk, Vec3i pos) {
		super(BlockManager.getInstance().getBlockType(BlockManager.getInstance().blockID("container")), chunk, pos);
		inventory = new DoubleContainerInventory();
		inventory.setSharedContent(Game.getInstance().getWorld().getActivePlayer().getSharedInventoryContent());
	}

	@Override
	public void performSpecialAction() {
		Game.getInstance().getWorld().setActivatedInventory(inventory);
	}

	@Override
	public void saveSpecialSaveData(DataOutputStream dos) throws IOException {
		int offset = DoubleContainerInventory.DoubleContainerInventoryRaster.CONTENT_OFFSET;

		InventoryIO.writeInventory(inventory, dos, offset, 54);
	}

	@Override
	public void readSpecialSaveData(DataInputStream dis) throws IOException {
		System.out.println("Read crafting table inventory");
		int offset = DoubleContainerInventory.DoubleContainerInventoryRaster.CONTENT_OFFSET;

		InventoryIO.readInventory(dis, inventory, offset);
	}
}
