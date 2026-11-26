package io.github.hello09x.fakeplayer.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public class Commands {
   public Commands() {
   }

   public static List<String> formatCommands(@NotNull List<String> commands, @NotNull String... pa) {
      if ((pa.length & 1) != 0) {
         throw new IllegalArgumentException("parameter 'pa' should be paired");
      } else {
         HashMap<String, String> args = new HashMap<>(pa.length / 2, 1.0F);

         for (int i = 0; i < pa.length; i += 2) {
            args.put(pa[i], pa[i + 1]);
         }

         ArrayList<String> ret = new ArrayList<>(commands.size());
         Iterator var4 = commands.iterator();

         while (true) {
            String cmd;
            while (true) {
               if (!var4.hasNext()) {
                  return ret;
               }

               cmd = (String)var4.next();
               cmd = cmd.trim();
               if (!cmd.startsWith("/")) {
                  break;
               }

               if (cmd.length() >= 2) {
                  cmd = cmd.substring(1);
                  break;
               }
            }

            if (!args.isEmpty()) {
               for (Entry<String, String> entry : args.entrySet()) {
                  cmd = cmd.replace(entry.getKey(), entry.getValue());
               }
            }

            if (!cmd.isBlank()) {
               ret.add(cmd);
            }
         }
      }
   }
}
