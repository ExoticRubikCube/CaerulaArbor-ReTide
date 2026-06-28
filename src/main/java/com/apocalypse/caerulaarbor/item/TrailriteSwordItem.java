
package com.apocalypse.caerulaarbor.item;

import com.apocalypse.caerulaarbor.init.CaerulaArborModItems;
import com.apocalypse.caerulaarbor.util.EffectUtils;
import com.apocalypse.caerulaarbor.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TrailriteSwordItem extends SwordItem {
	public TrailriteSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 7999;
			}

			public float getSpeed() {
				return 9f;
			}

			public float getAttackDamageBonus() {
				return 17f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 23;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(CaerulaArborModItems.TRAILRITE.get()));
			}
		}, 3, -2.4f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
        LevelAccessor world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double absorp = 0;
        double rate = 0;
        EntityUtils.deductSanity(entity, 330);
        if (!(entity instanceof Player)) {
            if (Math.random() < 0.2 + itemstack.getEnchantmentLevel(Enchantments.MOB_LOOTING) * 0.02) {
                rate = 0.025 + itemstack.getEnchantmentLevel(Enchantments.SHARPNESS) * 0.005;
                if (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * rate) {
                    absorp = ((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * rate;
                    if (absorp > ((Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1)) {
                        absorp = (Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1;
                    }
                    if (!(((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) - absorp < 1)) {
                        if ((Entity) entity instanceof LivingEntity _entity)
                            _entity.setHealth((float) (((Entity) entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) - absorp));
                        if ((Entity) sourceentity instanceof LivingEntity _entity)
                            _entity.setHealth((float) (((Entity) sourceentity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + absorp));
                        if (world instanceof Level _level) {
                                _level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.amethyst_block.resonate")), SoundSource.PLAYERS, 1, 1);
                        }
                        if (world instanceof ServerLevel _level)
                            _level.sendParticles(ParticleTypes.INSTANT_EFFECT, x, (y + 1), z, 32, 2, 2, 2, 0.25);
                    }
                }
            }
        }
        return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.caerula_arbor.trailrite_sword.description_0"));
		list.add(Component.translatable("item.caerula_arbor.trailrite_sword.description_1"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			EffectUtils.addReachEffect(entity, 20, 2);
	}
}
