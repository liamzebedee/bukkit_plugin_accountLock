package net.cryptum.dev.accountLock;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class AccountLockedAlert implements Runnable {
	Timer timer;
	AccountLockMain plugin;
	
	public AccountLockedAlert(AccountLockMain plugin)
	{
		this.plugin = plugin;
		run();
	}
	
	public void run() 
	{
		for(Player p : plugin.lockedPlayers){
			p.sendMessage(ChatColor.RED+"[AccountLock]"+ChatColor.WHITE+" - This account is currently locked." +
			" Please use '/account unlock [password]' to continue playing!");
		}
    }
	
}

