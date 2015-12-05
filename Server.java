package com.minsung.Server;

import java.net.*;
import java.io.*;

public class Server extends MulticastSocket
{
    public Server() throws IOException
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
