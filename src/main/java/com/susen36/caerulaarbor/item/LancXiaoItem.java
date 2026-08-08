
package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.Comparator;
import java.util.List;


public class LancXiaoItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_IRON_TOOL,
			725,
			6f,
			7f,
			12,
			() -> Ingredient.of(new ItemStack(CAItems.OCEAN_CRYSTAL.get()))
	);

	public LancXiaoItem() {
		super(TIER, new Item.Properties().attributes(SwordItem.createAttributes(TIER, 3, -1.6f)));
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putDouble("sklp", Math.max(tag.getDouble("sklp") - 1, 0)));
        return retval;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        double count = 0;
        if (isLancXiaoReady(itemstack)) {
            for (int index0 = 0; index0 < 9; index0++) {
                {
                    final Vec3 center = new Vec3(x, y, z);
                    List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
                    for (Entity entityiterator : entfound) {
                        if (entityiterator instanceof Monster || (entityiterator instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == entity) {
                            LivingEntity livEnt3 = (LivingEntity) entityiterator;
                            if (livEnt3.hasEffect(CAMobEffects.INVULNERABLE)) {
                                continue;
                            }
                            if (!entityiterator.isAlive()) {
                                continue;
                            }
                            if (entity.distanceTo(entityiterator) <= 16) {
                                count = count + 1;
                                CaerulaArbor.queueServerWork((int) (count * 2), () -> {
                                    if (entity.distanceTo(entityiterator) <= 16) {
                                        double atk;
                                        double tz;
                                        double ty;
                                        double tx;
                                        atk = entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                                        tx = entityiterator.getX() + Mth.nextDouble(RandomSource.create(), -0.25, 0.25);
                                        ty = entityiterator.getY();
                                        tz = entityiterator.getZ() + Mth.nextDouble(RandomSource.create(), -0.25, 0.25);
                                        spawnTeleportLinkParticles(world, entity.getX(), entity.getY(), entity.getZ(), tx, ty, tz);
                                        Entity ent = entity;
                                        ent.teleportTo(tx, ty, tz);
                                        if (ent instanceof ServerPlayer serverPlayer)
                                            serverPlayer.connection.teleport(tx, ty, tz, ent.getYRot(), ent.getXRot());
                                        if ((LevelAccessor) world instanceof ServerLevel level)
                                            level.sendParticles(CAParticles.ENDSPEAKER_PARTICLE.get(), tx, (ty + 0.75), tz, 18, 0.75, 0.75, 0.75, 0.15);
                                        if ((LevelAccessor) world instanceof Level level) {
                                                level.playSound(null, BlockPos.containing(tx, ty, tz), CASounds.ENDSPEAKER_ATTACK_HIT.get(), SoundSource.PLAYERS, (float) 1.5, 1);
                                        }
                                        entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.ENDSPEAKER_ATTACK, entity), (float) (atk * 2));
                                    }
                                });
                            }
                        }
                        if (count >= 9) {
                            break;
                        }
                    }
                }
                if (count >= 9) {
                    break;
                }
            }
            if (count > 0) {
                if ((LevelAccessor) world instanceof Level level) {
                        level.playSound(null, BlockPos.containing(x, y, z), CASounds.SKILL_RELEASE.get(), SoundSource.PLAYERS, (float) 0.75, 1);
                }
                if (!entity.level().isClientSide())
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 2, false, false));
                CaerulaArbor.queueServerWork((int) ((count + 2) * 2), () -> {
                    spawnTeleportLinkParticles(world, entity.getX(), entity.getY(), entity.getZ(), x, y, z);
                    {
                        Entity ent = entity;
                        ent.teleportTo(x, y, z);
                        if (ent instanceof ServerPlayer serverPlayer)
                            serverPlayer.connection.teleport(x, y, z, ent.getYRot(), ent.getXRot());
                    }
                });
                CustomData.update(DataComponents.CUSTOM_DATA, itemstack, tag -> tag.putDouble("sklp", 6));
            }
        }
        return ar;
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		ItemStack retval = new ItemStack(this);
		retval.setDamageValue(itemstack.getDamageValue() + 1);
		if (retval.getDamageValue() >= retval.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return retval;
	}

	@Override
	public boolean isRepairable(ItemStack itemstack) {
		return false;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
        double sklp;
        String prefix;
        sklp = 6 - itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("sklp");
        if (sklp >= 6) {
            prefix = Component.translatable("item.caerula_arbor.lanc_xiao.skill").getString() + "\u00A7b";
        } else {
            prefix = Component.translatable("item.caerula_arbor.lanc_xiao.skill").getString() + "\u00A7p";
        }
        String hoverText = Component.translatable("item.caerula_arbor.lanc_xiao.description_0").getString() + "\n" + Component.translatable("item.caerula_arbor.lanc_xiao.description_1").getString() + "\n"
                + Component.translatable("item.caerula_arbor.lanc_xiao.description_2").getString() + "\n" + prefix + Math.round(sklp) + " / 6";
        for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
        }
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack itemstack) {
		return isLancXiaoReady(itemstack);
	}

	private boolean isLancXiaoReady(ItemStack itemstack) {
		return itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("sklp") <= 0;
	}

	private static void spawnTeleportLinkParticles(LevelAccessor world, double fromX, double fromY, double fromZ, double toX, double toY, double toZ) {
		double vx = toX - fromX;
		double vy = toY - fromY;
		double vz = toZ - fromZ;
		double size = Math.max(Math.min(Math.round(Math.sqrt(vx * vx + vy * vy + vz * vz)), 32), 1);
		if (world instanceof Level level) {
			level.playSound(null, BlockPos.containing(fromX, fromY, fromZ), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1, 1);
		}
		for (int index0 = 0; index0 < (int) size; index0++) {
			if (world instanceof ServerLevel level) {
				level.sendParticles(CAParticles.ENDSPEAKER_INV.get(), fromX + (vx / size) * index0, fromY + (vy / size) * index0 + 0.5, fromZ + (vz / size) * index0, 8, 0.32, 0.5, 0.32, 0.05);
			}
		}
	}
}