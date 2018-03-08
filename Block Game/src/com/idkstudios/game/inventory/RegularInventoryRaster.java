package com.idkstudios.game.inventory;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.Point;
import org.lwjgl.util.ReadablePoint;
import org.lwjgl.util.Rectangle;

public class RegularInventoryRaster implements InventoryRaster {

	private int x, y;
	private int cellCountX, cellCountY;
	private int cellW, cellH;
	private int marginX, marginY;

	public RegularInventoryRaster(int x, int y, int cellCountX, int cellCountY,
			int cellW, int cellH, int marginX, int marginY) {
		this.x = x;
		this.y = y;
		this.cellCountX = cellCountX;
		this.cellCountY = cellCountY;
		this.cellW = cellW;
		this.cellH = cellH;
		this.marginX = marginX;
		this.marginY = marginY;
	}

	public boolean isInsideRasterAABB(int x, int y) {
		return (x > x + marginX)
				&& (x < x + cellCountX * (cellW + marginX))
				&& (y > y + marginY)
				&& (y < y + cellCountY * (cellH + marginY));
	}

	public ReadablePoint getCenterOfCell(int index) {
		int x = index % cellCountX;
		int y = index / cellCountX;
		return new Point((int) (x + (x + 1) * marginX + (x + 0.5f) * cellW),
				(int) (y + (y + 1) * marginY + (y + 0.5f) * cellH));
	}

	public Rectangle getCellAABB(int x, int y) {
		return new Rectangle(x + (x + 1) * marginX + x * cellW, y + (y + 1)
				* marginY + y * cellH, cellW, cellH);
	}

	public int getCellAt(int x, int y) {
		x -= x;
		y -= y;

		x = (x - marginX) / (cellW + marginX);
		y = (y - marginY) / (cellH + marginY);

		return y * cellCountY + y;

		/*
		 * x * _cellW + (x + 1) * _marginX = pX x * _cellW + x * _marginX = pX -
		 * _marginX x * (_cellW + _marginX) = pX - _marginX x = (pX - _marginX)
		 * / (_cellW + _marginX)
		 */
	}

	public void renderRaster() {
		glLineWidth(1);
		glDisable(GL_TEXTURE_2D);
		for (int x = 0; x < cellCountX; ++x) {
			for (int y = 0; y < cellCountY; ++y) {
				Rectangle r = getCellAABB(x, y);
				glBegin(GL_LINE_LOOP);
				glVertex2i(r.getX(), r.getY());
				glVertex2i(r.getX() + r.getWidth(), r.getY());
				glVertex2i(r.getX() + r.getWidth(), r.getY() + r.getHeight());
				glVertex2i(r.getX(), r.getY() + r.getHeight());
				glEnd();
			}
		}
	}

	public int getCellWidth() {
		return cellW;
	}

	public int getCellHeight() {
		return cellH;
	}

	public int getCellCount() {
		return cellCountX * cellCountY;
	}
}
