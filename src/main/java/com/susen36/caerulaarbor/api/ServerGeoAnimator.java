package com.susen36.caerulaarbor.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
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
import software.bernie.geckolib.model.data.EntityModelData;
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
	 * 驱动一帧 GeckoLib 服务端动画，并返回所有骨骼名 → 模型空间下方块单位绝对位置（未做世界 yaw 旋转，不接受 yaw）。
	 * <p>
	 * 等价于 {@code tickAndGetCurrentPose(tickCount, 0F, false)}，保留用于兼容现有调用。
	 *
	 * @param tickCount Entity 的 tickCount
	 * @return 骨骼名 → 方块单位位置的只读 Map（相对主实体 position，未绕 Y+ 做 yaw 旋转）
	 */
	public Map<String, Vec3> tickAndGetCurrentPose(int tickCount) {
		return tickAndGetCurrentPose(tickCount, 0F, false);
	}

	/**
	 * 驱动一帧 GeckoLib 服务端动画，并返回所有骨骼名 → 模型空间下方块单位绝对位置（未做世界 yaw 旋转，
	 * 但接受 entityYaw 以便之后在外部按实际 yaw 调用 {@link Vec3#yRot(float)} 自行旋转）。
	 * <p>
	 * 等价于 {@code tickAndGetCurrentPose(tickCount, entityYaw, false)}。
	 *
	 * @param tickCount  Entity 的 tickCount
	 * @param entityYaw  实体 getYRot()（度数，本重载内部不使用，仅作为参数保留以便在外部手动
	 *                   {@code vec.yRot((180F - entityYaw) * DEG_TO_RAD)} 时保持签名一致性）
	 * @return 骨骼名 → 方块单位位置的只读 Map（相对主实体 position，未绕 Y+ 做 yaw 旋转）
	 */
	public Map<String, Vec3> tickAndGetCurrentPose(int tickCount, float entityYaw) {
		return tickAndGetCurrentPose(tickCount, entityYaw, false);
	}

	/**
	 * 驱动一帧 GeckoLib 服务端动画，并按重载参数决定是否在 Helper 内部自动处理世界 yaw 旋转。
	 * <p>
	 * 顶层骨骼的 pivot 反向平移（根骨骼归零）始终自动执行（避免业务端被迫写 magic number 手动校准）。
	 *
	 * @param tickCount          Entity 的 tickCount
	 * @param entityYaw          实体 getYRot()（度数，仅 applyWorldRotation=true 时使用）
	 * @param applyWorldRotation {@code true}：在 Helper 内部按 GeckoLib 官方 applyRotations 公式
	 *                           {@code (180F - entityYaw) * DEG_TO_RAD} 自动把所有骨骼 Vec3 绕 Y+ 旋转，
	 *                           Entity 端拿到结果后可直接丢给 tickPart(x,y,z)，无需再 .yRot()；
	 *                           {@code false}：保留模型空间，Entity 端自行决定如何旋转 / 使用
	 * @return 骨骼名 → 方块单位位置的只读 Map（相对主实体 position 原点）
	 */
	public Map<String, Vec3> tickAndGetCurrentPose(int tickCount, float entityYaw, boolean applyWorldRotation) {
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
		state.setData(DataTickets.ENTITY_MODEL_DATA, new EntityModelData(false, false, 0.0F, 0.0F));
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
			// GeckoLib translateAwayFromPivotPoint，自动把 topLevelBone 的 pivot 反向平移归零（-pivot/16）。
			// 为什么要做这一步？渲染端 EntityRenderer 默认会把整个模型抬到 Minecraft 实体 position 之上，
			// 而 topLevelBone（如 "root"）的 pivot 通常会设在 y=10~24 像素区间（Blockbench 原点习惯），
			// 如果不先反向平移 pivot，算出来的所有骨骼 Y 都天然带上了这个"基高"，
			// 业务端（Entity 挂载子实体）就被迫要写死一个 "ROOT_PIVOT_Y_OFFSET = 10F/16F" 的 magic number 手动校准，掩盖设计缺陷。
			// 正确做法：直接复用 RenderUtil 的官方方法（等价于 translate(-pivotX/16, -pivotY/16, -pivotZ/16)），
			// 整个模型空间就自然标准化了，Entity 端拿到的 x/y/z 直接就是「相对主实体 position() 的方块单位偏移」。
			stack.pushPose();
			RenderUtil.translateAwayFromPivotPoint(stack, group);
			calcBoneRecursive(stack, group, bonePos);
			stack.popPose();
		}

		if (applyWorldRotation) {
			// GeckoLib GeoEntityRenderer.applyRotations 的官方公式：
			//  poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));
			// 这里在 Vec3 层做旋转（等价于先绕 Y+ 转 180° - yaw），结果与渲染端逐行一致，
			// 业务端拿到的 x/y/z 直接可用于 tickPart(x, y, z)，不再需要自己 .yRot()。
			float worldRotation = (180.0F - entityYaw) * Mth.DEG_TO_RAD;
			bonePos.replaceAll((name, vec) -> vec.yRot(worldRotation));
		}
		return bonePos;
	}

	/**
	 * 复用 GeckoLib 官方 {@link RenderUtil} 静态方法 + {@link PoseStack} 计算骨骼绝对位置（服务端线程安全可用）。
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

		Vector4f vec = bone.getModelSpaceMatrix().transform(new Vector4f(0F, 0F, 0F, 1.0F));
		result.put(bone.getName(), new Vec3(vec.x(), vec.y(), vec.z()));

		RenderUtil.translateAwayFromPivotPoint(stack, bone);

		for (GeoBone child : bone.getChildBones()) {
			calcBoneRecursive(stack, child, result);
		}

		stack.popPose();
	}

	public AnimationProcessor<T> getAnimationProcessor() {
		ensureInitialized();
		return this.animationProcessor;
	}

	public BakedGeoModel getBakedModel() {
		ensureInitialized();
		return this.bakedModel;
	}
}
