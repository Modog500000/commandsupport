package org.modogthedev.commandsupport.custom.items;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.modogthedev.commandsupport.core.ModSounds;
import org.modogthedev.commandsupport.markers.MarkerHandeler;

public class RadioItem extends Item {
    public RadioItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);

        if (MarkerHandeler.callSupportEvent(player.position(), level, 50)) {
            player.getCooldowns().addCooldown(item.getItem(),260);
            level.playSound(null,player.getX(),player.getY(),player.getZ(), ModSounds.RADIO.get(), SoundSource.PLAYERS, 10 ,1);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return super.use(level, player, hand);
    }
}
