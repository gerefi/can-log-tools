package com.gerefi.can.deprecated.decoders.bmw;

import com.gerefi.can.CANPacket;
import com.gerefi.can.SensorValue;
import com.gerefi.can.deprecated.PacketPayload;
import com.gerefi.can.deprecated.SensorType;
import com.gerefi.can.deprecated.decoders.AbstractPacketDecoder;

public class Bmw0BA extends AbstractPacketDecoder {
    public static final int ID = 0x0BA;
    public static final Bmw0BA INSTANCE = new Bmw0BA();

    public static int GEAR_UNKNOWN = -3;
    public static int GEAR_R = -2;
    public static int GEAR_P = -1;
    public static int GEAR_N = 0;

    private Bmw0BA() {
        super(ID);
    }

    @Override
    public PacketPayload decode(CANPacket packet) {
        int gearBits = packet.getUnsignedInt(0) & 0xF;

        int gear;
        if (gearBits == 1) {
            gear = GEAR_N;
        } else if (gearBits == 2) {
            gear = GEAR_R;
        } else if (gearBits == 3) {
            gear = GEAR_P;
        } else if (gearBits == 0xF) {
            gear = GEAR_UNKNOWN;
        } else if (gearBits >= 5 && gearBits <= 0xA) {
            /* gears 1 to 6 */
            gear = gearBits - 4;
        } else {
            throw unexpected("gear bits", packet);
        }

        if (gear == GEAR_UNKNOWN)
            return null;
        return new PacketPayload(0, new SensorValue(SensorType.GEAR, gear));
    }
}
