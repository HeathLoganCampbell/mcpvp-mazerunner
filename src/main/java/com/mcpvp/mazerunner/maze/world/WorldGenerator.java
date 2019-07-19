package com.mcpvp.mazerunner.maze.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class WorldGenerator {
	public static final boolean CIRCLE_SHAPE = true;
	@Getter
	private int width, height;
	private int area;
	private Point center;
	private List<Glade> glades;

	private static Random random = new Random();

	public WorldGenerator(int width, int height, int glades) {
		this.width = width;
		this.height = height;
		this.area = this.width * this.height;
		this.center = new Point(this.width / 2, this.height / 2);

		this.glades = new ArrayList<>();
		double diffDegree = 2 * Math.PI / (glades);
		int radius = (int) ((3 * Math.min(this.width, this.height)) / 8);
		double randomOffset = random.nextFloat();

		for (int i = 0; i < glades; i++) {
			double curDegree = randomOffset + diffDegree * i;
			int x = (int) (radius * Math.cos(curDegree) - Math.sin(curDegree));
			int y = (int) (radius * Math.sin(curDegree) + Math.cos(curDegree));

			x += this.center.x;
			y += this.center.y;

			if (x % 2 == 0)
				x += 1;
			if (y % 2 == 0)
				y += 1;

			Point location = new Point(x, y);
			System.out.println(location.toString());

			this.glades.add(new Glade(location, i));
		}
	}

	private boolean isValidTile(byte[] tiles, Point point) {
		for (Point neighbourTile : new Point[] { new Point(0, 1), new Point(0, -1), new Point(1, 0),
				new Point(-1, 0) }) {
			int x = point.x + neighbourTile.x;
			int y = point.y + neighbourTile.y;
			int pixelLoc = x + (y * this.width);
			if (x < 0 || y < 0 || x >= this.getWidth() || y >= this.getHeight())
				return false;
			if (tiles[pixelLoc] != 0x03 && tiles[pixelLoc] != 0x02)
				return false;
		}
		return true;
	}

	public void generateMazeHoles(byte[] tiles) {
		for (int x = 1; x < this.getWidth(); x++) {
			for (int y = 1; y < this.getHeight(); y++) {
				int pixelLoc = x + (y * this.width);
				if (((x % 2 == 1 && y % 2 == 0) || (x % 2 == 0 && y % 2 == 1)) && tiles[pixelLoc] == 0x03)
					if (random.nextInt(100) < 10)
					{
						boolean basesAround = false;
						for (Point nextPossibleTile : new Point[] { new Point(0, 2), new Point(0, -2), new Point(2, 0), new Point(-2, 0) })
						{
							int nextPixelLoc = (int) (x + nextPossibleTile.getX() + ((y + nextPossibleTile.getY()) * this.width));
							if(nextPixelLoc == 0x02 || nextPixelLoc == 0x04 ||  nextPixelLoc == 0x05)
								basesAround = true;
						}
						
						if(!basesAround)
							tiles[pixelLoc] = 0x00;
					}

			}
		}
	}

	public void generateMaze(byte[] tiles, int startX, int startY) {
		Stack<Point> visitedTiles = new Stack<>();
		visitedTiles.add(new Point(startX, startY));
		tiles[startX + (startY * this.getWidth())] = 0x00;

		while (visitedTiles.size() != 0) {
			Point lastVisitedTile = visitedTiles.peek();
			List<Point> pathOptions = new ArrayList<>();

			for (Point nextPossibleTile : new Point[] { new Point(0, 2), new Point(0, -2), new Point(2, 0),
					new Point(-2, 0) }) {
				int violations = 0;
				int stepX = lastVisitedTile.x + (nextPossibleTile.x * 1);
				int stepY = lastVisitedTile.y + (nextPossibleTile.y * 1);

				if (stepX < 0)
					continue;
				if (stepY < 0)
					continue;
				if (stepX >= this.getWidth())
					continue;
				if (stepY >= this.getHeight())
					continue;

				int stepPixel = stepX + (stepY * this.getWidth());

				if (tiles[stepPixel] == 0x00 || tiles[stepPixel] == 0x02)
					continue;
				
				if (!isValidTile(tiles, new Point(stepX, stepY)))
					violations += 3;

				if (violations <= 2) {
					pathOptions.add(new Point(stepX, stepY));
				}

			}

			if (pathOptions.size() == 0) {
				visitedTiles.pop();
				continue;
			}

			Point selectedPath = pathOptions.get(random.nextInt(pathOptions.size()));
			visitedTiles.push(selectedPath);
			tiles[selectedPath.x + (selectedPath.y * this.getWidth())] = 0x00;

			int connectorX = (int) ((selectedPath.x - lastVisitedTile.x) * 0.5);
			int connectorY = (int) ((selectedPath.y - lastVisitedTile.y) * 0.5);
			tiles[selectedPath.x - connectorX + ((selectedPath.y - connectorY) * this.getWidth())] = 0x00;
		}
	}

	public void generateFeast(byte[] tiles) {
		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				int pixelLoc = x + (y * this.width);
				if (!CIRCLE_SHAPE || (Math.sqrt((x - this.getWidth() / 2) * (x - this.getWidth() / 2)
						+ (y - this.getHeight() / 2) * (y - this.getHeight() / 2)) < this.getWidth() / 42 * Math.PI))
					tiles[pixelLoc] = 0x00;

			}
		}
	}

	public byte[] generate() {
		byte[] tiles = new byte[this.area];

		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				int pixelLoc = x + (y * this.width);
				if (!CIRCLE_SHAPE || (Math.sqrt((x - this.getWidth() / 2) * (x - this.getWidth() / 2)
						+ (y - this.getHeight() / 2) * (y - this.getHeight() / 2)) < this.getWidth() / 7 * Math.PI))
					tiles[pixelLoc] = 0x03;
				else
					tiles[pixelLoc] = 0x05;

			}
		}

		for (int i = 0; i < this.glades.size(); i++) {
			Glade glade = this.glades.get(i);
			Point location = glade.location;
			glade.markExits();
			int pixelLoc = location.x + (location.y * this.width);
			if (pixelLoc >= 0 && pixelLoc <= this.area)
				glade.render(tiles, getWidth(), getHeight());

			for (int e = 0; e < glade.exitsCount(); e++) {
				Stack<Point> pastPath = new Stack<>();
				pastPath.add(glade.getExit(e));

			}
		}

		generateMaze(tiles, this.width / 2, this.height / 2);
		generateMazeHoles(tiles);
		generateFeast(tiles);

		//re render team bases
		for (int i = 0; i < this.glades.size(); i++) {
			Glade glade = this.glades.get(i);
			glade.render(tiles, getWidth(), getHeight());
		}

		
		for (int i = 0; i < this.glades.size(); i++) {
			Glade glade = this.glades.get(i);
			glade.clearExits(tiles, width, height);
		}

		return tiles;
	}

	// each tile means 3 blocks, so 33 / 3 = 11 tiles

	@AllArgsConstructor
	@RequiredArgsConstructor
	public class Glade {
		public @NonNull Point location;
		public @NonNull int gladeId;
		public @NonNull int gladeWidth = 11;
		public @NonNull int gladeHeight = 11;

		private List<Point> exits = new ArrayList<>();

		private int startX = -(gladeWidth / 2);
		private int endX = (gladeWidth / 2) + 1;
		private int startY = -(gladeHeight / 2);
		private int endY = (gladeHeight / 2) + 1;

		public Point getExit(int i) {
			return this.exits.get(i);
		}

		public int exitsCount() {
			return this.exits.size();
		}

		public boolean isExit(int x, int y) {
			return ((x == startX || x == endX - 1) && y == 0) || ((y == startY || y == endY - 1) && x == 0);
		}

		public void markExits() {
			for (int x = startX; x < endX; x++) {
				for (int y = startY; y < endY; y++) {
					if (isExit(x, y))
						exits.add(new Point(location.x + x, location.y + y));
				}
			}
		}

		public void clearExits(byte[] tiles, int width, int height) {
			for (Point exit : this.exits) {
				int vecX = exit.x - this.location.x;
				int vecY = exit.y - this.location.y;

				int unitX = vecX / Math.abs(vecX == 0 ? 1 : vecX);
				int unitY = vecY / Math.abs(vecY == 0 ? 1 : vecY);

				for (int i = 0; i < 3; i++) {

					int x = this.location.x + unitX * i + endX * unitX;
					int y = this.location.y + unitY * i + endX * unitY;

					tiles[x + (y * width)] = 0x00;
				}
			}
		}

		public void render(byte[] pixels, int width, int height) {
			for (int x = startX - 1; x < endX + 1; x++) {
				for (int y = startY - 1; y < endY + 1; y++) {
					pixels[location.x + x + ((location.y + y) * width)] = 0x03;
				}
			}
			
			for (int x = startX; x < endX; x++) {
				for (int y = startY; y < endY; y++) {
					pixels[location.x + x + ((location.y + y) * width)] = 0x02;
					if (isExit(x, y))
						pixels[location.x + x + ((location.y + y) * width)] = 0x04;
				}
			}

		}
	}
}
