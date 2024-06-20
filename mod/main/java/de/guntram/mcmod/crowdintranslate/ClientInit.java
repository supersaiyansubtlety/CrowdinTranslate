package de.guntram.mcmod.crowdintranslate;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.util.Optional;
import java.util.function.Function;

import static de.guntram.mcmod.crowdintranslate.ClientInit.Keys.*;
import static de.guntram.mcmod.crowdintranslate.CrowdinTranslate.*;
import static net.fabricmc.loader.api.metadata.CustomValue.CvType.*;

public class ClientInit implements ClientModInitializer {
    // TODO name logger
    public static final Logger LOGGER = LogManget.getLogger();

    private static String getRequiredString(CustomValue.CvObject object, String key, String modId) {
        return getValue(object, key, STRING, CustomValue::getAsString, modId, true).orElse(null);
    }

    private static <T> Optional<T> getOptionalValue(
        CustomValue.CvObject object, String key,
        CustomValue.CvType type, Function<CustomValue, T> getter,
        String modId
    ) {
        return getValue(object, key, type, getter, modId, false);
    }

    private static <T> Optional<T> getValue(
        CustomValue.CvObject object, String key,
        CustomValue.CvType type, Function<CustomValue, T> getter,
        String modId, boolean logMissing
    ) {
        final var value = object.get(key);
        if (value == null) {
            if (logMissing) LOGGER.error(
                "Missing \"{}\" key in object for \"" + NAME + "\" entrypoint in " +
                    "fabric.mod.json of mod: {}",
                key, modId
            );

            return Optional.empty();
        } else {
            final var valueType = value.getType();
            if (valueType == type) return Optional.of(getter.apply(value));
            else {
                LOGGER.error(
                    "Invalid type for \"{}\" key in object for \"" + NAME + "\" entrypoint in " +
                        "fabric.mod.json of mod: {}\nExpected {}; found {}",
                    key, modId, type, valueType
                );

                return Optional.empty();
            }
        }
    }

    @Override
    public void onInitializeClient() {
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            final var metadata = container.getMetadata();
            final var customValue = metadata.getCustomValue(NAME);
            if (customValue == null) continue;

            final var type = customValue.getType();
            final String modId = metadata.getId();
            switch (type) {
                case BOOLEAN -> { if (customValue.getAsBoolean()) downloadTranslations(modId); }
                case STRING -> downloadTranslations(customValue.getAsString());
                case OBJECT -> {
                    final var object = customValue.getAsObject();

                    final var crowdinProjectName = getRequiredString(object, CROWDIN_PROJECT_NAME, modId);
                    if (crowdinProjectName == null) return;

                    final var minecraftProjectName = getRequiredString(object, MINECRAFT_PROJECT_NAME, modId);
                    if (minecraftProjectName == null) return;

                    final var verbose =
                        getOptionalValue(object, VERBOSE, BOOLEAN, CustomValue::getAsBoolean, modId)
                            .orElse(false);

                    final var sourcefileOverride =
                        getOptionalValue(object, SOURCE_FILE_OVERRIDE, STRING, CustomValue::getAsString, modId)
                            .orElse(null);

                    downloadTranslations(crowdinProjectName, minecraftProjectName, sourcefileOverride, verbose);
                }
                default -> LOGGER.error(
                    "Invalid type for \"" + NAME + "\" entrypoint in " +
                        "fabric.mod.json of mod: {}\nExpected {}, {}, or {}; found {}",
                    modId, BOOLEAN, STRING, OBJECT, type
                );
            }
        }
    }

    public interface Keys {
        String CROWDIN_PROJECT_NAME = "crowdinProjectName";
        String MINECRAFT_PROJECT_NAME = "minecraftProjectName";
        String SOURCE_FILE_OVERRIDE = "sourceFileOverride";
        String VERBOSE = "verbose";
    }
}
