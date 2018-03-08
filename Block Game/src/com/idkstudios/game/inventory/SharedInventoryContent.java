package com.idkstudios.game.inventory;

import com.idkstudios.game.inventory.Inventory.InventoryPlace;

public class SharedInventoryContent {

	private int size;
	private Inventory.InventoryPlace[] content;

	public SharedInventoryContent(int size) {
		this.size = size;
		content = new Inventory.InventoryPlace[size];
	}

	public InventoryPlace getContentAt(int index) {
		return content[index];
	}

	public void setContentAt(int index, InventoryPlace content) {
		this.content[index] = content;
	}

	public int size() {
		return size;
	}
}
