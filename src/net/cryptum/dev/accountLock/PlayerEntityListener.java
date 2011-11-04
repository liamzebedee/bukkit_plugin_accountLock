package net.cryptum.dev.accountLock;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerEntityListener extends EntityListener {
	AccountLockMain plugin;
	public PlayerEntityListener(AccountLockMain instance){
		this.plugin = instance;
	}
	
	public boolean accountLockOn(Player p){
		if(plugin.lockedPlayers.contains(p.getName())){
			return true;
		}
		return false;
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		// Basically checks if a player is attacking another player, and if its on account lock, cancel event
		if (event instanceof EntityDamageByEntityEvent && (event.getEntity() instanceof Player)) {
	         EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) event;
	         if (nEvent.getDamager() instanceof Player) {
	             Player p = (Player) nEvent.getDamager();
	             if(accountLockOn(p)){
	 				event.setCancelled(true);
	 			 }
	         }
	    }
		// Checks if player is being damaged, and cancels event if account locked
		if ((event.getEntity() instanceof Player)){
			Player p = (Player)event.getEntity();
			if(accountLockOn(p)){
				event.setCancelled(true);
			}
		}
		
	}
	
	public void onFoodLevelChange(FoodLevelChangeEvent event){
	    if (!(event.getEntity() instanceof Player)) return;
	    Player p = (Player)event.getEntity();
	    if(accountLockOn(p)){
				event.setCancelled(true);
	    }
	}   
	
	public void onEntityInteract(EntityInteractEvent event){
		if (!(event.getEntity() instanceof Player)) return;
	    Player p = (Player)event.getEntity();
	    if(accountLockOn(p)){
				event.setCancelled(true);
	    }
	}
	
	public void onEntityRegainHealth(EntityRegainHealthEvent event){
		if (!(event.getEntity() instanceof Player)) return;
	    Player p = (Player)event.getEntity();
	    if(accountLockOn(p)){
				event.setCancelled(true);
	    }
	}
	
	
}
