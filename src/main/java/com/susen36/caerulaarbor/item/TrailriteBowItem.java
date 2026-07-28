
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.init.CAItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class TrailriteBowItem extends BowItem {
	public TrailriteBowItem() {
		super(new Item.Properties().durability(10293).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trailrite_bow.desc"));
	}

	public static float getPowerForTime(int pCharge) {
        float f = (float) pCharge / 30.0F;
        f = (f * f + 3 * f) * 0.75F;
        if (f > 3F) f = 3F;
        return f;
    }

    public AbstractArrow customArrow(AbstractArrow arrow) {
        arrow.setBaseDamage(arrow.getBaseDamage() * 2);
        return arrow;
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return ALSO_OCEAN_ARROW;
    }
    
    public static final Predicate<ItemStack> ALSO_OCEAN_ARROW = (itemStack) -> {
        return ARROW_ONLY.test(itemStack) 
        || itemStack.getItem() == CAItems.OCEAN_ARROW.get()
        || itemStack.getItem() == CAItems.TRAILRITE_ARROW.get();
    };

    private double getRate(Player player){
        AttributeInstance inst = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if(inst == null) return 1;
        if(inst.getBaseValue() <= 0) return 1;
        return Math.max(inst.getValue() / inst.getBaseValue(), 1);
    }

    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(pLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.INFINITY), pStack) > 0;
            ItemStack itemstack = player.getProjectile(pStack);

            int i = this.getUseDuration(pStack, player) - pTimeLeft;
            i = net.neoforged.neoforge.event.EventHooks.onArrowLoose(pStack, pLevel, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getPowerForTime(i);
                if (!((double)f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, pStack, player));
                    if (!pLevel.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, itemstack, player, pStack);
                        abstractarrow = customArrow(abstractarrow);
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 0.2F);
                        if (f >= 1.0F) {
                            abstractarrow.setCritArrow(true);
                        }

                        int j = EnchantmentHelper.getItemEnchantmentLevel(pLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER), pStack);
                        if (j > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)j * 0.8D + 1);
                        }

                        double r = getRate(player);
                        if(r>1){
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() * r);
                        }

                        int k = EnchantmentHelper.getItemEnchantmentLevel(pLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.PUNCH), pStack);
                        if (k > 0) {
                            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + k * 0.5D);
                        }

                        AttributeInstance ist = player.getAttribute(Attributes.ATTACK_DAMAGE);

                        if (EnchantmentHelper.getItemEnchantmentLevel(pLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.FLAME), pStack) > 0) {
                            abstractarrow.setRemainingFireTicks(200);
                        }

                        pStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                        if (flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW))) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        double link = 4;

                        if (itemstack.is(CAItems.OCEAN_ARROW.get())){
                        	abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() * 1.25);
                        	abstractarrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                        	link = 6;
                        } else if(itemstack.is(CAItems.TRAILRITE_ARROW.get())) {
                        	abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() * 2);
                        	abstractarrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                        	link = 12;
                        }
						 abstractarrow.getPersistentData().putDouble("TrailriteLink", link);

                        pLevel.addFreshEntity(abstractarrow);
                    }

                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

	@Override
	public boolean canBeHurtBy(ItemStack stack, DamageSource pDamageSource) {
		return pDamageSource.is(DamageTypeTags.BYPASSES_EFFECTS);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<Item> onBroken) {
		return Math.min(amount, 1);
	}
}