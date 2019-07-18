package com.mcpvp.mazerunner.maze.world;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;


public class EmptyChunkGenerator extends ChunkGenerator
{
	@Override
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomes)
	{
		return new byte[world.getMaxHeight() >> 4][];
	}

}
