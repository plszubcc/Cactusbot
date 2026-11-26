package io.github.hello09x.fakeplayer.v1_21_11.network;

import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;

public class FakePlayerAdvancements extends AdvancementDataPlayer {
   public FakePlayerAdvancements(DataFixer datafixer, PlayerList playerlist, AdvancementDataWorld manager, Path path, EntityPlayer player) {
      super(datafixer, playerlist, manager, path, player);
      this.b();
   }

   public boolean a(AdvancementHolder advancementholder, String s) {
      return false;
   }

   public void a(EntityPlayer entityplayer, boolean flag) {
   }

   public AdvancementProgress b(AdvancementHolder advancement) {
      return new AdvancementProgress();
   }

   public boolean b(AdvancementHolder advancement, String s) {
      return false;
   }

   public void b() {
   }

   public void a(EntityPlayer player) {
   }

   public void a(AdvancementHolder advancement) {
   }

   public void a() {
   }
}
