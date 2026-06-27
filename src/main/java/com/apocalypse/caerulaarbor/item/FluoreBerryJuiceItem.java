
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FluoreBerryJuiceItem extends Item {
	public FluoreBerryJuiceItem() {
		super(new Item.Properties().stacksTo(4).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.fluore_berry_juice.description_0"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack resultStack = super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (entity != null) {
			if (!entity.level().isClientSide()) {
				entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
				entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0));
			}
			EntityUtils.restorePlayerLights(entity, 20);
			entity.removeEffect(MobEffects.BLINDNESS);
			if (!(entity instanceof Player)) {
				resultStack.shrink(1);
				ItemStack emptyCup = new ItemStack(CaerulaArborModItems.OCEANGLASS_CUP.get());
				if (resultStack.isEmpty()) {
					return emptyCup;
				}
			} else if (entity instanceof Player player && !player.getAbilities().instabuild) {
				resultStack.shrink(1);
				ItemStack emptyCup = new ItemStack(CaerulaArborModItems.OCEANGLASS_CUP.get());
				if (resultStack.isEmpty()) {
					return emptyCup;
				}
				if (!player.getInventory().add(emptyCup)) {
					player.drop(emptyCup, false);
				}
			}
		}
		return resultStack;
	}
}
