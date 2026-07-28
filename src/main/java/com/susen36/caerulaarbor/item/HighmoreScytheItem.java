
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArborMod;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CASounds;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;


public class HighmoreScytheItem extends Item implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public HighmoreScytheItem() {
		super(new Item.Properties().durability(8480).fireResistant().rarity(Rarity.UNCOMMON).attributes(createAttributes()));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.highmore_scythe.idle"));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}

	String prevAnim = "empty";

	private PlayState procedurePredicate(AnimationState event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
			if (!this.animationprocedure.equals(prevAnim))
				event.getController().forceAnimationReset();
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (this.animationprocedure.equals("empty")) {
			prevAnim = "empty";
			return PlayState.STOP;
		}
		prevAnim = this.animationprocedure;
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		AnimationController procedureController = new AnimationController(this, "procedureController", 0, this::procedurePredicate);
		data.add(procedureController);
		AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
		data.add(idleController);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 4F;
	}

	private static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "highmore_scythe_attack_damage"), 14.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "highmore_scythe_attack_speed"), -2.4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.highmore_scythe.desc_1"));
		list.add(Component.translatable("item.caerula_arbor.highmore_scythe.desc_2"));
		list.add(Component.translatable("item.caerula_arbor.highmore_scythe.desc_3"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

		ItemUtils.transferSharpnessToSynesthesia(world, x, y, z, entity, itemstack);
		return ar;
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity, InteractionHand hand) {
		boolean result = super.onEntitySwing(itemstack, entity, hand);
		if (entity instanceof Player player && this.canUseSpecialAttack(player, itemstack)) {
			double reach = player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
			HitResult hitResult = player.pick(reach, 0.0F, false);
			if (hitResult.getType() == HitResult.Type.MISS) {
				this.setAttackAnimation(itemstack);
				if (!player.level().isClientSide()) {
					this.scheduleAreaAttack(itemstack, player, player.getX(), player.getY(), player.getZ());
				}
			}
		}
		return result;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean result = super.hurtEnemy(itemstack, entity, sourceentity);
		if (sourceentity instanceof Player player && this.canUseSpecialAttack(player, itemstack)) {
			EntityUtils.giveLessArmor(entity, 15);
			this.setAttackAnimation(itemstack);
			if (!sourceentity.level().isClientSide()) {
				this.scheduleAreaAttack(itemstack, sourceentity, entity.getX(), entity.getY(), entity.getZ());
			}
		}
		return result;
	}

	private boolean canUseSpecialAttack(Player player, ItemStack itemstack) {
		return player.getMainHandItem() == itemstack && player.getAttackStrengthScale(0) >= 0.95F;
	}

	private void setAttackAnimation(ItemStack itemstack) {
		CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.highmore_scythe.attack"));
	}

	private void scheduleAreaAttack(ItemStack itemstack, LivingEntity attacker, double x, double y, double z) {
		CaerulaArborMod.queueServerWork(10, () -> {
			if (attacker.getMainHandItem() != itemstack) {
				return;
			}
			Level level = attacker.level();
			level.playSound(null, BlockPos.containing(x, y, z),
					CASounds.SCYTHE_HIGHMORE.get(),
					SoundSource.PLAYERS, 1.5F, 1.0F);
			Vec3 center = new Vec3(x, y + 0.5, z);
			List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4.0), target -> true).stream()
					.sorted(Comparator.comparingDouble(target -> target.distanceToSqr(center)))
					.toList();
			for (Entity nearbyEntity : nearbyEntities) {
				if (nearbyEntity.distanceTo(attacker) > 4.0F) {
					continue;
				}
				if (!(nearbyEntity instanceof LivingEntity) || nearbyEntity == attacker) {
					continue;
				}
				if (nearbyEntity instanceof TamableAnimal tamableAnimal && tamableAnimal.isOwnedBy(attacker)) {
					continue;
				}
				nearbyEntity.hurt(
						CADamageTypes.source(level, CADamageTypes.HIGHMORE_ATTACK, attacker),
						(float) (attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) * (1.5F + 0.2F * EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(attacker.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack))));
				EntityUtils.giveLessArmor(nearbyEntity, 15);
			}
			if (!(attacker instanceof Player player) || !player.getAbilities().instabuild) {
				if (level instanceof ServerLevel _level) {
					itemstack.hurtAndBreak(1, _level, null, _item -> itemstack.setDamageValue(0));
				}
			}
		});
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}