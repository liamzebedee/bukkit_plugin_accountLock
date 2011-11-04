package net.cryptum.dev.accountLock;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

public class PlayerBlockListener extends BlockListener {
	AccountLockMain plugin;
	public PlayerBlockListener(AccountLockMain instance){
		this.plugin = instance;
	}
	public boolean accountLockOn(Player p){
		if(plugin.lockedPlayers.contains(p.getName())){
			return true;
		}
		return false;
	}
	public void onBlockBreak(BlockBreakEvent event) {
		if(accountLockOn(event.getPlayer())){
			event.setCancelled(true);
		}
	}
	public void onBlockPlace(BlockPlaceEvent event) {
		if(accountLockOn(event.getPlayer())){
			event.setCancelled(true);
		}
	}
	
}
