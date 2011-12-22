package net.cryptum.dev.accountLock;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
 
public class AccountLockMain extends JavaPlugin {
 
	Logger log = Logger.getLogger("Minecraft");
	ArrayList<String> lockedPlayers = new ArrayList<String>();
	Vector defaultVelocity = new Vector(-4.9E-324,-0.0784000015258789,4.9E-324);
	
	public static Permission permission = null;

    private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
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
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerActionListener, Event.Priority.Lowest, this);
		
		pm.registerEvent(Event.Type.BLOCK_PLACE, playerBlockListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, playerBlockListener, Event.Priority.Lowest, this);
		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, playerEntityListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.ENTITY_INTERACT, playerEntityListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, playerEntityListener, Event.Priority.Lowest, this);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, 
				new AccountLockedAlert(this), 0, 600L); //Informs player that account is locked every 30s 
        setupPermissions();
		
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
		if(cmd.getName().equalsIgnoreCase("alock")){
			if(!permission.playerHas(p,"accountLock.basic")){ 
				p.sendMessage(ChatColor.RED+
						"[AccountLock] - You don't have the permissions to use this plugin");
				return true;
			}
			if(args.length <= 0){
				boolean locked = false;
				p.sendMessage(ChatColor.RED+
						"[AccountLock]");
				if(lockedPlayers.contains(p.getName())){locked = true;}
				p.sendMessage("Account Locked? "+locked);
				p.sendMessage("Use /alock help [command] for help on specific commands");
				p.sendMessage("/alock setPassword [password]");
				p.sendMessage("/alock lock");
				p.sendMessage("/alock unlock [password]");
				p.sendMessage("/alock killLock [password]");
				if(permission.playerHas(p,"accountLock.admin")){
					p.sendMessage("/alock lockR [displayName]");
					p.sendMessage("/alock unlockR [displayName]");
					p.sendMessage("/alock resetPassword [displayName]");
					p.sendMessage("/alock status [displayName]");
				}
				return true;
			}
			
			if(args[0].equals("status") && permission.playerHas(p,"accountLock.admin")){
				String playerName = args[1];
				if(this.getConfig().getBoolean(playerName+".accountAutoLockOn") || this.getConfig().getBoolean(playerName+".accountLockOn")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("Player '"+playerName+"' account is "+ChatColor.BLUE+"locked");
				}
				else{
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("Player '"+playerName+"' account is "+ChatColor.AQUA+"unlocked");
				}
			}
			
			if(args[0].equals("help") && permission.playerHas(p,"accountLock.basic")){
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
				if(helpCommand.equals("lockR") && permission.playerHas(p,"accountLock.admin")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("lockR [displayName]");
					p.sendMessage("Remotely locks a persons account by their displayName" +
							"If they are offline, it uses the displayName as their username instead");
				}
				if(helpCommand.equals("unlockR") && permission.playerHas(p,"accountLock.admin")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("unlockR [displayName]");
					p.sendMessage("Remotely UNlocks a persons account by their displayName");
				}
				if(helpCommand.equals("resetPassword") && permission.playerHas(p,"accountLock.admin")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("lockR [displayName]");
					p.sendMessage("Remotely resets a persons account password by their displayName" +
							"If they are offline, it uses the displayName as their username instead");
				}
				if(helpCommand.equals("status") && permission.playerHas(p,"accountLock.admin")){
					p.sendMessage(ChatColor.RED+
					"[AccountLock]");
					p.sendMessage("status [displayName]");
					p.sendMessage("Displays the account status of a user (locked or not) by their displayName" +
							"If they are offline, it uses the displayName as their username instead");
				}
			}
			
			if(args[0].equals("setPassword") && permission.playerHas(p,"accountLock.basic")){
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
			
			if(args[0].equals("lock") && permission.playerHas(p,"accountLock.basic")){
					if(this.getConfig().getString(p.getName()+".accountPasswordHash") != null){
						this.getConfig().set(p.getName()+".accountLockOn", true);
						this.getConfig().set(p.getName()+".accountAutoLockOn", true);
						this.saveConfig();
						lockedPlayers.add(p.getName());
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
			
			if(args[0].equals("tempLock") && permission.playerHas(p,"accountLock.basic")){
				if(this.getConfig().getString(p.getName()+".accountPasswordHash") != null){
					this.getConfig().set(p.getName()+".accountLockOn", true);
					this.saveConfig();
					lockedPlayers.add(p.getName());
					p.sendMessage(ChatColor.RED+
							"[AccountLock]"+ChatColor.WHITE+" - " +
								"Your account has been locked for this login");
					return true;
				}
				else{
					p.sendMessage(ChatColor.RED+
							"[AccountLock]"+ChatColor.WHITE+" - " +
									"ERROR! You cannot lock your account before setting a password");
					return true;
				}
			}
			
			if(args[0].equals("killLock") && permission.playerHas(p,"accountLock.basic")){
				this.getConfig().set(p.getName()+".accountAutoLockOn", false);
				this.getConfig().set(p.getName()+".accountLockOn", false);
				this.saveConfig();
				lockedPlayers.remove(p.getName());
				p.sendMessage(ChatColor.RED+
						"[AccountLock]"+ChatColor.WHITE+" - " +
								"Your account has been perma-unlocked");
				return true;
			}
			
			if(args[0].equals("unlock") && permission.playerHas(p,"accountLock.basic")){
				
				if(args.length < 2){args[1] = "";}
				String inputPassword = getMD5(args[1]);
				String cachePassword = new String(Base64.decode(
								this.getConfig().getString(p.getName()+".accountPasswordHash")));
				if(inputPassword.equals(cachePassword)){
					this.getConfig().set(p.getName()+".accountLockOn", false);
					this.saveConfig();
					lockedPlayers.remove(p.getName());
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
				if(permission.playerHas(p,"accountLock.admin")){ //If player has admin perms
					boolean playerOnline = false;
					for(Player onlinePlayer : this.getServer().getOnlinePlayers()){ 
						if(onlinePlayer.getDisplayName().equals(args[1])){ 
							Player target = this.getServer().getPlayer(args[1]);
							this.getConfig().set(target.getName()+".accountAutoLockOn", false);
							this.getConfig().set(target.getName()+".accountLockOn", false);
							this.saveConfig();
							lockedPlayers.remove(target.getName());
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
					if(!playerOnline){
						this.getConfig().set(args[1]+".accountAutoLockOn", false);
						this.getConfig().set(args[1]+".accountLockOn", false);
						this.saveConfig();
						lockedPlayers.remove(args[1]);
						p.sendMessage(ChatColor.RED+
								"[AccountLock]"+ChatColor.WHITE+" - " +
										"Player \""+args[1]+"\" is offline, " +
										"however their account has still been UNlocked");
						return true;
						
					}
					return true;
				}
			return false;
			}
			
			if(args[0].equals("lockR")){
				// We are doing a remote lock
				if(permission.playerHas(p,"accountLock.admin")){ //If player has admin perms
					boolean playerOnline = false;
					for(Player onlinePlayer : this.getServer().getOnlinePlayers()){ 
						if(onlinePlayer.getName().equals(args[1])){
							playerOnline = true;
							Player target = this.getServer().getPlayer(args[1]);
							this.getConfig().set(target.getName()+".accountLockOn", true);
							this.saveConfig();
							lockedPlayers.add(target.getName());
							p.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
											"Player \""+target.getDisplayName()+"\"'s account has been locked");
							target.sendMessage(ChatColor.RED+
									"[AccountLock]"+ChatColor.WHITE+" - " +
											"Your account has been locked");
							
						}
					}
					if(!playerOnline){
						this.getConfig().set(args[1]+".accountAutoLockOn", true);
						this.getConfig().set(args[1]+".accountLockOn", true);
						this.saveConfig();
						p.sendMessage(ChatColor.RED+
								"[AccountLock]"+ChatColor.WHITE+" - " +
										"Player \""+args[1]+"\" is offline, " +
										"however their account has still been locked");
						return true;
						
					}
					return true;
				}
				else{
					p.sendMessage(ChatColor.RED+"[AccountLock]"+ChatColor.WHITE+" - You don't have permission to do this");
				}
			}
			
			if(args[0].equals("resetPassword")){
				// We are doing a remote lock
				if(permission.playerHas(p,"accountLock.admin")){ //If player has admin perms
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