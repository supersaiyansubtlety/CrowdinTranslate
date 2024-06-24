package net.sssubtlety.crowdin_translate_sss.plugin;

import net.sssubtlety.crowdin_translate_sss.base.CrowdinTranslateSss;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DownloadTask extends DefaultTask {
    @TaskAction
    public void action() {
        CrowdinTranslateSssExtension parms = CrowdinTranslateSssPlugin.parameters;
        if (parms.getCrowdinProjectName() == null) {
            System.err.println("No crowdin project name given, nothing downloaded");
            return;
        }
        String[] args = new String[ (parms.getVerbose() ? 4 : 3) ];
        int argc = 0;
        if (parms.getVerbose()) {
            args[argc++] = "-v";
        }
        String cpn = parms.getCrowdinProjectName();
        String mpn = parms.getMinecraftProjectName();
        args[argc++] = cpn;
        args[argc++] = (mpn == null ? cpn : mpn);
        args[argc++] = parms.getJsonSourceName();

        CrowdinTranslateSss.main(args);
        this.setDidWork(true);
    }
}
