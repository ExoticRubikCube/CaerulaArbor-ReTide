package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.CaerulaArborMod;

import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.entity.TribunalHealerEntity;
import com.apocalypse.caerulaarbor.init.CaerulaArborModEntities;
import com.apocalypse.caerulaarbor.init.CaerulaArborModGameRules;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

public class TransformIndexProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		boolean trans = false;
		double rate = 0;
		double h = 0;
		if (entity instanceof Player) {
			return false;
		}
		if ((ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).contains("touhou_little_maid:maid")) {
			return false;
		}
		if (!(entity instanceof LivingEntity _livEnt2 && _livEnt2.getMobType() == MobType.UNDEAD || entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "cannot_transform"))))
				&& world.getLevelData().getGameRules().getBoolean(CaerulaArborModGameRules.OCEANIZATION_MODE) && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.isBaby())) {
			if (EntityUtils.getSeabornAround(world, x, y, z, entity) > Math.min((world.getLevelData().getGameRules().getInt(CaerulaArborModGameRules.CLONE_NUMBER_LIMIT)), (double) CaerulaConfigsConfiguration.CLONE_NUM.get()) * 2) {
				return false;
			}
			if (entity instanceof Villager && !(entity instanceof LivingEntity _livEnt9 && _livEnt9.isBaby()) || (ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).equals("guardvillagers:guard")) {
				if (Math.random() < 0.375) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_VILLAGER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Shulker) {
				if (Math.random() < 0.25) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_SHULKER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Chicken) {
				if (Math.random() < 0.45) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_CHICKEN.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof TribunalHealerEntity) {
				if (Math.random() < 0.15) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.COMPASSION_PRAYER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Rabbit) {
				if (Math.random() < 0.45) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZE_RABBIT.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof PolarBear) {
				if (Math.random() < 0.35) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_POLAR_BEAR.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if ((ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).equals("bobsoriginiumdream:mutant_giant_rock_spider")) {
				if (Math.random() < 0.32) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.TIDUTANT_ROCK_SPIDER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if ((ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).equals("bobsoriginiumdream:originiutant_excrescence")) {
				if (Math.random() < 0.5) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.TIDUTANT_EXCRESCENCE.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Fox) {
				if (Math.random() < 0.5) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_FOX.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "homo_sapiens")))) {
				if (Math.random() < 0.25) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.THE_ABANDONED.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Evoker) {
				if (Math.random() < 0.25) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_EVOKER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Vindicator) {
				if (Math.random() < 0.3) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_VINDICATOR.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Pillager) {
				if (Math.random() < 0.3) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_PILLAGER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Pig) {
				if (Math.random() < 0.5) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_PIG.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Cow || entity instanceof MushroomCow) {
				if (Math.random() < 0.45) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_COW.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Sheep) {
				if (Math.random() < 0.45) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_SHEEP.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Horse) {
				if (Math.random() < 0.35) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_HORSE.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Piglin) {
				if (Math.random() < 0.4) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_PIGLIN.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof PiglinBrute) {
				if (Math.random() < 0.2) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_BRUTE.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof CaveSpider || entity instanceof Spider || (ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).equals("twilightforest:hedge_spider")
					|| (ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString()).equals("twilightforest:king_spider")) {
				if (Math.random() < 0.65) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_SPIDER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof EnderMan) {
				if (Math.random() < 0.2) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_ENDERMAN.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Wolf) {
				if (!(entity instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) && Math.random() < 0.2) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WOLF.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				} else if (entity instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_DOG.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Witch) {
				if (Math.random() < 0.33) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WITCH.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Ravager) {
				if (Math.random() < 0.25) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_RAVAGER.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else if (entity instanceof Warden) {
				if (Math.random() < 0.1) {
					if (Math.random() < 0.02) {
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WARDENIS.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(entity.getYRot());
								entityToSpawn.setYBodyRot(entity.getYRot());
								entityToSpawn.setYHeadRot(entity.getYRot());
								entityToSpawn.setXRot(entity.getXRot());
							}
						}
					} else {
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_WARDEN.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(entity.getYRot());
								entityToSpawn.setYBodyRot(entity.getYRot());
								entityToSpawn.setYHeadRot(entity.getYRot());
								entityToSpawn.setXRot(entity.getXRot());
							}
						}
					}
					trans = true;
				}
			} else if (entity instanceof Cat || entity instanceof Ocelot) {
				if (Math.random() < 0.5) {
					if (world instanceof ServerLevel _level) {
						Entity entityToSpawn = CaerulaArborModEntities.OCEANIZED_CAT.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
						if (entityToSpawn != null) {
							entityToSpawn.setYRot(entity.getYRot());
							entityToSpawn.setYBodyRot(entity.getYRot());
							entityToSpawn.setYHeadRot(entity.getYRot());
							entityToSpawn.setXRot(entity.getXRot());
						}
					}
					trans = true;
				}
			} else {
				if (!(entity instanceof Player) && Math.random() < 0.25) {
					rate = 0.15;
					h = (double) CaerulaConfigsConfiguration.OCEANIZE_HEALTH.get();
					if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) < h) {
						rate = 0;
					}
					if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) > h * 4) {
						rate = 0.75;
					}
					if (entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:bosses")))) {
						rate = 1;
					}
					com.apocalypse.caerulaarbor.utils.WorldUtils.summonRandomSeaborn(world, rate, x, y, z);
					trans = true;
				}
			}
			if (trans) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie.converted_to_drowned")), SoundSource.HOSTILE, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.zombie.converted_to_drowned")), SoundSource.HOSTILE, 1, 1, false);
					}
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.EXPLOSION, x, (y + 0.2), z, 2, 0.1, 0.1, 0.1, 0.15);
			}
		}
		return trans;
	}
}

// TODO: Called 6 times; this procedure is very long and conversion-heavy, so keep as-is.
