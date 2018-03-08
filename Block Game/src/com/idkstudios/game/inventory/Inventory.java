package com.idkstudios.game.inventory;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.idkstudios.game.game.FontStorage;
import com.idkstudios.game.items.ItemManager;
import com.idkstudios.game.math.MathHelper;
import com.idkstudios.game.rendering.GLFont;

public abstract class Inventory {

	private InventoryPlace[] places;
	protected InventoryRaster raster;
	protected InventoryPlace draggingItem;
	protected SharedInventoryContent sharedContent;
	protected boolean dragging;

	public Inventory(int size) {
		places = new InventoryPlace[size];
	}

	public int size() {
		return places.length;
	}

	public final void update() {
	}

	public abstract void renderInventory();

	public abstract boolean acceptsToTakeItem(int index, InventoryItem item);

	public abstract boolean acceptsToPutItem(int index, InventoryItem item);

	protected abstract void inventoryEvent(final InventoryEvent evt);

	public boolean addToInventory(InventoryItem item) {
		short type = item.getInventoryTypeID();
		boolean stackable = item.isStackable();
		if (stackable) {
			int freeIndex = -1;
			for (int i = 0; i < places.length; ++i) {
				if (!acceptsToTakeItem(i, item)) {
					continue;
				}
				if (getInventoryPlace(i) != null) {
					if (getInventoryPlace(i).isStack()
							&& getInventoryPlace(i).stack.itemType == type) {
						getInventoryPlace(i).stack.increaseItemCount();
						inventoryEvent(new InventoryEvent(InventoryEvent.DROP,
								i, 1, item.getInventoryTypeID()));
						return true;
					}
				} else if (freeIndex == -1) {
					freeIndex = i;
				}
			}
			// No stack of this kind yet
			if (freeIndex != -1) {
				setContentAt(new InventoryPlace(freeIndex,
						new InventoryItemStack(type)), freeIndex);
				inventoryEvent(new InventoryEvent(InventoryEvent.DROP,
						freeIndex, 1, item.getInventoryTypeID()));
				return true;
			}
			return false;
		} else {
			// Search an empty place
			for (int i = 0; i < places.length; ++i) {
				if (!acceptsToTakeItem(i, item)) {
					continue;
				}
				if (places[i] == null) {
					setContentAt(new InventoryPlace(i, item), i);
					inventoryEvent(new InventoryEvent(InventoryEvent.DROP, i,
							1, item.getInventoryTypeID()));
					return true;
				}
			}
			return false;
		}
	}

	public int getInventoryPlaceContentType(int index) {
		InventoryPlace p = getInventoryPlace(index);
		if (p == null) {
			return 0;
		}
		return p.getItemTypeOrStackType();
	}

	public void setContentAt(InventoryPlace obj, int index) {
		if (sharedContent != null && index < sharedContent.size()) {
			sharedContent.setContentAt(index, obj);
		} else {
			places[index] = obj;
		}
		if (obj != null) {
			obj.index = index;
		}
	}

	public InventoryPlace getInventoryPlace(int index) {
		if (sharedContent != null && index < sharedContent.size()) {
			return sharedContent.getContentAt(index);
		}
		return places[index];
	}

	public static class InventoryPlace {

		private int index;
		private boolean isStack;
		private InventoryItemStack stack;
		private InventoryItem item;

		public InventoryPlace(int index, InventoryItem item) {
			this.index = index;
			this.item = item;
			isStack = false;
		}

		public InventoryPlace(int index, InventoryItemStack stack) {
			this.index = index;
			this.stack = stack;
			stack.place = this;
			isStack = true;
		}

		public void render() {
			if (isStack) {
				stack.render();
			} else {
				item.renderInventoryItem();
			}
		}

		public boolean isStack() {
			return isStack;
		}

		public InventoryItemStack getStack() {
			return stack;
		}

		public InventoryItem getItem() {
			return item;
		}

		public int getItemTypeOrStackType() {
			return isStack ? stack.itemType : item.getInventoryTypeID();
		}

		public InventoryItem getItemOrStackType() {
			return isStack ? ItemManager.getInstance().getInventoryItem(
					stack.itemType) : item;
		}

		public int getItemCount() {
			return isStack ? stack.itemCount : 1;
		}
	}

	public class InventoryItemStack {

		private InventoryPlace place;
		private int itemCount;
		private short itemType;

		public InventoryItemStack(short itemType) {
			this.itemType = itemType;
			itemCount = 1;
		}

