package me.often.aureliummobs.entities;

import me.often.aureliummobs.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static me.often.aureliummobs.Main.mm;

public class AureliumMob {

    double resDamage;
    double resHealth;

    public AureliumMob(Monster mob, int level, Main plugin) {

        if (mob instanceof Zombie) {
            for (AttributeModifier modifier : mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()) {
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().clear();
                break;
            }
        }

        int level1;
        if (level > 0) {
            level1 = level;
        } else {
            level1 = 1;
        }
        double startDamage = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        double startHealth = BigDecimal.valueOf(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()).setScale(2, RoundingMode.CEILING).doubleValue();
        resHealth = Math.max(startHealth, startHealth + 30 * (Math.log(level1) / Math.log(10)) - 20);
        resDamage = startDamage + (startDamage * (level / 10));
        double damage = BigDecimal.valueOf(resDamage).setScale(2, RoundingMode.CEILING).doubleValue();
        double health = BigDecimal.valueOf(resHealth).setScale(2, RoundingMode.CEILING).doubleValue();
        if (health > plugin.getMaxHealth()) {
            health = plugin.getMaxHealth();
        }
        if (damage > plugin.getMaxDamage()) {
            damage = plugin.getMaxDamage();
        }
        mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
        mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        mob.setHealth(health);
        mob.getPersistentDataContainer().set(Main.mobKey, PersistentDataType.INTEGER, level);
        mob.customName(
                mm.deserialize(getDName(level1))
                        .append(Component.translatable(mob.getType().translationKey()).decoration(TextDecoration.ITALIC, false))
                        .append(mm.deserialize(Main.name.replace("{lvl}", String.valueOf(level1))))
        );
        mob.setCustomNameVisible(true);
    }


    public static boolean isAureliumMob(Monster m) {
        return m.getPersistentDataContainer().has(Main.mobKey, PersistentDataType.INTEGER);
    }

    public static String getDName(Integer i) {
        if (i <= 20) {
            return "弱小的";
        } else if (i <= 50) {
            return "一般的";
        } else if (i <= 70) {
            return "精英";
        } else if (i <= 100) {
            return "超级";
        } else {
            return "究极";
        }
    }

}
