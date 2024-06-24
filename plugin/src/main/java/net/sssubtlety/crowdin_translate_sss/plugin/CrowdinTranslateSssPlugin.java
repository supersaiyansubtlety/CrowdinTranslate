/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sssubtlety.crowdin_translate_sss.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 *
 * @author gbl
 */
public class CrowdinTranslateSssPlugin implements Plugin<Project> {

    public static CrowdinTranslateSssExtension parameters;

    @Override
    public void apply(Project project) {
        
        parameters = project.getExtensions()
                .create("crowdinTranslateSss", CrowdinTranslateSssExtension.class);
        project.getTasks().create("downloadTranslations", DownloadTask.class);
    }
}
