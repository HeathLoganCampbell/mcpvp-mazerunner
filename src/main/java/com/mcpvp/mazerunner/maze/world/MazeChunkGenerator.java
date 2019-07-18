package com.mcpvp.mazerunner.maze.world;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

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
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {

		/*
		 * if (biome.getBiome(chunkX, chunkZ) != Biome.PLAINS) { // <- line 21
		 * biome.setBiome(chunkX, chunkZ, Biome.PLAINS); }
		 */
//		biome.setBiome(x, z, Biome.PLAINS);

		for (int blockX = 0; blockX < 16; blockX++) {
			for (int blockZ = 0; blockZ < 16; blockZ++) {
				biome.setBiome(blockX, blockZ, Biome.PLAINS);
			}
		}

		ChunkData chunkData = createChunkData(world);
		
		//4 basic stairs 
		//4 3/4 stairs
		//4 1/4 stairs
		
		//Will need a way to say if the wall is connected
		
		for(int y = 0; y < 15; y++)
		{
			chunkData.setRegion(0, 0, 0, 4, 10, 4, Material.SMOOTH_BRICK);
		}

//		chunkData.setBlock(1, 1, 1, Material.SMOOTH_BRICK);

//		chunkData.setRegion(0, 0, 0, 16, 1, 16, Material.GRASS);

		return chunkData;
	}

}
