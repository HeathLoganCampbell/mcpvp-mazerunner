package com.mcpvp.mazerunner;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mcpvp.heart.CustomPlugin;
import com.mcpvp.heart.modules.betterevents.player.PlayerDisconnectEvent;
import com.mcpvp.heart.modules.client.Rank;
import com.mcpvp.heart.modules.command.annotations.CommandHandler;
import com.mcpvp.heart.modules.command.types.CommandArgs;
import com.mcpvp.heart.modules.stages.StageManager;
import com.mcpvp.heart.modules.stages.condition.Condition;
import com.mcpvp.heart.modules.stages.condition.ConditionPlayerCountReached;
import com.mcpvp.heart.modules.stages.condition.ConditionTimed;
import com.mcpvp.heart.modules.stages.event.StageChangeEvent;
import com.mcpvp.heart.modules.stages.stages.StageBase;
import com.mcpvp.heart.modules.ticker.UnitType;
import com.mcpvp.heart.modules.ticker.UpdateEvent;
import com.mcpvp.heart.modules.worldgenerator.WorldGeneratorModule;
import com.mcpvp.mazerunner.maze.world.MazeChunkGenerator;
import com.mcpvp.mazerunner.maze.world.WorldGenerator;

import lombok.Getter;

public class Game extends StageManager implements Listener
{
	private @Getter String name;
	private @Getter World world;
	private @Getter CustomPlugin plugin;

	public Game(CustomPlugin plugin, String name, WorldGeneratorModule worldGenerator)
	{
		this.name = name;
		this.plugin = plugin;
		
		this.plugin.registerListener(this);
		
		StageBase generateStage = new StageBase("Generating");
		StageBase waitingStage = new StageBase("Waiting for players");
		StageBase countdownStage = new StageBase("Countdown");
		StageBase teleportingStage = new StageBase("Teleporting");
		
		StageBase liveStage = new StageBase("Live");
		
		StageBase championStage = new StageBase("Champion");
		StageBase finishingStage = new StageBase("Finishing");
		
		//Condition trees
		waitingStage.registerCondition(new ConditionPlayerCountReached(this, countdownStage, 10, false));
		
		//player count falls below 10
		countdownStage.registerCondition(new ConditionPlayerCountReached(this, waitingStage, 10, true));
		countdownStage.registerCondition(new ConditionTimed(this, 10, teleportingStage));
		
		teleportingStage.registerCondition(new Condition(this, liveStage)
				{
					@Override
					public boolean isTriggered(HashSet<UUID> players)
					{
						// Teleport all players
						return false;
					}
				});
		
		liveStage.registerCondition(new ConditionTimed(this, 60 * 30, championStage));
		liveStage.registerCondition(new ConditionPlayerCountReached(this, championStage, 1, true));
		//detemine winner
		
		championStage.registerCondition(new ConditionTimed(this, 20, finishingStage));
		//Kick all players
		
		// create linked list
		generateStage.setNextStage(waitingStage);
		waitingStage.setNextStage(countdownStage);
		countdownStage.setNextStage(teleportingStage);
		teleportingStage.setNextStage(liveStage);
		
		liveStage.setNextStage(championStage);
		championStage.setNextStage(finishingStage);
		
		this.activeStage = generateStage;
		
		
		worldGenerator.destroyWorld("mazerunner" + File.separatorChar +"world", false);

		//Each tile should be 4 blocks so we can quickly divide
		int mazeWidth = 110;
		int mazeHeight = 110;
		int teams = 4;
		WorldGenerator maze = new WorldGenerator(mazeWidth, mazeHeight, teams);
		byte[] tiles = maze.generate();
	
		world = worldGenerator.loadNewWorld("mazerunner" + File.separatorChar +"world" + Math.random(), new MazeChunkGenerator(tiles, mazeWidth, mazeHeight));
		world.setAllowSlime(false);
		
		this.setActiveStage(this.getActiveStage().getNextStage());
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		
		if(e.getType() != UnitType.SECOND) return;
		this.update();
	}
	
	@EventHandler
	public void onStageChange(StageChangeEvent e)
	{
		if(e.getStageManager() != this) return;
		
		
		if(e.getNextStage().getName().equalsIgnoreCase("Countdown"))
		{
			//Countdown
				//Count down in chat
		}
		else if(e.getNextStage().getName().equalsIgnoreCase("Teleporting"))
		{
			this.getPlayers().stream()
							  .map(Bukkit::getPlayer)
							  .filter(player -> player != null)
							  .forEach(player -> {
								  player.teleport(new Location(this.world, 110 * 2, 100,  110 * 2));
							  });
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.getPlugin(), () ->{
				this.setActiveStage(this.getActiveStage().getNextStage());
			}, 20L);
		}
		else if(e.getNextStage().getName().equalsIgnoreCase("Live"))
		{
			//Live
			
		}
		else if(e.getNextStage().getName().equalsIgnoreCase("Champion"))
		{
			//Champion
			// broadcast winners
			
		}
		else if(e.getNextStage().getName().equalsIgnoreCase("Finishing"))
		{
			this.getPlayers().stream()
										  .map(Bukkit::getPlayer)
										  .filter(player -> player != null)
										  .forEach(player -> {
											  //Game restarting
											  player.kickPlayer("Game restarting");
										  });
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		this.addPlayer(player.getUniqueId());
	}
	
	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent e)
	{
		Player player = e.getPlayer();
		this.removePlayer(player.getUniqueId());
	}

	@CommandHandler(name = "game.debug.addplayer", requiredRank = Rank.STAFF)
	public void addFakePlayer(CommandArgs args) 
	{
		this.addPlayer(UUID.randomUUID());
	}
	
	@CommandHandler(name = "game.debug.removeplayer", requiredRank = Rank.STAFF)
	public void removeFakePlayer(CommandArgs args) 
	{
		Optional<Player> nonplayer = this.getPlayers().stream().map(Bukkit::getPlayer).filter(player -> !player.isOnline()).findFirst();
		this.removePlayer(nonplayer.get().getUniqueId());
	}
	

	@CommandHandler(name = "game.debug.setStage", requiredRank = Rank.STAFF)
	public void onStage(CommandArgs args) 
	{
		this.activeStage = new StageBase("Ahey");
	}
}
