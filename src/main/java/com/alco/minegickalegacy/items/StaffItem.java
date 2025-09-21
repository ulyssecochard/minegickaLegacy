package com.alco.minegickalegacy.items;

import com.alco.minegickalegacy.MinegickaMod;
import com.alco.minegickalegacy.network.MinegickaNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StaffItem extends Item {
    private static final String TAG_ROOT = "MinegickaStaff";
    private static final String TAG_POWER = "Power";
    private static final String TAG_ATTACK_SPEED = "AttackSpeed";
    private static final String TAG_CONSUME = "Consume";
    private static final String TAG_RECOVER = "Recover";
    private static final String TAG_ENCHANTED = "Enchanted";

    private final Stats baseStats;

    public StaffItem(final Stats stats, final Properties properties) {
        super(properties.stacksTo(1));
        this.baseStats = stats;
    }

    public Stats getStats() {
        return baseStats;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            if (player.isShiftKeyDown()) {
                MinegickaNetwork.requestStaffAbility(hand);
            }
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        final Stats stats = getEffectiveStats(stack);
        player.displayClientMessage(Component.translatable("item.minegicka.staff.use", stats.power(), stats.attackSpeed(), stats.consume(), stats.recover()), true);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    public void activateAbility(final ServerLevel level, final ServerPlayer player, final ItemStack stack) {
        MinegickaMod.LOGGER.debug("Staff ability triggered for {} using {}", player.getGameProfile().getName(), stack.getItem().toString());
        // TODO: port active staff abilities from legacy implementation.
    }

    @Override
    public void appendHoverText(final ItemStack stack, @Nullable final Level level, final List<Component> tooltip, final TooltipFlag flag) {
        final Stats stats = getEffectiveStats(stack);
        tooltip.add(Component.translatable("item.minegicka.staff.power", stats.power()));
        tooltip.add(Component.translatable("item.minegicka.staff.attack_speed", stats.attackSpeed()));
        tooltip.add(Component.translatable("item.minegicka.staff.consume", stats.consume()));
        tooltip.add(Component.translatable("item.minegicka.staff.recover", stats.recover()));
        if (isEnchanted(stack)) {
            tooltip.add(Component.translatable("item.minegicka.staff.enchanted").withStyle(ChatFormatting.AQUA));
        }
    }

    public static Stats getEffectiveStats(final ItemStack stack) {
        if (!(stack.getItem() instanceof StaffItem staff)) {
            return new Stats(1.0D, 1.0D, 1.0D, 1.0D);
        }
        final Stats base = staff.getStats();
        final CompoundTag tag = stack.getTagElement(TAG_ROOT);
        if (tag == null) {
            return base;
        }
        final double power = tag.contains(TAG_POWER) ? tag.getDouble(TAG_POWER) : base.power();
        final double attackSpeed = tag.contains(TAG_ATTACK_SPEED) ? tag.getDouble(TAG_ATTACK_SPEED) : base.attackSpeed();
        final double consume = tag.contains(TAG_CONSUME) ? tag.getDouble(TAG_CONSUME) : base.consume();
        final double recover = tag.contains(TAG_RECOVER) ? tag.getDouble(TAG_RECOVER) : base.recover();
        return new Stats(power, attackSpeed, consume, recover);
    }

    public static void applyEnchantment(final ItemStack stack, final Stats stats) {
        final CompoundTag tag = stack.getOrCreateTagElement(TAG_ROOT);
        tag.putDouble(TAG_POWER, stats.power());
        tag.putDouble(TAG_ATTACK_SPEED, stats.attackSpeed());
        tag.putDouble(TAG_CONSUME, stats.consume());
        tag.putDouble(TAG_RECOVER, stats.recover());
        tag.putBoolean(TAG_ENCHANTED, true);
    }

    public static boolean isEnchanted(final ItemStack stack) {
        final CompoundTag tag = stack.getTagElement(TAG_ROOT);
        return tag != null && tag.getBoolean(TAG_ENCHANTED);
    }

    public record Stats(double power, double attackSpeed, double consume, double recover) {
    }
}
