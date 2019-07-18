package com.mcpvp.mazerunner.maze.world;

import org.bukkit.Location;

public class MapLoader 
{
	private int width, height;
	private Location  centerLocation;
	
	//centerLocation will normally be ( 0, 0 , 0 )
	public MapLoader(Location centerLocation, int width, int height, int glades)
	{
		this.width = width;
		this.height = height;
		this.centerLocation = centerLocation;
		
		// We divide by 3 because the maze has a 3 wide gap for everything
		WorldGenerator world = new WorldGenerator(width/3, height/3, glades);
		byte[] tiles = world.generate();
	}
	
	public void loadMap()
	{
		for(int x = 0; x < 100; x++)
			for(int z = 0; z < 100; z++)
			{
				
			}
	}
	
	public void centerMazeToMcWorld()
	{
		// align our maze with the centre of the mc world
		int centerXWorld = centerLocation.getBlockX();
		int centerZWorld = centerLocation.getBlockZ();
		
		int centerXMaze = this.width / 2;
		int centerZMaze = this.height / 2;
		
		//top left (where we start would be)
		int topLeftX = centerXWorld - centerXMaze;
		int topLeftZ = centerZWorld + centerZMaze;
		
		//top right
		int topRightX = centerXWorld + centerXMaze;
		int topRightZ = centerZWorld + centerZMaze;
	}
	
	public void loadChunk(int chunkX, int chunkZ)
	{
		//16 x 16
		
		
		
	}
}
