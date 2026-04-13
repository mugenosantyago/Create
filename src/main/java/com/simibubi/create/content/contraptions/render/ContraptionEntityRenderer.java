package com.simibubi.create.content.contraptions.render;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ClientContraption.RenderedBlocks;
import com.simibubi.create.foundation.render.BlockEntityRenderHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.ShadedBlockSbbBuilder;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Renders contraption entities (moving structures).
 *
 * <p>In 1.21.2+, {@link EntityRenderer} uses the render-state pattern. The entity's contraption
 * data is extracted once per frame in {@link #extractRenderState} and consumed in {@link #render}.
 */
public class ContraptionEntityRenderer<C extends AbstractContraptionEntity>
        extends EntityRenderer<C, ContraptionEntityRenderer.ContraptionRenderState> {
	/**
	 * Layers used for world/chunk-style block rendering. Replaces removed {@code RenderType.chunkBufferLayers()}.
	 */
	public static final List<RenderType> CHUNK_BUFFER_RENDER_TYPES = List.of(
			RenderType.SOLID,
			RenderType.CUTOUT_MIPPED,
			RenderType.CUTOUT,
			RenderType.TRANSLUCENT_MOVING_BLOCK,
			RenderType.TRIPWIRE);

	private static final Map<RenderType, ChunkSectionLayer> RENDER_TYPE_TO_CHUNK_LAYER = Map.of(
			RenderType.SOLID, ChunkSectionLayer.SOLID,
			RenderType.CUTOUT_MIPPED, ChunkSectionLayer.CUTOUT_MIPPED,
			RenderType.CUTOUT, ChunkSectionLayer.CUTOUT,
			RenderType.TRANSLUCENT_MOVING_BLOCK, ChunkSectionLayer.TRANSLUCENT,
			RenderType.TRIPWIRE, ChunkSectionLayer.TRIPWIRE);

	public static final SuperByteBufferCache.Compartment<Pair<Contraption, RenderType>> CONTRAPTION = new SuperByteBufferCache.Compartment<>();
	private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);

	public ContraptionEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ContraptionRenderState createRenderState() {
		return new ContraptionRenderState();
	}

	@Override
	public void extractRenderState(C entity, ContraptionRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		// Contraption rendering still reads matrices and BE views from the live entity.
		state.entity = entity;
	}

	public static SuperByteBuffer getBuffer(Contraption contraption, VirtualRenderWorld renderWorld, RenderType renderType) {
		return SuperByteBufferCache.getInstance().get(CONTRAPTION, Pair.of(contraption, renderType), () -> buildStructureBuffer(contraption, renderWorld, renderType));
	}

	private static SuperByteBuffer buildStructureBuffer(Contraption contraption, VirtualRenderWorld renderWorld, RenderType layer) {
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		ModelBlockRenderer renderer = dispatcher.getModelRenderer();
		ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();

		PoseStack poseStack = objects.poseStack;
		RandomSource random = objects.random;
		var clientContraption = contraption.getOrCreateClientContraptionLazy();
		RenderedBlocks blocks = clientContraption.getRenderedBlocks();

		ShadedBlockSbbBuilder sbbBuilder = objects.sbbBuilder;
		sbbBuilder.begin();

		ChunkSectionLayer targetLayer = RENDER_TYPE_TO_CHUNK_LAYER.get(layer);

		ModelBlockRenderer.enableCaching();
		if (targetLayer != null) {
			for (BlockPos pos : blocks.positions()) {
				BlockState state = blocks.lookup().apply(pos);
				if (state.getRenderShape() != RenderShape.MODEL) {
					continue;
				}
				if (!ItemBlockRenderTypes.getChunkRenderType(state).equals(targetLayer)) {
					continue;
				}

				BlockStateModel model = dispatcher.getBlockModel(state);
				List<BlockModelPart> parts = model.collectParts(renderWorld, pos, state, random);
				if (parts.isEmpty()) {
					continue;
				}

				long randomSeed = state.getSeed(pos);
				random.setSeed(randomSeed);
				poseStack.pushPose();
				poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
				renderer.tesselateBlock(renderWorld, parts, state, pos, poseStack, sbbBuilder, true, OverlayTexture.NO_OVERLAY);
				poseStack.popPose();
			}
		}
		ModelBlockRenderer.clearCache();

		return sbbBuilder.end();
	}

	@Override
	public boolean shouldRender(C entity, Frustum frustum, double cameraX, double cameraY,
		double cameraZ) {
		if (entity.getContraption() == null)
			return false;
		if (!entity.isAliveOrStale())
			return false;
		if (!entity.isReadyForRender())
			return false;

		return super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ);
	}

	@Override
	public void render(ContraptionRenderState state, PoseStack poseStack, MultiBufferSource buffers, int light) {
		super.render(state, poseStack, buffers, light);

		C entity = (C) state.entity;
		if (entity == null)
			return;

		Contraption contraption = entity.getContraption();
		if (contraption == null) {
			return;
		}

		Level level = entity.level();
		ClientContraption clientContraption = contraption.getOrCreateClientContraptionLazy();
		VirtualRenderWorld renderWorld = clientContraption.getRenderLevel();
		ContraptionMatrices matrices = clientContraption.getMatrices();
		matrices.setup(poseStack, entity);

		if (!VisualizationManager.supportsVisualization(level)) {
			for (RenderType renderType : CHUNK_BUFFER_RENDER_TYPES) {
				SuperByteBuffer sbb = getBuffer(contraption, renderWorld, renderType);
				if (!sbb.isEmpty()) {
					VertexConsumer vc = buffers.getBuffer(renderType);
					sbb.transform(matrices.getModel())
						.useLevelLight(level, matrices.getWorld())
						.renderInto(poseStack, vc);
				}
			}
		}

		var adjustRenderedBlockEntities = clientContraption.getAndAdjustShouldRenderBlockEntities();

		clientContraption.scratchErroredBlockEntities.clear();

		BlockEntityRenderHelper.renderBlockEntities(clientContraption.renderedBlockEntityView, adjustRenderedBlockEntities, clientContraption.scratchErroredBlockEntities, renderWorld, level, matrices.getModelViewProjection(), matrices.getLight(), buffers, AnimationTickHolder.getPartialTicks());

		clientContraption.shouldRenderBlockEntities.andNot(clientContraption.scratchErroredBlockEntities);
		renderActors(level, renderWorld, contraption, matrices, buffers);

		matrices.clear();
	}

	private static void renderActors(Level level, VirtualRenderWorld renderWorld, Contraption c,
		ContraptionMatrices matrices, MultiBufferSource buffer) {
		PoseStack m = matrices.getModel();

		for (Pair<StructureTemplate.StructureBlockInfo, MovementContext> actor : c.getActors()) {
			MovementContext context = actor.getRight();
			if (context == null)
				continue;
			if (context.world == null)
				context.world = level;
			StructureTemplate.StructureBlockInfo blockInfo = actor.getLeft();

			MovementBehaviour movementBehaviour = MovementBehaviour.REGISTRY.get(blockInfo.state());
			if (movementBehaviour != null) {
				if (c.isHiddenInPortal(blockInfo.pos()))
					continue;
				m.pushPose();
				TransformStack.of(m)
					.translate(blockInfo.pos());
				movementBehaviour.renderInContraption(context, renderWorld, matrices, buffer);
				m.popPose();
			}
		}
	}

	/** Render state for contraption entities (entity retained for contraption/mesh access). */
	public static class ContraptionRenderState extends EntityRenderState {
		public AbstractContraptionEntity entity;
	}

	private static class ThreadLocalObjects {
		public final PoseStack poseStack = new PoseStack();
		public final RandomSource random = RandomSource.createNewThreadLocalInstance();
		public final ShadedBlockSbbBuilder sbbBuilder = ShadedBlockSbbBuilder.create();
	}
}
