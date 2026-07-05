/*
 *    MCreator 注：此文件会在每次构建时重新生成。
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.CaerulaArborMod;
import com.apocalypse.caerulaarbor.client.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CAParticles {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CaerulaArborMod.MODID);
	
	public static final RegistryObject<SimpleParticleType> LIFELOSS = REGISTRY.register("lifeloss", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> SHIELDLOSS = REGISTRY.register("shieldloss", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> BLOODOOZE = REGISTRY.register("bloodooze", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> KING_SLAY = REGISTRY.register("king_slay", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> KING_SLAY_RED = REGISTRY.register("king_slay_red", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> ARCHFIEND_KEEP = REGISTRY.register("archfiend_keep", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> ARCHFIEND_RESEV = REGISTRY.register("archfiend_resev", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> DIZZINESS = REGISTRY.register("dizziness", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> KNIFEPTC = REGISTRY.register("knifeptc", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MISS = REGISTRY.register("miss", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> CRACKER_BUFF_0 = REGISTRY.register("cracker_buff_0", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> CRACKER_BUFF_1 = REGISTRY.register("cracker_buff_1", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> INV_PTC = REGISTRY.register("inv_ptc", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MUTENESS = REGISTRY.register("muteness", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> INV_PTC_BLUE = REGISTRY.register("inv_ptc_blue", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> INV_PTC_VOILET = REGISTRY.register("inv_ptc_voilet", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> PURPLE_FLAME = REGISTRY.register("purple_flame", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> EDERMAN_PTC = REGISTRY.register("ederman_ptc", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> NUMBNESS = REGISTRY.register("numbness", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MARTUS_CHARS = REGISTRY.register("martus_chars", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> ENDSPEAKER_PARTICLE = REGISTRY.register("endspeaker_particle", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> ENDSPEAKER_INV = REGISTRY.register("endspeaker_inv", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> SEA_SPLASH = REGISTRY.register("sea_splash", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> SEA_RIPPLE = REGISTRY.register("sea_ripple", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> SPECTER_GLITTER = REGISTRY.register("specter_glitter", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> SPECTER_CHARS = REGISTRY.register("specter_chars", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> IMMORTAL_PTC = REGISTRY.register("immortal_ptc", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> CORRUPTED_FISH = REGISTRY.register("corrupted_fish", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> ISHARMLA_CURSE_PARTICLE = REGISTRY.register("isharmla_curse_particle", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> BULLETS = REGISTRY.register("bullets", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> MOIST_BOOM = REGISTRY.register("moist_boom", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> LIVING_BARRIER_SHOW = REGISTRY.register("living_barrier_show", () -> new SimpleParticleType(false));

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class CAParticleType {
		@SubscribeEvent
		public static void registerParticles(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(CAParticles.LIFELOSS.get(), LifelossParticle::provider);
			event.registerSpriteSet(CAParticles.SHIELDLOSS.get(), ShieldlossParticle::provider);
			event.registerSpriteSet(CAParticles.BLOODOOZE.get(), BloodoozeParticle::provider);
			event.registerSpriteSet(CAParticles.KING_SLAY.get(), KingSlayParticle::provider);
			event.registerSpriteSet(CAParticles.KING_SLAY_RED.get(), KingSlayRedParticle::provider);
			event.registerSpriteSet(CAParticles.ARCHFIEND_KEEP.get(), ArchfiendKeepParticle::provider);
			event.registerSpriteSet(CAParticles.ARCHFIEND_RESEV.get(), ArchfiendResevParticle::provider);
			event.registerSpriteSet(CAParticles.DIZZINESS.get(), DizzinessParticle::provider);
			event.registerSpriteSet(CAParticles.KNIFEPTC.get(), KnifeptcParticle::provider);
			event.registerSpriteSet(CAParticles.MISS.get(), MissParticle::provider);
			event.registerSpriteSet(CAParticles.CRACKER_BUFF_0.get(), CrackerBuff0Particle::provider);
			event.registerSpriteSet(CAParticles.CRACKER_BUFF_1.get(), CrackerBuff1Particle::provider);
			event.registerSpriteSet(CAParticles.INV_PTC.get(), InvPtcParticle::provider);
			event.registerSpriteSet(CAParticles.MUTENESS.get(), MutenessParticle::provider);
			event.registerSpriteSet(CAParticles.INV_PTC_BLUE.get(), InvPtcBlueParticle::provider);
			event.registerSpriteSet(CAParticles.INV_PTC_VOILET.get(), InvPtcVoiletParticle::provider);
			event.registerSpriteSet(CAParticles.PURPLE_FLAME.get(), PurpleFlameParticle::provider);
			event.registerSpriteSet(CAParticles.EDERMAN_PTC.get(), EdermanPtcParticle::provider);
			event.registerSpriteSet(CAParticles.NUMBNESS.get(), NumbnessParticle::provider);
			event.registerSpriteSet(CAParticles.MARTUS_CHARS.get(), MartusCharsParticle::provider);
			event.registerSpriteSet(CAParticles.ENDSPEAKER_PARTICLE.get(), EndspeakerParticleParticle::provider);
			event.registerSpriteSet(CAParticles.ENDSPEAKER_INV.get(), EndspeakerInvParticle::provider);
			event.registerSpriteSet(CAParticles.SEA_SPLASH.get(), SeaSplashParticle::provider);
			event.registerSpriteSet(CAParticles.SEA_RIPPLE.get(), SeaRippleParticle::provider);
			event.registerSpriteSet(CAParticles.SPECTER_GLITTER.get(), SpecterGlitterParticle::provider);
			event.registerSpriteSet(CAParticles.SPECTER_CHARS.get(), SpecterCharsParticle::provider);
			event.registerSpriteSet(CAParticles.IMMORTAL_PTC.get(), ImmortalPtcParticle::provider);
			event.registerSpriteSet(CAParticles.CORRUPTED_FISH.get(), CorruptedFishParticle::provider);
			event.registerSpriteSet(CAParticles.ISHARMLA_CURSE_PARTICLE.get(), IsharmlaCurseParticleParticle::provider);
			event.registerSpriteSet(CAParticles.BULLETS.get(), BulletsParticle::provider);
			event.registerSpriteSet(CAParticles.MOIST_BOOM.get(), MoistBoomParticle::provider);
			event.registerSpriteSet(CAParticles.LIVING_BARRIER_SHOW.get(), LivingBarrierShowParticle::provider);
		}
	}
}
