package com.idkstudios.game.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.idkstudios.game.game.ControlSettings.KeyboardPreset;
import com.idkstudios.game.math.Vec3f;

public class Configuration {

	/* Predefined viewing distances */
	public static final float VIEWING_DISTANCE_ULTRA = 120.0f;
	public static final float VIEWING_DISTANCE_FAR = 90.0f;
	public static final float VIEWING_DISTANCE_NORMAL = 60.0f;
	public static final float VIEWING_DISTANCE_SHORT = 30.0f;
	public static final float VIEWING_DISTANCE_TINY = 15.0f;

	/* Configurations */
	private float viewingDistance;
	private float fireViewingDistance;
	private float maxPlayerEditingDistance;
	private float fovy;
	private int width;
	private int height;
	private boolean fullscreen;
	private int fps;
	private boolean vsync;
	private boolean updateVisibleOnly;
	private String texturePack;
	private Vec3f fogColor;
	private ControlSettings.KeyboardPreset keyboard;

	public Configuration() {
		fogColor = new Vec3f(0.75f, 0.75f, 1.0f);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setViewingDistance(float viewingDistance) {
		this.viewingDistance = viewingDistance;
	}

	public float getViewingDistance() {
		return viewingDistance;
	}

	public void setDisplaySettings(int w, int h, boolean fullscreen) {
		this.width = w;
		this.height = h;
		this.fullscreen = fullscreen;
	}

	public void setMaximumPlayerEditingDistance(float maxPlayerEditingDistance) {
		this.maxPlayerEditingDistance = maxPlayerEditingDistance;
	}

	public float getMaximumPlayerEditingDistance() {
		return maxPlayerEditingDistance;
	}

	public Vec3f getFogColor() {
		return fogColor;
	}

	public int getFPS() {
		return fps;
	}

	public void setFPS(int _fps) {
		this.fps = _fps;
	}

	public boolean getVSync() {
		return vsync;
	}

	public boolean getUpdateVisibleOnly() {
		return updateVisibleOnly;
	}

	public String getTexturePack() {
		return texturePack;
	}

	public float getFOVY() {
		return fovy;
	}

	public void loadFromFile(String string) throws IOException {
		File f = new File(string);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(f), "UTF-8"));

		String line;
		while ((line = br.readLine()) != null) {
			if (line.trim().isEmpty() || line.trim().startsWith("#")) {
				continue;
			}
			String[] prop = line.split("=");
			String p = prop[0];
			String v = prop[1];

			if (p.equals("fullscreen")) {
				fullscreen = Boolean.parseBoolean(v);
			} else if (p.equals("width")) {
				width = Integer.parseInt(v);
			} else if (p.equals("height")) {
				height = Integer.parseInt(v);
			} else if (p.equals("vsync")) {
				vsync = Boolean.parseBoolean(v);
			} else if (p.equals("update_visible_only")) {
				updateVisibleOnly = Boolean.parseBoolean(v);
			} else if (p.equals("fps")) {
				fps = Integer.parseInt(v);
			} else if (p.equals("viewing_distance")) {
				viewingDistance = Float.parseFloat(v);
			} else if (p.equals("fire_viewing_distance")) {
				fireViewingDistance = Float.parseFloat(v);
			} else if (p.equals("texture_pack")) {
				texturePack = v;
			} else if (p.equals("fovy")) {
				fovy = Integer.parseInt(v);
			} else if (p.equals("keyboard")) {
				keyboard = KeyboardPreset.valueOf(v.toUpperCase());
				ControlSettings.initialize(keyboard);
			}

		}

		br.close();

	}

	public float getFireViewingDistance() {
		return Math.min(fireViewingDistance, viewingDistance);
	}
}
