package org.spigotmc;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.nbt.NbtSizeTracker;

public class LimitStream extends FilterInputStream {

    private final NbtSizeTracker limit;

    public LimitStream(InputStream is, NbtSizeTracker limit) {
        super(is);
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        limit.add(1);
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        limit.add(b.length);
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        limit.add(len);
        return super.read(b, off, len);
    }

}