		public InventoryItemStack(short type, int count) {
			this(type);
			itemCount = count;
		}

		public void setItemCount(int itemCount) {
			this.itemCount = itemCount;
		}

		public int getItemCount() {
			return itemCount;
		}

		public int getItemType() {
			return itemType;
		}

		public void increaseItemCount() {
			itemCount++;
		}

		public void decreaseItemCount() {
			itemCount--;
			System.out.println(itemCount);
			if (itemCount <= 0) {
				setContentAt(null, place.index);
			}
		}

		private void render() {
			/* Render the little image */
			ItemManager.getInstance().getInventoryItem(itemType)
					.renderInventoryItem();

			/* Render stack amount */
			GLFont font = FontStorage.getFont("InventoryAmount");
			GL11.glColor3f(0.0f, 0.0f, 0.0f);
			font.print(-12, -18, String.valueOf(itemCount));
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			font.print(-13, -17, String.valueOf(itemCount));
		}

		public void addAmount(int count) {
			if (count < 0) {
				subtractAmount(-count);
			} else {
				itemCount += count;
			}
		}

		private void subtractAmount(int count) {
			if (count < 0) {
				addAmount(-count);
			} else {
				itemCount -= count;
				if (itemCount <= 0) {
					setContentAt(null, place.index);
				}
			}
		}
	}

	public void mouseEvent() {
		int currentMouseIndex = raster.getCellAt(Mouse.getEventX(),
				Mouse.getEventY());
		InventoryPlace draggingItemTemp = draggingItem;
		if (currentMouseIndex == -1) // No current cell
		{
			if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0
					&& dragging) // Cancel
									// dragging
			{
				if (acceptsToPutItem(draggingItem.index,
						draggingItem.getItemOrStackType())) {
					if (getInventoryPlace(draggingItem.index) == null) {
						setContentAt(draggingItem, draggingItem.index);
						dragging = false;
						draggingItem = null;
						inventoryEvent(new InventoryEvent(
								InventoryEvent.CANCELED,
								draggingItemTemp.index,
								draggingItemTemp.getItemCount(),
								draggingItemTemp.getItemTypeOrStackType()));
					} else {
						InventoryPlace place = getInventoryPlace(draggingItem.index);
						if (place.isStack()
								&& place.getItemTypeOrStackType() == draggingItemTemp
										.getItemTypeOrStackType()) {
							place.getStack().addAmount(
									draggingItemTemp.getItemCount());
							dragging = false;
							draggingItem = null;
							inventoryEvent(new InventoryEvent(
									InventoryEvent.CANCELED,
									draggingItemTemp.index,
									draggingItemTemp.getItemCount(),
									draggingItemTemp.getItemTypeOrStackType()));
						}
					}
				}
			}
			return;
		}
		InventoryPlace currentMousePlace = getInventoryPlace(currentMouseIndex);
		boolean currentMousePlaceContainsObject = currentMousePlace != null;
		int draggingItemType = draggingItem == null ? -1 : draggingItem
				.getItemTypeOrStackType();

