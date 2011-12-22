package net.cryptum.dev.accountLock;

import java.util.Timer;
import org.bukkit.ChatColor;

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
		for(String playerName : plugin.lockedPlayers){
			plugin.getServer().getPlayer(playerName).
			sendMessage(ChatColor.RED+"[AccountLock]"+ChatColor.WHITE+
			" - This account is currently locked." +
			" Please use '/account unlock [password]' to continue playing!");
		}
    }
	
}