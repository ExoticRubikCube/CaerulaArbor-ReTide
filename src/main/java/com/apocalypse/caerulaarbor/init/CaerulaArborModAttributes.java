/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CaerulaArborModAttributes {
	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, CaerulaArborMod.MODID);
	public static final RegistryObject<Attribute> SANITY = REGISTRY.register("sanity", () -> new RangedAttribute("attribute.caerula_arbor.sanity", 1000, -1, 1000).setSyncable(true));
	public static final RegistryObject<Attribute> SANITY_MODIFIER = REGISTRY.register("sanity_modifier", () -> new RangedAttribute("attribute.caerula_arbor.sanity_modifier", 1, 0, 999).setSyncable(true));
	public static final RegistryObject<Attribute> SANITY_RATE = REGISTRY.register("sanity_rate", () -> new RangedAttribute("attribute.caerula_arbor.sanity_rate", 0, 0, 999).setSyncable(true));
	public static final RegistryObject<Attribute> EVOLVED = REGISTRY.register("evolved", () -> new RangedAttribute("attribute.caerula_arbor.evolved", 0, 0, 1).setSyncable(true));
	public static final RegistryObject<Attribute> SUMMONABLE = REGISTRY.register("summonable", () -> new RangedAttribute("attribute.caerula_arbor.summonable", 1, 0, 1).setSyncable(true));
	public static final RegistryObject<Attribute> MISSRATE = REGISTRY.register("missrate", () -> new RangedAttribute("attribute.caerula_arbor.missrate", 0, 0, 100).setSyncable(true));
	public static final RegistryObject<Attribute> MAGIC_RESISTANCE = REGISTRY.register("magic_resistance", () -> new RangedAttribute("attribute.caerula_arbor.magic_resistance", 0, 0, 100).setSyncable(true));
	public static final RegistryObject<Attribute> GENERAL_DEFENSE = REGISTRY.register("general_defense", () -> new RangedAttribute("attribute.caerula_arbor.general_defense", 0, 0, 131071).setSyncable(true));
	public static final RegistryObject<Attribute> NUMB = REGISTRY.register("numb", () -> new RangedAttribute("attribute.caerula_arbor.numb", 0, 0, 9).setSyncable(true));
	public static final RegistryObject<Attribute> SANITY_RESISTANCE = REGISTRY.register("sanity_resistance", () -> new RangedAttribute("attribute.caerula_arbor.sanity_resistance", 0, 0, 100).setSyncable(true));
	public static final RegistryObject<Attribute> LIVING_BARRIER = REGISTRY.register("living_barrier", () -> new RangedAttribute("attribute.caerula_arbor.living_barrier", 0, 0, 214748364).setSyncable(true));

	@SubscribeEvent
	public static void addAttributes(EntityAttributeModificationEvent event) {
		event.getTypes().forEach(entity -> event.add(entity, SANITY.get()));
		event.getTypes().forEach(entity -> event.add(entity, SANITY_MODIFIER.get()));
		event.getTypes().forEach(entity -> event.add(entity, SANITY_RATE.get()));
		List.of(CaerulaArborModEntities.ACCUMULATOR_CLONE.get(), CaerulaArborModEntities.ACCUMULATOR_PROKARYOTE.get(), CaerulaArborModEntities.APOSTLE_PROKARYOTE.get(), CaerulaArborModEntities.BASELAYER_ABYSSAL.get(),
				CaerulaArborModEntities.BISHOP_FISH.get(), CaerulaArborModEntities.BONE_FISH.get(), CaerulaArborModEntities.CHEST_FISH.get(), CaerulaArborModEntities.CHISELER_FISH.get(), CaerulaArborModEntities.CHITIN_GOLEM.get(),
				CaerulaArborModEntities.COLLECTOR_PROKARYOTE.get(), CaerulaArborModEntities.CRACKER_ABYSSAL.get(), CaerulaArborModEntities.CREEPER_FISH.get(), CaerulaArborModEntities.DEPOSITER_PROKARYOTE.get(),
				CaerulaArborModEntities.DIVICELLULAR_GO.get(), CaerulaArborModEntities.FAKE_OFFSPRING.get(), CaerulaArborModEntities.FEEDER_PROKARYOTE.get(), CaerulaArborModEntities.FIRST_TO_TALK.get(), CaerulaArborModEntities.FLEE_FISH.get(),
				CaerulaArborModEntities.FLOATER_PROKARYOTE.get(), CaerulaArborModEntities.FLY_FISH.get(), CaerulaArborModEntities.GUIDE_ABYSSAL.get(), CaerulaArborModEntities.HIGHMORE.get(), CaerulaArborModEntities.HIGHMORE_SHOOT.get(),
				CaerulaArborModEntities.IZUMIK.get(), CaerulaArborModEntities.IZUMIK_OFFSPRING.get(), CaerulaArborModEntities.MARTUS.get(), CaerulaArborModEntities.MEGA_CHEST.get(), CaerulaArborModEntities.OCEANIZED_BRUTE.get(),
				CaerulaArborModEntities.OCEANIZED_COW.get(), CaerulaArborModEntities.OCEANIZED_DOG.get(), CaerulaArborModEntities.OCEANIZED_ENDERMAN.get(), CaerulaArborModEntities.OCEANIZED_EVOKER.get(), CaerulaArborModEntities.OCEANIZED_HORSE.get(),
				CaerulaArborModEntities.OCEANIZED_PIG.get(), CaerulaArborModEntities.OCEANIZED_PIGLIN.get(), CaerulaArborModEntities.OCEANIZED_PILLAGER.get(), CaerulaArborModEntities.OCEANIZED_RAVAGER.get(),
				CaerulaArborModEntities.OCEANIZED_SHEEP.get(), CaerulaArborModEntities.OCEANIZED_SPIDER.get(), CaerulaArborModEntities.OCEANIZED_VILLAGER.get(), CaerulaArborModEntities.OCEANIZED_VINDICATOR.get(),
				CaerulaArborModEntities.OCEANIZED_WOLF.get(), CaerulaArborModEntities.OCEANIZED_WITCH.get(), CaerulaArborModEntities.PREDATOR_ABYSSAL.get(), CaerulaArborModEntities.PREGNANT_FISH.get(), CaerulaArborModEntities.PUNCTURE_FISH.get(),
				CaerulaArborModEntities.REAPER_FISH.get(), CaerulaArborModEntities.REAPER_PET.get(), CaerulaArborModEntities.ROUTE_FRACTAL.get(), CaerulaArborModEntities.ROUTE_SHAPER.get(), CaerulaArborModEntities.RUN_FISH.get(),
				CaerulaArborModEntities.SHOOTER_FISH.get(), CaerulaArborModEntities.SKADI.get(), CaerulaArborModEntities.SLIDER_FISH.get(), CaerulaArborModEntities.SONS.get(), CaerulaArborModEntities.SPIKE_CHEST.get(),
				CaerulaArborModEntities.SPLASHER_ABYSSAL.get(), CaerulaArborModEntities.SUPER_SLIDER.get(), CaerulaArborModEntities.TELLER_SHOT.get(), CaerulaArborModEntities.THE_ABANDONED.get(), CaerulaArborModEntities.TIDE_BISHOP.get(),
				CaerulaArborModEntities.TIDE_DEATHREPELLER.get(), CaerulaArborModEntities.UMBRELLA_ABYSSAL.get(), CaerulaArborModEntities.ABANDONED_SHOOT.get(), CaerulaArborModEntities.ANCHOR_FLY.get(),
				CaerulaArborModEntities.CORRECTIONAL_PHALAX_VANGUARD.get(), CaerulaArborModEntities.CORRECTIONAL_PHALANXY_INFANTRY.get(), CaerulaArborModEntities.FAKERGG_SHOOT.get(), CaerulaArborModEntities.FISH_SHOOT.get(),
				CaerulaArborModEntities.FISH_SPLASH.get(), CaerulaArborModEntities.FLEEFISH_BULLET.get(), CaerulaArborModEntities.GUNMU.get(), CaerulaArborModEntities.HEAL_BULLLET.get(), CaerulaArborModEntities.JUNIOR_WARRIOR_PRIEST.get(),
				CaerulaArborModEntities.OCEANIZED_CAT.get(), CaerulaArborModEntities.OCEANIZED_WARDEN.get(), CaerulaArborModEntities.SHOT_OCEAN_ARROW.get(), CaerulaArborModEntities.THROWABLE_POTION.get(),
				CaerulaArborModEntities.TRIBUNAL_HEALER.get(), CaerulaArborModEntities.WARRIOR_PRIEST.get(), CaerulaArborModEntities.NUCLEIC_MALEFICENT.get(), CaerulaArborModEntities.COMPLEX_CHITIN_GOLEM.get(),
				CaerulaArborModEntities.OCEANIZED_WARDENIS.get(), CaerulaArborModEntities.SUPER_BIG_CAT.get(), CaerulaArborModEntities.OCEANIZED_WITHER.get(), CaerulaArborModEntities.OCEANIZED_WITHERIA.get(),
				CaerulaArborModEntities.LAST_KNIGHT_AND_HORSE.get(), CaerulaArborModEntities.THE_LAST_KNIGHT.get(), CaerulaArborModEntities.WITHER_SHOOT_PRE.get(), CaerulaArborModEntities.APOCATA.get(), CaerulaArborModEntities.OCEANIZED_FOX.get(),
				CaerulaArborModEntities.ROCINANTE.get(), CaerulaArborModEntities.TIDUTANT_EXCRESCENCE.get(), CaerulaArborModEntities.OCEANIZED_POLAR_BEAR.get(), CaerulaArborModEntities.ENDSPEAKER_0.get(), CaerulaArborModEntities.ENDSPEAKER_1.get(),
				CaerulaArborModEntities.TIDUTANT_ROCK_SPIDER.get(), CaerulaArborModEntities.ENDSPEAKER_2.get(), CaerulaArborModEntities.TIDE_CHIMERA.get(), CaerulaArborModEntities.AL_1_S_HELPER.get(), CaerulaArborModEntities.DAMAGE_TESTER.get(),
				CaerulaArborModEntities.ENDSPEAKER_3.get(), CaerulaArborModEntities.GLADIIA.get(), CaerulaArborModEntities.GLADIIA_WHIRL.get(), CaerulaArborModEntities.IRENE.get(), CaerulaArborModEntities.LINGERING_PATHSHAPER.get(),
				CaerulaArborModEntities.LINGERING_FRACTAL.get(), CaerulaArborModEntities.LITTLE_HELPER.get(), CaerulaArborModEntities.SPECTER.get(), CaerulaArborModEntities.SPECTER_DOLL.get(), CaerulaArborModEntities.ULPIANS.get(),
				CaerulaArborModEntities.CARMEN_BULLET.get(), CaerulaArborModEntities.FLAMARINE_GOLEM.get(), CaerulaArborModEntities.FLAMARINE_STATUE.get(), CaerulaArborModEntities.NAUTILUS_HEADHUNTER.get(),
				CaerulaArborModEntities.OCEAN_ILLUSION.get(), CaerulaArborModEntities.OCEANIZE_RABBIT.get(), CaerulaArborModEntities.OCEANIZED_ILLUSIONER.get(), CaerulaArborModEntities.SAINT_CARMEN.get(),
				CaerulaArborModEntities.SKADI_CORRUPTED.get(), CaerulaArborModEntities.XANTIS.get(), CaerulaArborModEntities.ISHARMLA.get(), CaerulaArborModEntities.OCEANIZED_VEX.get(), CaerulaArborModEntities.QUNYOU_WANTED_ISHARMLA.get(),
				CaerulaArborModEntities.COMPASSION_PRAYER.get(), CaerulaArborModEntities.ISHARMLA_TEAR.get(), CaerulaArborModEntities.MOIST_DRAGON_BREATH.get(), CaerulaArborModEntities.MOIST_ENDER_CRYSTAL.get(),
				CaerulaArborModEntities.OCEANIZED_ENDERINA.get(), CaerulaArborModEntities.PRAYER_SPLASH.get(), CaerulaArborModEntities.THIRSTER.get(), CaerulaArborModEntities.ABSORBER_LIMB.get(), CaerulaArborModEntities.OCEANIZED_CHICKEN.get(),
				CaerulaArborModEntities.SCREAM_CHEST_FISH.get(), CaerulaArborModEntities.NETHERSEA_SLIME.get(), CaerulaArborModEntities.OCEANIZED_SHULKER.get()).stream().filter(DefaultAttributes::hasSupplier)
				.map(entityType -> (EntityType<? extends LivingEntity>) entityType).collect(Collectors.toList()).forEach(entity -> event.add(entity, EVOLVED.get()));
		List.of(CaerulaArborModEntities.ACCUMULATOR_CLONE.get(), CaerulaArborModEntities.ACCUMULATOR_PROKARYOTE.get(), CaerulaArborModEntities.ANCHOR_FLY.get(), CaerulaArborModEntities.APOSTLE_PROKARYOTE.get(),
				CaerulaArborModEntities.BASELAYER_ABYSSAL.get(), CaerulaArborModEntities.BISHOP_FISH.get(), CaerulaArborModEntities.BONE_FISH.get(), CaerulaArborModEntities.CHEST_FISH.get(), CaerulaArborModEntities.CHISELER_FISH.get(),
				CaerulaArborModEntities.CHITIN_GOLEM.get(), CaerulaArborModEntities.COLLECTOR_PROKARYOTE.get(), CaerulaArborModEntities.CRACKER_ABYSSAL.get(), CaerulaArborModEntities.CREEPER_FISH.get(),
				CaerulaArborModEntities.DEPOSITER_PROKARYOTE.get(), CaerulaArborModEntities.FAKE_OFFSPRING.get(), CaerulaArborModEntities.FAKERGG_SHOOT.get(), CaerulaArborModEntities.FEEDER_PROKARYOTE.get(),
				CaerulaArborModEntities.FIRST_TO_TALK.get(), CaerulaArborModEntities.FISH_SHOOT.get(), CaerulaArborModEntities.FISH_SPLASH.get(), CaerulaArborModEntities.FLEE_FISH.get(), CaerulaArborModEntities.FLEEFISH_BULLET.get(),
				CaerulaArborModEntities.FLOATER_PROKARYOTE.get(), CaerulaArborModEntities.FLY_FISH.get(), CaerulaArborModEntities.GUIDE_ABYSSAL.get(), CaerulaArborModEntities.HIGHMORE.get(), CaerulaArborModEntities.HIGHMORE_SHOOT.get(),
				CaerulaArborModEntities.IZUMIK_OFFSPRING.get(), CaerulaArborModEntities.MEGA_CHEST.get(), CaerulaArborModEntities.OCEANIZED_BRUTE.get(), CaerulaArborModEntities.OCEANIZED_COW.get(), CaerulaArborModEntities.OCEANIZED_DOG.get(),
				CaerulaArborModEntities.OCEANIZED_ENDERMAN.get(), CaerulaArborModEntities.OCEANIZED_HORSE.get(), CaerulaArborModEntities.OCEANIZED_PIG.get(), CaerulaArborModEntities.OCEANIZED_PIGLIN.get(),
				CaerulaArborModEntities.OCEANIZED_PILLAGER.get(), CaerulaArborModEntities.OCEANIZED_RAVAGER.get(), CaerulaArborModEntities.OCEANIZED_SHEEP.get(), CaerulaArborModEntities.OCEANIZED_SPIDER.get(),
				CaerulaArborModEntities.OCEANIZED_VILLAGER.get(), CaerulaArborModEntities.OCEANIZED_VINDICATOR.get(), CaerulaArborModEntities.OCEANIZED_WOLF.get(), CaerulaArborModEntities.OCEANIZED_WITCH.get(),
				CaerulaArborModEntities.PREDATOR_ABYSSAL.get(), CaerulaArborModEntities.PREGNANT_FISH.get(), CaerulaArborModEntities.PUNCTURE_FISH.get(), CaerulaArborModEntities.REAPER_FISH.get(), CaerulaArborModEntities.REAPER_PET.get(),
				CaerulaArborModEntities.ROUTE_FRACTAL.get(), CaerulaArborModEntities.ROUTE_SHAPER.get(), CaerulaArborModEntities.RUN_FISH.get(), CaerulaArborModEntities.SHOOTER_FISH.get(), CaerulaArborModEntities.SHOT_OCEAN_ARROW.get(),
				CaerulaArborModEntities.SKADI.get(), CaerulaArborModEntities.SLIDER_FISH.get(), CaerulaArborModEntities.SONS.get(), CaerulaArborModEntities.SPIKE_CHEST.get(), CaerulaArborModEntities.SPLASHER_ABYSSAL.get(),
				CaerulaArborModEntities.SUPER_SLIDER.get(), CaerulaArborModEntities.TELLER_SHOT.get(), CaerulaArborModEntities.THROWABLE_POTION.get(), CaerulaArborModEntities.TIDE_BISHOP.get(), CaerulaArborModEntities.TIDE_DEATHREPELLER.get(),
				CaerulaArborModEntities.UMBRELLA_ABYSSAL.get(), CaerulaArborModEntities.IZUMIK.get(), CaerulaArborModEntities.NUCLEIC_MALEFICENT.get(), CaerulaArborModEntities.ABANDONED_SHOOT.get(), CaerulaArborModEntities.COMPLEX_CHITIN_GOLEM.get(),
				CaerulaArborModEntities.CORRECTIONAL_PHALAX_VANGUARD.get(), CaerulaArborModEntities.CORRECTIONAL_PHALANXY_INFANTRY.get(), CaerulaArborModEntities.DIVICELLULAR_GO.get(), CaerulaArborModEntities.GUNMU.get(),
				CaerulaArborModEntities.HEAL_BULLLET.get(), CaerulaArborModEntities.JUNIOR_WARRIOR_PRIEST.get(), CaerulaArborModEntities.MARTUS.get(), CaerulaArborModEntities.OCEANIZED_CAT.get(), CaerulaArborModEntities.OCEANIZED_EVOKER.get(),
				CaerulaArborModEntities.OCEANIZED_WARDEN.get(), CaerulaArborModEntities.OCEANIZED_WARDENIS.get(), CaerulaArborModEntities.SUPER_BIG_CAT.get(), CaerulaArborModEntities.THE_ABANDONED.get(), CaerulaArborModEntities.TRIBUNAL_HEALER.get(),
				CaerulaArborModEntities.WARRIOR_PRIEST.get()).stream().filter(DefaultAttributes::hasSupplier).map(entityType -> (EntityType<? extends LivingEntity>) entityType).collect(Collectors.toList())
				.forEach(entity -> event.add(entity, SUMMONABLE.get()));
		event.getTypes().forEach(entity -> event.add(entity, MISSRATE.get()));
		event.getTypes().forEach(entity -> event.add(entity, MAGIC_RESISTANCE.get()));
		event.getTypes().forEach(entity -> event.add(entity, GENERAL_DEFENSE.get()));
		event.getTypes().forEach(entity -> event.add(entity, NUMB.get()));
		event.getTypes().forEach(entity -> event.add(entity, SANITY_RESISTANCE.get()));
		event.getTypes().forEach(entity -> event.add(entity, LIVING_BARRIER.get()));
	}

	@Mod.EventBusSubscriber
	public static class PlayerAttributesSync {
		@SubscribeEvent
		public static void playerClone(PlayerEvent.Clone event) {
			Player oldPlayer = event.getOriginal();
			Player newPlayer = event.getEntity();
			newPlayer.getAttribute(SANITY.get()).setBaseValue(oldPlayer.getAttribute(SANITY.get()).getBaseValue());
			newPlayer.getAttribute(SANITY_MODIFIER.get()).setBaseValue(oldPlayer.getAttribute(SANITY_MODIFIER.get()).getBaseValue());
			newPlayer.getAttribute(SANITY_RATE.get()).setBaseValue(oldPlayer.getAttribute(SANITY_RATE.get()).getBaseValue());
			newPlayer.getAttribute(MISSRATE.get()).setBaseValue(oldPlayer.getAttribute(MISSRATE.get()).getBaseValue());
			newPlayer.getAttribute(MAGIC_RESISTANCE.get()).setBaseValue(oldPlayer.getAttribute(MAGIC_RESISTANCE.get()).getBaseValue());
			newPlayer.getAttribute(GENERAL_DEFENSE.get()).setBaseValue(oldPlayer.getAttribute(GENERAL_DEFENSE.get()).getBaseValue());
			newPlayer.getAttribute(NUMB.get()).setBaseValue(oldPlayer.getAttribute(NUMB.get()).getBaseValue());
			newPlayer.getAttribute(SANITY_RESISTANCE.get()).setBaseValue(oldPlayer.getAttribute(SANITY_RESISTANCE.get()).getBaseValue());
			newPlayer.getAttribute(LIVING_BARRIER.get()).setBaseValue(oldPlayer.getAttribute(LIVING_BARRIER.get()).getBaseValue());
		}
	}
}
