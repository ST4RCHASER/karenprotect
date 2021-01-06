package me.starchaser.karenprotect.WGRegionEvents.events;

import me.starchaser.karenprotect.WGRegionEvents.MovementWay;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;









public abstract class RegionEvent
  extends PlayerEvent
{
  private static final HandlerList handlerList = new HandlerList();
  
  private ProtectedRegion region;
  private MovementWay movement;
  public PlayerEvent parentEvent;
  
  public RegionEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent)
  {
    super(player);
    this.region = region;
    this.movement = movement;
    parentEvent = parent;
  }
  
  public HandlerList getHandlers()
  {
    return handlerList;
  }
  
  public ProtectedRegion getRegion()
  {
    return region;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlerList;
  }
  
  public MovementWay getMovementWay()
  {
    return movement;
  }
  










  public PlayerEvent getParentEvent()
  {
    return parentEvent;
  }
}
