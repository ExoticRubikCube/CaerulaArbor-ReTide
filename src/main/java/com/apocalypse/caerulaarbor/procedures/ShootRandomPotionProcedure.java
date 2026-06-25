package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.init.CaerulaArborModPotions;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShootRandomPotionProcedure {
	public static void execute(Entity shootFrom) {
		if (shootFrom == null)
			return;
		double potion;
		Entity enemy;
		if (shootFrom.isAlive()) {
			enemy = shootFrom instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
			if (!(enemy == null)) {
				shootFrom.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3((enemy.getX()), (enemy.getY() + enemy.getBbHeight() * 0.9), (enemy.getZ())));
			}
			potion = Mth.nextInt(RandomSource.create(), 0, 4);
			if (potion == 0) {
				{
                    Level projectileLevel = shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getPotion(Level level, Entity shooter) {
								ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
								entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.HARMING));
								entityToSpawn.setOwner(shooter);
								return entityToSpawn;
							}
						}.getPotion(projectileLevel, shootFrom);
						_entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
						_entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, 1, 2);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			} else if (potion == 1) {
				{
                    Level projectileLevel = shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getPotion(Level level, Entity shooter) {
								ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
								entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), CaerulaArborModPotions.INST_SANITY.get()));
								entityToSpawn.setOwner(shooter);
								return entityToSpawn;
							}
						}.getPotion(projectileLevel, shootFrom);
						_entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
						_entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, 1, 2);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			} else if (potion == 2) {
				{
                    Level projectileLevel = shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getPotion(Level level, Entity shooter) {
								ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
								entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.POISON));
								entityToSpawn.setOwner(shooter);
								return entityToSpawn;
							}
						}.getPotion(projectileLevel, shootFrom);
						_entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
						_entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, 1, 2);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			} else if (potion == 3) {
				{
                    Level projectileLevel = shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getPotion(Level level, Entity shooter) {
								ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
								entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.LONG_WEAKNESS));
								entityToSpawn.setOwner(shooter);
								return entityToSpawn;
							}
						}.getPotion(projectileLevel, shootFrom);
						_entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
						_entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, 1, 2);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			} else if (potion == 4) {
				{
                    Level projectileLevel = shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getPotion(Level level, Entity shooter) {
								ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
								entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.SLOWNESS));
								entityToSpawn.setOwner(shooter);
								return entityToSpawn;
							}
						}.getPotion(projectileLevel, shootFrom);
						_entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
						_entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, 1, 2);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			}
			if (Math.random() < 0.5) {
				{
                    Level projectileLevel = shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getPotion(Level level, Entity shooter) {
								ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
								entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), Potions.HARMING));
								entityToSpawn.setOwner(shooter);
								return entityToSpawn;
							}
						}.getPotion(projectileLevel, shootFrom);
						_entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
						_entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, 1, 2);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			} else {
				{
                    Level projectileLevel = shootFrom.level();
					if (!projectileLevel.isClientSide()) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getPotion(Level level, Entity shooter) {
								ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
								entityToSpawn.setItem(PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), CaerulaArborModPotions.INST_SANITY.get()));
								entityToSpawn.setOwner(shooter);
								return entityToSpawn;
							}
						}.getPotion(projectileLevel, shootFrom);
						_entityToSpawn.setPos(shootFrom.getX(), shootFrom.getEyeY() - 0.1, shootFrom.getZ());
						_entityToSpawn.shoot(shootFrom.getLookAngle().x, shootFrom.getLookAngle().y, shootFrom.getLookAngle().z, 1, 2);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			}
		}
	}
}

// TODO: 调用次数 = 18，副作用密集（生成实体），保持原样不重构
