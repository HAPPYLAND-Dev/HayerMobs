package me.often.aureliummobs.listeners;

import me.often.aureliummobs.Main;
import me.often.aureliummobs.entities.AureliumMob;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MobDamage implements Listener {

    private final Main plugin;

    public MobDamage(Main plugin) {
        this.plugin = plugin;
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
