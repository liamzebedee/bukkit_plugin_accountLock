package net.cryptum.dev.accountLock;

import java.awt.Color;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import net.cryptum.dev.accountLock.*;



public class PlayerActionListener extends PlayerListener {
	AccountLockMain plugin;
	public PlayerActionListener(AccountLockMain instance){
		this.plugin = instance;
	}
	public boolean accountLockOn(PlayerEvent event){
		Player p = event.getPlayer();
		if(plugin.lockedPlayers.contains(p)){			
			return true;
		}
		return false;
	}
	
	public void onPlayerMove(PlayerMoveEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerChat(PlayerChatEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerInteract(PlayerChatEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerInteractEntity(PlayerChatEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerCommandPreprocess(PlayerChatEvent event){
		if(event.getMessage().startsWith("/account")){
			
		}
		else{
			if(accountLockOn(event)){
				event.setCancelled(true);
			}
		}
	}
	public void onPlayerDropItem(PlayerChatEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerGameModeChange(PlayerChatEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerPickupItem(PlayerChatEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerTeleport(PlayerTeleportEvent event){
		if(accountLockOn(event)){
			event.setCancelled(true);
		}
	}
	public void onPlayerJoin(PlayerJoinEvent event){
		Player p = event.getPlayer();
		if(plugin.getConfig().getBoolean(p.getName()+".accountAutoLockOn")){
			plugin.lockedPlayers.add(event.getPlayer());
		}		
	}
}
