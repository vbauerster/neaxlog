package bauer.neax.mail;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by vbauer on 07/08/14.
 */
public class CryptoTest {

    public static void main(String[] args) throws GeneralSecurityException, IOException {

        byte b = (byte) -27;
        int v = b;
        System.out.println(v);
        int uv = b & 0xff;
        System.out.println(uv);

        String signed = Integer.toHexString(v);
        System.out.println("Signed: " + signed);
//        System.out.println(Integer.parseInt("ff", 16));
        System.out.println("& 0xFF: " + Integer.toHexString(255));
        String unsigned = Integer.toHexString(uv);
        System.out.println("Unsigned: " + unsigned);
        System.out.println("Parsed: " + Integer.parseInt(unsigned, 16));

//        byte original = (byte) Integer.parseInt(unsigned, 16);
//        System.out.println("Original byte: " + original);

        String signed_ = Integer.toBinaryString(v);
        System.out.printf("%-10s:", "Signed");
        System.out.println(String.format("%32s",signed_).replace(' ', '0'));

        System.out.printf("%-10s:", "0xff");
        System.out.println(String.format("%32s", Integer.toBinaryString(255)).replace(' ', '0'));

        String unsigned_ = Integer.toBinaryString(uv);
        System.out.printf("%-10s:", "Unsigned");
        System.out.println(String.format("%32s", unsigned_).replace(' ', '0'));


    }

    private static String byteArrayToHexString(byte[] b){
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++){
            int v = b[i] & 0xff; //makes unsigned byte
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++){
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }
}
