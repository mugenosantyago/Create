package net.minecraft.gametest.framework;

/** Stub for removed TestFunction (MC 1.21.8 port) */
public class TestFunction {
    private final String testName;
    private final String templateName;
    
    public TestFunction(String batchName, String testName, String templateName, int maxTicks, int setupTicks, boolean required, Runnable test) {
        this.testName = testName;
        this.templateName = templateName;
    }
    
    public String getTestName() { return testName; }
    public String getTemplateName() { return templateName; }
}
