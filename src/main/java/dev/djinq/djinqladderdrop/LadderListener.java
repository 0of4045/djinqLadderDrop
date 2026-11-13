package dev.djinq.djinqladderdrop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public final class LadderListener implements Listener {
    private final DjinqLadderDrop plugin;
    private final Set<Block> collapsing = new HashSet<>();

    public LadderListener(DjinqLadderDrop plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block broken = event.getBlock();
        if (!plugin.isLadderDropEnabled() || broken.getType() != Material.LADDER || collapsing.contains(broken)) {
            return;
        }

        List<BlockState> ladders = laddersAbove(broken);
        if (ladders.isEmpty()) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = event.getPlayer();
            if (event.isCancelled() || !plugin.isLadderDropEnabled() || !player.isOnline() || player.isDead()
                    || !broken.getWorld().isChunkLoaded(broken.getX() >> 4, broken.getZ() >> 4)
                    || !broken.getType().isAir()) {
                return;
            }
            for (BlockState ladder : ladders) {
                if (!ladder.getBlock().getBlockData().equals(ladder.getBlockData())) {
                    return;
                }
            }
            for (int index = ladders.size() - 1; index >= 0; index--) {
                if (!breakNaturally(player, ladders.get(index))) {
                    break;
                }
            }
        });
    }

    private static List<BlockState> laddersAbove(Block broken) {
        List<BlockState> ladders = new ArrayList<>();
        Block current = broken.getRelative(BlockFace.UP);
        while (current.getType() == Material.LADDER) {
            ladders.add(current.getState());
            current = current.getRelative(BlockFace.UP);
        }
        return ladders;
    }

    private boolean breakNaturally(Player player, BlockState expected) {
        Block ladder = expected.getBlock();
        if (!ladder.getBlockData().equals(expected.getBlockData())) {
            return false;
        }
        collapsing.add(ladder);
        try {
            BlockBreakEvent event = new BlockBreakEvent(ladder, player);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled() || !ladder.getBlockData().equals(expected.getBlockData())) {
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
