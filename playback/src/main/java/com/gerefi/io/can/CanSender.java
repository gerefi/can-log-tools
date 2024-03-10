package com.gerefi.io.can;

public interface CanSender {
    boolean send(int id, byte[] payload);
}
