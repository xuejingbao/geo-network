package net.gcdc.geonetworking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class LinkLayerUdpToEthernet implements LinkLayer, AutoCloseable {
    private final static Logger logger = LoggerFactory.getLogger(LinkLayerUdpToEthernet.class);

    final SocketAddress remoteAddress;
    final boolean hasEthernetHeader;

    final DatagramSocket socket;
    final int BUFFER_LENGTH = 65535;
    final byte[] buffer = new byte[BUFFER_LENGTH];
    final DatagramPacket receptionPacket = new DatagramPacket(buffer, BUFFER_LENGTH);

    private final static int ETHERTYPE_START_BIT = 12;

    public LinkLayerUdpToEthernet(int localPort, SocketAddress remoteAddress, boolean hasEthernetHeader)
            throws SocketException {
        this.socket = new DatagramSocket(localPort);
        this.remoteAddress = remoteAddress;
        this.hasEthernetHeader = hasEthernetHeader;
    }

    @Override
    public byte[] receive() throws IOException, SocketTimeoutException {
        socket.receive(receptionPacket);
        if (hasEthernetHeader) {
            short ethertype = ByteBuffer.wrap(buffer).getShort(ETHERTYPE_START_BIT);
            while (ethertype != GeonetStation.GN_ETHER_TYPE) {
                // logger.debug("Ignoring packet with ethertype {}", ethertype);
                socket.receive(receptionPacket);
                ethertype = ByteBuffer.wrap(buffer).getShort(12);
            }
        }
        return Arrays.copyOfRange(receptionPacket.getData(), 0, receptionPacket.getLength());
    }

    @Override
    public void send(byte[] payload) throws IOException {
        // logger.info("即将发送udp：{}",ByteBufUtil.hexDump(payload));
        socket.send(new DatagramPacket(payload, payload.length, remoteAddress));
    }

    @Override
    public void close() {
        socket.close();
    }

    @Override
    public boolean hasEthernetHeader() {
        return hasEthernetHeader;
    }

}
