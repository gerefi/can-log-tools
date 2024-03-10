package com.gerefi.can;

import com.gerefi.util.BitMathUtil;

public class CANPacket {
    private final double timeStamp;
    private final int id;
    private final byte[] data;

    public CANPacket(double timeStamp, int id, byte[] data) {
        this.timeStamp = timeStamp;
        this.id = id;
        this.data = data;
    }

    public StringBuilder asLua(String arrayName) {
        StringBuilder result = new StringBuilder();
        result.append(arrayName + " = {");

        byte[] data = getData();
        System.out.println(String.format("Got ECU 0x%x", getId()) + " " + data.length);

        for (int index = 0; index < data.length; index++) {
            if (index > 0)
                result.append(", ");

            result.append(String.format("0x%02x", data[index]));

        }
        result.append("}\n");
        return result;
    }

    /**
     * @param index, starting from zero
     */
    public int getTwoBytesByByteIndex(int index) {
        return getByBitIndex(index * 8, 16);
    }

    public int getByBitIndex(int bitIndex, int bitWidth) {
        int byteIndex = bitIndex / 8;
        int shift = bitIndex - byteIndex * 8;
        int value = getUnsigned(byteIndex);
        if (shift + bitIndex > 8) {
            value = value + getUnsigned(byteIndex + 1) * 256;
        }
        return value >> shift & BitMathUtil.mask(bitWidth);
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public int getUnsigned(int i) {
        return Byte.toUnsignedInt(data[i]);
    }

    public void assertThat(String msg, PackerAssertion assertion) {
        if (!assertion.test(this))
            throw new IllegalStateException("Not " + msg + " " + Utils.bytesToHexWithSpaces(data));
    }

    public int getUnsignedInt(int index) {
        return Byte.toUnsignedInt(data[index]);
    }

    public Object getBytesAsString() {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < data.length; index++) {
            if (index > 0)
                result.append(" ");

            result.append(String.format("0x%02x", data[index]));

        }
        return result;
    }

    public interface PackerAssertion {
        boolean test(CANPacket packet);
    }
}
