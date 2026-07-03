package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import com.apocalypse.caerulaarbor.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class InterphoneItem extends Item {
	public InterphoneItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.interphone.description_0"));
		list.add(Component.translatable("item.caerula_arbor.interphone.description_1"));
		list.add(Component.translatable("item.caerula_arbor.interphone.description_2"));
		list.add(Component.translatable("item.caerula_arbor.interphone.description_3"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemStack itemstack = ar.getObject();
        if (!((Entity) entity instanceof Player _plrCldCheck1 && _plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
            if (!entity.isShiftKeyDown()) {
                dispatchInquisition(world, entity, itemstack, entity.getLookAngle().x * 2 + x, y, entity.getLookAngle().z * 2 + z);
            } else {
                teleportInquisitions(world, entity, itemstack, entity.getLookAngle().x * 2 + x, y, entity.getLookAngle().z * 2 + z);
            }
        }
        return ar;
	}

	private void dispatchInquisition(LevelAccessor world, Entity chief, ItemStack itemstack, double tx, double ty, double tz) {
		if (chief == null)
			return;
		double num = 0;
		double tX;
		double tZ;
		double rand = 0;
		double tY;
		String log;
		String name;
		if (!(chief instanceof Player _plrCldCheck1 && _plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
			tX = tx;
			tY = ty;
			tZ = tz;
			name = chief.getDisplayName().getString();
			{
				final Vec3 _center = new Vec3(tx, ty, tz);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(48 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))) {
						num = num + 1;
						entityiterator.getPersistentData().putString("recentCommander", name);
						EntityUtils.clearTarget(entityiterator);
						if (entityiterator instanceof Mob _entity)
							_entity.getNavigation().moveTo((tx + Mth.nextDouble(RandomSource.create(), -2, 2)), tY, (tz + Mth.nextDouble(RandomSource.create(), -2, 2)), 1);
					}
				}
			}
			if (num > 0) {
				if (!(new Object() {
					public boolean checkGamemode(Entity _ent) {
						if (_ent instanceof ServerPlayer _serverPlayer) {
							return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
						} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
							return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
									&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
						}
						return false;
					}
				}.checkGamemode(chief))) {
					if (chief instanceof Player _player)
						_player.getCooldowns().addCooldown(itemstack.getItem(), 40);
				}
				log = Component.translatable("interphone.dispatch.single").getString();
				log = log.replace("{num}", "" + Math.round(num));
				log = log.replace("{x}", "" + Math.round(Math.pow(10, 2) * tX) / Math.pow(10, 2));
				log = log.replace("{z}", "" + Math.round(Math.pow(10, 2) * tZ) / Math.pow(10, 2));
				log = log.replace("{y}", "" + Math.round(Math.pow(10, 2) * tY) / Math.pow(10, 2));
				if (chief instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal(log), true);
				if (chief instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
			}
		}
	}

	private void teleportInquisitions(LevelAccessor world, Entity chief, ItemStack itemstack, double tx, double ty, double tz) {
		if (chief == null)
			return;
		double num = 0;
		double tX;
		double tZ;
		double tY;
		double dx;
		double dz;
		String log;
		String name;
		if (!(chief instanceof Player _plrCldCheck1 && _plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
			tX = tx;
			tY = ty;
			tZ = tz;
			name = chief.getDisplayName().getString();
			if (world instanceof ServerLevel serverLevel) {
				for (Entity entityiterator : serverLevel.getAllEntities()) {
					if (entityiterator instanceof LivingEntity livingEntity && livingEntity.hasEffect(CAMobEffects.COOLDOWN_SINAL.get())) {
						continue;
					}
					if (entityiterator.level().dimension() != chief.level().dimension()) {
						continue;
					}
					if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))) {
						for (int index0 = 0; index0 < 8; index0++) {
							dx = Mth.nextDouble(RandomSource.create(), -2, 2);
							dz = Mth.nextDouble(RandomSource.create(), -2, 2);
							if (WorldUtils.isValidHumanoidPlace(world, tx + dx, tY, tz + dz)) {
								num = num + 1;
								entityiterator.getPersistentData().putString("recentCommander", name);
								EntityUtils.clearTarget(entityiterator);
								entityiterator.teleportTo(tx + dx, tY, tz + dz);
								if (entityiterator instanceof ServerPlayer serverPlayer)
									serverPlayer.connection.teleport(tx + dx, tY, tz + dz, entityiterator.getYRot(), entityiterator.getXRot());
								if (entityiterator instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
									livingEntity.addEffect(new MobEffectInstance(CAMobEffects.COOLDOWN_SINAL.get(), 300, 0, false, false));
								break;
							}
						}
						if (num >= 9) {
							break;
						}
					}
				}
			}
			if (num > 0) {
				if (!(new Object() {
					public boolean checkGamemode(Entity _ent) {
						if (_ent instanceof ServerPlayer _serverPlayer) {
							return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
						} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
							return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
									&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
						}
						return false;
					}
				}.checkGamemode(chief))) {
					if (chief instanceof Player _player)
						_player.getCooldowns().addCooldown(itemstack.getItem(), 60);
				}
				log = Component.translatable("interphone.dispatch.teleport").getString();
				log = log.replace("{num}", "" + Math.round(num));
				log = log.replace("{x}", "" + Math.round(Math.pow(10, 2) * tX) / Math.pow(10, 2));
				log = log.replace("{z}", "" + Math.round(Math.pow(10, 2) * tZ) / Math.pow(10, 2));
				log = log.replace("{y}", "" + Math.round(Math.pow(10, 2) * tY) / Math.pow(10, 2));
				if (chief instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal(log), true);
				if (chief instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
			}
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
        LevelAccessor world = context.getLevel();
        double x = context.getClickedPos().getX();
        double y = context.getClickedPos().getY();
        double z = context.getClickedPos().getZ();
        Direction direction = context.getClickedFace();
        Entity entity = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (direction != null && entity != null) {
            String log = "";
            double num = 0;
            double tX = 0;
            double tZ = 0;
            double rand = 0;
            double tY = 0;
            if (!(entity instanceof Player _plrCldCheck1 && _plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
                if (!entity.isShiftKeyDown()) {
                    dispatchInquisition(world, entity, itemstack, x + direction.getStepX() + 0.5, y + direction.getStepY(), z + direction.getStepZ() + 0.5);
                } else {
					teleportInquisitions(world, entity, itemstack, x + direction.getStepX() + 0.5, y + direction.getStepY(), z + direction.getStepZ() + 0.5);
                }
            }
        }
        return InteractionResult.SUCCESS;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        if (entity != null && sourceentity != null) {
            String log;
            double rand = 0;
            double tX = 0;
            double tZ = 0;
            double num = 0;
            if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))) {
                {
                    final Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                    List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
                    for (Entity entityiterator : _entfound) {
                        if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "inquisition")))) {
                            if (entityiterator instanceof Mob _entity && (Entity) entity instanceof LivingEntity _ent)
                                _entity.setTarget(_ent);
                            num = num + 1;
                        }
                    }
                }
                log = Component.translatable("interphone.dispatch.attack").getString();
                log = log.replace("{num}", "" + Math.round(num));
                log = log.replace("{enemy}", entity.getDisplayName().getString());
                if ((Entity) sourceentity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal(log), true);
            }
        }
        return retval;
	}

}
