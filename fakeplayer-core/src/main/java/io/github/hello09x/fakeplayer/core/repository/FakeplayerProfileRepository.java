package io.github.hello09x.fakeplayer.core.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.database.jdbc.JdbcTemplate;
import io.github.hello09x.devtools.database.jdbc.rowmapper.BooleanRowMapper;
import io.github.hello09x.fakeplayer.core.repository.model.FakePlayerProfile;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class FakeplayerProfileRepository {

    private final JdbcTemplate jdbc;

    @Inject
    public FakeplayerProfileRepository(JdbcTemplate jdbc) {
        super();
        this.jdbc = jdbc;
        this.initTables();
    }

    public void insert(@NotNull String name, @NotNull UUID uuid) {
        String sql = "INSERT INTO fake_player_profile (name, uuid) VALUES (?, ?)";

        this.jdbc.update(sql, name, uuid.toString());
    }

    public boolean existsByUUID(@NotNull UUID uuid) {
        String sql = "SELECT EXISTS(SELECT 1 FROM fake_player_profile WHERE uuid = ?)";

        return Boolean.TRUE.equals(this.jdbc.queryForObject(sql, new BooleanRowMapper(), uuid.toString()));
    }

    public @Nullable UUID selectUUIDByName(@NotNull String name) {
        return (UUID) Optional.ofNullable(this.selectByName(name)).map(FakePlayerProfile::uuid).map(UUID::fromString).orElse((Object) null);
    }

    public @Nullable FakePlayerProfile selectByName(@NotNull String name) {
        String sql = "SELECT * FROM fake_player_profile WHERE name = ?";

        return (FakePlayerProfile) this.jdbc.queryForObject(sql, FakePlayerProfile.FakePlayerProfileRowMapper.instance, name);
    }

    private void initTables() {
        this.jdbc.execute("create table if not exists fake_player_profile\n(\n    id   integer  not null primary key autoincrement,\n    name text(32) not null,\n    uuid text(36) not null\n);\n");
        this.jdbc.execute("create unique index if not exists fake_player_profile_name\n           on fake_player_profile (name);\n");
        this.jdbc.execute("create unique index if not exists fake_player_profile_uuid\n           on fake_player_profile (uuid);\n");
    }
}
