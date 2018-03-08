package com.idkstudios.game.game;

import com.idkstudios.game.utils.MultiTimer;

public class PerformanceMonitor {

	private static final PerformanceMonitor monitor = new PerformanceMonitor();

	public static PerformanceMonitor getInstance() {
		return monitor;
	}

	public static enum Operation {
		RENDER_ALL, RENDER_OPAQUE, RENDER_TRANSLUCENT, RENDER_MANUAL, RENDER_SKY, RENDER_CLOUDS, RENDER_OVERLAY, UPDATE
	}

	private MultiTimer timer;

	public PerformanceMonitor() {
		timer = new MultiTimer(Operation.values().length);
	}

	public void start(Operation op) {
		timer.start(op.ordinal());
	}

	public void stop(Operation op) {
		timer.stop(op.ordinal());
	}

	/**
	 * Returns the time in milliseconds
	 */
	public float get(Operation op) {
		return timer.get(op.ordinal()) / 1000000.0f;
	}
}
