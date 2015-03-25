/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hdlcchat;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nbury059
 */
public class Primary extends Station{
    Secondary B;
    Secondary C;
    ServerSocket conn;
     /**
	 * Stream used to read from the client.
	 */
	
    PrintWriter out;
    public final static int PORT = 13131;
    static int portB = 10101;
    static int portC = 12321;
    public final static String HOST = "localhost";
    boolean received_RR_F_From_B = false;
   

	/**
	 * Indicates if the thread is ready to stop. Set to true when closing of the
	 * connection is initiated.
	 */
        boolean ReadyToStop;
    Primary(){
         
            try  {
                conn = new ServerSocket(PORT);
                client = conn.accept();
                out = new PrintWriter(client.getOutputStream());
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                this.output = (ObjectOutputStream) client.getOutputStream();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
