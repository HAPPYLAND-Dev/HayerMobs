package me.often.aureliummobs.listeners;

import me.often.aureliummobs.entities.AureliumMob;
import me.often.aureliummobs.Main;
import me.often.aureliummobs.util.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

import static me.often.aureliummobs.Main.mm;

public class MobDamage implements Listener {

    private Main plugin;

    public MobDamage(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobDamage(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Monster m)) {
            return;
        }

        if (!AureliumMob.isAureliumMob(m)){
            return;
        }

        int level = m.getPersistentDataContainer().get(Main.mobKey, PersistentDataType.INTEGER);
        double resHealth = m.getHealth() - e.getDamage();
        String formattedHealth = plugin.getFormatter().format(resHealth);
        try {
            m.customName(mm.deserialize(plugin.getConfigString("settings.name-format")
                    .replace("{lvl}", Integer.toString(level))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", plugin.getFormatter().format(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
            ).append(Component.translatable(e.getEntity().getType().translationKey())).decoration(TextDecoration.ITALIC, false)
            );
        } catch (NullPointerException ex){
            m.customName(mm.deserialize(plugin.getConfigString("settings.name-format")
                    .replace("{lvl}", Integer.toString(level))
                    .replace("{health}", formattedHealth)
                    .replace("{maxhealth}", plugin.getFormatter().format(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
            ).append(Component.translatable(e.getEntity().getType().translationKey())).decoration(TextDecoration.ITALIC, false)
            );
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Projectile p)) {
            return;
        }

        if (!(p.getShooter() instanceof Monster m)) {
            return;
        }

        if (!AureliumMob.isAureliumMob(m)) {
            return;
        }

        e.setDamage(m.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());

    }

}
