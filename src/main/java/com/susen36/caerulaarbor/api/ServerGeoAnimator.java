package com.susen36.caerulaarbor.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端专用：GeckoLib 骨骼动画 + 模型空间位置计算泛型 Helper。
 * <p>
 * 把原本塞在每个 Geo Entity 里的 5 个字段（model/processor/bakedModel/animTick/lastUpdateTime）
 * 和 3 个方法（ensureInitialized/tickAnimation/calcBoneRecursive）抽出来，后续 Hydra/Leviathan 等
 * 任何 {@code extends Entity} 且实现 {@link GeoAnimatable} 的多段 / 有子实体的实体可直接复用。
 * <p>
 * 核心原则：
 * 1. 双端安全：内部只做纯数学矩阵（PoseStack + RenderUtil），无 GL/无渲染绑定，
 *    服务端 / 客户端（子实体需要时）均可调用；但实际业务调用方建议自行加 level.isClientSide 判定。
 * 2. 100% 官方 API 路径：
 *    - 动画 tick：完全复制 AnimatableManager 的 startedAt/updatedAt + AnimationState.animationTick
 *    - 矩阵顺序：PoseStack push→RenderUtil.* 五个静态方法→pop，与 GeoEntityRenderer.renderRecursively 逐行一致
 *    - 骨骼位置：直接读 modelSpaceMatrix 变换原点，复用 GeoBone.getModelPosition 的 X 翻号（-x, y, z），不手搓 /16
 *
 * @param <T> 目标 Entity 的类型，必须是 Entity 子类且实现 GeoAnimatable（例如 OceanizedEnderDragonEntity）
 */
public class ServerGeoAnimator<T extends Entity & GeoAnimatable> {
	private final T animatable;
	private final GeoModel<T> modelProvider;

	private AnimationProcessor<T> animationProcessor;
	private BakedGeoModel bakedModel;
	private double animTickTime = 0.0;
	private double lastUpdateTime = -1.0;

	public ServerGeoAnimator(T animatable, GeoModel<T> modelProvider) {
		this.animatable = animatable;
		this.modelProvider = modelProvider;
	}

	/**
	 * 懒加载（幂等，可重复调用）：初始化 AnimationProcessor、BakedGeoModel、setActiveModel。
	 */
	private void ensureInitialized() {
		if (this.animationProcessor != null) return;
		AnimationProcessor<T> proc = this.modelProvider.getAnimationProcessor();
		this.animationProcessor = proc;
		BakedGeoModel model = this.modelProvider.getBakedModel(this.modelProvider.getModelResource(this.animatable, null));
		// 第二个参数传 null（@Nullable GeoRenderer<T>）：服务端没有渲染器，官方 GeoModel 默认实现就是单参版本 fallback，所以结果完全一致。
		// 这样可以避免调用 @Deprecated 的单参 getModelResource(T)，消除 IDE 废弃警告。
		this.bakedModel = model;
		proc.setActiveModel(model);
	}

