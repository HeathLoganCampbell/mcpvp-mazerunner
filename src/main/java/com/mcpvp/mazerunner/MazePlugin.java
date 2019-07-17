package com.mcpvp.mazerunner;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import com.mcpvp.heart.CustomPlugin;
import com.mcpvp.heart.api.RequestManager;
import com.mcpvp.heart.modules.betterevents.BetterEventModule;
import com.mcpvp.heart.modules.client.ClientModule;
import com.mcpvp.heart.modules.command.CommandModule;
import com.mcpvp.heart.modules.command.commands.BroadcastCommand;
import com.mcpvp.heart.modules.joinquit.JoinQuitModule;
import com.mcpvp.heart.modules.joinquit.Option;
import com.mcpvp.heart.modules.ticker.TickerModule;
import com.mcpvp.heart.modules.worldgenerator.WorldGeneratorModule;
import com.mcpvp.heart.utils.Config;

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
	
		JoinQuitModule joinQuitModule = new JoinQuitModule(this, commandModule, clientModule);
		
		WorldGeneratorModule worldGenerator = new WorldGeneratorModule(this, commandModule);
		World world = worldGenerator.loadNewWorld("tournament" + File.separatorChar +"world");
		worldGenerator.generateWorld(world, 100, 64, 64, true);
		
		joinQuitModule.addJoinOption(new Option("default.join", true, (client, player) ->  {
			if(player.getWorld() == world) return;
			Block highestBlock = world.getHighestBlockAt(0, 0);
			double highestY = highestBlock.getY();
			player.teleport(new Location(world, 0, highestY + 20, 0));
			player.sendMessage(player.getWorld().getName());
		}));
		
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
		return commandModule;
	}
}
