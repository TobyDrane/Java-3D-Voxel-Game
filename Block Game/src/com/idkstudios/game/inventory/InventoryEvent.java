package com.idkstudios.game.inventory;

public class InventoryEvent {

	public static final int TAKE = 1;
	public static final int DROP = 2;
	public static final int CANCELED = 3;

	private int action;
	private int count;
	private int index;
	private int type;

	public InventoryEvent(int _action, int _index, int _count, int _type) {
		this.action = _action;
		this.index = _index;
		this.count = _count;
		this.type = _type;
	}

	public int getAction() {
		return action;
	}

	public int getCount() {
		return count;
	}

	public int getIndex() {
		return index;
	}

	public int getType() {
		return type;
	}
}