	/**
	 * 驱动一帧 GeckoLib 服务端动画，并返回所有骨骼名 → 模型空间下方块单位绝对位置。
	 * <p>
	 * 建议 Entity 端每 tick 调用一次（aiStep 内），返回的 Map 含整个模型树的所有骨骼，
	 * 业务端按需按子实体名取对应位置即可。
	 *
	 * @param tickCount Entity 的 tickCount（用于 startedAt/updatedAt，保证不跳帧）
	 * @return 骨骼名 → 方块单位位置的只读 Map（若未初始化 / manager 缺失返回空 Map）
	 */
	public Map<String, Vec3> tickAndGetCurrentPose(int tickCount) {
		ensureInitialized();
		float partialTick = 1.0F;
		AnimatableInstanceCache cache = this.animatable.getAnimatableInstanceCache();
		if (cache == null) return Collections.emptyMap();
		AnimatableManager<T> manager = cache.getManagerForId(this.animatable.getId());
		if (manager == null) return Collections.emptyMap();

        double currentFrameTime = (double) tickCount + partialTick;
		if (manager.getFirstTickTime() == -1) manager.startedAt(currentFrameTime);
		manager.updatedAt(currentFrameTime);
		double lastUpdate = manager.getLastUpdateTime();
		if (this.lastUpdateTime < 0) this.lastUpdateTime = lastUpdate;
		this.animTickTime += lastUpdate - this.lastUpdateTime;
		this.lastUpdateTime = lastUpdate;

		AnimationState<T> state = new AnimationState<>(this.animatable, 0F, 0F, partialTick, false);
		state.setData(DataTickets.TICK, (double) tickCount);
		state.setData(DataTickets.ENTITY, this.animatable);
		state.animationTick = this.animTickTime;
		AnimationProcessor<T> proc = this.animationProcessor;
		Collection<GeoBone> bones = proc.getRegisteredBones();

		bones.forEach(GeoBone::resetStateChanges);
		proc.preAnimationSetup(state, this.animTickTime);
		if (!bones.isEmpty()) {
			proc.tickAnimation(this.animatable, this.modelProvider, manager, this.animTickTime, state, false);
		}
		this.modelProvider.setCustomAnimations(this.animatable, this.animatable.getId(), state);

		Map<String, Vec3> bonePos = new HashMap<>();
		PoseStack stack = new PoseStack();
		for (GeoBone group : this.bakedModel.topLevelBones()) {
			calcBoneRecursive(stack, group, bonePos);
		}
		return bonePos;
	}

	/**
	 * 100% 复用 GeckoLib 官方 {@link RenderUtil} 静态方法 + {@link PoseStack} 计算骨骼绝对位置（服务端线程安全可用）。
	 * <p>
	 * 流程与 GeckoLib 渲染端 GeoEntityRenderer.renderRecursively 的矩阵累积与写入顺序完全逐行一致：
	 * <pre>
	 * 1. pushPose()
	 * 2. RenderUtil.translateMatrixToBone(stack, bone)
	 * 3. RenderUtil.translateToPivotPoint(stack, bone)
	 * 4. RenderUtil.rotateMatrixAroundBone(stack, bone)
	 * 5. RenderUtil.scaleMatrixForBone(stack, bone)
	 *    ┗━ 此时 stack 就是 pivot 点对应的全局 poseState → 立刻 bone.setModelSpaceMatrix()
	 * 6. 读 GeoBone.getModelSpaceMatrix() 变换原点，复用 GeoBone.getModelPosition 的 X 翻号（-x, y, z），不做最后一步 ×16 → 天然方块单位，无手搓 /16
	 * 7. RenderUtil.translateAwayFromPivotPoint(stack, bone)（供 child 骨骼继承）
	 * 8. 递归 child 骨骼
	 * 9. popPose()
	 * </pre>
	 * 全程无手搓矩阵、无手搓符号/单位换算、无手搓欧拉顺序。
	 */
	private static void calcBoneRecursive(PoseStack stack, GeoBone bone, Map<String, Vec3> result) {
		stack.pushPose();
		RenderUtil.translateMatrixToBone(stack, bone);
		RenderUtil.translateToPivotPoint(stack, bone);
		RenderUtil.rotateMatrixAroundBone(stack, bone);
		RenderUtil.scaleMatrixForBone(stack, bone);

		// 与渲染端同一时机（translateAwayFromPivot 之前）写入 modelSpaceMatrix
		Matrix4f poseState = new Matrix4f(stack.last().pose());
		bone.setModelSpaceMatrix(poseState);

		// 复用 GeoBone.getModelPosition() 前半段官方逻辑，不执行最后一步 *16f → 直接得到 Minecraft 方块单位
		Vector4f vec = bone.getModelSpaceMatrix().transform(new Vector4f(0F, 0F, 0F, 1F));
		result.put(bone.getName(), new Vec3(-vec.x(), vec.y(), vec.z()));

		RenderUtil.translateAwayFromPivotPoint(stack, bone);

		for (GeoBone child : bone.getChildBones()) {
			calcBoneRecursive(stack, child, result);
		}

		stack.popPose();
	}

	// === 供业务端的便捷访问（可选，按需扩展） ===

	public AnimationProcessor<T> getAnimationProcessor() {
		ensureInitialized();
		return this.animationProcessor;
	}

	public BakedGeoModel getBakedModel() {
		ensureInitialized();
		return this.bakedModel;
	}
}
