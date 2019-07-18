package com.mcpvp.mazerunner;

import java.io.File;

import org.bukkit.Bukkit;
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
	@Override
	public void onEnable()
	{
		RequestManager requstManager = this.loadRequestManager();
		CommandModule commandModule = this.loadCommandModule();
		
		TickerModule tickerModule = new TickerModule(this);
		
		BetterEventModule betterEventModule = new BetterEventModule(this);
		
		ClientModule clientModule = new ClientModule(this, betterEventModule, commandModule, requstManager);
	
		//Each tile should be 4 blocks so we can quickly divide
//		WorldGenerator world = new WorldGenerator(width/3, height/3, glades);
//		byte[] tiles = world.generate();
		
		WorldGeneratorModule worldGenerator = new WorldGeneratorModule(this, commandModule);
		World world = worldGenerator.loadNewWorld("mazerunner" + File.separatorChar +"world", new MazeChunkGenerator());
		world.setAllowSlime(false);
		//		worldGenerator.generateWorld(world, 100, 64, 64, true);
		
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
		int worldIndex = 0;
		if(args.length() > 0)
			worldIndex = Integer.parseInt(args.getArgs(0));
		if(worldIndex >= Bukkit.getWorlds().size())
		{
			args.getSender().sendMessage("Not enough worlds");
			return;
		}
		args.getPlayer().teleport(Bukkit.getWorlds().get(worldIndex).getSpawnLocation());
	}
}
