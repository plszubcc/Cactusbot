package io.github.hello09x.fakeplayer.core.repository;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.Exceptions;
import io.github.hello09x.fakeplayer.core.Main;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated
@Singleton
public class UsedIdRepository {

    public static final UsedIdRepository instance = new UsedIdRepository();
    private static final Logger log = Main.getInstance().getLogger();
    private final Set<UUID> UUIDS = new HashSet();

    @Inject
    public UsedIdRepository() {
        super();
        this.load();
    }

    public boolean contains(@NotNull UUID uuid) {
        return this.UUIDS.contains(uuid);
    }

    public void add(@NotNull UUID uuid) {
        this.UUIDS.add(uuid);
    }

    public void remove(@NotNull UUID uuid) {
        this.UUIDS.remove(uuid);
    }

    public boolean exists(@NotNull UUID uuid) {
        return this.UUIDS.contains(uuid);
    }

    public void load() {
        File file = new File(Main.getInstance().getDataFolder(), "used-uuids.txt");

        if (file.exists() && file.isFile()) {
            try (FileReader in = new FileReader(file)) {
                for (String line : IOUtils.readLines(in)) {
                    try {
                        if (!line.isBlank()) {
                            this.UUIDS.add(UUID.fromString(line));
                        }
                    } catch (Throwable throwable) {
                        UsedIdRepository.log.warning("used-uuids.txt contains illegal UUID: " + line);
                    }
                }
            } catch (IOException ioexception) {
                UsedIdRepository.log.warning("Failed to read used-uuids.txt\n" + Throwables.getStackTraceAsString(ioexception));
            }

        }
    }

    public void saveAll() {
        File folder = Main.getInstance().getDataFolder();

        if (folder.exists() || folder.mkdirs()) {
            File file = new File(folder, "used-uuids.txt");

            try (FileWriter out = new FileWriter(file)) {
                IOUtils.writeLines(this.UUIDS, (String) null, out);
            } catch (IOException ioexception) {
                UsedIdRepository.log.warning("Failed to save used-uuids.txt\n" + Throwables.getStackTraceAsString(ioexception));
            }

        }
    }

    public void onDisable() {
        Exceptions.suppress(Main.getInstance(), this::saveAll);
    }
}
