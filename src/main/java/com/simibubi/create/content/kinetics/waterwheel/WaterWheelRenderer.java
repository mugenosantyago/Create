package com.simibubi.create.content.kinetics.waterwheel;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BlockStateModelUtil;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.StitchedSprite;
import net.createmod.catnip.render.SuperBufferFactory;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WaterWheelRenderer<T extends WaterWheelBlockEntity> extends KineticBlockEntityRenderer<T> {
	public static final SuperByteBufferCache.Compartment<ModelKey> WATER_WHEEL = new SuperByteBufferCache.Compartment<>();

	public static final StitchedSprite OAK_PLANKS_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_planks"));
	public static final StitchedSprite OAK_LOG_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log"));
	public static final StitchedSprite OAK_LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace("block/oak_log_top"));

	protected final boolean large;

	public WaterWheelRenderer(Context context, boolean large) {
		super(context);
		this.large = large;
	}

	public static <T extends WaterWheelBlockEntity> WaterWheelRenderer<T> standard(Context context) {
		return new WaterWheelRenderer<>(context, false);
	}

	public static <T extends WaterWheelBlockEntity> WaterWheelRenderer<T> large(Context context) {
		return new WaterWheelRenderer<>(context, true);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(T be, BlockState state) {
		ModelKey key = new ModelKey(large, state, be.material);
		return SuperByteBufferCache.getInstance().get(WATER_WHEEL, key, () -> {
			BlockStateModel model = generateModel(key);
			BlockState state1 = key.state();
			Direction dir;
			if (key.large()) {
				dir = Direction.fromAxisAndDirection(state1.getValue(LargeWaterWheelBlock.AXIS), AxisDirection.POSITIVE);
			} else {
				dir = state1.getValue(WaterWheelBlock.FACING);
			}
			PoseStack transform = CachedBuffers.rotateToFaceVertical(dir).get();
			return SuperBufferFactory.getInstance().createForBlock(model, Blocks.AIR.defaultBlockState(), transform);
		});
	}

	public static BlockStateModel generateModel(ModelKey key) {
		return generateModel(Variant.of(key.large(), key.state()), key.material());
	}

	public static BlockStateModel generateModel(Variant variant, BlockState material) {
		return generateModel(variant.model(), material);
	}

	public static BlockStateModel generateModel(BlockStateModel template, BlockState planksBlockState) {
		Block planksBlock = planksBlockState.getBlock();
		ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(planksBlock);
		String wood = plankStateToWoodName(planksBlockState);

		if (wood == null)
			return BakedModelHelper.generateModel(template, sprite -> null);

		String namespace = id.getNamespace();
		BlockState logBlockState = getLogBlockState(namespace, wood);

		Map<TextureAtlasSprite, TextureAtlasSprite> map = new Reference2ReferenceOpenHashMap<>();
		map.put(OAK_PLANKS_TEMPLATE.get(), getSpriteOnSide(planksBlockState, Direction.UP));
		map.put(OAK_LOG_TEMPLATE.get(), getSpriteOnSide(logBlockState, Direction.SOUTH));
		map.put(OAK_LOG_TOP_TEMPLATE.get(), getSpriteOnSide(logBlockState, Direction.UP));

		return BakedModelHelper.generateModel(template, map::get);
	}

	@Nullable
	private static String plankStateToWoodName(BlockState planksBlockState) {
		Block planksBlock = planksBlockState.getBlock();
		ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(planksBlock);
		String path = id.getPath();

		if (path.endsWith("_planks")) // Covers most wood types
			return (path.startsWith("archwood") ? "blue_" : "") + path.substring(0, path.length() - 7);

		if (path.contains("wood/planks/")) // TerraFirmaCraft
			return path.substring(12);

		// Handle vanilla wood types (oak, spruce, birch, jungle, acacia, dark_oak, etc.)
		if (path.equals("oak_planks") || path.equals("spruce_planks") || 
			path.equals("birch_planks") || path.equals("jungle_planks") ||
			path.equals("acacia_planks") || path.equals("dark_oak_planks") ||
			path.equals("mangrove_planks") || path.equals("cherry_planks") ||
			path.equals("bamboo_planks") || path.equals("crimson_planks") ||
			path.equals("warped_planks"))
			return path.substring(0, path.length() - 7); // Remove "_planks" suffix

		return null;
	}

	private static final String[] LOG_LOCATIONS = new String[] {

		"x_log", "x_stem", "x_block", // Covers most wood types
		"wood/log/x", // TerraFirmaCraft
		"oak_log", "spruce_log", "birch_log", "jungle_log", // Vanilla wood types
		"acacia_log", "dark_oak_log", "mangrove_log", "cherry_log",
		"bamboo_block", "crimson_stem", "warped_stem"

	};

	private static BlockState getLogBlockState(String namespace, String wood) {
		// First try the pattern-based locations
		for (String location : LOG_LOCATIONS) {
			Block block = BuiltInRegistries.BLOCK.getValue(ResourceLocation.fromNamespaceAndPath(namespace, location.replace("x", wood)));
			if (block != null)
				return block.defaultBlockState();
		}
		
		// Fallback to vanilla log naming convention
		Block vanillaLog = BuiltInRegistries.BLOCK.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", wood + "_log"));
		if (vanillaLog != null)
			return vanillaLog.defaultBlockState();
			
		return Blocks.OAK_LOG.defaultBlockState();
	}

	private static TextureAtlasSprite getSpriteOnSide(BlockState state, Direction side) {
		BlockStateModel model = Minecraft.getInstance()
			.getBlockRenderer()
			.getBlockModel(state);
		if (model == null)
			return null;
		RandomSource random = RandomSource.create();
		random.setSeed(42L);
		List<BakedQuad> quads = BlockStateModelUtil.collectQuads(model, state, side, random);
		if (!quads.isEmpty()) {
			return quads.get(0)
				.sprite();
		}
		random.setSeed(42L);
		quads = BlockStateModelUtil.collectQuads(model, state, null, random);
		if (!quads.isEmpty()) {
			for (BakedQuad quad : quads) {
				if (quad.direction() == side) {
					return quad.sprite();
				}
			}
		}
		return model.particleIcon();
	}

	public enum Variant {
		SMALL(AllPartialModels.WATER_WHEEL),
		LARGE(AllPartialModels.LARGE_WATER_WHEEL),
		LARGE_EXTENSION(AllPartialModels.LARGE_WATER_WHEEL_EXTENSION),
		;

		private final PartialModel partial;

		Variant(PartialModel partial) {
			this.partial = partial;
		}

		public BlockStateModel model() {
			return partial.get();
		}

		public static Variant of(boolean large, BlockState blockState) {
			if (large) {
				boolean extension = blockState.getValue(LargeWaterWheelBlock.EXTENSION);
				if (extension) {
					return LARGE_EXTENSION;
				} else {
					return LARGE;
				}
			} else {
				return SMALL;
			}
		}
	}

	public record ModelKey(boolean large, BlockState state, BlockState material) {
	}
}
