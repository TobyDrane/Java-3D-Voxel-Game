package com.idkstudios.game.items;

import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.BlockType;
import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.game.TextureStorage;
import com.idkstudios.game.math.Vec2i;

public abstract class Tool extends TexturedItem {

	public enum Material {

		WOOD, STONE, IRON, DIAMOND, GOLD, BEDROCK
	}

	private BlockClass blockClass;
	private float health;
	private Material material;

	protected Tool(String name, BlockClass blockClass, Material material,
			Vec2i texturePosition, float animationSpeed) {
		super(name, animationSpeed, TextureStorage.getTexture("items"),
				texturePosition);

		this.blockClass = blockClass;
		this.material = material;
	}

	@Override
	public void update() {
		// Do nothing
	}

	@Override
	public float calcDamageFactorToBlock(byte block) {
		BlockType bt = BlockManager.getInstance().getBlockType(block);

		if (bt.getBlockClass() == getBlockClass()) {
			if (material == null)
				return 4.0f;
			return (material.ordinal() / 4.0f) + 4.0f;
		}
		return 1.2f;
	}

	@Override
	public float calcDamageInflictedByBlock(byte block) {
		if (material == Material.BEDROCK) {
			return 0.0f;
		}
		BlockType bt = BlockManager.getInstance().getBlockType(block);
		float materialResistance = (0.2f / (float) Math.pow(
				material.ordinal(), 1.2d));
		if (bt.getBlockClass() == getBlockClass()) {
			return materialResistance * (0.05f * bt.getResistance());
		}
		return bt.getResistance() * materialResistance;
	}

	public BlockClass getBlockClass() {
		return blockClass;
	}

	@Override
	public boolean isStackable() {
		return false;
	}

	@Override
	public void inflictDamage(float toolDamage) {
		this.health -= toolDamage;
	}

	@Override
	public float getHealth() {
		return health;
	}
}
