package net.createmod.ponder.client;

/**
 * Shared flag for tracking when AbstractSimiScreen is inside renderWindowBackground.
 * Used to prevent duplicate blur calls in MC 1.21.8+.
 */
public class BlurFlag {
	private static final ThreadLocal<Boolean> INSIDE_RENDER_WINDOW_BG = ThreadLocal.withInitial(() -> false);

	public static void setInsideRenderWindowBackground(boolean value) {
		INSIDE_RENDER_WINDOW_BG.set(value);
	}

	public static boolean isInsideRenderWindowBackground() {
		return INSIDE_RENDER_WINDOW_BG.get();
	}
}
