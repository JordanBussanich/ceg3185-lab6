/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hdlcchat;

/**
 *
 * @author nbury059
 */

public class BitSet {
    int nbits;
    byte[] bytes;
    BitSet(int n){
        this.nbits = n;
        bytes = new byte[n];
    }
    
}
