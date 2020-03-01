package bgu.spl.net.impl.BGSServer.Messages;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageRegister extends Message {

    private String name;
    private String password;
    private byte[] bytes ; //start with 1k
    private int len ;

    public MessageRegister(){
        super((short) 1);
        name="";
        password="";
        bytes = new byte[1 << 10]; //start with 1k
        len = 0;
    }

    @Override
    public boolean decode(byte nextByte) {
        if (nextByte == '\0') {
            if (name == "") { //first string in the msg = username
                name = new String(bytes, 0, len, StandardCharsets.UTF_8);
                bytes = new byte[1 << 10];
                len = 0;
            }
            else {//first string in the msg = password
                password = new String(bytes, 0, len, StandardCharsets.UTF_8);
                return true;
            }
        }
        else{ //not completed string, add next byte, and increase size of array if reached to the limit
            if (len >= bytes.length) {
                bytes = Arrays.copyOf(bytes, len * 2);
            }
            bytes[len++] = nextByte;
        }
        return false;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
