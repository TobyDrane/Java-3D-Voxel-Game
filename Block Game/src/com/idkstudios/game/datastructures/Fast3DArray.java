package com.idkstudios.game.datastructures;

import com.idkstudios.game.math.Vec3i;

public class Fast3DArray<T> {

	private Object array[];
	private final int lX, lY, lZ;
	private final int size;

	/**
	 * Init. a new 3D array with the given dimensions.
	 */
	public Fast3DArray(int x, int y, int z) {
		lX = x;
		lY = y;
		lZ = z;

		size = lX * lY * lZ;
		array = new Object[size];
	}

	public void rawIndexToVec3i(int index, Vec3i vec) {
		vec.setX(index / (lX * lY));
		vec.setY((index / lX) % lY);
		vec.setZ(index % lZ);
	}

	/**
	 * Returns the byte value at the given position.
	 */
	@SuppressWarnings("unchecked")
	public T get(int x, int y, int z) {

		int pos = (x * lX * lY) + (y * lX) + z;

		if (x >= lX || y >= lY || z >= lZ || x < 0 || y < 0 || z < 0) {
			return null;
		}

		return (T) array[pos];
	}

	/**
	 * Sets the value for the given position.
	 * 
	 * @return The previous value
	 */
	@SuppressWarnings("unchecked")
	public T set(int x, int y, int z, T b) {
		if (x >= lX || y >= lY || z >= lZ || x < 0 || y < 0 || z < 0) {
			return null;
		}

		int pos = (x * lX * lY) + (y * lX) + z;

		T obj = (T) array[pos];
		array[pos] = b;
		return obj;
	}

	/**
	 * Returns the raw object at the given index.
	 */
	@SuppressWarnings("unchecked")
	public T getRawObject(int i) {
		return (T) array[i];
	}

	/**
	 * Sets the raw object for the given index.
	 */
	@SuppressWarnings("unchecked")
	public T setRawObject(int i, T b) {
		T old = (T) array[i];
		array[i] = b;
		return old;
	}

	/**
	 * Returns the size of this array.
	 */
	public int size() {
		return size;
	}

	public void clear() {
		array = null;
	}
}
