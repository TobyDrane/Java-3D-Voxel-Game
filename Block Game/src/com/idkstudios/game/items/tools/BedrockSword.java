package com.idkstudios.game.items.tools;

import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class BedrockSword extends Tool {

	public BedrockSword() {
		super("bedrock_sword", null, Material.BEDROCK, new Vec2i(4, 16), 5.0f);
	}
}
