package com.apocalypse.caerulaarbor.events;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;
import com.apocalypse.caerulaarbor.network.CaerulaArborModVariables;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class PlayerEatFuncProcedure {
	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity(), event.getItem());
		}
	}

    private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		String str = "";
		double least = 0;
		double most = 0;
		double index = 0;
		double life = 0;
		double maxLife = 0;
		double curLife = 0;
		if ((ForgeRegistries.ITEMS.getKey(itemstack.getItem()).toString()).equals("alexscaves:biome_treat")) {
			return;
		}
		if (itemstack.getItem().isEdible() && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) >= (entity instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) * 0.6) {
			if ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) < 20 && (entity instanceof Player _plr ? _plr.getFoodData().getSaturationLevel() : 0) < 20) {
				index = (itemstack.getItem().isEdible() ? itemstack.getItem().getFoodProperties().getNutrition() : 0) * 0.01;
				if ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) > 16) {
					index = index * 1.5;
				}
				if ((itemstack.getItem().isEdible() ? itemstack.getItem().getFoodProperties().getSaturationModifier() : 0) > 0.5) {
					index = index * 1.25;
				}
				if (Math.random() < index) {
					most = 1;
					str = Component.translatable("gameplay.life_point.revive.2").getString();
					if ((itemstack.getItem().isEdible() ? itemstack.getItem().getFoodProperties().getSaturationModifier() : 0) > 0.1 && Math.random() < 0.33) {
						most = 2;
						str = Component.translatable("gameplay.life_point.revive.1").getString();
					}
					if ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) > 16 && Math.random() < 0.33) {
						str = Component.translatable("gameplay.life_point.revive.0").getString();
					}
					life = Mth.nextInt(RandomSource.create(), 1, (int) most);
					maxLife = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_maxlive;
					curLife = (entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_lives;
					if (curLife < maxLife) {
						{
							double _setval = Math.min(curLife + life, maxLife);
							entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
								capability.player_lives = _setval;
								capability.syncPlayerVariables(entity);
							});
						}
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal(("\u00A7a" + str.replace("{num}", "" + Math.round(life)))), true);
					}
				}
			}
		}
		for (String stringiterator : CaerulaConfigsConfiguration.LIGHTS_FOOD.get()) {
			index = stringiterator.indexOf(", ", 0);
			if (index < 0) {
				CaerulaArborMod.LOGGER.warn(("Wrong lights food config for item" + stringiterator));
				return;
			}
			str = stringiterator.substring(0, (int) index);
			if ((ForgeRegistries.ITEMS.getKey(itemstack.getItem()).toString()).equals(str)) {
				str = stringiterator.substring((int) (index + 2));
				index = str.indexOf("/", 0);
				if (index < 0) {
					CaerulaArborMod.LOGGER.warn(("Wrong lights food config for item" + stringiterator));
					return;
				}
				least = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(str.substring(0, (int) index));
				most = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(str.substring((int) (index + 1)));
				{
					double _setval = Math.min(100,
							(entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new CaerulaArborModVariables.PlayerVariables())).player_light + Mth.nextInt(RandomSource.create(), (int) least, (int) most));
					entity.getCapability(CaerulaArborModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.player_light = _setval;
						capability.syncPlayerVariables(entity);
					});
				}
			}
		}
	}
}
