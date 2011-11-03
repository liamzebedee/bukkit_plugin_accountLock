package net.cryptum.dev.accountLock;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
 
public class AccountLockMain extends JavaPlugin {
 
	Logger log = Logger.getLogger("Minecraft");
	ArrayList<Player> lockedPlayers = new ArrayList<Player>();
	Vector defaultVelocity = new Vector(-4.9E-324,-0.0784000015258789,4.9E-324);
 
	public void onEnable(){
		this.getConfig();
		PluginManager pm = this.getServer().getPluginManager();
		PlayerActionListener playerActionListener = new PlayerActionListener(this);
		PlayerBlockListener playerBlockListener = new PlayerBlockListener(this);
		PlayerEntityListener playerEntityListener = new PlayerEntityListener(this);
		
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerActionListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerActionListener, Event.Priority.Lowest, this);
		
		pm.registerEvent(Event.Type.BLOCK_PLACE, playerBlockListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, playerBlockListener, Event.Priority.Lowest, this);
		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, playerEntityListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.ENTITY_INTERACT, playerEntityListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, playerEntityListener, Event.Priority.Lowest, this);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, 
				new AccountLockedAlert(this), 0, 600L); //Informs player that account is locked every 30s 
        
		
		log.info("[AccountLock] by liamzebedee has been enabled!");		
	}
 
	public void onDisable(){
		this.saveConfig();
		log.info("[AccountLock] by liamzebedee has been disabled.");
	}
	
	public String getMD5(String s){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] passwordHash = md.digest(s.getBytes());
		String hash = new String(passwordHash);
		return hash;
	}
	
	public String encodeBase64(String s){
		String newS = new String(
				Base64.encodeBytes(
						s.getBytes(),Base64.DONT_BREAK_LINES|Base64.ENCODE));
		return newS;
	}
	
	public String decodeBase64(String s){
		String newS = new String(Base64.decode(s));
		return newS;
	}
	
	public String applyPWD(String s){
		return encodeBase64(getMD5(s));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("account")){ 
			if(args.length <= 0){
				boolean locked = false;
				p.sendMessage(ChatColor.RED+
						"[AccountLock]");
				if(lockedPlayers.contains(p)){locked = true;}
				p.sendMessage("Account Locked? "+locked);
				p.sendMessage("Use /account help [command] for help on specific commands");
				p.sendMessage("/account setPassword [password]");
				p.sendMessage("/account lock");
				p.sendMessage("/account unlock [password]");
				p.sendMessage("/account killLock [password]");
				if(p.hasPermission("accountLock.admin")){
					p.sendMessage("/account lockR [displayName]");
					p.sendMessage("/account unlockR [displayName]");
					p.sendMessage("/account resetPassword [displayName]");
				}
				return true;
			}
			
			if(args[0].equals("help") && p.hasPermission("accountLock.basic")){
				if(args.length <= 1){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("ERROR: Empty command input for help!");
					return true;
				}
				String helpCommand = args[1];
				if(helpCommand.equals("setPassword")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("setPassword [password]");
					p.sendMessage("Sets the password for use when locking/unlocking your account");
				}
				if(helpCommand.equals("lock")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("lock");
					p.sendMessage("Locks your account, every login will prompt you to" +
							" unlock your account, actions will be suspended");
				}
				if(helpCommand.equals("unlock")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("unlock [password]");
					p.sendMessage("Unlocks your account for use in this login");
				}
				if(helpCommand.equals("killLock")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("killLock [password]");
					p.sendMessage("Unlocks your account for all future logins. Requires authenitcation with password");
				}
				if(helpCommand.equals("lockR") && p.hasPermission("accountLock.admin")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("lockR [displayName]");
					p.sendMessage("Remotely locks a persons account by their displayName");
				}
				if(helpCommand.equals("unlockR") && p.hasPermission("accountLock.admin")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("lockR [displayName]");
					p.sendMessage("Remotely UNlocks a persons account by their displayName");
				}
				if(helpCommand.equals("resetPassword") && p.hasPermission("accountLock.admin")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("lockR [displayName]");
					p.sendMessage("Remotely resets a persons account password by their displayName");
				}
			}
			
			if(args[0].equals("setPassword") && p.hasPermission("accountLock.basic")){
				String password = args[1];
				if(this.getConfig().getString(p.getName()+".accountPasswordHash") != null){
					p.sendMessage(ChatColor.RED+
							"[AccountLock]"+ChatColor.WHITE+" - " +
									"ERROR! Password already set. Contact admin if you want it changed");
					return true;
				}
				this.getConfig().set(p.getName()+".accountPasswordHash", applyPWD(password));
				this.saveConfig();
				p.sendMessage(ChatColor.RED+
						"[AccountLock]"+ChatColor.WHITE+" - " +
								"Password set!");
				return true;
			}
			
			if(args[0].equals("lock") && p.hasPermission("accountLock.basic")){
					if(this.getConfig().getString(p.getName()+".accountPasswordHash") != null){
						this.getConfig().set(p.getName()+".accountLockOn", true);
						this.getConfig().set(p.getName()+".accountAutoLockOn", true);
						this.saveConfig();
						lockedPlayers.add(p);
						p.sendMessage(ChatColor.RED+
								"[AccountLock]"+ChatColor.WHITE+" - " +
									"Your account has been locked");
						return true;
					}
					else{
						p.sendMessage(ChatColor.RED+
								"[AccountLock]"+ChatColor.WHITE+" - " +
										"ERROR! You cannot lock your account before setting a password");
						return true;
					}
			}
			
			if(args[0].equals("killLock") && p.hasPermission("accountLock.basic")){
				this.getConfig().set(p.getName()+".accountAutoLockOn", false);
				this.getConfig().set(p.getName()+".accountLockOn", false);
				this.saveConfig();
				lockedPlayers.remove(p);
				p.sendMessage(ChatColor.RED+
						"[AccountLock]"+ChatColor.WHITE+" - " +
								"Your account has been perma-unlocked");
				return true;
			}
			
			if(args[0].equals("unlock") && p.hasPermission("accountLock.basic")){
				
				if(args.length < 2){args[1] = "";}
				String inputPassword = getMD5(args[1]);
				String cachePassword = new String(Base64.decode(
								this.getConfig().getString(p.getName()+".accountPasswordHash")));
				if(inputPassword.equals(cachePassword)){
					this.getConfig().set(p.getName()+".accountLockOn", false);
					this.saveConfig();
					lockedPlayers.remove(p);
					log.info("[AccountLock] Player:"+p.getName()+" successfully logged in from IP:"+p.getAddress());
					p.setVelocity(defaultVelocity);
					p.setFallDistance(0);
					p.sendMessage(ChatColor.RED+"[AccountLock]"+ChatColor.WHITE+" - Account Unlocked!");
					
					return true;
				}
				else{
					p.sendMessage(ChatColor.RED+"[AccountLock]"+ChatColor.WHITE+" - Incorrect Password");
					log.info("[AccountLock] Player:"+p.getName()+" failed login from IP:"+p.getAddress());
					return true;
				}
			}
			
			if(args[0].equals("unlockR")){
				// We are doing a remote unlock
				if(p.hasPermission("accountLock.admin")){ //If player has admin perms
					for(Player onlinePlayer : this.getServer().getOnlinePlayers()){ 
						if(onlinePlayer.getDisplayName().equals(args[1])){ 
							Player target = this.getServer().getPlayer(args[1]);
							this.getConfig().set(target.getName()+".accountLockOn", false);
							this.saveConfig();
							lockedPlayers.remove(target);
							target.setVelocity(defaultVelocity);
							target.setFallDistance(0);
							p.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
											"Player \""+target.getDisplayName()+"\"'s account has been UNlocked");
							target.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
										"Your account has been UNlocked");
							return true;
						}
					}
				}
			return false;
			}
			
			if(args[0].equals("lockR")){
				// We are doing a remote lock
				if(p.hasPermission("accountLock.admin")){ //If player has admin perms
					for(Player onlinePlayer : this.getServer().getOnlinePlayers()){ 
						if(onlinePlayer.getDisplayName().equals(args[1])){ 
							Player target = this.getServer().getPlayer(args[1]);
							this.getConfig().set(target.getName()+".accountLockOn", true);
							this.saveConfig();
							lockedPlayers.add(target);
							p.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
											"Player \""+target.getDisplayName()+"\"'s account has been locked");
							target.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
											"Your account has been locked");
							
						}
					}
				}
				else{
					p.sendMessage(ChatColor.RED+"[AccountLock]"+ChatColor.WHITE+" - You don't have permission to do this");
				}
			}
			
			if(args[0].equals("resetPassword")){
				// We are doing a remote lock
				if(p.hasPermission("accountLock.admin")){ //If player has admin perms
					for(Player onlinePlayer : this.getServer().getOnlinePlayers()){ 
						if(onlinePlayer.getDisplayName().equals(args[1])){ 
							Player target = this.getServer().getPlayer(args[1]);
							this.getConfig().set(target.getName()+".accountPasswordHash", null);
							this.saveConfig();
							p.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
											"Player \""+target.getDisplayName()+"\"'s account password has been reset");
							target.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
											"Your account password has been reset");
							
						}
					}
				}
				else{
					p.sendMessage(ChatColor.RED+"[AccountLock]"+ChatColor.WHITE+" - You don't have permission to do this");
				}
			}
			
			return true;
		}
		
		
		return false; 
	}
}