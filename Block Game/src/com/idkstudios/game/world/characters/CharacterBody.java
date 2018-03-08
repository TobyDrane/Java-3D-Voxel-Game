package com.idkstudios.game.world.characters;

import org.lwjgl.opengl.GL11;

import com.idkstudios.game.game.Game;

public class CharacterBody {

	protected boolean usingRightHand;
	protected float progressRightHand;
	protected boolean dirRightHand;
	protected float animationSpeedRightHand;
	protected float blockDistance;

	public void enableUsingRightHand() {
		usingRightHand = true;
	}

	public void disableUsingRightHand() {
		usingRightHand = false;
	}

	public void update() {
		updateRightHand();
	}

	public void updateRightHand() {
		if (usingRightHand) {
			if (dirRightHand) {
				progressRightHand -= Game.getInstance().getStep()
						* animationSpeedRightHand;
				if (progressRightHand < 0.0f) {
					dirRightHand = !dirRightHand;
				}
			} else {
				progressRightHand += Game.getInstance().getStep()
						* animationSpeedRightHand;
				if (progressRightHand > 1.0f) {
					dirRightHand = !dirRightHand;
				}
			}
		} else {
			if (progressRightHand > 0) {
				progressRightHand -= Game.getInstance().getStep()
						* animationSpeedRightHand * 0.9f;
				if (progressRightHand < 0) {
					dirRightHand = true;
				}
			}
		}
	}

	public void airSmash() {
		usingRightHand = true;
		progressRightHand = 0.1f;
	}

	public void setBlockDistance(float distance) {
		this.blockDistance = distance;
	}

	public void forceDisableUsingRightHand() {
		disableUsingRightHand();
		progressRightHand = 0.0f;
		dirRightHand = false;
	}

	public void transformToRightHand() {
		/* Transform the matix */
		GL11.glLoadIdentity();

		GL11.glTranslatef(0.2f - progressRightHand / 10.0f, -0.05f, -0.2f
				- progressRightHand / 20.0f
				* +(progressRightHand * blockDistance * 1.2f));
		GL11.glRotatef(-65, 0, 1, 0);
		GL11.glRotated(progressRightHand * 50 - 10.0d, 0, 0, 1);
		GL11.glRotated(progressRightHand * 20, 1, 1, 0);
		GL11.glRotatef(90, 0, 0, 1);
		GL11.glRotatef(20, 0, 1, 0);
	}
}
