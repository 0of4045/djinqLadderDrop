package dev.djinq.djinqladderdrop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public final class LadderListener implements Listener {
    private final DjinqLadderDrop plugin;
    private final Set<Block> collapsing = new HashSet<>();

    public LadderListener(DjinqLadderDrop plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block broken = event.getBlock();
        if (!plugin.isLadderDropEnabled() || broken.getType() != Material.LADDER || collapsing.contains(broken)) {
            return;
        }

        List<Block> ladders = laddersAbove(broken);
        for (int index = ladders.size() - 1; index >= 0; index--) {
            if (!breakNaturally(event.getPlayer(), ladders.get(index))) {
                break;
            }
        }
    }

    private static List<Block> laddersAbove(Block broken) {
        List<Block> ladders = new ArrayList<>();
        Block current = broken.getRelative(BlockFace.UP);
        while (current.getType() == Material.LADDER) {
            ladders.add(current);
            current = current.getRelative(BlockFace.UP);
        }
        return ladders;
    }

    private boolean breakNaturally(Player player, Block ladder) {
        collapsing.add(ladder);
        try {
            BlockBreakEvent event = new BlockBreakEvent(ladder, player);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            if (!event.isDropItems()) {
                ladder.setType(Material.AIR, true);
                return true;
            }

            return ladder.breakNaturally(player.getInventory().getItemInMainHand(), true);
        } finally {
            collapsing.remove(ladder);
        }
    }
}
