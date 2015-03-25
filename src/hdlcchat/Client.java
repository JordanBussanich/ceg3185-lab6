package hdlcchat;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client {
	public final static int PORT = 13131;
	public final static String HOST = "localhost";
	public static void main(String args[]) {

        // Get the string we're going to send, then encode it
        System.out.println("Enter the binary string you wish to send:");
        Scanner scan = new Scanner(System.in);
        String raw = scan.nextLine();
        String encoded = encode(raw);
        System.out.println("Raw string:" + raw);
        System.out.println("Encoded string: " + encoded);

		// Attempt to establish a connection to PORT:HOST
		Socket conn = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try  {
			conn = new Socket(HOST, PORT);
			out = new PrintWriter(conn.getOutputStream());
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Wait for the server to respond
		boolean waiting = true;
		while (waiting) {
			// Ask the server if we can send
            System.out.println("Asking the server to transmit.");
			out.println("RTS");
            out.flush();
			String ctsResponse = "";
			try {
				ctsResponse = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (ctsResponse.equals("CTS")) {
                System.out.println("Clear to Send received, starting transmission.");
                waiting = false;    // Good to go, we can start transmitting now
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


		// Transmit the encoded string to the server then close the connection
		out.println(encoded);
        out.flush();

        System.out.println("Transmission finished, closing connection.");

        out.println("END");
        out.flush();

		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String encode(String bin) {
		// This assumes odd bits since previous violation
		String result = "";
		boolean odd = false;
		boolean previousIsPositive = false;
		for (int i=0; i<bin.length(); i++) {
			if (bin.charAt(i) == '1' && previousIsPositive) {
				result = result.concat("-");
				previousIsPositive = false;
				odd = !odd;
			} else if (bin.charAt(i) == '1' && !previousIsPositive) {
				result = result.concat("+");
				previousIsPositive = true;
				odd = !odd;
			} else if (bin.charAt(i) == '0') {
				// We need to look at the next four characters and see if they're all zeros
				if (i+4 > bin.length()-1) {
					result = result.concat("0");
				} else {
					boolean needViolation = false;
					if (bin.charAt(i+1) == '0' && bin.charAt(i+2) == '0' && bin.charAt(i+3) == '0') {
						needViolation = true;
					}
					/*for (int j=0; j<4; j++) {
						if (bin.charAt(i+j) == '1') {
							break;
						}
					}*/
					if (!needViolation) {
						result = result.concat("0");
					} else {
						if (previousIsPositive && odd) {
							result = result.concat("000+");
							odd = false;
						} else if (!previousIsPositive && odd) {
							result = result.concat("000-");
							odd = false;
						} else if (previousIsPositive && !odd) {
							result = result.concat("+00+");
						} else if (!previousIsPositive && !odd) {
							result = result.concat("-00-");
						}
						i = i + 3;
					}
				}
			}
		}
		
		return result;
	}
}
