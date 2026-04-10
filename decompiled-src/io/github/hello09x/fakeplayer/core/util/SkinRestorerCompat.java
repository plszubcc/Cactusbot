package io.github.hello09x.fakeplayer.core.util;

import io.github.hello09x.devtools.core.utils.SchedulerUtils;
import io.github.hello09x.fakeplayer.core.Main;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class SkinRestorerCompat {

    private static final Logger log = Main.getInstance().getLogger();
    private static volatile SkinRestorerCompat.Hook hook;
    private static volatile boolean loggedInitFailure;

    private SkinRestorerCompat() {
        super();
    }

    public static boolean isAvailable() {
        return getHook() != null;
    }

    public static @NotNull CompletableFuture<Boolean> applySkinByNameAsync(@NotNull Plugin plugin, @NotNull Player player, @NotNull String skinName) {
        return applySkinByInputAsync(plugin, player, skinName, (String) null);
    }

    public static @NotNull CompletableFuture<Boolean> applySkinByInputAsync(@NotNull Plugin plugin, @NotNull Player player, @NotNull String skinInput, String s1) {
        SkinRestorerCompat.Hook h = getHook();

        return h == null ? CompletableFuture.completedFuture(false) : (skinInput.isBlank() ? CompletableFuture.completedFuture(false) : CompletableFuture.supplyAsync(() -> {
            return h.findIdentifier(skinInput, variantHint);
        }).thenCompose((identifierOptx) -> {
            if (identifierOptx.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            } else {
                Object identifier = identifierOptx.get();

                return SchedulerUtils.runTask(plugin, () -> {
                    try {
                        h.applyIdentifier(player.getUniqueId(), identifier);
                        h.applyVisual(player);
                        return true;
                    } catch (Throwable throwable) {
                        SkinRestorerCompat.log.warning("SkinsRestorer failed to apply skin '" + skinInput + "' to " + player.getName() + ": " + throwable.getMessage());
                        return false;
                    }
                });
            }
        }));
    }

    private static SkinRestorerCompat.Hook getHook() {
        SkinRestorerCompat.Hook existing = SkinRestorerCompat.hook;

        if (existing != null) {
            return existing == SkinRestorerCompat.Hook.NOOP ? null : existing;
        } else {
            synchronized (SkinRestorerCompat.class) {
                if (SkinRestorerCompat.hook != null) {
                    return SkinRestorerCompat.hook == SkinRestorerCompat.Hook.NOOP ? null : SkinRestorerCompat.hook;
                } else {
                    try {
                        SkinRestorerCompat.hook = SkinRestorerCompat.Hook.tryCreate();
                    } catch (Throwable throwable) {
                        if (!SkinRestorerCompat.loggedInitFailure) {
                            SkinRestorerCompat.loggedInitFailure = true;
                            Plugin plugin = Bukkit.getPluginManager().getPlugin("SkinsRestorer");

                            if (plugin != null) {
                                Logger logger = SkinRestorerCompat.log;
                                String s = plugin.getDescription().getVersion();

                                logger.warning("SkinsRestorer is installed (" + s + ") but CactusBot failed to hook its API: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
                            }
                        }

                        SkinRestorerCompat.hook = SkinRestorerCompat.Hook.NOOP;
                    }

                    return SkinRestorerCompat.hook == SkinRestorerCompat.Hook.NOOP ? null : SkinRestorerCompat.hook;
                }
            }
        }
    }

    private static void noop() {}

    private static record Hook(@NotNull Object api, @NotNull Object skinStorage, @NotNull Object playerStorage, @NotNull Method findOrCreateSkinData, @NotNull Method getIdentifier, @NotNull Method setSkinIdOfPlayer, @NotNull Method getSkinApplier, @NotNull Method applySkinMethod, SkinRestorerCompat.@NotNull ApplySkinMode applySkinMode) {

        static final SkinRestorerCompat.Hook NOOP = new SkinRestorerCompat.Hook(new Object(), new Object(), new Object(), nullMethod(), nullMethod(), nullMethod(), nullMethod(), nullMethod(), SkinRestorerCompat.ApplySkinMode.NOOP);

        private Hook {
            super();
        }

        static SkinRestorerCompat.@NotNull Hook tryCreate() throws Exception {
            Object api = tryGetFromProvider();

            if (api == null) {
                api = tryLoadService("net.skinsrestorer.api.SkinsRestorer");
            }

            if (api == null) {
                api = tryLoadService("net.skinsrestorer.api.SkinsRestorerAPI");
            }

            if (api == null) {
                throw new IllegalStateException("SkinsRestorer API not available");
            } else {
                Class<?> apiClass = api.getClass();
                Method getSkinStorage = apiClass.getMethod("getSkinStorage");
                Method getPlayerStorage = apiClass.getMethod("getPlayerStorage");
                Object skinStorage = getSkinStorage.invoke(api);
                Object playerStorage = getPlayerStorage.invoke(api);
                Class<?> skinStorageClass = skinStorage.getClass();

                Method findOrCreateSkinData;

                try {
                    findOrCreateSkinData = skinStorageClass.getMethod("findOrCreateSkinData", String.class);
                } catch (NoSuchMethodException nosuchmethodexception) {
                    Class<?> skinVariantClass = Class.forName("net.skinsrestorer.api.property.SkinVariant");

                    findOrCreateSkinData = skinStorageClass.getMethod("findOrCreateSkinData", String.class, skinVariantClass);
                }

                Class<?> inputDataResultClass = Class.forName("net.skinsrestorer.api.property.InputDataResult");
                Method getIdentifier = inputDataResultClass.getMethod("getIdentifier");
                Class<?> playerStorageClass = playerStorage.getClass();

                Method setSkinIdOfPlayer;

                try {
                    Class<?> skinIdentifierClass = Class.forName("net.skinsrestorer.api.property.SkinIdentifier");

                    setSkinIdOfPlayer = playerStorageClass.getMethod("setSkinIdOfPlayer", UUID.class, skinIdentifierClass);
                } catch (NoSuchMethodException | ClassNotFoundException classnotfoundexception) {
                    setSkinIdOfPlayer = playerStorageClass.getMethod("setSkinIdOfPlayer", UUID.class, String.class);
                }

                Object applier;
                Method getSkinApplier;

                try {
                    getSkinApplier = apiClass.getMethod("getSkinApplier", Class.class);
                    applier = getSkinApplier.invoke(api, Player.class);
                } catch (NoSuchMethodException nosuchmethodexception1) {
                    getSkinApplier = apiClass.getMethod("getSkinApplier");
                    applier = getSkinApplier.invoke(api);
                }

                SkinRestorerCompat.ApplyMethod apply = findApplySkinMethod(applier);

                return new SkinRestorerCompat.Hook(api, skinStorage, playerStorage, findOrCreateSkinData, getIdentifier, setSkinIdOfPlayer, getSkinApplier, apply.method, apply.mode);
            }
        }

        Optional<Object> findIdentifier(@NotNull String skinInput, String s1) {
            try {
                Optional<Object> opt;

                if (this.findOrCreateSkinData.getParameterCount() == 1) {
                    opt = (Optional) this.findOrCreateSkinData.invoke(this.skinStorage, skinInput);
                } else {
                    opt = (Optional) this.findOrCreateSkinData.invoke(this.skinStorage, skinInput, this.resolveVariant(variantHint));
                }

                if (opt != null && !opt.isEmpty()) {
                    Object inputData = opt.get();
                    Object identifier = this.getIdentifier.invoke(inputData);

                    return identifier == null ? Optional.empty() : Optional.of(identifier);
                } else {
                    return Optional.empty();
                }
            } catch (Throwable throwable) {
                return Optional.empty();
            }
        }

        private Object resolveVariant(String s) {
            if (this.findOrCreateSkinData.getParameterCount() < 2) {
                return null;
            } else {
                Class<?> parameterType = this.findOrCreateSkinData.getParameterTypes()[1];

                if (!parameterType.isEnum()) {
                    return null;
                } else if (variantHint != null && !variantHint.isBlank()) {
                    for (Object constant : parameterType.getEnumConstants()) {
                        String name = constant.toString();

                        if (name.equalsIgnoreCase(variantHint)) {
                            return constant;
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            }
        }

        void applyIdentifier(@NotNull UUID playerId, @NotNull Object identifier) throws Exception {
            if (this.setSkinIdOfPlayer.getParameterTypes()[1] == String.class) {
                this.setSkinIdOfPlayer.invoke(this.playerStorage, playerId, identifier.toString());
            } else {
                this.setSkinIdOfPlayer.invoke(this.playerStorage, playerId, identifier);
            }

        }

        void applyVisual(@NotNull Player player) throws Exception {
            Object applier;

            if (this.getSkinApplier.getParameterCount() == 1) {
                applier = this.getSkinApplier.invoke(this.api, Player.class);
            } else {
                applier = this.getSkinApplier.invoke(this.api);
            }

            switch (this.applySkinMode.ordinal()) {
                case 0:
                default:
                    break;
                case 1:
                    this.applySkinMethod.invoke(applier, player);
                    break;
                case 2:
                    this.applySkinMethod.invoke(applier, player.getUniqueId());
                    break;
                case 3:
                    this.applySkinMethod.invoke(applier, player.getUniqueId(), true);
                    break;
                case 4:
                    this.applySkinMethod.invoke(applier, player, true);
            }

        }

        private static Method nullMethod() {
            try {
                return SkinRestorerCompat.class.getDeclaredMethod("noop");
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        private static SkinRestorerCompat.ApplyMethod findApplySkinMethod(@NotNull Object applier) throws NoSuchMethodException {
            Method best = null;
            SkinRestorerCompat.ApplySkinMode bestMode = SkinRestorerCompat.ApplySkinMode.NOOP;

            for (Method m : applier.getClass().getMethods()) {
                if (m.getName().equals("applySkin")) {
                    Class<?>[] params = m.getParameterTypes();

                    if (params.length == 1 && params[0].isAssignableFrom(Player.class) && !UUID.class.isAssignableFrom(params[0])) {
                        best = m;
                        bestMode = SkinRestorerCompat.ApplySkinMode.PLAYER_1;
                        break;
                    }

                    if (params.length == 1 && UUID.class.isAssignableFrom(params[0])) {
                        best = m;
                        bestMode = SkinRestorerCompat.ApplySkinMode.UUID_1;
                    }

                    if (params.length == 2 && UUID.class.isAssignableFrom(params[0]) && (params[1] == Boolean.TYPE || params[1] == Boolean.class)) {
                        best = m;
                        bestMode = SkinRestorerCompat.ApplySkinMode.UUID_BOOL_2;
                    }

                    if (params.length == 2 && params[0].isAssignableFrom(Player.class) && (params[1] == Boolean.TYPE || params[1] == Boolean.class)) {
                        best = m;
                        bestMode = SkinRestorerCompat.ApplySkinMode.PLAYER_BOOL_2;
                    }
                }
            }

            if (best == null) {
                throw new NoSuchMethodException(applier.getClass().getName() + ".applySkin(...)");
            } else {
                return new SkinRestorerCompat.ApplyMethod(best, bestMode);
            }
        }

        private static Object tryGetFromProvider() {
            try {
                Class<?> providerClass = Class.forName("net.skinsrestorer.api.SkinsRestorerProvider");

                for (String methodName : new String[]{"get", "getAPI", "getApi"}) {
                    try {
                        Method m = providerClass.getMethod(methodName);
                        Object api = m.invoke((Object) null);

                        if (api != null) {
                            return api;
                        }
                    } catch (NoSuchMethodException nosuchmethodexception) {
                        ;
                    }
                }

                return null;
            } catch (Throwable throwable) {
                return null;
            }
        }

        private static Object tryLoadService(@NotNull String className) {
            try {
                Class apiClass = Class.forName(className);

                return Bukkit.getServicesManager().load(apiClass);
            } catch (Throwable throwable) {
                return null;
            }
        }
    }

    private static enum ApplySkinMode {

        NOOP, PLAYER_1, UUID_1, UUID_BOOL_2, PLAYER_BOOL_2;

        private ApplySkinMode() {}
    }

    private static record ApplyMethod(@NotNull Method method, SkinRestorerCompat.@NotNull ApplySkinMode mode) {

        private ApplyMethod {
            super();
        }
    }
}
