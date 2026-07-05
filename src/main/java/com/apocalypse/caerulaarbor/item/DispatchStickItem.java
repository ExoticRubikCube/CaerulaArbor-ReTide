package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.init.CAMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class DispatchStickItem extends Item {
	public DispatchStickItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.dispatch_stick.description_0"));
		list.add(Component.translatable("item.caerula_arbor.dispatch_stick.description_1"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
        final Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
        for (Entity entityiterator : _entfound) {
            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring")))) {
                if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
                    _entity.addEffect(new MobEffectInstance(CAMobEffects.ANGER_OF_TIDE.get(), 131072, 0, false, true));
            }
        }
        return super.use(world, entity, hand);
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        final Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
        for (Entity entityiterator : _entfound) {
            if (entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(CaerulaArborMod.MODID, "oceanoffspring"))) && !(entity == entityiterator)) {
                if (entityiterator instanceof Mob _entity && (Entity) entity instanceof LivingEntity _ent)
                    _entity.setTarget(_ent);
            }
        }
        return retval;
	}
}
