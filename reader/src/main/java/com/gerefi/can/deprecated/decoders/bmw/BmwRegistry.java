package com.gerefi.can.deprecated.decoders.bmw;

import com.gerefi.can.deprecated.decoders.PacketDecoder;

import java.util.HashMap;
import java.util.Map;

public class BmwRegistry {
    public static final BmwRegistry INSTANCE = new BmwRegistry();

    public Map<Integer, PacketDecoder> decoderMap = new HashMap<>();

    public BmwRegistry() {
        register(new Bmw0AA());
        register(new Bmw1D0());
    }

    private void register(PacketDecoder decoder) {
        decoderMap.put(decoder.getId(), decoder);
    }

}
