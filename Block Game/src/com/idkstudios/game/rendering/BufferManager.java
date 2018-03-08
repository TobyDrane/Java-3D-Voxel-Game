package com.idkstudios.game.rendering;

import org.lwjgl.opengl.GL15;

import com.idkstudios.game.utils.IntList;

public class BufferManager {

	private static BufferManager instance;

	public static BufferManager getInstance() {
		if (instance == null) {
			instance = new BufferManager();
		}
		return instance;
	}

	private IntList buffersToDelete;
	private IntList buffers;
	private Thread mainThread;

	private BufferManager() {
		mainThread = Thread.currentThread();
		buffers = new IntList(512);
		buffersToDelete = new IntList(32);
	}

	private void verifyThread() {
		if (mainThread != Thread.currentThread()) {
			throw new IllegalThreadStateException(
					"Trying to access buffer operations from wrong thread");
		}
	}

	public int createBuffer() {
		verifyThread();
		int buffer = GL15.glGenBuffers();
		buffers.add(buffer);
		return buffer;
	}

	public void deleteBuffer(int buffer) {
		if (mainThread == Thread.currentThread()) {
			deleteBufferDirect(buffer);
		} else {
			buffersToDelete.add(buffer);
		}
	}

	private void deleteBufferDirect(int buffer) {
		GL15.glDeleteBuffers(buffer);
		buffers.removeValue(buffer);
	}

	public int deleteQueuedBuffers() {
		int count = buffersToDelete.size();

		if (count == 0)
			return 0;

		for (int i = 0; i < count; ++i) {
			deleteBufferDirect(buffersToDelete.get(i));
		}
		buffersToDelete.clear();

		return count;
	}

	public int getAliveBuffers() {
		return buffers.size();
	}
}
