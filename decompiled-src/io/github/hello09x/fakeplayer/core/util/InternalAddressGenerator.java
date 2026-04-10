package io.github.hello09x.fakeplayer.core.util;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

public class InternalAddressGenerator {

    private final AtomicInteger next = new AtomicInteger(1);

    public InternalAddressGenerator() {
        super();
    }

    public static boolean canBeGenerated(@NotNull InetAddress address) {
        return address.getAddress()[0] == 127;
    }

    public @NotNull InetAddress next() {
        try {
            int ip = this.next.getAndIncrement();

            if (ip == 16777214) {
                this.next.set(0);
            }

            int p2 = ip >> 16 & 255;
            int p3 = ip >> 8 & 255;
            int p4 = ip & 255;

            return InetAddress.getByAddress(new byte[]{127, (byte) p2, (byte) p3, (byte) p4});
        } catch (Throwable throwable) {
            throw throwable;
        }
    }
}
