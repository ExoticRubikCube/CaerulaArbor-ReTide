package com.susen36.caerulaarbor.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.susen36.caerulaarbor.entity.MoistDragonBreathEntity;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAEnchantments;
import com.susen36.caerulaarbor.init.CAItems;
import com.susen36.caerulaarbor.init.CASounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class DragonWandItem extends Item {
	public DragonWandItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 72000;
	}

	public final UUID MAGIC_UUID = new UUID(this.toString().hashCode(), 0);

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, 
				new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 9.5d, AttributeModifier.Operation.ADDITION));
			builder.put(CAAttributes.MAGIC_RESISTANCE.get(),
				new AttributeModifier(MAGIC_UUID, "Item modifier", 15d, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.dragon_wand.description_0"));
		list.add(Component.translatable("item.caerula_arbor.dragon_wand.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		entity.startUsingItem(hand);
		return super.use(world, entity, hand);
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            double x = entity.getX();
            double y = entity.getY()+1.25;
            double z = entity.getZ();
            Entity recentAttacker;
            Entity recentVictim;
            Entity t0 = null;
            Entity t1 = null;
            Entity t2 = null;
            double gap;
            boolean IsCreative;
            boolean ApocataMode = false;
            gap = 30;
            if (EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack) != 0) {
                gap = Math.max(gap - EnchantmentHelper.getItemEnchantmentLevel(CAEnchantments.getHolder(entity.level().registryAccess(), CAEnchantments.SYNESTHESIA), itemstack) * 4, 10);
            }
            if ((entity instanceof LivingEntity livEnt ? livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == CAItems.APOCATA_SWORD.get()) {
                gap = 2;
                ApocataMode = true;
            }
            if ((entity instanceof LivingEntity entUseTicks6 ? entUseTicks6.getTicksUsingItem() : 0) % gap == 0 && (entity instanceof LivingEntity entUseTicks7 ? entUseTicks7.getTicksUsingItem() : 0) > 0) {
                IsCreative = new Object() {
                    public boolean checkGamemode(Entity ent) {
                        if (ent instanceof ServerPlayer serverPlayer) {
                            return serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (ent.level().isClientSide() && ent instanceof Player player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode(entity) || ApocataMode;
                if ((entity instanceof Player plr ? plr.totalExperience : 0) >= 15 || IsCreative) {
                    {
                        final Vec3 center = new Vec3(x, y, z);
                        List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                        for (Entity entityiterator : entfound) {
                            if (!(entityiterator instanceof LivingEntity)) {
                                continue;
                            }
                            if (entityiterator == entity) {
                                continue;
                            }
                            if (!(entityiterator instanceof Monster)) {
                                recentVictim = (entity instanceof LivingEntity living) ? living.getLastHurtMob() : null;
                                recentAttacker = (entity instanceof LivingEntity living) ? living.getLastHurtByMob() : null;
                                if (!((entityiterator instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entity || entityiterator == recentVictim || entityiterator == recentAttacker)) {
                                    continue;
                                }
                            }
                            if (entity.distanceTo(entityiterator) <= 24) {
                                if (t0 == null) {
                                    t0 = entityiterator;
                                } else if (t1 == null) {
                                    t1 = entityiterator;
                                } else if (t2 == null) {
                                    t2 = entityiterator;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    if (t0 == null) {
                        return;
                    }
                    if (t1 == null) {
                        t1 = t0;
                    }
                    if (t2 == null) {
                        t2 = t1;
                    }
                    if (!((LevelAccessor) world).isClientSide()) {
                        if ((LevelAccessor) world instanceof Level level) {
                                level.playSound(null, BlockPos.containing(x, y, z), CASounds.CASTER_CAST.get(), SoundSource.PLAYERS, 2,
                                        (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
                        }
                    }
                    MoistDragonBreathEntity.spawn(world, x, y, z, entity, t0, 1);
                    MoistDragonBreathEntity.spawn(world, x, y, z, entity, t1, 0);
                    MoistDragonBreathEntity.spawn(world, x, y, z, entity, t2, 0);
                    if (ApocataMode) {
                        for (int index0 = 0; index0 < 2; index0++) {
                            MoistDragonBreathEntity.spawn(world, x, y, z, entity, t0, 1);
                            MoistDragonBreathEntity.spawn(world, x, y, z, entity, t1, 0);
                            MoistDragonBreathEntity.spawn(world, x, y, z, entity, t2, 0);
                        }
                    }
                    if (!IsCreative) {
                        if (entity instanceof Player player)
                            player.giveExperiencePoints(-(15));
                    }
                }
            }
        }
	}
}
