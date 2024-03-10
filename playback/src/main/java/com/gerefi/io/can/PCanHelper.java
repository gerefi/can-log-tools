package com.gerefi.io.can;


import peak.can.basic.*;

import static peak.can.basic.TPCANMessageType.PCAN_MESSAGE_STANDARD;

public class PCanHelper {
    public static final TPCANHandle CHANNEL = TPCANHandle.PCAN_USBBUS1;

    //    @NotNull
    public static PCANBasic createPCAN() {
        PCANBasic can = new PCANBasic();
        can.initializeAPI();
        return can;
    }

    public static TPCANStatus init(PCANBasic can) {
        return can.Initialize(CHANNEL, TPCANBaudrate.PCAN_BAUD_500K, TPCANType.PCAN_TYPE_NONE, 0, (short) 0);
    }

    public static TPCANStatus send(PCANBasic can, int id, byte[] payLoad) {
        //log.info(String.format("Sending id=%x %s", id, HexBinary.printByteArray(payLoad)));
        TPCANMsg msg = new TPCANMsg(id, PCAN_MESSAGE_STANDARD.getValue(),
                (byte) payLoad.length, payLoad);
        return can.Write(CHANNEL, msg);
    }

    public static PCANBasic createAndInit() {
        PCANBasic pcan = createPCAN();
        TPCANStatus initStatus = init(pcan);
        if (initStatus != TPCANStatus.PCAN_ERROR_OK) {
            System.out.println("createAndInit: *** ERROR *** TPCANStatus " + initStatus);
            System.exit(-1);
        }
        return pcan;
    }

    public static CanSender create() {
        PCANBasic pcan = createAndInit();
        return (id, payload) -> {
            TPCANStatus status = send(pcan, id, payload);

            if (status == TPCANStatus.PCAN_ERROR_XMTFULL || status == TPCANStatus.PCAN_ERROR_QXMTFULL) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                System.out.println(String.format("Let's retry ID=%x", packet.getId()) + " OK=" + okCounter);
                status = send(pcan, id, payload);
            }

            boolean isHappy = status == TPCANStatus.PCAN_ERROR_OK;
            if (!isHappy) {
                System.out.println("Error sending " + status);
            }
            return isHappy;
        };
    }
}
