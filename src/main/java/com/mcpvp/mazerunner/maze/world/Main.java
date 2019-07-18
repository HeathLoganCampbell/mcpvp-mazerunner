package com.mcpvp.mazerunner.maze.world;


import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		int d = 0;
		while (true) {
			int w = 128 * 2;
			int h = 128  * 2;

			WorldGenerator world = new WorldGenerator(w, h, 8);
			
			byte[] tiles = world.generate();

			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			int[] pixels = new int[w * h];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int i = x + y * w;

					if (tiles[i] == 0x01) pixels[i] = 0x000000; //air
					if (tiles[i] == 0x02) pixels[i] = 0xffffff;
					if (tiles[i] == 0x03) pixels[i] = 0xa0a0a0; //wall
					if (tiles[i] == 0x04) pixels[i] = 0xff2020; //exits
				}
			}
			img.setRGB(0, 0, w, h, pixels, 0, w);
			JOptionPane.showMessageDialog(null, null, "Another", JOptionPane.YES_NO_OPTION, new ImageIcon(img.getScaledInstance(w * 2, h * 2, Image.SCALE_AREA_AVERAGING)));
		}
	}

}