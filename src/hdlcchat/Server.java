package hdlcchat;

import java.net.*;
import java.io.*;

public class Server {
	public final static int PORT = 13131;
	public final static String HOST = "localhost";
	public static void main(String args[]) {
		
		ServerSocket conn = null;
		Socket client = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try  {
			conn = new ServerSocket(PORT);
			client = conn.accept();
			out = new PrintWriter(client.getOutputStream());
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
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
		
		// Send CTS to the client then wait for data
		out.println("CTS");
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

        // Decode and print the data
		System.out.println("Received: " + data);
        System.out.println("Decoded:  " + decode(data));
	}
	
	public static String decode(String bin) {
		String result = "";
		boolean previousIsPositive = false;
		for (int i=0; i<bin.length(); i++) {
			
			if (bin.charAt(i) == '0') {
				result = result.concat("0");
			} else if (bin.charAt(i) == '+') {
				if (previousIsPositive) {
					result = result.concat("0");
				} else {
					result = result.concat("1");
					previousIsPositive = true;
				}
			} else if (bin.charAt(i) == '-') {
				if (!previousIsPositive) {
					result = result.concat("0");
				} else {
					result = result.concat("1");
					previousIsPositive = false;
				}
			}
		}
		return result;
	}
}
