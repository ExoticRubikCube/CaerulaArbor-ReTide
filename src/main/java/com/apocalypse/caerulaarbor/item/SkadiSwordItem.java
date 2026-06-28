package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CaerulaArborModMobEffects;
import com.apocalypse.caerulaarbor.utils.EntityUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
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
        double damage = 0;
        double r = 0;
        Entity enemy = null;
        Entity target = null;
        r = 3;
        enemy = ((Entity) sourceentity instanceof LivingEntity _entity) ? _entity.getLastHurtByMob() : null;
        enemy = ((Entity) sourceentity instanceof LivingEntity _entity) ? _entity.getLastHurtMob() : null;
        damage = (Entity) sourceentity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE) ? _livingEntity2.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 0;
        {
            final Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
            List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate((2 * r) / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
            for (Entity entityiterator : _entfound) {
                if (!(entityiterator instanceof LivingEntity)) {
                    continue;
                }
                if (!(entityiterator instanceof Monster)) {
                    if (!(((Entity) entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) == sourceentity)) {
                        continue;
                    }
                }
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "is_humanside")))) {
                    if (!(entityiterator == enemy)) {
                        continue;
                    }
                }
                if (entityiterator instanceof Player || (entityiterator instanceof TamableAnimal _tamEnt && _tamEnt.isTame())) {
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
                    entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "hunter_attack"))), sourceentity),
                            (float) damage);
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
            if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CaerulaArborModMobEffects.BOOST_OF_SILENCE.get()))) {
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.BOOST_OF_SILENCE.get(), 10, 6, false, false));
                if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CaerulaArborModMobEffects.ADD_REACH.get(), 10, 2, false, false));
            }
        }
	}
}
