package com.mcpvp.mazerunner.maze.world;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;

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
	
	public byte getTileType(int x, int y)
	{
		int index = x + ((y) * this.width);
		
		if(x >= this.width || x < 0)
			return -0x01;
		if(y >= this.height || y < 0)
			return -0x01;
		
		return this.tiles[index];
	}
	
	@SuppressWarnings("deprecation")
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
		boolean teamTile = false;
		
		int yBase = 64;
		
		MaterialData wall = new MaterialData(Material.STAINED_CLAY, (byte) 0x11);
		
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
					byte tileType = this.getTileType(tileX, tileY);
					
					byte yNegtileType = this.getTileType(tileX, tileY - 1);
					byte yPostileType = this.getTileType(tileX, tileY + 1);
					byte xPostileType = this.getTileType(tileX + 1, tileY);
					byte xNegtileType = this.getTileType(tileX - 1, tileY);
					
					int xMod16 = curBlockX % 16;
					int zMod16 = curBlockZ % 16;
					
					int xMod4 = curBlockX % 4;
					int zMod4 = curBlockZ % 4;
					
					if(tileType == 0x03)	
					{
						chunkData.setRegion(x, yBase, z, 
											x + 1, yBase + 10, z + 1, 
											Material.SMOOTH_BRICK);
						
					}
					else if(tileType == 0x00)//air
					{
						chunkData.setBlock(x, yBase, z, Material.COBBLESTONE);
					}
					else if(tileType == 0x02 || tileType == 0x04)//teams 0x04 == exits
					{
						
						chunkData.setBlock(x, yBase, z, Material.GRASS);
						chunkData.setBlock(x, yBase - 1, z, Material.DIRT);
						chunkData.setBlock(x, yBase - 2, z, Material.DIRT);
						chunkData.setBlock(x, yBase - 3, z, Material.DIRT);
						chunkData.setBlock(x, yBase - 4, z, Material.BEDROCK);
						
						
						//block can't be 0 or 16
						// it can be any value of 4
						//working
						if((yNegtileType == 0x03 || yNegtileType == 0x00) && zMod4 == 0)
						{
							chunkData.setBlock(x, yBase, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 1, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 2, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 3, z, Material.BEDROCK);
							if(yNegtileType == 0x03)
							{
								for(int i = 0; i < 10; i++)
								{
									chunkData.setBlock(x, yBase + i, z, wall);
								}
							}
						}
						
						if((yPostileType == 0x03 || yPostileType == 0x00) && zMod4 == 3)
						{
							chunkData.setBlock(x, yBase, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 1, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 2, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 3, z, Material.BEDROCK);
							if(yPostileType == 0x03)
							{
								for(int i = 0; i < 10; i++)
								{
									chunkData.setBlock(x, yBase + i, z, wall);
								}
							}
						}
						
						if((xPostileType == 0x03 || xPostileType == 0x00) && xMod4 == 3)
						{
							chunkData.setBlock(x, yBase, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 1, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 2, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 3, z, Material.BEDROCK);
							if(xPostileType == 0x03)
							{
								for(int i = 0; i < 10; i++)
								{
									chunkData.setBlock(x, yBase + i, z, wall);
								}
							}
						}
						
						
						//working
						if((xNegtileType == 0x03  || xNegtileType == 0x00) && xMod4 == 0)
						{
							chunkData.setBlock(x, yBase, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 1, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 2, z, Material.BEDROCK);
							chunkData.setBlock(x, yBase - 3, z, Material.BEDROCK);
							if(xNegtileType == 0x03)
							{
								for(int i = 0; i < 10; i++)
								{
									chunkData.setBlock(x, yBase + i, z, wall);
								}
							}
						}
						
						//We want to get the tiles around our current tile
						
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
