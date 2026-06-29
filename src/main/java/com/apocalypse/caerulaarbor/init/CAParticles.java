/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.apocalypse.caerulaarbor.init;

import com.apocalypse.caerulaarbor.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CAParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(CAParticleTypes.LIFELOSS.get(), LifelossParticle::provider);
		event.registerSpriteSet(CAParticleTypes.SHIELDLOSS.get(), ShieldlossParticle::provider);
		event.registerSpriteSet(CAParticleTypes.BLOODOOZE.get(), BloodoozeParticle::provider);
		event.registerSpriteSet(CAParticleTypes.KING_SLAY.get(), KingSlayParticle::provider);
		event.registerSpriteSet(CAParticleTypes.KING_SLAY_RED.get(), KingSlayRedParticle::provider);
		event.registerSpriteSet(CAParticleTypes.ARCHFIEND_KEEP.get(), ArchfiendKeepParticle::provider);
		event.registerSpriteSet(CAParticleTypes.ARCHFIEND_RESEV.get(), ArchfiendResevParticle::provider);
		event.registerSpriteSet(CAParticleTypes.DIZZINESS.get(), DizzinessParticle::provider);
		event.registerSpriteSet(CAParticleTypes.KNIFEPTC.get(), KnifeptcParticle::provider);
		event.registerSpriteSet(CAParticleTypes.MISS.get(), MissParticle::provider);
		event.registerSpriteSet(CAParticleTypes.CRACKER_BUFF_0.get(), CrackerBuff0Particle::provider);
		event.registerSpriteSet(CAParticleTypes.CRACKER_BUFF_1.get(), CrackerBuff1Particle::provider);
		event.registerSpriteSet(CAParticleTypes.INV_PTC.get(), InvPtcParticle::provider);
		event.registerSpriteSet(CAParticleTypes.MUTENESS.get(), MutenessParticle::provider);
		event.registerSpriteSet(CAParticleTypes.INV_PTC_BLUE.get(), InvPtcBlueParticle::provider);
		event.registerSpriteSet(CAParticleTypes.INV_PTC_VOILET.get(), InvPtcVoiletParticle::provider);
		event.registerSpriteSet(CAParticleTypes.PURPLE_FLAME.get(), PurpleFlameParticle::provider);
		event.registerSpriteSet(CAParticleTypes.EDERMAN_PTC.get(), EdermanPtcParticle::provider);
		event.registerSpriteSet(CAParticleTypes.NUMBNESS.get(), NumbnessParticle::provider);
		event.registerSpriteSet(CAParticleTypes.MARTUS_CHARS.get(), MartusCharsParticle::provider);
		event.registerSpriteSet(CAParticleTypes.ENDSPEAKER_PARTICLE.get(), EndspeakerParticleParticle::provider);
		event.registerSpriteSet(CAParticleTypes.ENDSPEAKER_INV.get(), EndspeakerInvParticle::provider);
		event.registerSpriteSet(CAParticleTypes.SEA_SPLASH.get(), SeaSplashParticle::provider);
		event.registerSpriteSet(CAParticleTypes.SEA_RIPPLE.get(), SeaRippleParticle::provider);
		event.registerSpriteSet(CAParticleTypes.SPECTER_GLITTER.get(), SpecterGlitterParticle::provider);
		event.registerSpriteSet(CAParticleTypes.SPECTER_CHARS.get(), SpecterCharsParticle::provider);
		event.registerSpriteSet(CAParticleTypes.IMMORTAL_PTC.get(), ImmortalPtcParticle::provider);
		event.registerSpriteSet(CAParticleTypes.CORRUPTED_FISH.get(), CorruptedFishParticle::provider);
		event.registerSpriteSet(CAParticleTypes.ISHARMLA_CURSE_PARTICLE.get(), IsharmlaCurseParticleParticle::provider);
		event.registerSpriteSet(CAParticleTypes.BULLETS.get(), BulletsParticle::provider);
		event.registerSpriteSet(CAParticleTypes.MOIST_BOOM.get(), MoistBoomParticle::provider);
		event.registerSpriteSet(CAParticleTypes.LIVING_BARRIER_SHOW.get(), LivingBarrierShowParticle::provider);
	}
}
