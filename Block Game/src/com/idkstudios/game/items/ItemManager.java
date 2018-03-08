package com.idkstudios.game.items;

import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.inventory.InventoryItem;

public class ItemManager {

	private static ItemManager instance;
	public static final int ITEM_OFFSET = 128;

	public static ItemManager getInstance() {
		if (instance == null) {
			instance = new ItemManager();
		}
		return instance;
	}

	private InventoryItem[] items;

	private ItemManager() {
		items = new InventoryItem[Short.MAX_VALUE - ITEM_OFFSET];
	}

	public InventoryItem getInventoryItem(short type) {
		if (type < ITEM_OFFSET) {
			return BlockManager.getInstance().getBlockType((byte) type);
		}
		return items[type - ITEM_OFFSET];
	}

	public void putInventoryItem(short type, InventoryItem item) {
		if (type < ITEM_OFFSET) {
			return;
		}
		System.out.println("Put Item: " + type + " (" + item.getName() + " <"
				+ item.getClass().getName() + ">)");
		items[type - ITEM_OFFSET] = item;
	}

	public short getItemID(String name) {
		byte blockType = BlockManager.getInstance().blockID(name);
		if (blockType != -1) {
			return blockType;
		}

		for (int i = 0; i < items.length; ++i) {
			InventoryItem item = items[i];
			if (item != null && item.getName().equalsIgnoreCase(name)) {
				return (short) (i + ITEM_OFFSET);
			}
		}
		return -1;
	}
}
