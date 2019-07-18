package com.mcpvp.mazerunner;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

public class Game extends StageManager implements Listener
{
	private StageManager stageManager;

	public Game()
	{
		stageManager = new StageManager();
		
		StageBase generateStage = new StageBase("Generating");
		StageBase waitingStage = new StageBase("Waiting for players");
		StageBase countdownStage = new StageBase("Countdown");
		StageBase teleportingStage = new StageBase("Teleporting");
		
		StageBase liveStage = new StageBase("Live");
		
		StageBase championStage = new StageBase("Champion");
		StageBase finishingStage = new StageBase("Finishing");
		
		//Condition trees
		waitingStage.registerCondition(new ConditionPlayerCountReached(stageManager, countdownStage, 10, true));
		
		//player count falls below 10
		countdownStage.registerCondition(new ConditionPlayerCountReached(stageManager, waitingStage, 10, false));
		countdownStage.registerCondition(new ConditionTimed(stageManager, 60, teleportingStage));
		
		teleportingStage.registerCondition(new Condition(stageManager, liveStage)
				{
					@Override
					public boolean isTriggered(HashSet<UUID> players)
					{
						// Teleport all players
						return false;
					}
				});
		
		liveStage.registerCondition(new ConditionTimed(stageManager, 60 * 30, championStage));
		liveStage.registerCondition(new ConditionPlayerCountReached(stageManager, championStage, 1, false));
		//detemine winner
		
		championStage.registerCondition(new ConditionTimed(stageManager, 20, finishingStage));
		//Kick all players
		
		// create linked list
		generateStage.setNextStage(waitingStage);
		waitingStage.setNextStage(countdownStage);
		countdownStage.setNextStage(teleportingStage);
		teleportingStage.setNextStage(liveStage);
		
		liveStage.setNextStage(championStage);
		championStage.setNextStage(finishingStage);
		
		this.setActiveStage(generateStage);
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e)
	{
		if(e.getType() != UnitType.SECOND) return;
		this.stageManager.update();
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
			this.stageManager.getPlayers().stream()
										  .map(Bukkit::getPlayer)
										  .filter(player -> player != null)
										  .forEach(player -> {
											  //Teleport to teams base
										  });
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
			this.stageManager.getPlayers().stream()
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
}
