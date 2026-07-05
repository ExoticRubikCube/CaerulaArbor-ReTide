/*
 *    MCreator 注：此文件会在每次构建时重新生成。
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

import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CAAttributes {
	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, CaerulaArborMod.MODID);
	public static final RegistryObject<Attribute> SANITY_MODIFIER = REGISTRY.register("sanity_modifier", () -> new RangedAttribute("attribute.caerula_arbor.sanity_modifier", 1, 0, 999).setSyncable(true));
	public static final RegistryObject<Attribute> SANITY_RATE = REGISTRY.register("sanity_rate", () -> new RangedAttribute("attribute.caerula_arbor.sanity_rate", 0, 0, 999).setSyncable(true));
	public static final RegistryObject<Attribute> SANITY_INJURY_DAMAGE = REGISTRY.register("sanity_injury_damage",
			() -> new RangedAttribute("attribute.caerula_arbor.sanity_injury_damage", 0, 0, Integer.MAX_VALUE).setSyncable(true));
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
		event.getTypes().forEach(entity -> event.add(entity, SANITY_MODIFIER.get()));
		event.getTypes().forEach(entity -> event.add(entity, SANITY_RATE.get()));
		event.getTypes().forEach(entity -> event.add(entity, SANITY_INJURY_DAMAGE.get()));
		Stream.of(CAEntities.ACCUMULATOR_CLONE.get(), CAEntities.ACCUMULATOR_PROKARYOTE.get(), CAEntities.APOSTLE_PROKARYOTE.get(), CAEntities.BASELAYER_ABYSSAL.get(),
				CAEntities.BISHOP_FISH.get(), CAEntities.BONE_FISH.get(), CAEntities.CHEST_FISH.get(), CAEntities.CHISELER_FISH.get(), CAEntities.CHITIN_GOLEM.get(),
				CAEntities.COLLECTOR_PROKARYOTE.get(), CAEntities.CRACKER_ABYSSAL.get(), CAEntities.CREEPER_FISH.get(), CAEntities.DEPOSITER_PROKARYOTE.get(),
				CAEntities.DIVICELLULAR_GO.get(), CAEntities.FAKE_OFFSPRING.get(), CAEntities.FEEDER_PROKARYOTE.get(), CAEntities.FIRST_TO_TALK.get(), CAEntities.FLEE_FISH.get(),
				CAEntities.FLOATER_PROKARYOTE.get(), CAEntities.FLY_FISH.get(), CAEntities.GUIDE_ABYSSAL.get(), CAEntities.HIGHMORE.get(), CAEntities.HIGHMORE_SHOOT.get(),
				CAEntities.IZUMIK.get(), CAEntities.IZUMIK_OFFSPRING.get(), CAEntities.MARTUS.get(), CAEntities.MEGA_CHEST.get(), CAEntities.OCEANIZED_BRUTE.get(),
				CAEntities.OCEANIZED_COW.get(), CAEntities.OCEANIZED_DOG.get(), CAEntities.OCEANIZED_ENDERMAN.get(), CAEntities.OCEANIZED_EVOKER.get(), CAEntities.OCEANIZED_HORSE.get(),
				CAEntities.OCEANIZED_PIG.get(), CAEntities.OCEANIZED_PIGLIN.get(), CAEntities.OCEANIZED_PILLAGER.get(), CAEntities.OCEANIZED_RAVAGER.get(),
				CAEntities.OCEANIZED_SHEEP.get(), CAEntities.OCEANIZED_SPIDER.get(), CAEntities.OCEANIZED_VILLAGER.get(), CAEntities.OCEANIZED_VINDICATOR.get(),
				CAEntities.OCEANIZED_WOLF.get(), CAEntities.OCEANIZED_WITCH.get(), CAEntities.PREDATOR_ABYSSAL.get(), CAEntities.PREGNANT_FISH.get(), CAEntities.PUNCTURE_FISH.get(),
				CAEntities.REAPER_FISH.get(), CAEntities.REAPER_PET.get(), CAEntities.ROUTE_FRACTAL.get(), CAEntities.ROUTE_SHAPER.get(), CAEntities.RUN_FISH.get(),
				CAEntities.SHOOTER_FISH.get(), CAEntities.SKADI.get(), CAEntities.SLIDER_FISH.get(), CAEntities.SONS.get(), CAEntities.SPIKE_CHEST.get(),
				CAEntities.SPLASHER_ABYSSAL.get(), CAEntities.SUPER_SLIDER.get(), CAEntities.TELLER_SHOT.get(), CAEntities.THE_ABANDONED.get(), CAEntities.TIDE_BISHOP.get(),
				CAEntities.TIDE_DEATHREPELLER.get(), CAEntities.UMBRELLA_ABYSSAL.get(), CAEntities.ABANDONED_SHOOT.get(), CAEntities.ANCHOR_FLY.get(),
				CAEntities.CORRECTIONAL_PHALAX_VANGUARD.get(), CAEntities.CORRECTIONAL_PHALANXY_INFANTRY.get(), CAEntities.FAKERGG_SHOOT.get(), CAEntities.FISH_SHOOT.get(),
				CAEntities.FISH_SPLASH.get(), CAEntities.FLEEFISH_BULLET.get(), CAEntities.GUNMU.get(), CAEntities.HEAL_BULLLET.get(), CAEntities.JUNIOR_WARRIOR_PRIEST.get(),
				CAEntities.OCEANIZED_CAT.get(), CAEntities.OCEANIZED_WARDEN.get(), CAEntities.SHOT_OCEAN_ARROW.get(), CAEntities.THROWABLE_POTION.get(),
				CAEntities.TRIBUNAL_HEALER.get(), CAEntities.WARRIOR_PRIEST.get(), CAEntities.NUCLEIC_MALEFICENT.get(), CAEntities.COMPLEX_CHITIN_GOLEM.get(),
				CAEntities.OCEANIZED_WARDENIS.get(), CAEntities.SUPER_BIG_CAT.get(), CAEntities.OCEANIZED_WITHER.get(), CAEntities.OCEANIZED_WITHERIA.get(),
				CAEntities.LAST_KNIGHT_AND_HORSE.get(), CAEntities.THE_LAST_KNIGHT.get(), CAEntities.WITHER_SHOOT_PRE.get(), CAEntities.APOCATA.get(), CAEntities.OCEANIZED_FOX.get(),
				CAEntities.ROCINANTE.get(), CAEntities.TIDUTANT_EXCRESCENCE.get(), CAEntities.OCEANIZED_POLAR_BEAR.get(), CAEntities.ENDSPEAKER.get(),
				CAEntities.TIDUTANT_ROCK_SPIDER.get(), CAEntities.TIDE_CHIMERA.get(), CAEntities.AL_1_S_HELPER.get(),
				CAEntities.GLADIIA.get(), CAEntities.GLADIIA_WHIRL.get(), CAEntities.IRENE.get(), CAEntities.LINGERING_PATHSHAPER.get(),
				CAEntities.LINGERING_FRACTAL.get(), CAEntities.LITTLE_HELPER.get(), CAEntities.SPECTER.get(), CAEntities.SPECTER_DOLL.get(), CAEntities.ULPIANS.get(),
				CAEntities.CARMEN_BULLET.get(), CAEntities.FLAMARINE_GOLEM.get(), CAEntities.FLAMARINE_STATUE.get(), CAEntities.NAUTILUS_HEADHUNTER.get(),
				CAEntities.OCEAN_ILLUSION.get(), CAEntities.OCEANIZE_RABBIT.get(), CAEntities.OCEANIZED_ILLUSIONER.get(), CAEntities.SAINT_CARMEN.get(),
				CAEntities.SKADI_CORRUPTED.get(), CAEntities.XANTIS.get(), CAEntities.ISHARMLA.get(), CAEntities.OCEANIZED_VEX.get(), CAEntities.QUNYOU_WANTED_ISHARMLA.get(),
				CAEntities.COMPASSION_PRAYER.get(), CAEntities.ISHARMLA_TEAR.get(), CAEntities.MOIST_DRAGON_BREATH.get(), CAEntities.MOIST_ENDER_CRYSTAL.get(),
				CAEntities.OCEANIZED_ENDERINA.get(), CAEntities.PRAYER_SPLASH.get(), CAEntities.THIRSTER.get(), CAEntities.ABSORBER_LIMB.get(), CAEntities.OCEANIZED_CHICKEN.get(),
				CAEntities.SCREAM_CHEST_FISH.get(), CAEntities.NETHERSEA_SLIME.get(), CAEntities.OCEANIZED_SHULKER.get()).filter(DefaultAttributes::hasSupplier)
				.map(entityType -> (EntityType<? extends LivingEntity>) entityType).toList().forEach(entity -> event.add(entity, EVOLVED.get()));
		Stream.of(CAEntities.ACCUMULATOR_CLONE.get(), CAEntities.ACCUMULATOR_PROKARYOTE.get(), CAEntities.ANCHOR_FLY.get(), CAEntities.APOSTLE_PROKARYOTE.get(),
				CAEntities.BASELAYER_ABYSSAL.get(), CAEntities.BISHOP_FISH.get(), CAEntities.BONE_FISH.get(), CAEntities.CHEST_FISH.get(), CAEntities.CHISELER_FISH.get(),
				CAEntities.CHITIN_GOLEM.get(), CAEntities.COLLECTOR_PROKARYOTE.get(), CAEntities.CRACKER_ABYSSAL.get(), CAEntities.CREEPER_FISH.get(),
				CAEntities.DEPOSITER_PROKARYOTE.get(), CAEntities.FAKE_OFFSPRING.get(), CAEntities.FAKERGG_SHOOT.get(), CAEntities.FEEDER_PROKARYOTE.get(),
				CAEntities.FIRST_TO_TALK.get(), CAEntities.FISH_SHOOT.get(), CAEntities.FISH_SPLASH.get(), CAEntities.FLEE_FISH.get(), CAEntities.FLEEFISH_BULLET.get(),
				CAEntities.FLOATER_PROKARYOTE.get(), CAEntities.FLY_FISH.get(), CAEntities.GUIDE_ABYSSAL.get(), CAEntities.HIGHMORE.get(), CAEntities.HIGHMORE_SHOOT.get(),
				CAEntities.IZUMIK_OFFSPRING.get(), CAEntities.MEGA_CHEST.get(), CAEntities.OCEANIZED_BRUTE.get(), CAEntities.OCEANIZED_COW.get(), CAEntities.OCEANIZED_DOG.get(),
				CAEntities.OCEANIZED_ENDERMAN.get(), CAEntities.OCEANIZED_HORSE.get(), CAEntities.OCEANIZED_PIG.get(), CAEntities.OCEANIZED_PIGLIN.get(),
				CAEntities.OCEANIZED_PILLAGER.get(), CAEntities.OCEANIZED_RAVAGER.get(), CAEntities.OCEANIZED_SHEEP.get(), CAEntities.OCEANIZED_SPIDER.get(),
				CAEntities.OCEANIZED_VILLAGER.get(), CAEntities.OCEANIZED_VINDICATOR.get(), CAEntities.OCEANIZED_WOLF.get(), CAEntities.OCEANIZED_WITCH.get(),
				CAEntities.PREDATOR_ABYSSAL.get(), CAEntities.PREGNANT_FISH.get(), CAEntities.PUNCTURE_FISH.get(), CAEntities.REAPER_FISH.get(), CAEntities.REAPER_PET.get(),
				CAEntities.ROUTE_FRACTAL.get(), CAEntities.ROUTE_SHAPER.get(), CAEntities.RUN_FISH.get(), CAEntities.SHOOTER_FISH.get(), CAEntities.SHOT_OCEAN_ARROW.get(),
				CAEntities.SKADI.get(), CAEntities.SLIDER_FISH.get(), CAEntities.SONS.get(), CAEntities.SPIKE_CHEST.get(), CAEntities.SPLASHER_ABYSSAL.get(),
				CAEntities.SUPER_SLIDER.get(), CAEntities.TELLER_SHOT.get(), CAEntities.THROWABLE_POTION.get(), CAEntities.TIDE_BISHOP.get(), CAEntities.TIDE_DEATHREPELLER.get(),
				CAEntities.UMBRELLA_ABYSSAL.get(), CAEntities.IZUMIK.get(), CAEntities.NUCLEIC_MALEFICENT.get(), CAEntities.ABANDONED_SHOOT.get(), CAEntities.COMPLEX_CHITIN_GOLEM.get(),
				CAEntities.CORRECTIONAL_PHALAX_VANGUARD.get(), CAEntities.CORRECTIONAL_PHALANXY_INFANTRY.get(), CAEntities.DIVICELLULAR_GO.get(), CAEntities.GUNMU.get(),
				CAEntities.HEAL_BULLLET.get(), CAEntities.JUNIOR_WARRIOR_PRIEST.get(), CAEntities.MARTUS.get(), CAEntities.OCEANIZED_CAT.get(), CAEntities.OCEANIZED_EVOKER.get(),
				CAEntities.OCEANIZED_WARDEN.get(), CAEntities.OCEANIZED_WARDENIS.get(), CAEntities.SUPER_BIG_CAT.get(), CAEntities.THE_ABANDONED.get(), CAEntities.TRIBUNAL_HEALER.get(),
				CAEntities.WARRIOR_PRIEST.get()).filter(DefaultAttributes::hasSupplier).map(entityType -> (EntityType<? extends LivingEntity>) entityType).toList()
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
			newPlayer.getAttribute(SANITY_MODIFIER.get()).setBaseValue(oldPlayer.getAttribute(SANITY_MODIFIER.get()).getBaseValue());
			newPlayer.getAttribute(SANITY_RATE.get()).setBaseValue(oldPlayer.getAttribute(SANITY_RATE.get()).getBaseValue());
			newPlayer.getAttribute(SANITY_INJURY_DAMAGE.get()).setBaseValue(oldPlayer.getAttribute(SANITY_INJURY_DAMAGE.get()).getBaseValue());
			newPlayer.getAttribute(MISSRATE.get()).setBaseValue(oldPlayer.getAttribute(MISSRATE.get()).getBaseValue());
			newPlayer.getAttribute(MAGIC_RESISTANCE.get()).setBaseValue(oldPlayer.getAttribute(MAGIC_RESISTANCE.get()).getBaseValue());
			newPlayer.getAttribute(GENERAL_DEFENSE.get()).setBaseValue(oldPlayer.getAttribute(GENERAL_DEFENSE.get()).getBaseValue());
			newPlayer.getAttribute(NUMB.get()).setBaseValue(oldPlayer.getAttribute(NUMB.get()).getBaseValue());
			newPlayer.getAttribute(SANITY_RESISTANCE.get()).setBaseValue(oldPlayer.getAttribute(SANITY_RESISTANCE.get()).getBaseValue());
			newPlayer.getAttribute(LIVING_BARRIER.get()).setBaseValue(oldPlayer.getAttribute(LIVING_BARRIER.get()).getBaseValue());
		}
	}
}