		if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0) // Select
																		// /
																		// Drop
																		// all
		{
			if (dragging) // Drop ALL
			{
				if (!acceptsToPutItem(currentMouseIndex,
						draggingItem.getItemOrStackType())) // Cancel
															// movement
				{
					// Check if it was taken from a forbidden place
					if (!acceptsToPutItem(draggingItem.index,
							draggingItem.getItemOrStackType())) {
						// Can't cancel the movement because where it was taken
						// from a place where you can't put items
						// So, check for alternative actions
						if (currentMousePlaceContainsObject) {
							if (draggingItem.isStack()
									&& currentMousePlace.isStack()) {
								if (currentMousePlace.getItemTypeOrStackType() == draggingItemType) {
									draggingItem.getStack().addAmount(
											currentMousePlace.getItemCount());
									setContentAt(null, currentMouseIndex);
									inventoryEvent(new InventoryEvent(
											InventoryEvent.TAKE,
											currentMouseIndex,
											currentMousePlace.getItemCount(),
											draggingItemType));
								}
							}
						}
						return;
					}
					// Check if it came from a stack
					InventoryPlace sourcePlace = getInventoryPlace(draggingItem.index);
					int count = draggingItem.getItemCount();
					if (sourcePlace == null) {
						setContentAt(draggingItem, draggingItem.index);
					} else if (sourcePlace.isStack()
							&& sourcePlace.getItemTypeOrStackType() == draggingItemType) {
						sourcePlace.getStack().addAmount(count);
					}
					dragging = false;
					draggingItem = null;
					inventoryEvent(new InventoryEvent(InventoryEvent.CANCELED,
							draggingItemTemp.index,
							draggingItemTemp.getItemCount(), draggingItemType));
				} else
				// Accepts to drop item
				{
					if (currentMousePlaceContainsObject) // There is already
															// something
					{
						if (currentMousePlace.isStack()
								&& currentMousePlace.getItemTypeOrStackType() == draggingItemType) // Add
																									// it
																									// to
																									// the
																									// stack
						{
							currentMousePlace.getStack().addAmount(
									draggingItem.getItemCount());
							dragging = false;
							draggingItem = null;
							inventoryEvent(new InventoryEvent(
									InventoryEvent.DROP, currentMouseIndex,
									draggingItemTemp.getItemCount(),
									draggingItemType));
						} else
						// Swap the dragging item with the cell content
						{
							setContentAt(draggingItem, currentMouseIndex);
							draggingItem = currentMousePlace;
							inventoryEvent(new InventoryEvent(
									InventoryEvent.TAKE, currentMouseIndex,
									currentMousePlace.getItemCount(),
									currentMousePlace.getItemTypeOrStackType()));
							inventoryEvent(new InventoryEvent(
									InventoryEvent.DROP, currentMouseIndex,
									draggingItem.getItemCount(),
									draggingItem.getItemTypeOrStackType()));
						}
					} else {
						setContentAt(draggingItem, currentMouseIndex);
						dragging = false;
						draggingItem = null;
						inventoryEvent(new InventoryEvent(InventoryEvent.DROP,
								currentMouseIndex,
								draggingItemTemp.getItemCount(),
								draggingItemType));
					}
				}
			} else
			// Select ALL
			{
				if (!currentMousePlaceContainsObject) {
					return;
				}
				draggingItem = currentMousePlace;
				dragging = true;
				setContentAt(null, currentMouseIndex);
				inventoryEvent(new InventoryEvent(InventoryEvent.TAKE,
						currentMouseIndex, draggingItem.getItemCount(),
						draggingItem.getItemTypeOrStackType()));
			}
		} else if (Mouse.getEventButtonState() && Mouse.getEventButton() == 1) // Drop
																				// ONE
																				// /
																				// Select
																				// half
		{
			if (dragging) // Drop ONE
			{
				boolean draggingItemIsStack = draggingItem.isStack();

				if (currentMousePlaceContainsObject) // Drop place contains
														// already something
				{
					if (currentMousePlace.isStack()
							&& currentMousePlace.getItemTypeOrStackType() == draggingItemType) // Same
																								// type
					{
						if ((draggingItem.isStack() && --draggingItem
								.getStack().itemCount == 0)
								|| !draggingItem.isStack()) {
							draggingItem = null;
							dragging = false;
						}

						currentMousePlace.getStack().increaseItemCount();
						inventoryEvent(new InventoryEvent(InventoryEvent.DROP,
								currentMouseIndex, 1, draggingItemType));
					}
				} else
				// Drop place is empty
				{
					if ((draggingItemIsStack && --draggingItem.getStack().itemCount == 0)
							|| !draggingItem.isStack()) {
						draggingItem = null;
						dragging = false;
					}
					if (draggingItemIsStack) {
						setContentAt(
								new InventoryPlace(currentMouseIndex,
										new InventoryItemStack(
												(short) draggingItemType)),
								currentMouseIndex);
					} else {
						setContentAt(new InventoryPlace(currentMouseIndex,
								draggingItemTemp.getItemOrStackType()),
								currentMouseIndex);
					}
					inventoryEvent(new InventoryEvent(InventoryEvent.DROP,
							currentMouseIndex, 1, draggingItemType));
				}
			} else
			// Select half
			{
				if (currentMousePlaceContainsObject
						&& currentMousePlace.isStack()) {
					int newStackSize = MathHelper.round(currentMousePlace
							.getItemCount() / 2.0f);
					dragging = true;
					draggingItem = new InventoryPlace(currentMouseIndex,
							new InventoryItemStack((short) currentMousePlace
									.getItemTypeOrStackType(), newStackSize));
					currentMousePlace.getStack().subtractAmount(newStackSize);
					inventoryEvent(new InventoryEvent(InventoryEvent.TAKE,
							currentMouseIndex, newStackSize,
							currentMousePlace.getItemTypeOrStackType()));
				}
			}
		}
	}
}
