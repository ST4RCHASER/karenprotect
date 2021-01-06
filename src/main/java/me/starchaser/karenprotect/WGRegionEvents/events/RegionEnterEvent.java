package me.starchaser.karenprotect.WGRegionEvents.events;

import me.starchaser.karenprotect.WGRegionEvents.MovementWay;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;







public class RegionEnterEvent
  extends RegionEvent
  implements Cancellable
{
  private boolean cancelled;
  private boolean cancellable;
  
  public RegionEnterEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent)
  {
    super(region, player, movement, parent);
    cancelled = false;
    cancellable = true;
    
    if ((movement == MovementWay.SPAWN) || 
      (movement == MovementWay.DISCONNECT))
    {
      cancellable = false;
    }
  }
  






  public void setCancelled(boolean cancelled)
  {
    if (!cancellable)
    {
      return;
    }
    
    this.cancelled = cancelled;
  }
  





  public boolean isCancelled()
  {
    return cancelled;
  }
  





  public boolean isCancellable()
  {
    return cancellable;
  }
  
  protected void setCancellable(boolean cancellable)
  {
    this.cancellable = cancellable;
    
    if (!this.cancellable)
    {
      cancelled = false;
    }
  }
}
