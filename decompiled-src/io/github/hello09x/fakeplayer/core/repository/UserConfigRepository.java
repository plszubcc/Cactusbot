package io.github.hello09x.fakeplayer.core.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.database.jdbc.JdbcTemplate;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import io.github.hello09x.fakeplayer.core.repository.model.UserConfig;
import io.github.hello09x.fakeplayer.core.repository.model.UserConfigRowMapper;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class UserConfigRepository {

    private final JdbcTemplate jdbc;

    @Inject
    public UserConfigRepository(JdbcTemplate jdbc) {
        super();
        this.jdbc = jdbc;
        this.initTables();
    }

    public @Nullable UserConfig selectByPlayerIdAndKey(@NotNull UUID playerId, @NotNull Feature feature) {
        String sql = "select * from user_config where player_id = ? and `key` = ?";

        return (UserConfig) this.jdbc.queryForObject(sql, UserConfigRowMapper.instance, playerId.toString(), feature.name());
    }

    public int saveOrUpdate(@NotNull UserConfig config) {
        String sql = "insert or replace into user_config(\n    id, player_id, `key`, `value`\n) values (\n    (select id from user_config where player_id = ? and `key` = ?),\n    ?,\n    ?,\n    ?\n  )\n";

        return this.jdbc.update(sql, config.playerId().toString(), config.key().name(), config.playerId(), config.key().name(), config.value());
    }

    public @NotNull List<UserConfig> selectByPlayerId(@NotNull UUID playerId) {
        String sql = "select * from user_config where player_id = ?";

        return this.jdbc.query(sql, UserConfigRowMapper.instance, playerId.toString());
    }

    protected void initTables() {
        this.jdbc.execute("create table if not exists user_config\n    (\n        id        integer  not null primary key autoincrement,\n        player_id text(36) not null,\n        `key`       text     not null,\n        `value`     text     not null\n    );\n");
        this.jdbc.execute("create unique index if not exists table_name_player_id_key_uindex\n           on user_config (player_id, `key`);\n");
    }
}
