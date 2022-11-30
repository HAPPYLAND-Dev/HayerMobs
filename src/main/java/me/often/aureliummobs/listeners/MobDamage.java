package me.often.aureliummobs.listeners;

import me.often.aureliummobs.Main;
import me.often.aureliummobs.entities.AureliumMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import static me.often.aureliummobs.Main.mm;

public class MobDamage implements Listener {

    private final Main plugin;

    public MobDamage(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobDamage(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Monster m)) {
            return;
        }

        if (!AureliumMob.isAureliumMob(m)) {
            return;
        }

        int level = m.getPersistentDataContainer().get(Main.mobKey, PersistentDataType.INTEGER);
        double resHealth = m.getHealth() - e.getDamage();
        String formattedHealth = plugin.getFormatter().format(resHealth);
        Component customName = mm.deserialize(Main.name
                        .replace("{lvl}", Integer.toString(level))
                )
                .append(Component.translatable(e.getEntity().getType().translationKey())).decoration(TextDecoration.ITALIC, false)
                .append(mm.deserialize(Main.health
                        .replace("{health}", formattedHealth)
                        .replace("{maxhealth}", plugin.getFormatter().format(m.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))));
        try {
            m.customName(customName);
        } catch (NullPointerException ex) {
            m.customName(customName);
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
