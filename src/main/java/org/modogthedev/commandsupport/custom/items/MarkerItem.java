package org.modogthedev.commandsupport.custom.items;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.modogthedev.commandsupport.Commandsupport;
import org.modogthedev.commandsupport.core.ModParticles;
import org.modogthedev.commandsupport.core.ModSounds;
import org.modogthedev.commandsupport.markers.MarkerHandeler;
import org.modogthedev.commandsupport.markers.MarkerMarker;

public class MarkerItem  extends Item {
    public TYPE type;

    public MarkerItem(MarkerProperties properties, Item.Properties properties1) {
        super(properties1);
        this.type = properties.type;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        MarkerHandeler.addMarker(player.position(),level, type, player);
        ItemStack item = player.getItemInHand(hand);
        level.playSound(null,player.getX(),player.getY(),player.getZ(), ModSounds.FLARE.get(), SoundSource.PLAYERS, 0.5f ,1);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(item.getItem(),20);
        item.shrink(1);
        return InteractionResultHolder.consume(item);
    }
    public enum TYPE {
        SUPPLY,
        AIRSTRIKE,
        NUKE
    }

    public static class MarkerProperties {
        public TYPE type;
        public MarkerProperties type(TYPE type) {
            this.type = type;
            return this;
        }
    }
}
