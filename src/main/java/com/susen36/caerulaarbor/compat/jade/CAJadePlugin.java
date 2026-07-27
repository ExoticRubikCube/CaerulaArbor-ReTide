package com.susen36.caerulaarbor.compat.jade;

import com.susen36.caerulaarbor.block.BlockKettleBlock;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CAJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(CASanityProvider.INSTANCE, LivingEntity.class);
        registration.registerEntityComponent(CAAttributeProvider.INSTANCE, LivingEntity.class);
        registration.registerEntityComponent(CAMissNumbProvider.INSTANCE, LivingEntity.class);
        registration.registerEntityComponent(CABarrierProvider.INSTANCE, LivingEntity.class);
        registration.registerBlockComponent(CAHotKettleProvider.INSTANCE, BlockKettleBlock.class);
    }
}