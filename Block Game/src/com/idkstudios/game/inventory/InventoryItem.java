package com.idkstudios.game.inventory;

import org.lwjgl.opengl.GL11;

import com.idkstudios.game.GameObject;
import com.idkstudios.game.world.LightBuffer;

public abstract class InventoryItem extends GameObject {

	protected String name;
	protected float animationSpeed;

	protected InventoryItem(String name, float animationSpeed) {
		this.name = name;
		this.animationSpeed = animationSpeed;
	}

	public abstract float calcDamageFactorToBlock(byte block);

	public abstract float calcDamageInflictedByBlock(byte block);

	public abstract short getInventoryTypeID();

	public abstract void renderInventoryItem();

	public abstract float getHealth();

	public abstract boolean isStackable();

	public abstract void inflictDamage(float toolDamage);

	public String getName() {
		return name;
	}

	public void renderHoldableObject(LightBuffer lightBuffer) {
		GL11.glPushMatrix();
		float scale = 0.1f / 16.0f;
		float light = lightBuffer.get(1, 1, 1) / 30.001f;
		GL11.glScalef(scale, scale, scale);
		GL11.glColor3f(0.5f * light, 0.5f * light, 0.5f * light);
		/* Render the texture */
		for (float i = 0.0f; i < 0.02f; i += 0.002f) {
			if (i > 0.016f) {
				GL11.glColor3f(1.0f * light, 1.0f * light, 1.0f * light);
			}
			renderInventoryItem();
			GL11.glTranslatef(0, 0, 0.002f / scale);
		}
		GL11.glPopMatrix();
	}

	@Override
	public void render() {
		throw new UnsupportedOperationException(
				"You shouldn't use this method!");
	}

	public float getAnimationSpeed() {
		return animationSpeed;
	}
}
