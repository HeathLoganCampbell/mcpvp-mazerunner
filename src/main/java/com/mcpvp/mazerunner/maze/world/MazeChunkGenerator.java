package com.mcpvp.mazerunner.maze.world;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import com.mcpvp.heart.utils.CC;

public class MazeChunkGenerator extends ChunkGenerator 
{
	private byte[] tiles;
	private int width, height;
	
	public MazeChunkGenerator(byte[] tiles, int width, int height)
	{
		this.tiles = tiles;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
		//Set all blocks to only plains
		for (int blockX = 0; blockX < 16; blockX++) {
			for (int blockZ = 0; blockZ < 16; blockZ++) {
				biome.setBiome(blockX, blockZ, Biome.PLAINS);
			}
		}

		ChunkData chunkData = createChunkData(world);
		
		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;
		boolean inTile = false;
		boolean inVoid = false;
		
		for (int x = 0; x < 16; x++) 
		{
			for (int z = 0; z < 16; z++) 
			{
				int curBlockX = (blockX + x);
				int curBlockZ = (blockZ + z);
				
				int tileX = (curBlockX) >> 2;//divide by 2
				int tileY = (curBlockZ) >> 2;//Divide by 2
			
				if(tileX >= this.width || tileX < 0)
					continue;
				if(tileY >= this.height || tileY < 0)
					continue;
				
				try {
					inTile = true;
					byte tileType = this.tiles[tileX + (tileY * this.width)];
					if(tileType == 0x03)
					{
						chunkData.setRegion(x, 0, z, 
											x + 1, 10, z + 1, 
											Material.SMOOTH_BRICK);
						
					}
					else if(tileType == 0x00)
					{
						chunkData.setBlock(x, 0, z, Material.COBBLESTONE);
					}
					else if(tileType == 0x05)
					{
						inVoid = true;
					}
				} catch(Exception e)
				{
					Bukkit.broadcastMessage(curBlockX +" , " + curBlockZ + "   " + tileX + ", " + tileY);
				}
				
			}
		}
		
		//floor
//		if(inTile && !inVoid)
//			chunkData.setRegion(0, 0, 0, 16, 1, 16, Material.COBBLESTONE);
		

		return chunkData;
	}

}
