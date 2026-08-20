package com.susen36.caerulaarbor.entity;

import com.susen36.babel.init.BabelAttributes;
import com.susen36.babel.init.BabelMobEffects;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.block.AbandonedSulptureBlock;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.entity.base.SeaMonsterBoss;
import com.susen36.caerulaarbor.init.*;
import com.susen36.caerulaarbor.manager.spwan.SeabornSpawnManager;
import com.susen36.caerulaarbor.util.EntityUtils;
import com.susen36.caerulaarbor.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import static com.susen36.caerulaarbor.init.CAEntityTypeTags.SEABORN;

public class MartusEntity extends SeaMonsterBoss {
    private final DynamicGameEventListener<MartusDeathListener> dynamicDeathListener;
    private int releaseTime = 0;
    // 攻击增益计时器：非持久化，仅第二形态生效，每55秒叠加一层力量
    private int attackBoostTimer = 0;

    public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_1 = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_SKILLP_2 = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<CompoundTag> DATA_BLESSED_IDS = SynchedEntityData.defineId(MartusEntity.class, EntityDataSerializers.COMPOUND_TAG);

    // 祝福属性修饰器ID：断连时按ID移除即可无损还原，避免对baseValue做乘除运算留下精度/叠加残留
    private static final ResourceLocation BLESS_MAX_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "bless_max_health");
    private static final ResourceLocation BLESS_ATTACK_DAMAGE_MODIFIER = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "bless_attack_damage");

    private boolean swinging;
    private long lastSwing;
    public String animationprocedure = "empty";

    public MartusEntity(Level world) {
        this(CAEntities.MARTUS.get(), world);
    }

    public MartusEntity(EntityType<MartusEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ServerBossEvent(this.getDisplayName(), ServerBossEvent.BossBarColor.BLUE, ServerBossEvent.BossBarOverlay.NOTCHED_6);
        this.dynamicDeathListener = new DynamicGameEventListener<>(new MartusDeathListener(this));
        xpReward = 64;
        setNoAi(false);
        setNoGravity(true);
        this.noCulling = true;
        setPersistenceRequired();
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANIMATION, "undefined");
        builder.define(DATA_PHASE, 0);
        builder.define(DATA_SKILLP_1, 200);
        builder.define(DATA_SKILLP_2, 200);
        builder.define(DATA_BLESSED_IDS, new CompoundTag());
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new FlyingPathNavigation(this, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = MartusEntity.this.getRandom();
                double dir_x = MartusEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_y = MartusEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dir_z = MartusEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dir_x, dir_y, dir_z);
            }
        });
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new FloatGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    @Override
    public boolean causeFallDamage(float l, float d, DamageSource source) {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL))
            return false;
        boolean isKiller = source.is(CADamageTypes.INV_KILLER);
        if (isKiller) {
            this.releaseTime = 10;
            super.hurt(source, amount);
        }
        return isKiller;
    }

    @Override
    public void die(DamageSource source) {
        if (this.getEntityData().get(DATA_PHASE) == 0) {
            this.removeEffect(CAMobEffects.INVULNERABLE);
            if (!this.level().isClientSide()) {
                this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 200, 1, false, false));
                this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 200, 1, false, false));
            }
            this.getEntityData().set(DATA_PHASE, 1);
            this.getEntityData().set(DATA_SKILLP_1, 600);
            this.getEntityData().set(DATA_SKILLP_2, 200);
            // 进入第二形态清空祝福名单，连线与发光改由哺育生机驱动
            this.getEntityData().set(DATA_BLESSED_IDS, new CompoundTag());
            return;
        }
        super.die(source);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        LevelAccessor world1 = this.level();
        new Object() {
            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                double r;
                double d;
                double tx;
                double tz;
                double ty;
                for (int index0 = 0; index0 < 8; index0++) {
                    r = Mth.nextDouble(RandomSource.create(), 0, 6.283);
                    d = Mth.nextDouble(RandomSource.create(), 6, 12);
                    tx = MartusEntity.this.getX() + d * Math.cos(r);
                    ty = MartusEntity.this.getY() + Mth.nextDouble(RandomSource.create(), 4, 9);
                    tz = MartusEntity.this.getZ() + d * Math.sin(r);
                    if ((world1.getBlockState(BlockPos.containing(tx, ty, tz))).canBeReplaced()) {
                        if (world1 instanceof ServerLevel level) {
                            Direction dir = Direction.NORTH;
                            int num = Mth.nextInt(RandomSource.create(), 1, 4);
                            if (num == 1) {
                                dir = Direction.EAST;
                            } else if (num == 2) {
                                dir = Direction.SOUTH;
                            } else if (num == 3) {
                                dir = Direction.WEST;
                            }
                            FallingBlockEntity.fall(level, BlockPos.containing(tx, ty, tz), CABlocks.ABANDONED_SULPTURE.get().defaultBlockState().setValue(AbandonedSulptureBlock.FACING, dir));
                        }
                        break;
                    }
                }
                final int tick2 = ticks;
                CaerulaArbor.queueServerWork(tick2, () -> {
                    if (timedlooptotal > timedloopiterator + 1) {
                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                    }
                });
            }
        }.timedLoop(0, 8, 1);
        return super.finalizeSpawn(world, difficulty, reason, livingdata);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Phase", this.entityData.get(DATA_PHASE));
        compound.putInt("Skillp1", this.entityData.get(DATA_SKILLP_1));
        compound.putInt("Skillp2", this.entityData.get(DATA_SKILLP_2));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Phase")) {
            this.entityData.set(DATA_PHASE, compound.getInt("Phase"));
        }
        if (compound.contains("Skillp1")) {
            this.entityData.set(DATA_SKILLP_1, compound.getInt("Skillp1"));
        }
        if (compound.contains("Skillp2")) {
            this.entityData.set(DATA_SKILLP_2, compound.getInt("Skillp2"));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Level world = this.level();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity tgt;
        double sklp1;
        double sklp2;
        double phase;
        double perc;
        if (this.isAlive()) {
            sklp1 = this.getEntityData().get(DATA_SKILLP_1);
            sklp2 = this.getEntityData().get(DATA_SKILLP_2);
            phase = this.getEntityData().get(DATA_PHASE);

            // 第一形态才同步祝福名单；第二形态的名单由哺育生机单独填充，避免被这里覆盖
            if (phase == 0 && tickCount % 12 == 0) {
                double num = 0;
                double limit;
                limit = 2;
                if (MapVariables.get(world).strategy_subsisting > 3) {
                    limit = 3;
                }
                final Vec3 center = new Vec3(x, y, z);
                List<Mob> entfound = world.getEntitiesOfClass(Mob.class, new AABB(center, center).inflate(48), e -> true);
                ListTag blessedIds = new ListTag();
                // 先过滤被祝福海嗣，再按(到本体的距离,实体ID)稳定排序，避免遍历顺序变化导致连线目标跳变
                List<Mob> blessedNearby = entfound.stream()
                        .filter(e -> e.getPersistentData().getBoolean("blessed"))
                        .sorted(Comparator.<Mob>comparingDouble(e -> e.distanceToSqr(this)).thenComparingInt(Entity::getId))
                        .toList();
                for (Mob entityiterator : blessedNearby) {
                    if (num >= limit) {
                        break;
                    }
                    num = num + 1;
                    blessedIds.add(IntTag.valueOf(entityiterator.getId()));
                }
                if (!world.isClientSide()) {
                    CompoundTag syncTag = new CompoundTag();
                    syncTag.put("Blessed", blessedIds);
                    this.getEntityData().set(DATA_BLESSED_IDS, syncTag);
                }
            }
            if (tickCount % 100 == 0) {
                if (WorldUtils.hasNoSolidGroundBelow(world, x, y, z,14)) {
                    push(0, (-0.64), 0);
                }
            }
            // 粒子光环：第一形态常驻，第二形态仅在血量高于 33% 时保留
            if (tickCount % 2 == 0) {
                if (phase < 0.33 || this.getHealth() > this.getMaxHealth() * 0.33) {
                    double angleOffset = Mth.nextInt(RandomSource.create(), 0, 59);
                    for (int index0 = 0; index0 < 60; index0++) {
                        double angle = angleOffset + index0 * 6;
                        double radius = 2.5 + 0.5 * Math.sin(Math.toRadians(index0 * 24));
                        double particleX = this.getX() + radius * Math.sin(Math.toRadians(angle));
                        double particleZ = this.getZ() + radius * Math.cos(Math.toRadians(angle));
                        this.level().addParticle(CAParticles.MARTUS_CHARS.get(), particleX, this.getY() + 1, particleZ, 0, 0.15, 0);
                        this.level().addParticle(CAParticles.MARTUS_CHARS.get(), particleX, this.getY() + 0.8, particleZ, 0, -0.08, 0);
                    }
                    if (!this.level().isClientSide() && !this.hasEffect(MobEffects.GLOWING)) {
                        this.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false));
                    }
                }
            }
            // 断连判定：被祝福海嗣离开扫描范围外则取消祝福，移除发光与祝福属性修饰器，让出祝福名额供重新祝福
            // 范围与 %12 扫描一致(48格方形)，避免边界海嗣在"扫描内/判定超距"间反复横跳导致连线闪烁
            if (tickCount % 10 == 0 && !this.level().isClientSide()) {
                for (Entity entityiterator : world.getEntities(this, new AABB((x + 48), (y + 48), (z + 48), (x - 48), (y - 48), (z - 48)))) {
                    if (!(entityiterator instanceof LivingEntity livEnt) || !livEnt.getPersistentData().getBoolean("blessed")) {
                        continue;
                    }
                    if (Math.abs(livEnt.getX() - x) <= 48.0 && Math.abs(livEnt.getY() - y) <= 48.0 && Math.abs(livEnt.getZ() - z) <= 48.0) {
                        continue;
                    }
                    livEnt.getPersistentData().putBoolean("blessed", false);
                    livEnt.removeEffect(MobEffects.GLOWING);
                    if (livEnt.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                        livEnt.getAttribute(Attributes.MAX_HEALTH).removeModifier(BLESS_MAX_HEALTH_MODIFIER);
                    }
                    if (livEnt.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                        livEnt.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(BLESS_ATTACK_DAMAGE_MODIFIER);
                    }
                }
            }
            if (phase < 0.33) {
                if (!this.hasEffect(CAMobEffects.INVULNERABLE)) {
                    if (!this.level().isClientSide())
                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, -1, 9, false, false));
                }
                if (sklp1 > 0) {
                    this.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
                    if (MapVariables.get(world).strategy_subsisting > 3) {
                        this.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
                    }
                } else {
                    if (tickCount % 10 == 0 && !this.level().isClientSide()) {
                        // 只要断连范围内仍有存活的被祝福海嗣，就不挑选新祝福目标，避免打断既有连线
                        boolean anyBlessedAlive = world.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(48.0),
                                e -> e != this && e.getPersistentData().getBoolean("blessed")).stream().anyMatch(LivingEntity::isAlive);
                        if (!anyBlessedAlive) {
                            Entity result;
                            Entity tgt_ent = null;
                            double num = 0;
                            double max_h = 0;
                            double limit;
                            limit = 2;
                            if (MapVariables.get(world).strategy_subsisting > 3) {
                                limit = 3;
                            }
                            for (Entity entityiterator : world.getEntities(this, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                                if (!(entityiterator instanceof Mob livEnt1)) {
                                    continue;
                                }
                                if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                                    continue;
                                }
                                if (entityiterator instanceof MartusEntity) {
                                    continue;
                                }
                                if (entityiterator.getType().is(CAEntityTypeTags.SEABORN_BOSS)) {
                                    continue;
                                }
                                if (entityiterator.getPersistentData().getBoolean("blessed")) {
                                    // Martus 正锁定目标时，被祝福海嗣同步跟进同一目标；目标一致则无需重复赋值
                                    if (this.getTarget() != null && this.getTarget() != entityiterator && livEnt1.getTarget() != this.getTarget()) {
                                        livEnt1.setTarget(this.getTarget());
                                    }
                                    num = num + 1;
                                    continue;
                                }
                                if (num >= limit) {
                                    break;
                                }
                                if (livEnt1.getMaxHealth() > max_h) {
                                    max_h = livEnt1.getMaxHealth();
                                    tgt_ent = entityiterator;
                                }
                            }
                            result = tgt_ent;
                            tgt = result;
                            if (!(tgt == null) && tgt.isAlive() && !tgt.getPersistentData().getBoolean("blessed")) {
                                LivingEntity blessedTgt = (LivingEntity) tgt;
                                // 已被其他 Martus 祝福的海嗣（祝福修饰器已存在）不再施加属性，防止多个 Martus 叠加增益
                                boolean hasBlessModifier = blessedTgt.getAttributes().hasAttribute(Attributes.MAX_HEALTH)
                                        && blessedTgt.getAttribute(Attributes.MAX_HEALTH).hasModifier(BLESS_MAX_HEALTH_MODIFIER);
                                if (!hasBlessModifier) {
                                    this.getEntityData().set(DATA_SKILLP_1, 400);
                                    this.setAnimation("animation.martus.buff");
                                    tgt.getPersistentData().putBoolean("blessed", true);
                                    perc = blessedTgt.getHealth() / blessedTgt.getMaxHealth();
                                    if (blessedTgt.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                                        blessedTgt.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(
                                                new AttributeModifier(BLESS_MAX_HEALTH_MODIFIER, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                                    }
                                    if (blessedTgt.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                                        blessedTgt.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(
                                                new AttributeModifier(BLESS_ATTACK_DAMAGE_MODIFIER, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                                    }
                                    blessedTgt.setHealth((float) (blessedTgt.getMaxHealth() * perc));
                                    if (!this.level().isClientSide())
                                        blessedTgt.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false));
                                }
                            }
                        }
                    }
                }
                if (tickCount % 300 == 0) {
                    martusTimedSpawn(world, x, y, z);
                }
            } else {
                // 第二形态：发光仅在哺育生机祝福期间随连线名单施加，见skill1分支
                if (this.getHealth() > this.getMaxHealth() * 0.33) {
                    if (!this.hasEffect(CAMobEffects.INVULNERABLE)) {
                        if (!this.level().isClientSide())
                            this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 20, 9, false, false));
                    }
                }
                // 攻击增益计时器：非持久化，每55秒触发一次，力量持续120秒并可叠加至3级
                this.attackBoostTimer++;
                if (this.attackBoostTimer >= 1100) {
                    this.attackBoostTimer = 0;
                    if (!this.level().isClientSide()) {
                        int boostLevel = 0;
                        if (this.hasEffect(MobEffects.DAMAGE_BOOST)) {
                            boostLevel = Math.min(this.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() + 1, 2);
                        }
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2400, boostLevel, false, false));
                    }
                }
                if (sklp1 > 0) {
                    this.getEntityData().set(DATA_SKILLP_1, (int) (sklp1 - 1));
                } else {
                    if (tickCount % 10 == 0 && EntityUtils.getSeabornAround(world, x, y, z, this) > 0) {
                        this.getEntityData().set(DATA_SKILLP_1, 1000);
                        this.setAnimation("animation.martus.cure");
                        new Object() {
                            void timedLoop(int timedloopiterator, int timedlooptotal, int ticks) {
                                if (MartusEntity.this.getHealth() > MartusEntity.this.getMaxHealth() * 0.02) {
                                    MartusEntity.this.hurtMartus(null, 0, 0.015);
                                }
                                final int tick2 = ticks;
                                CaerulaArbor.queueServerWork(tick2, () -> {
                                    if (timedlooptotal > timedloopiterator + 1) {
                                        timedLoop(timedloopiterator + 1, timedlooptotal, tick2);
                                    }
                                });
                            }
                        }.timedLoop(0, 10, 20);
                        CaerulaArbor.queueServerWork(10, () -> {
                            final Vec3 center = new Vec3(x, y, z);
                            List<Mob> entfound = world.getEntitiesOfClass(Mob.class, new AABB(center, center).inflate(32), e -> true);
                            ListTag blessedIds = new ListTag();
                            for (Mob entityiterator : entfound) {
                                if (!entityiterator.isAlive()) {
                                    continue;
                                }
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))
                                        && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn_boss")))) {
                                    blessedIds.add(IntTag.valueOf(entityiterator.getId()));
                                    if (!this.level().isClientSide())
                                        this.addEffect(new MobEffectInstance(CAMobEffects.FAKE_DEATH, 200, 1));
                                    if (!this.level().isClientSide())
                                        this.addEffect(new MobEffectInstance(CAMobEffects.INVULNERABLE, 200, 0));
                                    // 发光仅在哺育生机祝福期间随连线名单添加，持续10秒与祝福同步
                                    if (!this.level().isClientSide())
                                        entityiterator.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, false, false));
                                }
                                if (world instanceof ServerLevel level)
                                    level.sendParticles(ParticleTypes.DOLPHIN, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 32, 0.85, 0.85, 0.85, 0.1);
                            }
                            if (!this.level().isClientSide()) {
                                CompoundTag syncTag = new CompoundTag();
                                syncTag.put("Blessed", blessedIds);
                                this.getEntityData().set(DATA_BLESSED_IDS, syncTag);
                                // 祝福结束(10秒)后清空连线名单，等待下次哺育生机
                                CaerulaArbor.queueServerWork(200, () -> {
                                    this.getEntityData().set(DATA_BLESSED_IDS, new CompoundTag());
                                });
                            }
                        });
                    }
                }
                if (sklp2 > 0) {
                    this.getEntityData().set(DATA_SKILLP_2, (int) (sklp2 - 1));
                } else {
                    this.getEntityData().set(DATA_SKILLP_2, 600);
                    this.setAnimation("animation.martus.reject");
                    CaerulaArbor.queueServerWork(15, () -> {
                        Entity tgt_ent;
                        double max_h = 0;
                        tgt_ent = this.getTarget();
                        if (tgt_ent == null || (tgt_ent instanceof LivingEntity livEnt2 && livEnt2.hasEffect(MobEffects.POISON))) {
                            tgt_ent = this.getLastHurtByMob();
                        }
                        if (tgt_ent == null || (tgt_ent instanceof LivingEntity livEnt5 && livEnt5.hasEffect(MobEffects.POISON))) {
                            for (Entity entityiterator : world.getEntities(this, new AABB((x + 32), (y + 32), (z + 32), (x - 32), (y - 32), (z - 32)))) {
                                if (!(entityiterator instanceof Mob livEnt8)) {
                                    continue;
                                }
                                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "seaborn")))) {
                                    continue;
                                }
                                if (livEnt8.hasEffect(MobEffects.POISON)) {
                                    continue;
                                }
                                if (livEnt8.getMaxHealth() > max_h) {
                                    max_h = livEnt8.getMaxHealth();
                                    tgt_ent = entityiterator;
                                }
                            }
                        }
                        // 人性之囚：2级中毒+1级无力施加给选中的目标，而非玛利图斯自身
                        if (tgt_ent instanceof LivingEntity livEnt13 && !this.level().isClientSide()) {
                            livEnt13.addEffect(new MobEffectInstance(MobEffects.POISON, 800, 1, false, false));
                            livEnt13.addEffect(new MobEffectInstance(BabelMobEffects.FEEBLENESS, 800, 0, false, false));
                        }
                    });
                }
                if (tickCount % 200 == 0) {
                    martusTimedSpawn(world, x, y, z);
                }
            }
        }
        this.refreshDimensions();
        if (this.releaseTime > 0) this.releaseTime--;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader world) {
        return world.isUnobstructed(this);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canUsePortal(boolean allowVehicles) {
        return false;
    }

    private void martusTimedSpawn(Level world, double x, double y, double z) {
        if (EntityUtils.getSeabornAround(world, x, y, z, this) < (world.getLevelData().getGameRules().getInt(CAGameRules.CLONE_NUMBER_LIMIT))) {
            for (int index0 = 0; index0 < 2; index0++) {
                SeabornSpawnManager.summonRandomSeaborn(world, 0.33, x, y, z);
                if (world instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.CLOUD, x, y, z, 18, 0.6, 0.6, 0.6, 0.16);
            }
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {}

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.5);
        builder = builder.add(Attributes.MAX_HEALTH, 500);
        builder = builder.add(Attributes.ARMOR, 15);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
        builder = builder.add(Attributes.FOLLOW_RANGE, 24);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 10);
        builder = builder.add(Attributes.FLYING_SPEED, 0.5);
        builder = builder.add(NeoForgeMod.SWIM_SPEED, 0.5);
        builder = builder.add(BabelAttributes.MAGIC_RESISTANCE, 75);
        builder = builder.add(BabelAttributes.MAX_ELEMENTAL_VALUE, 100);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6f);
        return builder;
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            if (this.isDeadOrDying()) {
                return event.setAndContinue(RawAnimation.begin().thenPlay("animation.martus.die"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.martus.idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState event) {
        if (getAttackAnim(event.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 20L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.martus.attack"));
        }
        return PlayState.CONTINUE;
    }

    String prevAnim = "empty";

    private PlayState procedurePredicate(AnimationState event) {
        if (!animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED || (!this.animationprocedure.equals(prevAnim) && !this.animationprocedure.equals("empty"))) {
            if (!this.animationprocedure.equals(prevAnim))
                event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                this.animationprocedure = "empty";
                event.getController().forceAnimationReset();
            }
        } else if (animationprocedure.equals("empty")) {
            prevAnim = "empty";
            return PlayState.STOP;
        }
        prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 25) {
            super.tickDeath();
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        if (this.level().getDifficulty() != Difficulty.PEACEFUL && pReason == RemovalReason.DISCARDED) {
            return;
        }
        super.remove(pReason);
    }

    @Override
    public void heal(float amount) {
        super.heal(0);
    }

    @Override
    public void setHealth(float pHealth) {
        if (this.releaseTime <= 0) {
            if (this.hasEffect(CAMobEffects.INVULNERABLE)) return;
        }
        super.setHealth(pHealth);
    }

    public String getSyncedAnimation() {
        return this.entityData.get(DATA_ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(DATA_ANIMATION, animation);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
        data.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public void setAnimationProcedure(String animation) {
        this.animationprocedure = animation;
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> updater) {
        super.updateDynamicGameEventListener(updater);
        if (this.level() instanceof ServerLevel serverLevel) {
            updater.accept(this.dynamicDeathListener, serverLevel);
        }
    }

    public void hurtMartus(@Nullable LivingEntity source, double num, double perc) {
        double amount = this.getMaxHealth() * perc + num;
        if (amount > 0) {
            this.hurt(CADamageTypes.source(this.level(), CADamageTypes.INV_KILLER, source), (float) amount);
        }
    }

    public void respondToNearbyCreatureDeath(LivingEntity killedEntity, @Nullable LivingEntity killer) {
        float maxHp = this.getMaxHealth();
        double targetHp = killedEntity.getMaxHealth();

        if (killedEntity.getPersistentData().getBoolean("blessed")) {
            double rawDamage = Math.max(maxHp * 0.4, targetHp * 0.4);
            this.hurtMartus(killer, rawDamage, 0);
        } else if (killedEntity.getType().is(SEABORN) && this.getEntityData().get(DATA_PHASE) >= 1) {
            double rawDamage = Math.max(maxHp * 0.018, targetHp * 0.036);
            this.hurtMartus(killer, rawDamage, 0);
        }
    }

    private static class MartusDeathListener implements GameEventListener {
        private final MartusEntity martus;
        private final PositionSource positionSource;

        public MartusDeathListener(MartusEntity martus) {
            this.martus = martus;
            this.positionSource = new EntityPositionSource(martus, martus.getEyeHeight());
        }

        @Override
        public PositionSource getListenerSource() {
            return this.positionSource;
        }

        @Override
        public int getListenerRadius() {
            return 48;
        }

        @Override
        public boolean handleGameEvent(ServerLevel level, Holder<GameEvent> event, GameEvent.Context context, Vec3 pos) {
            if (event.is(GameEvent.ENTITY_DIE)) {
                Entity sourceEntity = context.sourceEntity();
                if (sourceEntity instanceof LivingEntity deadEntity && deadEntity != this.martus) {
                    boolean isBlessed = deadEntity.getPersistentData().getBoolean("blessed");
                    boolean isOcean = deadEntity.getType().is(SEABORN);
                    if (isBlessed || isOcean) {
                        LivingEntity killer = deadEntity.getLastHurtByMob();
                        this.martus.respondToNearbyCreatureDeath(deadEntity, killer);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}