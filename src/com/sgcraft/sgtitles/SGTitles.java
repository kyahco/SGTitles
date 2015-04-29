/*
 *  SGTitles - Give your users a collection of titles
 *  Copyright (C) 2012  SGCraft
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sgcraft.sgtitles;

import com.sgcraft.sgtitles.commands.TitleCommands;
import com.sgcraft.sgtitles.title.Title;
import com.sgcraft.sgtitles.title.TitleManager;
import lib.PatPeter.SQLibrary.SQLite;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

//import org.simiancage.DeathTpPlus.DeathTpPlus;
//import com.gmail.nossr50.mcMMO;
//import com.herocraftonline.dev.heroes.Heroes;

public class SGTitles extends JavaPlugin {
	public static SGTitles plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static FileConfiguration config;
	public static SQLite sql;
	public static HashMap<String, Title> TitleList = new HashMap<String, Title>();  
	public static Permission permission = null;
	//public static mcMMO mcPlugin;
	//public static DeathTpPlus dtpPlugin;
	//public static Heroes hPlugin;
	//public static boolean spoutEnabled = false;
	
	public static List<ChatColor> getAllColors() {
		List<ChatColor> colors = new ArrayList<ChatColor>();
		for (ChatColor color : ChatColor.values()) {
			colors.add(color);
		}
		return colors;
	}
	
	private void addCommands() {
		getCommand("title").setExecutor(new TitleCommands(this));
	}
	
	/*private boolean checkMcMMO() {
		Plugin mmo = getServer().getPluginManager().getPlugin("mcMMO");
		if (mmo != null && config.getBoolean("mcmmo.enabled")) {
			mcPlugin = (mcMMO) mmo;
			return true;
		} else {
			mcPlugin = null;
			return false;
		}
	}*/
	
	/*private boolean checkDtp() {
		Plugin dtp = getServer().getPluginManager().getPlugin("DeathTpPlus");
		if (dtp != null && config.getBoolean("deathtp.enabled")) {
			dtpPlugin = (DeathTpPlus) dtp;
			return true;
		} else {
			dtpPlugin = null;
			return false;
		}
	}*/
	
	/*private boolean checkHeroes() {
		Plugin heroes = getServer().getPluginManager().getPlugin("Heroes");
		if (heroes != null && config.getBoolean("heroes.enabled")) {
			hPlugin = (Heroes) heroes;
			return true;
		} else {
			hPlugin = null;
			return false;
		}
	}*/
	
	/*private boolean checkSpout() {
		Plugin sPlugin = getServer().getPluginManager().getPlugin("Spout");
		if (sPlugin != null) {
			spoutEnabled = true;
			return true;
		} else
			return false;
	}*/
	
	private void createTables() {
		try {
			// Create Titles Table
			sql.query("CREATE TABLE IF NOT EXISTS titles (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, data TEXT NOT NULL, position TEXT NOT NULL)");

			// Player/Title Association Table
			sql.query("CREATE TABLE if not exists player_titles (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT NOT NULL, title_name INTEGER NOT NULL)");

			// Active Title Database
			sql.query("CREATE TABLE if not exists active_titles (player_name TEXT NOT NULL, title_prefix TEXT, title_suffix TEXT, title_color TEXT)");
		}catch (SQLException e){
			logger.info(e.getMessage());
		}
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		logger.info("[" + pdf.getName() + "] is now disabled!");
		this.getDataFolder();
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdf = this.getDescription();
		config = getConfig();
        config.options().copyDefaults(true);
		saveConfig();
		//logger.info(getDataFolder().getPath());
		sql = new SQLite(logger, "[ " + pdf.getName() + "]", getDataFolder().getPath(), "titles");
		try {
			sql.open();
		}catch (Exception e){
			logger.info(e.getMessage());
		}
		createTables();

		
		/*if (checkMcMMO())
			logger.info("[" + pdf.getName() + "] McMMO detected. Loading support...");
		if (checkDtp())
			logger.info("[" + pdf.getName() + "] DeathTpPlus detected. Loading support...");
		if (checkHeroes())
			logger.info("[" + pdf.getName() + "] Heroes detected. Loading support...");
		if (checkSpout())
			logger.info("[" + pdf.getName() + "] Spout detected. Loading support...");*/
		
		addCommands();
		startListeners();
		setupPermissions();
		TitleManager.loadAllTitles();
		logger.info("[" + pdf.getName() + "] v" + pdf.getVersion() + " is now enabled!");
	}
	
	public void reload() {
		reloadConfig();
		config = getConfig();
		logger.info("[SGTitles] Config Reloaded!");
	}
	
	private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	private void startListeners() {
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		/*if (mcPlugin != null)
			getServer().getPluginManager().registerEvents(new McMMOListener(this), this);
		if (dtpPlugin != null)
			getServer().getPluginManager().registerEvents(new DtpListener(this),this);
		if (hPlugin != null)
			getServer().getPluginManager().registerEvents(new HeroesListener(this), this);*/
	}
}
