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
public class CaerulaArborModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(CaerulaArborModParticleTypes.LIFELOSS.get(), LifelossParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.SHIELDLOSS.get(), ShieldlossParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.BLOODOOZE.get(), BloodoozeParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.KING_SLAY.get(), KingSlayParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.KING_SLAY_RED.get(), KingSlayRedParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.ARCHFIEND_KEEP.get(), ArchfiendKeepParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.ARCHFIEND_RESEV.get(), ArchfiendResevParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.DIZZINESS.get(), DizzinessParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.KNIFEPTC.get(), KnifeptcParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.MISS.get(), MissParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.CRACKER_BUFF_0.get(), CrackerBuff0Particle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.CRACKER_BUFF_1.get(), CrackerBuff1Particle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.INV_PTC.get(), InvPtcParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.MUTENESS.get(), MutenessParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.INV_PTC_BLUE.get(), InvPtcBlueParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.INV_PTC_VOILET.get(), InvPtcVoiletParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.PURPLE_FLAME.get(), PurpleFlameParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.EDERMAN_PTC.get(), EdermanPtcParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.NUMBNESS.get(), NumbnessParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.MARTUS_CHARS.get(), MartusCharsParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.ENDSPEAKER_PARTICLE.get(), EndspeakerParticleParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.ENDSPEAKER_INV.get(), EndspeakerInvParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.SEA_SPLASH.get(), SeaSplashParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.SPECTER_GLITTER.get(), SpecterGlitterParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.SPECTER_CHARS.get(), SpecterCharsParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.IMMORTAL_PTC.get(), ImmortalPtcParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.CORRUPTED_FISH.get(), CorruptedFishParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.ISHARMLA_CURSE_PARTICLE.get(), IsharmlaCurseParticleParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.BULLETS.get(), BulletsParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.MOIST_BOOM.get(), MoistBoomParticle::provider);
		event.registerSpriteSet(CaerulaArborModParticleTypes.LIVING_BARRIER_SHOW.get(), LivingBarrierShowParticle::provider);
	}
}
