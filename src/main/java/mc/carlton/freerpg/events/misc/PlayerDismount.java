package mc.carlton.freerpg.events.misc;

import mc.carlton.freerpg.utils.game.HorseRiding;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerDismount implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  void onPlayerTakeDamage(EntityDismountEvent e) {
    if (e.isCancelled()) {
      return;
    }
    Entity mount = e.getDismounted();
    HorseRiding horseRiding = new HorseRiding();
    Player p = horseRiding.getPlayerFromMount(mount.getUniqueId());
    if (p != null) {
      horseRiding.deletePlayerData(p);
    }

  }
}
