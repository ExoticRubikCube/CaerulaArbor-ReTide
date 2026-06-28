package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModAttributes;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEnchantments;
import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;

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
			builder.put(CaerulaArborModAttributes.MAGIC_RESISTANCE.get(), 
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
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected) {
            double x = entity.getX();
            double y = entity.getY()+1.25;
            double z = entity.getZ();
            Entity recentAttacker = null;
            Entity recentVictim = null;
            Entity t0 = null;
            Entity t1 = null;
            Entity t2 = null;
            double gap = 0;
            boolean IsCreative = false;
            boolean ApocataMode = false;
            gap = 30;
            if (EnchantmentHelper.getItemEnchantmentLevel(CaerulaArborModEnchantments.SYNESTHESIA.get(), itemstack) != 0) {
                gap = Math.max(gap - itemstack.getEnchantmentLevel(CaerulaArborModEnchantments.SYNESTHESIA.get()) * 4, 10);
            }
            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == CaerulaArborModItems.APOCATA_SWORD.get()) {
                gap = 2;
                ApocataMode = true;
            }
            if ((entity instanceof LivingEntity _entUseTicks6 ? _entUseTicks6.getTicksUsingItem() : 0) % gap == 0 && (entity instanceof LivingEntity _entUseTicks7 ? _entUseTicks7.getTicksUsingItem() : 0) > 0) {
                IsCreative = new Object() {
                    public boolean checkGamemode(Entity _ent) {
                        if (_ent instanceof ServerPlayer _serverPlayer) {
                            return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                        } else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
                            return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
                        }
                        return false;
                    }
                }.checkGamemode(entity) || ApocataMode;
                if ((entity instanceof Player _plr ? _plr.totalExperience : 0) >= 15 || IsCreative) {
                    {
                        final Vec3 _center = new Vec3(x, y, z);
                        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                        for (Entity entityiterator : _entfound) {
                            if (!(entityiterator instanceof LivingEntity)) {
                                continue;
                            }
                            if (entityiterator == entity) {
                                continue;
                            }
                            if (!(entityiterator instanceof Monster)) {
                                recentVictim = (entity instanceof LivingEntity _entity) ? _entity.getLastHurtMob() : null;
                                recentAttacker = (entity instanceof LivingEntity _entity) ? _entity.getLastHurtByMob() : null;
                                if (!((entityiterator instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == entity || entityiterator == recentVictim || entityiterator == recentAttacker)) {
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
                        if ((LevelAccessor) world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(CaerulaArborMod.MODID, "caster_cast")), SoundSource.PLAYERS, 2,
                                        (float) Mth.nextDouble(RandomSource.create(), 0.9, 1.1));
                        }
                    }
                    EntityUtils.castDragonBreath(world, x, y, z, entity, t0, 1);
                    EntityUtils.castDragonBreath(world, x, y, z, entity, t1, 0);
                    EntityUtils.castDragonBreath(world, x, y, z, entity, t2, 0);
                    if (ApocataMode) {
                        for (int index0 = 0; index0 < 2; index0++) {
                            EntityUtils.castDragonBreath(world, x, y, z, entity, t0, 1);
                            EntityUtils.castDragonBreath(world, x, y, z, entity, t1, 0);
                            EntityUtils.castDragonBreath(world, x, y, z, entity, t2, 0);
                        }
                    }
                    if (!IsCreative) {
                        if (entity instanceof Player _player)
                            _player.giveExperiencePoints(-(15));
                    }
                }
            }
        }
	}
}
