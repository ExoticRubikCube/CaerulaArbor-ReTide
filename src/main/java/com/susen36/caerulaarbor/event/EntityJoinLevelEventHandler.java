package com.susen36.caerulaarbor.event;

import com.susen36.babel.difficulty.NDifficulty;
import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.capability.map.MapVariables;
import com.susen36.caerulaarbor.init.CAAttributes;
import com.susen36.caerulaarbor.init.CAConfigs;
import com.susen36.caerulaarbor.init.CAEntityTypeTags;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.manager.upgrade.GrowUpgradeManager;
import com.susen36.caerulaarbor.manager.upgrade.SubsistingUpgradeManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class EntityJoinLevelEventHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        handleBornFunc(event);
    }

    private static void handleBornFunc(EntityJoinLevelEvent event) {
        Level world = event.getLevel();
        // 已进化(且带 EVOLVED 属性)的实体不再重复强化属性
        if (event.getEntity() instanceof LivingEntity living
                && living.getAttributes().hasAttribute(CAAttributes.EVOLVED))
            return;

        // Seaborn：海生原生物。非海生怪物时按移动速度放大游泳速度，再按策略/难度进行属性成长
        if (event.getEntity().getType().is(CAEntityTypeTags.SEABORN)
                && event.getEntity() instanceof LivingEntity living) {
            if (!event.getEntity().getType().is(CAEntityTypeTags.MARINEMOBS)
                    && living.getAttributes().hasAttribute(NeoForgeMod.SWIM_SPEED)) {
                double baseSpeed = living.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)
                        ? living.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue()
                        : 0;
                living.getAttribute(NeoForgeMod.SWIM_SPEED).setBaseValue(baseSpeed * 10);
            }

            MapVariables map = MapVariables.get(world);
            double healthIndex = SubsistingUpgradeManager.getSubsistHealthMultiplier(map.strategy_subsisting);
            double attackIndex = GrowUpgradeManager.getGrowAttackMultiplier(map.strategy_grow);
            double armorIndex = 1;
            double difficulty = NDifficulty.multiplier(world);
            if (difficulty > 0) {
                healthIndex *= difficulty;
                attackIndex *= difficulty;
                armorIndex *= difficulty;
            }
            healthIndex = Math.max(CAConfigs.HEALTH_MULT.get(), 0.1) * healthIndex;
            attackIndex = Math.max(CAConfigs.ATTACK_MULT.get(), 0.1) * attackIndex;
            armorIndex = Math.max(CAConfigs.ARMOR_MULT.get(), 0.1) * armorIndex;

            // 按当前生命占比缩放新血量，保证相对比例不变
            double percentage = living.getHealth() / living.getMaxHealth();
            if (living.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                double base = living.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(base * healthIndex);
                living.setHealth((float) (living.getAttribute(Attributes.MAX_HEALTH).getValue() * percentage));
            }
            if (living.getAttributes().hasAttribute(Attributes.ARMOR)) {
                double base = living.getAttribute(Attributes.ARMOR).getBaseValue();
                double bonus = SubsistingUpgradeManager.getSubsistArmorBonus(map.strategy_subsisting);
                living.getAttribute(Attributes.ARMOR).setBaseValue((base + bonus) * armorIndex);
            }
            if (living.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)) {
                double base = living.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue();
                double bonus = SubsistingUpgradeManager.getSubsistDefenseBonus(map.strategy_subsisting);
                living.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue((base + bonus) * armorIndex);
            }
            if (living.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS)) {
                double base = living.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue();
                double bonus = SubsistingUpgradeManager.getSubsistArmorBonus(map.strategy_subsisting);
                living.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue((base + bonus) * armorIndex);
            }
            if (living.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                double base = living.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                living.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(base * attackIndex);
            }

            // 升华策略强化：海生 BOSS 不受升华影响
            double subl = map.strategy_sublimation;
            if (subl >= 1.0
                    && !event.getEntity().getType().is(CAEntityTypeTags.SEABORN_BOSS)
                    && living.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                double base = living.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(base * (1.0 + 0.1 * subl));
            }

            // 延后 10 tick，若未被锚之力压制则正式完成进化
            final LivingEntity evolved = living;
            CaerulaArbor.queueServerWork(10, () -> {
                if (!evolved.hasEffect(CAMobEffects.POWER_OF_ANCHOR)
                        && evolved.getAttributes().hasAttribute(CAAttributes.EVOLVED))
                    evolved.getAttribute(CAAttributes.EVOLVED).setBaseValue(1);
            });
        }

        // Golem：傀儡。按策略成长生命/攻击与防御，难度大于 1 时再等比放大
        if (event.getEntity().getType().is(CAEntityTypeTags.GOLEMS)
                && event.getEntity() instanceof LivingEntity living) {
            MapVariables map = MapVariables.get(world);
            double subsistHealth = SubsistingUpgradeManager.getSubsistHealthMultiplier(map.strategy_subsisting);
            double subsistArmor = SubsistingUpgradeManager.getSubsistArmorBonus(map.strategy_subsisting);
            double subsistDefense = SubsistingUpgradeManager.getSubsistDefenseBonus(map.strategy_subsisting);
            double growAttack = GrowUpgradeManager.getGrowAttackMultiplier(map.strategy_grow);

            double percentage = living.getHealth() / living.getMaxHealth();
            if (living.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                double base = living.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(base * subsistHealth);
                living.setHealth((float) (living.getAttribute(Attributes.MAX_HEALTH).getValue() * percentage));
            }
            if (living.getAttributes().hasAttribute(Attributes.ARMOR)) {
                double base = living.getAttribute(Attributes.ARMOR).getBaseValue();
                living.getAttribute(Attributes.ARMOR).setBaseValue(base + subsistArmor);
            }
            if (living.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)) {
                double base = living.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue();
                living.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue(base + subsistDefense);
            }
            if (living.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS)) {
                double base = living.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue();
                living.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(base + subsistArmor);
            }
            if (living.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                double base = living.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                living.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(base * growAttack);
            }

            double difficulty = NDifficulty.multiplier(world);
            if (difficulty > 1.0) {
                percentage = living.getHealth() / living.getMaxHealth();
                if (living.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
                    double base = living.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
                    living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(base * difficulty);
                    living.setHealth((float) (living.getAttribute(Attributes.MAX_HEALTH).getValue() * percentage));
                }
                if (living.getAttributes().hasAttribute(Attributes.ARMOR)) {
                    double base = living.getAttribute(Attributes.ARMOR).getBaseValue();
                    living.getAttribute(Attributes.ARMOR).setBaseValue(base * difficulty);
                }
                if (living.getAttributes().hasAttribute(CAAttributes.GENERAL_DEFENSE)) {
                    double base = living.getAttribute(CAAttributes.GENERAL_DEFENSE).getBaseValue();
                    living.getAttribute(CAAttributes.GENERAL_DEFENSE).setBaseValue(base * difficulty);
                }
                if (living.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS)) {
                    double base = living.getAttribute(Attributes.ARMOR_TOUGHNESS).getBaseValue();
                    living.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(base * difficulty);
                }
                if (living.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                    double base = living.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                    living.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(base * difficulty);
                }
            }
            if (living.getAttributes().hasAttribute(CAAttributes.EVOLVED))
                living.getAttribute(CAAttributes.EVOLVED).setBaseValue(1);
        }
    }
}