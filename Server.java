package com.minsung.Server;

import java.net.*;

public class Server extends DatagramSocket
{
    public Server()
    {
        while(true)
        {
            byte[] buf = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            this.receive(packet);
            System.out.println(new String(packet.getData()));
        }
    }
}
