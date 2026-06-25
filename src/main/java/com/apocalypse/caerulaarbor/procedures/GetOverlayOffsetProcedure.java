package com.apocalypse.caerulaarbor.procedures;

import com.apocalypse.caerulaarbor.configuration.CaerulaConfigsConfiguration;


public class GetOverlayOffsetProcedure {
	public static int x() {
		return Math.toIntExact(Math.round(CaerulaConfigsConfiguration.X_OFFSET.get()));
	}
	public static int y(){
		return Math.toIntExact(Math.round(CaerulaConfigsConfiguration.Y_OFFSET.get()));
	}
}
//TODO:修改为面向对象，但是是独立方法