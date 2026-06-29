
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CAItems;
import com.apocalypse.caerulaarbor.init.CAParticleTypes;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

public class LancXiaoItem extends SwordItem {
	public LancXiaoItem() {
		super(new Tier() {
			public int getUses() {
				return 725;
			}

			public float getSpeed() {
				return 6f;
			}

			public float getAttackDamageBonus() {
				return 7f;
			}

			public int getLevel() {
				return 2;
			}

			public int getEnchantmentValue() {
				return 12;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CAItems.OCEAN_CRYSTAL.get()));
			}
		}, 3, -1.6f, new Item.Properties());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        itemstack.getOrCreateTag().putDouble("sklp", Math.max(itemstack.getOrCreateTag().getDouble("sklp") - 1, 0));
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
                    final Vec3 _center = new Vec3(x, y, z);
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        if (entityiterator instanceof Monster || (entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entity) {
                            LivingEntity _livEnt3 = (LivingEntity) entityiterator;
                            if (_livEnt3.hasEffect(CAMobEffects.INVULNERABLE.get())) {
                                continue;
                            }
                            if (!entityiterator.isAlive()) {
                                continue;
                            }
                            if (entity.distanceTo(entityiterator) <= 16) {
                                count = count + 1;
                                CaerulaArborMod.queueServerWork((int) (count * 2), () -> {
                                    if (entity.distanceTo(entityiterator) <= 16) {
                                        double atk;
                                        double tz;
                                        double ty;
                                        double tx;
                                        atk = entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
                                        tx = entityiterator.getX() + Mth.nextDouble(RandomSource.create(), -0.25, 0.25);
                                        ty = entityiterator.getY();
                                        tz = entityiterator.getZ() + Mth.nextDouble(RandomSource.create(), -0.25, 0.25);
                                        EntityUtils.endspeakerLinkPtcTo(world, entity.getX(), entity.getY(), entity.getZ(), tx, ty, tz);
                                        Entity _ent = entity;
                                        _ent.teleportTo(tx, ty, tz);
                                        if (_ent instanceof ServerPlayer _serverPlayer)
                                            _serverPlayer.connection.teleport(tx, ty, tz, _ent.getYRot(), _ent.getXRot());
                                        if ((LevelAccessor) world instanceof ServerLevel _level)
                                            _level.sendParticles(CAParticleTypes.ENDSPEAKER_PARTICLE.get(), tx, (ty + 0.75), tz, 18, 0.75, 0.75, 0.75, 0.15);
                                        if ((LevelAccessor) world instanceof Level _level) {
                                                _level.playSound(null, BlockPos.containing(tx, ty, tz), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack_hit")), SoundSource.PLAYERS, (float) 1.5, 1);
                                        }
                                        entityiterator.hurt(new DamageSource(((LevelAccessor) world).registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "endspeaker_attack"))), entity), (float) (atk * 2));
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
                if ((LevelAccessor) world instanceof Level _level) {
                        _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "skill_release")), SoundSource.PLAYERS, (float) 0.75, 1);
                }
                LivingEntity _entity = (LivingEntity) (Entity) entity;
                if (!_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 2, false, false));
                CaerulaArborMod.queueServerWork((int) ((count + 2) * 2), () -> {
                    EntityUtils.endspeakerLinkPtcTo(world, entity.getX(), entity.getY(), entity.getZ(), x, y, z);
                    {
                        Entity _ent = entity;
                        _ent.teleportTo(x, y, z);
                        if (_ent instanceof ServerPlayer _serverPlayer)
                            _serverPlayer.connection.teleport(x, y, z, _ent.getYRot(), _ent.getXRot());
                    }
                });
                itemstack.getOrCreateTag().putDouble("sklp", 6);
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
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		Entity entity = itemstack.getEntityRepresentation();
        double sklp;
        String prefix;
        sklp = 6 - itemstack.getOrCreateTag().getDouble("sklp");
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
		return itemstack.getOrCreateTag().getDouble("sklp") <= 0;
	}
}
