package com.susen36.caerulaarbor.item;

import com.susen36.caerulaarbor.CaerulaArbor;
import com.susen36.caerulaarbor.init.CADamageTypes;
import com.susen36.caerulaarbor.init.CAMobEffects;
import com.susen36.caerulaarbor.util.EntityUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.Comparator;
import java.util.List;


public class SkadiSwordItem extends SwordItem {
	private static final Tier TIER = new SimpleTier(
			BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
			0,
			16f,
			20f,
			12,
            Ingredient::of
	);

	public SkadiSwordItem() {
		super(TIER, new Item.Properties().fireResistant().attributes(SwordItem.createAttributes(TIER, 3, -2.8f)));
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
                if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "is_humanside")))) {
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
	public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, context, list, flag);
		list.add(Component.translatable("item.caerula_arbor.skadi_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.skadi_sword.description_1"));
		list.add(Component.translatable("item.caerula_arbor.skadi_sword.description_2"));
	}

	private static final ResourceLocation SKADI_ATTACK_ID = ResourceLocation.fromNamespaceAndPath(CaerulaArbor.MODID, "skadi_sword_attack");

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (entity instanceof LivingEntity livingEntity && !livingEntity.level().isClientSide()) {
			AttributeInstance attackAttr = livingEntity.getAttribute(Attributes.ATTACK_DAMAGE);
			if (selected && EntityUtils.getHealthPerc(livingEntity) >= 0.5) {
				if (attackAttr.getModifier(SKADI_ATTACK_ID) == null) {
					attackAttr.addTransientModifier(new AttributeModifier(SKADI_ATTACK_ID, 0.25D * 7.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
				}
				livingEntity.addEffect(new MobEffectInstance(CAMobEffects.ADD_REACH, 10, 2, false, false));
			} else if (attackAttr.getModifier(SKADI_ATTACK_ID) != null) {
				attackAttr.removeModifier(SKADI_ATTACK_ID);
			}
		}
	}
}