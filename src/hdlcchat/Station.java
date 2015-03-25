/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hdlcchat;

import static hdlcchat.Server.PORT;
import static hdlcchat.Server.decode;
import java.net.*;
import java.io.*;
import java.util.List;

/**
 *
 * @author nbury059
 */
public class Station {
    Socket SMTPClient;
    List<Socket> chat;
    String flag;
    String address;
    String control;
    String[] frame;
    byte delimeter;
    int NS;
    
     /**
	 * Stream used to read from the client.
	 */
	private ObjectInputStream	input;

	/**
	 * Stream used to write to the client.
	 */
	private ObjectOutputStream	output;
    
    Station(){
        this.delimeter = (byte) 01111110;
        SMTPClient = new Socket();
       
    }
}
