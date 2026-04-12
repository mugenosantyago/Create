package net.minecraft.gametest.framework;

import java.util.function.Consumer;

import net.minecraft.world.level.block.Rotation;

/**
 * Stub for legacy {@code TestFunction} usage during the MC 1.21.8 port (Create's
 * {@link com.simibubi.create.infrastructure.gametest.CreateTestFunction}).
 */
public class TestFunction {
	private final String batchName;
	private final String name;
	private final String templateName;
	private final Rotation rotation;
	private final int maxTicks;
	private final long setupTicks;
	private final boolean required;
	private final boolean manualOnly;
	private final int maxAttempts;
	private final int requiredSuccesses;
	private final boolean skyAccess;
	private final Consumer<GameTestHelper> function;

	public TestFunction(
		String batchName,
		String name,
		String templateName,
		Rotation rotation,
		int maxTicks,
		long setupTicks,
		boolean required,
		boolean manualOnly,
		int maxAttempts,
		int requiredSuccesses,
		boolean skyAccess,
		Consumer<GameTestHelper> function
	) {
		this.batchName = batchName;
		this.name = name;
		this.templateName = templateName;
		this.rotation = rotation;
		this.maxTicks = maxTicks;
		this.setupTicks = setupTicks;
		this.required = required;
		this.manualOnly = manualOnly;
		this.maxAttempts = maxAttempts;
		this.requiredSuccesses = requiredSuccesses;
		this.skyAccess = skyAccess;
		this.function = function;
	}

	public String testName() {
		return name;
	}

	public String getTestName() {
		return name;
	}

	public String getTemplateName() {
		return templateName;
	}

	public Consumer<GameTestHelper> getFunction() {
		return function;
	}
}
