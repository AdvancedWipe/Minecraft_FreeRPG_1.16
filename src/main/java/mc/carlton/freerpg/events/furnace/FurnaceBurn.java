package mc.carlton.freerpg.events.furnace;

import mc.carlton.freerpg.skills.perksAndAbilities.Smelting;
import mc.carlton.freerpg.utils.game.FurnaceUserTracker;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class FurnaceBurn implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  void onBurn(FurnaceBurnEvent e) {
    if (e.isCancelled()) {
      return;
    }
    boolean isBlast = false;
    if (e.getBlock().getType() == Material.BLAST_FURNACE
        || e.getBlock().getType() == Material.SMOKER) {
      isBlast = true;
    }
    Furnace furnace = (Furnace) e.getBlock().getState();
    FurnaceUserTracker furnaceTracker = new FurnaceUserTracker();
    Player p = furnaceTracker.getPlayer(furnace.getLocation());
    if (p != null && p.isOnline()) {
      Smelting smeltingClass = new Smelting(p);
      smeltingClass.fuelBurn(furnace, isBlast);
    }

  }
}
