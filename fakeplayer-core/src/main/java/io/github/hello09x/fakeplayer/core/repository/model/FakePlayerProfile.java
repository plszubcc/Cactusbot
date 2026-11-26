package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.devtools.database.jdbc.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FakePlayerProfile(Integer id, @NotNull String name, @NotNull String uuid) {
   public static class FakePlayerProfileRowMapper implements RowMapper<FakePlayerProfile> {
      public static final FakePlayerProfile.FakePlayerProfileRowMapper instance = new FakePlayerProfile.FakePlayerProfileRowMapper();

      public FakePlayerProfileRowMapper() {
      }

      @Nullable
      public FakePlayerProfile mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
         return new FakePlayerProfile(rs.getInt("id"), rs.getString("name"), rs.getString("uuid"));
      }
   }
}
