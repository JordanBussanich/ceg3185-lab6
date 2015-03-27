/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hdlcchat;

import java.io.*;
import java.io.PrintWriter;
import java.lang.Runnable;
import java.lang.System;
import java.lang.Thread;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nbury059
 */
public class Primary {

    public final String B_ADDRESS = "01010101";
    public final String C_ADDRESS = "10101010";

    public final String FLAG = "01111110";

    public final String SNRM_FIRST_4 = "1100";
    public final String SNRM_LAST_3 = "001";

    public final String UA_FIRST_4 = "1100";
    public final String UA_LAST_3 = "110";

    /*
    Secondary B;
    Secondary C;
    ServerSocket conn; */
     /**
	 * Stream used to read from the client.
	 */
    public final static int PORT = 10000;
    public final int PORT_B = 10101;
    public final int PORT_C = 11111;
    public final static String HOST = "localhost";
   

	/**
	 * Indicates if the thread is ready to stop. Set to true when closing of the
	 * connection is initiated.
	 */
        boolean ReadyToStop;

    class Handler implements Runnable{
        List<Socket> connectedClients = new ArrayList<Socket>();    // This holds the connections of all the clients

        int port;
        Socket client;
        BufferedReader bIn = null;
        BufferedReader cIn = null;
        PrintWriter bOut = null;
        PrintWriter cOut = null;

        public Handler(int port, Socket s) {
            this.port = port;
            this.client = s;
        }

        public void run() {
            ServerSocket connB = null;
            ServerSocket connC = null;
            bOut = new PrintWriter(client.getOutputStream());
            bIn = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Initialise B
            try {
                System.out.println("Waiting for B to connect...");
                connB = new ServerSocket(PORT);
                Socket clientB = connB.accept();
                connectedClients.add(clientB);
                bOut = new PrintWriter(clientB.getOutputStream());
                bIn = new BufferedReader(new InputStreamReader(clientB.getInputStream()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Successfully connected to B!");

            // Initialise C
            try {
                System.out.println("Waiting for C to connect...");
                connC = new ServerSocket(PORT);
                Socket clientC = connC.accept();
                connectedClients.add(clientC);
                cOut = new PrintWriter(clientC.getOutputStream());
                cIn = new BufferedReader(new InputStreamReader(clientC.getInputStream()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Successfully connected to C!");

            // B and C are now connected, send SNRM to B
            String bSnrm = FLAG + B_ADDRESS + SNRM_FIRST_4 + "1" + SNRM_LAST_3 + FLAG;

            bOut.println(bSnrm);
            bOut.flush();

            System.out.println("Sent SNRM to B");

            // Wait for UA from B
            boolean gotBUA = false;
            final String bUA = FLAG + B_ADDRESS + UA_FIRST_4 + "1" + UA_LAST_3 + FLAG;

            while(!gotBUA) {
                String message = "";
                try {
                    message = bIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (message != null)
                    if (message.equals(bUA))
                        gotBUA = true;
            }

            System.out.println("Received UA from B");

            // Send SNRM to C
            String cSnrm = FLAG + C_ADDRESS + SNRM_FIRST_4 + "1" + SNRM_LAST_3 + FLAG;

            cOut.println(cSnrm);
            cOut.flush();

            System.out.println("Sent SNRM to C");

            // Wait for UA from C
            boolean gotCUA = false;
            final String cUA = FLAG + C_ADDRESS + UA_FIRST_4 + "1" + UA_LAST_3 + FLAG;

            while(!gotCUA) {
                String message = "";
                try {
                    message = cIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (message != null)
                    if (message.equals(cUA))
                        gotCUA = true;
            }

            System.out.println("Received UA from C");
        }
    }

    Primary() {
        boolean running = true;
        while(running) {
            ServerSocket conn = null;
            Socket client = null;
            try {
                conn = new ServerSocket(PORT);
                client = conn.accept();
                Thread t = new Thread(Handler(PORT, client));
                t.start();
                this.output = (ObjectOutputStream) client.getOutputStream();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        // Listen for a request to send
        boolean receivedRTS = false;
        System.out.println("Waiting for request to send.");
        while (!receivedRTS) {
            String rtsResponse = "";
            try {
                rtsResponse = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (rtsResponse != null) {
                if (rtsResponse.equals("RTS")) {
                    receivedRTS = true;
                    System.out.println("Request received, sending clear to send.");
                }
            }
        }

        try {
            // Send CTS to the client then wait for data
            output.writeObject("SNRM");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        out.flush();

        boolean gotData = false;
        String raw = "";
        String data = "";
        System.out.println("Waiting for data.");
        while (!gotData) {
            try {
                raw = in.readLine();
                if (raw.equals("END")) {
                    break;
                } else {
                    data = raw;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            /*
			if (!raw.isEmpty()) {
				gotData = true;
			}*/
        }

        try {
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
