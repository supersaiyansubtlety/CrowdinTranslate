package net.sssubtlety.crowdin_translate_sss.plugin;

public class CrowdinTranslateExtension {
    String crowdinProjectName;
    String minecraftProjectName;
    String jsonSourceName;
    boolean verbose;
    
    public void setCrowdinProjectname(String s) {
        crowdinProjectName = s;
    }
    
    public String getCrowdinProjectName() {
        return crowdinProjectName;
    }
    
    public void setMinecraftProjectName(String s) {
        minecraftProjectName = s;
    }
    
    public String getMinecraftProjectName() {
        return minecraftProjectName;
    }
    
    public void setJsonSourceName(String s) {
        jsonSourceName = s;
    }
    
    public String getJsonSourceName() {
        return jsonSourceName;
    }
    
    public void setVerbose(boolean b) {
        verbose = b;
    }
    
    public boolean getVerbose() {
        return verbose;
    }
}
