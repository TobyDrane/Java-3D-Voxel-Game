package com.idkstudios.game.recipes;

import com.idkstudios.game.items.ItemManager;

public class Recipe {

	private int resultingItem;
	private int resultAmount;
	private int[][] ingredients;
	private int width;
	private int height;

	public Recipe(String i, int result, int resultAmount) {
		this.resultAmount = resultAmount;
		this.resultingItem = result;
		this.ingredients = parseIngredients(i);
		this.height = ingredients.length;
		this.width = ingredients[0].length;
	}

	public static int[][] parseIngredients(String ingredients) {
		String[] lines = ingredients.split(";");
		int height = lines.length;

		int width = 0;
		for (String line : lines) {
			width = Math.max(width, line.split(",").length);
		}
		int[][] ret = new int[height][width];

		for (int y = 0; y < height; ++y) {
			String line = lines[y];
			String[] elems = line.split(",");
			for (int x = 0; x < elems.length; ++x) {
				int elem = 0;
				if (elems[x].length() > 0) {
					try {
						elem = Integer.parseInt(elems[x]);
					} catch (Exception e) {
						elem = ItemManager.getInstance().getItemID(elems[x]);
					}
				}

				ret[y][x] = elem;
			}
		}

		return ret;
	}

	public Recipe(String recipe, String result, int count) {
		this(recipe, ItemManager.getInstance().getItemID(result), count);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int[][] getIngredients() {
		return ingredients;
	}

	public int getResultingItem() {
		return resultingItem;
	}

	public int getResultAmount() {
		return resultAmount;
	}

	public boolean equalsRecipe(int[][] rec) {
		int minX = 10;
		int minY = 10;
		int maxX = 0;
		int maxY = 0;
		boolean containsData = false;

		for (int y = 0; y < rec.length; ++y) {
			for (int x = 0; x < rec[0].length; ++x) {
				int elem = rec[y][x];
				if (elem != 0) {
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
					containsData = true;
				}
			}
		}
		if (!containsData) {
			return false;
		}

		int w = maxX - minX + 1;
		int h = maxY - minY + 1;

		if (w != width || h != height) {
			return false;
		}

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (rec[minY + y][minX + x] != ingredients[y][x]) {
					return false;
				}
			}
		}
		return true;
	}
}
