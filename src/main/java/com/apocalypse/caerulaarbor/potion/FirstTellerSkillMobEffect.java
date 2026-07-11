
package com.apocalypse.caerulaarbor.potion;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.api.event.SanityEvent;
import com.apocalypse.caerulaarbor.capability.ModCapabilities;
import com.apocalypse.caerulaarbor.capability.player.PlayerVariable;
import com.apocalypse.caerulaarbor.capability.sanity.SIHelper;
import com.apocalypse.caerulaarbor.entity.FirstTellerEntity;
import com.apocalypse.caerulaarbor.entity.bullets.TellerShotEntity;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAEntities;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.init.CASounds;
import com.apocalypse.caerulaarbor.util.MathUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class FirstTellerSkillMobEffect extends MobEffect {
    public FirstTellerSkillMobEffect() {
        super(MobEffectCategory.NEUTRAL, -16751002);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "38677639-a80e-3dd4-b522-6bafa560ad71", 5, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double ayk;
        Entity enemy;
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + -2.5), y, z, 64, 0.1, 0.2, 2, 0.1);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, (x + 2.5), y, z, 64, 0.1, 0.2, 2, 0.1);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, (z + 2.5), 64, 2, 0.2, 0.1, 0.1);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, (z + -2.5), 64, 2, 0.2, 0.1, 0.1);
                final int tick2 = ticks;
                CaerulaArborMod.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 5, 1);
        if (!(world.getDifficulty() == Difficulty.PEACEFUL)) {
            if (world instanceof Level level) {
                    level.playSound(null, BlockPos.containing(x, y, z), CASounds.FIRETTELLER_SKILL_ATTACK.get(), SoundSource.NEUTRAL, 3,
                            (float) Mth.nextDouble(RandomSource.create(), 0.85, 1.15));
            }
            enemy = world.getEntitiesOfClass(FirstTellerEntity.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).stream().min(new Object() {
                Comparator<Entity> compareDistOf(double x, double y, double z) {
                    return Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(x, y, z));
                }
            }.compareDistOf(x, y, z)).orElse(null);
            if (enemy == null) {
                if ((Entity) entity instanceof LivingEntity livingEntity)
                    livingEntity.removeEffect(CAMobEffects.FIRST_TELLER_SKILL.get());
                return;
            }
            if (!enemy.isAlive()) {
                if ((Entity) entity instanceof LivingEntity livingEntity)
                    livingEntity.removeEffect(CAMobEffects.FIRST_TELLER_SKILL.get());
                return;
            }
            ayk = enemy instanceof LivingEntity livingEntity13 && livingEntity13.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity13.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
            for (Entity entityiterator : world.getEntities(entity, new AABB((x + 2.5), (y + 4), (z + 2.5), (x + -2.5), y, (z + -2.5)))) {
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArborMod.MODID, "oceanoffspring")))) {
                    continue;
                }
                if (entityiterator instanceof Player && (entityiterator.getCapability(ModCapabilities.PLAYER_VARIABLE, null).orElse(new PlayerVariable())).player_oceanization >= 3) {
                    continue;
                }
                if (!(entityiterator instanceof Mob livingEntity)) {
                    continue;
                }
                entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC),
                        (float) (ayk * 0.6));
                SIHelper.causeSanityInjury(livingEntity, ayk * 60, SanityEvent.Hurt.Type.POTION);
            }
            if (world instanceof ServerLevel projectileLevel) {
                Projectile entityToSpawn = new Object() {
                    public Projectile getArrow(Level level, float damage, int knockback, byte piercing) {
                        AbstractArrow entityToSpawn = new TellerShotEntity(CAEntities.TELLER_SHOT.get(), level);
                        entityToSpawn.setBaseDamage(damage);
                        entityToSpawn.setKnockback(knockback);
                        entityToSpawn.setSilent(true);
                        entityToSpawn.setPierceLevel(piercing);
                        return entityToSpawn;
                    }
                }.getArrow(projectileLevel, (float) (ayk * 0.6), 0, (byte) 1);
                entityToSpawn.setPos(x, (y + 8), z);
                entityToSpawn.shoot((Mth.nextDouble(RandomSource.create(), -0.125, 0.125)), (-1), (Mth.nextDouble(RandomSource.create(), -0.125, 0.125)), 1, 5);
                projectileLevel.addFreshEntity(entityToSpawn);
            }
            ((Entity) entity).hurt(CADamageTypes.source(world, CADamageTypes.OCEAN_MAGIC), (float) (ayk * 0.6));
            SIHelper.causeSanityInjury(entity, ayk * 60, SanityEvent.Hurt.Type.POTION);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return MathUtils.isMultipleOf(duration, 10);
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance effect) {
                return false;
            }

            @Override
            public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                return false;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return false;
            }
        });
    }
}
