package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CADamageTypes;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class SkadiSwordItem extends SwordItem {
	public SkadiSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 0;
			}

			public float getSpeed() {
				return 16f;
			}

			public float getAttackDamageBonus() {
				return 20f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 12;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 3, -2.8f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double damage;
        double r;
        Entity enemy;
        r = 3;
        enemy = ((Entity) sourceentity instanceof LivingEntity livingEntity) ? livingEntity.getLastHurtMob() : null;
        damage = (Entity) sourceentity instanceof LivingEntity livingEntity2 && livingEntity2.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? livingEntity2.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        {
            final Vec3 center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
            List<Entity> entfound = world.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate((2 * r) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(entcnd -> entcnd.distanceToSqr(center))).toList();
            for (Entity entityiterator : entfound) {
                if (!(entityiterator instanceof LivingEntity)) {
                    continue;
                }
                if (!(entityiterator instanceof Monster)) {
                    if (!(((Entity) entity instanceof Mob mobEnt ? (Entity) mobEnt.getTarget() : null) == sourceentity)) {
                        continue;
                    }
                }
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                    if (!(entityiterator == enemy)) {
                        continue;
                    }
                }
                if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal tamEnt && tamEnt.isTame())) {
                    if (!(entityiterator == enemy)) {
                        continue;
                    }
                }
                if (entityiterator == sourceentity) {
                    continue;
                }
                if (entityiterator == entity) {
                    continue;
                }
                if (entity.distanceTo(entityiterator) <= r) {
                    entityiterator.hurt(CADamageTypes.source(world, CADamageTypes.HUNTER_ATTACK, sourceentity), (float) damage);
                }
            }
        }
        return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.skadi_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.skadi_sword.description_1"));
		list.add(Component.translatable("item.caerula_arbor.skadi_sword.description_2"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected && EntityUtils.getHealthPerc(entity) >= 0.5) {
            if (!(entity instanceof LivingEntity livEnt0 && livEnt0.hasEffect(CAMobEffects.BOOST_OF_SILENCE.get()))) {
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.BOOST_OF_SILENCE.get(), 10, 6, false, false));
                if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide())
                    livingEntity.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH.get(), 10, 2, false, false));
            }
        }
	}
}
