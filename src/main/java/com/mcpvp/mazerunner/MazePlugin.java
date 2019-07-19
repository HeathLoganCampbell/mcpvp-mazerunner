package com.mcpvp.mazerunner;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.mcpvp.heart.CustomPlugin;
import com.mcpvp.heart.api.RequestManager;
import com.mcpvp.heart.modules.betterevents.BetterEventModule;
import com.mcpvp.heart.modules.client.ClientModule;
import com.mcpvp.heart.modules.client.Rank;
import com.mcpvp.heart.modules.command.CommandModule;
import com.mcpvp.heart.modules.command.annotations.CommandHandler;
import com.mcpvp.heart.modules.command.commands.BroadcastCommand;
import com.mcpvp.heart.modules.command.types.CommandArgs;
import com.mcpvp.heart.modules.ticker.TickerModule;
import com.mcpvp.heart.modules.worldgenerator.WorldGeneratorModule;
import com.mcpvp.heart.utils.Config;
import com.mcpvp.mazerunner.maze.world.MazeChunkGenerator;
import com.mcpvp.mazerunner.maze.world.WorldGenerator;

public class MazePlugin extends CustomPlugin
{
	private WorldGeneratorModule worldGenerator;
	
	@Override
	public void onEnable()
	{
		RequestManager requstManager = this.loadRequestManager();
		CommandModule commandModule = this.loadCommandModule();
		
		TickerModule tickerModule = new TickerModule(this);
		
		BetterEventModule betterEventModule = new BetterEventModule(this);
		
		ClientModule clientModule = new ClientModule(this, betterEventModule, commandModule, requstManager);
	
		//		worldGenerator.generateWorld(world, 100, 64, 64, true);
		worldGenerator = new WorldGeneratorModule(this, commandModule);
		
		Game game = new Game(this, "Mazerunner game", worldGenerator);
		commandModule.registerCommands(game);
	}
	
	@Override
	public void onDisable()
	{
	
	}
	
	public RequestManager loadRequestManager()
	{

		Config config = new Config("config", this)
				{
					public void onCreate(FileConfiguration configFile)
					{
						configFile.set("api.key", "fillMeOut");
						configFile.set("api.domain", "localhost/api/v1");
						
					}
				};
		config.save();	
				
		String apiKey = config.getConfig().getString("api.key");
		String apiDomain = config.getConfig().getString("api.domain");
		
		return new RequestManager(this, apiKey, apiDomain);
	}
	
	public CommandModule loadCommandModule()
	{
		CommandModule commandModule = new CommandModule(this);
		commandModule.registerCommands(new BroadcastCommand());
		commandModule.registerCommands(this);
		return commandModule;
		
	}
	
	@CommandHandler(name = "demo", aliases = { "d" }, requiredRank = Rank.STAFF)
	public void broadcastCmd(CommandArgs args) 
	{
		worldGenerator.destroyWorld("mazerunner" + File.separatorChar +"world", false);

		//Each tile should be 4 blocks so we can quickly divide
		int mazeWidth = 200;
		int mazeHeight = 200;
		int teams = 4;
		WorldGenerator maze = new WorldGenerator(mazeWidth, mazeHeight, teams);
		byte[] tiles = maze.generate();
	
		World world = worldGenerator.loadNewWorld("mazerunner" + File.separatorChar +"world" + Math.random(), new MazeChunkGenerator(tiles, mazeWidth, mazeHeight));
		world.setAllowSlime(false);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
		{
			args.getPlayer().teleport(new Location(world, 400, 100, 400));
		}, 20 * 5);
	}
}
