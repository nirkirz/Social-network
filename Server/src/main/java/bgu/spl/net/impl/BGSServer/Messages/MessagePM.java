package bgu.spl.net.impl.BGSServer.Messages;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessagePM extends Message {

    private byte[] bytes ;
    private int len ;
    private String receiver;
    private String content;

    public MessagePM(){
        super((short) 6);
        receiver = "";
        content = "";
        bytes = new byte[1 << 10];
        len = 0;
    }

    public boolean decode(byte nextByte) {
        if (nextByte == '\0') {
            if (receiver == "") {
                receiver = new String(bytes, 0, len, StandardCharsets.UTF_8);
                bytes = new byte[1 << 10];
                len = 0;
            }
            else{
                content = new String(bytes, 0, len, StandardCharsets.UTF_8);
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
    public byte[] encode() { //**TODO !!!
        return new byte[0];
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }
}
