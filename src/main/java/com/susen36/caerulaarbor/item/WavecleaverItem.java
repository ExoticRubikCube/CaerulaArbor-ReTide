
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;


public class WavecleaverItem extends Item implements GeoItem, SyncedAnimationItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public String animationprocedure = "empty";

	public WavecleaverItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC).attributes(createAttributes()));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	private PlayState idlePredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.wavelceaver.idle"));
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
	public int getEnchantmentValue() {
		return 24;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 2F;
	}

	private static ItemAttributeModifiers createAttributes() {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "wave_cleaver_attack_damage"), 21.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("caerulaarbor", "wave_cleaver_attack_speed"), -2.8D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.wave_cleaver.description_0"));
		list.add(Component.translatable("item.caerula_arbor.wave_cleaver.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ItemStack itemstack = ar.getObject();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

        if (!((Entity) entity instanceof Player plrCldCheck1 && plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
            if (itemstack.getItem() instanceof WavecleaverItem)
                CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putString("geckoAnim", "animation.wavelceaver.spin"));
            CaerulaArbor.queueServerWork(5, () -> {
                if (entity.isHolding(CAItems.WAVECLEAVER.get())) {
                    new Object() {
                        void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                            if (entity.isHolding(CAItems.WAVECLEAVER.get())) {
                                double xx = entity.getX() + 2 * entity.getLookAngle().x;
                                double yy = entity.getY() + 2 * entity.getLookAngle().y;
                                double zz = entity.getZ() + 2 * entity.getLookAngle().z;
                                {
                                    final Vec3 center = new Vec3(xx, yy, zz);
                                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                                    for (Entity entityiterator : entfound) {
                                        if (entityiterator == entity) continue;

                                        if (entityiterator instanceof TamableAnimal tamEnt && tamEnt.getOwner() == entity)
                                            continue;

                                        if (!(entityiterator instanceof LivingEntity)) continue;

                                        if (entityiterator instanceof Player) continue;

                                        if (new Vec3((entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ())).distanceTo(new Vec3(xx, yy, zz)) <= 2.5) {
                                            entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.CLEAVER_MIX, entity), (float) ((entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0) * 0.8));
                                        }
                                    }
                                }
                            }
                            final int tick2 = ticks;
                            CaerulaArbor.queueServerWork(tick2, () -> {
                                if (timedlooptotal > timedloopiterator + 1) {
                                    timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                }
                            });
                        }
                    }.timedLoop(0, 10, 2);
                }
            });
            if ((Entity) entity instanceof Player player)
                player.getCooldowns().addCooldown(itemstack.getItem(), 240);
        }
        return ar;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.ADD_REACH))) {
                if (entity instanceof LivingEntity living && !living.level().isClientSide())
                    living.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 20, 2, false, false));
            }
        }
	}


	@Override
	public void setAnimationProcedure(String animation) {
		this.animationprocedure = animation;
	}
}