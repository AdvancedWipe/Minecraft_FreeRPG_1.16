package mc.carlton.freerpg.events.brewing;

import java.util.Map;
import mc.carlton.freerpg.FreeRPG;
import mc.carlton.freerpg.config.ConfigLoad;
import mc.carlton.freerpg.core.info.player.ChangeStats;
import mc.carlton.freerpg.skills.perksAndAbilities.Alchemy;
import mc.carlton.freerpg.utils.game.BrewingStandUserTracker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class FinishedBrewing implements Listener {

  Plugin plugin = FreeRPG.getPlugin(FreeRPG.class);

  @EventHandler(priority = EventPriority.HIGH)
  void onBrewComplete(BrewEvent e) {
    if (e.isCancelled()) {
      return;
    }
    ConfigLoad configLoad = new ConfigLoad();
    if (!configLoad.getAllowedSkillsMap().get("alchemy")) {
      return;
    }
    BrewerInventory inventory = e.getContents();
    ItemStack ingredient = inventory.getItem(3).clone();
    ItemStack[] slotItems = {inventory.getItem(0), inventory.getItem(1), inventory.getItem(2)};
    BrewingStandUserTracker brewTracker = new BrewingStandUserTracker();
    Player p = brewTracker.getPlayer(inventory.getHolder());
    if (p != null) {
      Alchemy alchemyClass = new Alchemy(p);
      alchemyClass.giveBrewingEXP(ingredient, slotItems);
    }
    if (ingredient.getType() == Material.DRAGON_BREATH
        || ingredient.getType() == Material.GUNPOWDER) {
      new BukkitRunnable() {
        @Override
        public void run() {
          BrewingStand stand = inventory.getHolder();
          for (int i = 0; i < 3; i++) {
            ItemStack slot_i = slotItems[i];
            if (slot_i != null) {
              if (slot_i.getType() != Material.AIR) {
                if (slot_i.getEnchantments().containsKey(Enchantment.LOYALTY)
                    || slot_i.getEnchantments().containsKey(Enchantment.DURABILITY)) {
                  PotionMeta slotMeta = (PotionMeta) slot_i.getItemMeta();
                  String normalName = slotMeta.getDisplayName().substring(2);
                  if (ingredient.getType() == Material.GUNPOWDER) {
                    if (slot_i.getType() == Material.POTION) {
                      slot_i.setType(Material.SPLASH_POTION);
                      slotMeta.setDisplayName(ChatColor.RESET + "Splash " + normalName);
                    }
                  } else {
                    if (slot_i.getType() == Material.SPLASH_POTION) {
                      slot_i.setType(Material.LINGERING_POTION);
                      normalName = normalName.substring(7);
                      slotMeta.setDisplayName(ChatColor.RESET + "Lingering " + normalName);
                      PotionEffectType effectType = slotMeta.getCustomEffects().get(0).getType();
                      int newLength = (int) Math.round(
                          slotMeta.getCustomEffects().get(0).getDuration() * 0.25);
                      int level = slotMeta.getCustomEffects().get(0).getAmplifier();
                      slotMeta.addCustomEffect(new PotionEffect(effectType, newLength, level),
                          true);
                    }
                  }
                  slot_i.setItemMeta(slotMeta);
                  stand.getSnapshotInventory().setItem(i, slot_i);
                  stand.update();
                  if (p != null) {
                    ConfigLoad configLoad1 = new ConfigLoad();
                    Map<String, Integer> expMap = configLoad1.getExpMapForSkill("alchemy");
                    ChangeStats increaseStats = new ChangeStats(p);
                    increaseStats.changeEXP("alchemy", expMap.get("brewSplashPotion"));
                  }
                }
              }

            }
          }

        }
      }.runTaskLater(plugin, 1);
    }

  }
}
