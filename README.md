Crowdin Translate SSS - a library to add Crowdin Internationalization to your mods
=============================================================================

This is a continuation of [CrowdinTranslate](https://github.com/gbl/CrowdinTranslate) by [GBL](https://github.com/gbl)
for Minecraft versions 1.21 and above.

Crowdin Translate SSS is a library that's intended to make Internationalization as 
easy as possible in your mods. The project provides:

- A main program which you can use to easily download translations from
CrowdIn, and distribute these translations to the correct file names in the
correct folder

- A gradle plugin to automate getting translations from your build process

- A Fabric library mod which you can be an optional dependency or bundled in your own mods which downloads
updated translations, and makes them available in a resource pack, to your
users, so you don't have to publish a new version of your mod, and people
don't need to re-download, when new translations appear

## Getting started

Create a CrowdIn project, (if possible, use the same project name as your mod id).
Upload your en_us.json, get it translated, build the project. More detailed 
info below.

You can view available versions in the
[package registry](https://gitlab.com/supersaiyansubtlety-group/minecraft-mods/crowdin_translate_sss/-/packages).

If you're having trouble you can contact me on [discord](https://discord.gg/xABmPngXAH).

## Manual usage:

Run `java -jar crowdin_translate_sss_base-<version>.jar <projectname>` from the main
mod directory to download translations and distribute them
to `src/main/resources/assets/<projectname>/lang/`.

If you weren't able to use your modid for your crowdin project, run
`java -jar crowdintranslate-<version>.jar <projectname> <modid>` instead.

## Automatic usage:

In your `settings.gradle`, add this:

```
pluginManagement {
    repositories {
        maven {
            name = "Crowdin Translate SSS"
            url = "https://gitlab.com/api/v4/projects/59105494/packages/maven"
        }
    }
}
```

Then in `build.gradle` add:

```
plugins {
    id 'net.sssubtlety.crowdin-translate-sss' version '<version>'
}

crowdinTranslateSss {
    crowdinProjectName = '<modid>'
    minecraftProjectName = '<modid>'
    verbose = false
}
```

You can omit the minecraftProjectName if the ids are the same, and you can
set verbose to true to see more about what's happening in the build process.

This will give you a new gradle task: `gradle downloadTranslations` fetches 
all translations to your src/main/resources/assets/<modid>/lang directory.

To do this automatically when you build the project, add something like this
to the end of your build.gradle:

```
build {
    dependsOn downloadTranslations
}
```

## Have your mod automatically check for new translations

That way your users can get new translations automatically, without
you re-publishing your mod, and them having to re-download it.

Add this to your `build.gradle`:

```groovy
repositories {
	maven {
		url = "https://gitlab.com/api/v4/projects/59105494/packages/maven"
	}
}
dependencies {
    modImplementation "net.sssubtlety:crowdin_translate_sss_mod:<version>"
    // include the line below ONLY if you want to bundle the library
    include "net.sssubtlety:crowdin_translate_sss_mod:<version>"
}
```

Crowdin Translate SSS adds a new way to use it at runtime: a custom `fabric.mod.json` field.  
This is the preferred method because you needn't interact with Crowdin Translate SSS through java at all,
so it can be an optional dependency. You can still bundle it if you prefer.

The custom FMJ field has several forms:

- use your mod id for Crowdin and Minecraft project names
```json
"custom": {
    "crowdin_translate_sss": true
}
```

- use the string value for Crowdin and Minecraft project names
```json
"custom": {
    "crowdin_translate_sss": "some-mod"
}
```

- specify the params for `CrowdinTranslate#downloadTranslations(String crowdinProjectName, String minecraftProjectName,
String sourceFileOverride, boolean verbose)`; `sourceFileOverride`, and `verbose` are optional
```json
"custom": {
    "crowdin_translate_sss": {
        "crowdinProjectName": "some-other-mod",
        "minecraftProjectName": "some_other_mod",
        "sourceFileOverride": "thing.json",
        "verbose": true
    }
}
```

You can instead use Crowdin Translate SSS in your `ClientModInitializer` like before
(don't this if you're already using the custom FMJ field):

```
CrowdinTranslateSss.downloadTranslations("modid");
```

for example

```
public class MyModClass implements ClientModInitializer 
{
    static public final String MODID="modid";
    @Override
    public void onInitializeClient() {
        CrowdinTranslateSss.downloadTranslations(MODID);
    }
}
```

If your CrowdIn project name does not match your Minecraft Mod ID, you need
to use the two parameter form with CrowdIn name first, and mod id second:

```
CrowdinTranslateSss.downloadTranslations("projectname", "modid");
```

This will download the translations from
`https://crowdin.com/project/projectname`
to `assets/modid/lang`.

### What if I have the translation files for several mods in the same crowdin project?

Since version 1.3, you can override the translation source name that
crowdin-translate checks for. So, if your mods are named foo, bar, and baz,
you can have one single crowdin project that has them all, and have file names
`foo.json`, `bar.json` and `thisisnotbaz.json` for your source.

Assuming your crowdin project name is `allmymods`,
adjust the above use cases like this:

- manual usage:
```
java -jar crowdin_translate_sss_base-<version>.jar allmymods foo foo
java -jar crowdin_translate_sss_base-<version>.jar allmymods bar bar
java -jar crowdin_translate_sss_base-<version>.jar allmymods baz thisisnotbaz
```

- usage in gradle: add a 'jsonSourceName' parameter
```
crowdinTranslateSss.jsonSourceName = 'thisisnotbaz'
```

- usage in your `ClientModInitializer`: use the 3 argument call:
```
CrowdinTranslateSss.downloadTranslations("allmymods", "baz", "thisisnotbaz");
```
