package me.often.aureliummobs;

import com.osiris.dyml.DYValueContainer;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.DYWriterException;
import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalListException;
import me.often.aureliummobs.api.WorldGuardHook;
import me.often.aureliummobs.commands.AureliumMobsCommand;
import me.often.aureliummobs.commands.tabcompleters.AureliumMobsCommandTabCompleter;
import me.often.aureliummobs.listeners.*;
import me.often.aureliummobs.util.Formatter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {

    private static final int bstatsId = 12142;
    public static List<String> enabledworlds;
    public static boolean world_whitelist;
    public static NamespacedKey mobKey;
    public static WorldGuardHook wghook;
    public static int globalLevel;
    public static MiniMessage mm = MiniMessage.miniMessage();
    public static String name = " <color:#cfcb72>Lv.{lvl}</color>";
    private static Main instance;
    private static double maxHealth;
    private static double maxDamage;
    private Formatter formatter;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            wghook = new WorldGuardHook(true);
        }
    }

    @Override
    public void onEnable() {
        globalLevel = 0;
        //this.saveDefaultConfig();
        for (Player player : this.getServer().getOnlinePlayers()) {
            globalLevel += getLevel(player);
        }
        mobKey = new NamespacedKey(this, "isAureliumMob");
        this.getServer().getPluginManager().registerEvents(new MobSpawn(this), this);

        this.getServer().getPluginManager().registerEvents(new MobDamage(this), this);
        this.getServer().getPluginManager().registerEvents(new MobTransform(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerEvent(), this);

        getServer().getPluginManager().registerEvents(new MobDeath(), this);
        /*try {
            this.initConfig();
        } catch (IOException | DYWriterException | DuplicateKeyException | DYReaderException | IllegalListException e) {
            e.printStackTrace();
        }*/
        instance = this;
        initCommands();
        loadWorlds();
        maxHealth = Bukkit.spigot().getConfig().getDouble("settings.attribute.maxHealth.max");
        maxDamage = Bukkit.spigot().getConfig().getDouble("settings.attribute.attackDamage.max");

        formatter = new Formatter(getConfigInt("settings.health-format-max-places"));
    }

    @Override
    public void onDisable() {
    }

    public void loadWorlds() {
        enabledworlds = this.getConfigStringList("worlds.list");
        world_whitelist = this.getConfigString("worlds.type").equalsIgnoreCase("whitelist");
    }

    public void initCommands() {
        getCommand("aureliummobs").setExecutor(new AureliumMobsCommand());
        getCommand("aureliummobs").setTabCompleter(new AureliumMobsCommandTabCompleter());
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getMaxDamage() {
        return maxDamage;
    }

    public String getConfigString(String path) {
        return this.getConfig().getString(path);
    }

    public List<String> getConfigStringList(String path) {
        return this.getConfig().getStringList(path);
    }

    public int getConfigInt(String path) {
        return this.getConfig().getInt(path);
    }

    public void initConfig() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException {
        File oldCfg = new File(this.getDataFolder(), "config_old.yml");
        File cfg = new File(this.getDataFolder(), "config.yml");
        DreamYaml dreamYaml = new DreamYaml(cfg).load();
        if (!cfg.exists()) {
            this.saveDefaultConfig();
        } else {
            if (oldCfg.exists()) {
                oldCfg.delete();
            }
            cfg.renameTo(oldCfg);
            Files.copy(this.getResource("config.yml"), cfg.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.reloadConfig();
            YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config_old.yml"));
            for (String key : oldConfig.getKeys(true)) {
                if (this.getConfig().get(key) != null && !(this.getConfig().get(key) instanceof ConfigurationSection)) {
                    this.getConfig().set(key, oldConfig.get(key));
                    saveKey(dreamYaml, oldConfig.get(key), key);
                }
            }
            dreamYaml.save();
            System.out.println((this.getConfig().saveToString()));
            PrintWriter printWriter = new PrintWriter(new File(this.getDataFolder(), "config.yml"));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    public void saveKey(DreamYaml config, Object value, String key) {

        if (value instanceof String string) {
            config.get(key).setValues(string);
        } else if (value instanceof Integer integer) {
            config.get(key).setValues("" + integer);
        } else if (value instanceof Double doubl) {
            config.get(key).setValues("" + doubl);
        } else if (value instanceof Float fl) {
            config.get(key).setValues("" + fl);
        } else if (value instanceof Boolean bool) {
            config.get(key).setValues("" + bool);
        } else if (value instanceof List list) {
            List<DYValueContainer> newList = new ArrayList<>();
            for (Object o : list) {
                newList.add(new DYValueContainer("" + o));
            }
            config.get(key).setValues(newList);
        }

    }

    public boolean getConfigBool(String path) {
        return this.getConfig().getBoolean(path);
    }

    public int getGlobalLevel() {
        return globalLevel;
    }

    public int getLevel(Player p) {

        int level = 0;

        PlayerInventory inventory = p.getInventory();
        ItemStack chestplate = inventory.getChestplate();
        if (chestplate != null) {
            for (Integer i : chestplate.getEnchantments().values()) {
                level = level + i;
            }
        }

        ItemStack boots = inventory.getBoots();
        if (boots != null) {
            for (Integer i : boots.getEnchantments().values()) {
                level = level + i;
            }
        }

        ItemStack helmet = inventory.getHelmet();
        if (helmet != null) {
            for (Integer i : helmet.getEnchantments().values()) {
                level = level + i;
            }
        }

        ItemStack leggings = inventory.getLeggings();
        if (leggings != null) {
            for (Integer i : leggings.getEnchantments().values()) {
                level = level + i;
            }
        }

        return level;

    }

    public Formatter getFormatter() {
        return formatter;
    }

}
