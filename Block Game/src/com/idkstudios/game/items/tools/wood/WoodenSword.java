package com.idkstudios.game.items.tools.wood;

import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class WoodenSword extends Tool {

	public WoodenSword() {
		super("wooden_sword", null, Material.WOOD, new Vec2i(0, 4), 7.0f);
	}
}
