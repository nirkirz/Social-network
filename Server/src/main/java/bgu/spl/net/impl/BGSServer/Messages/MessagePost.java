package bgu.spl.net.impl.BGSServer.Messages;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessagePost extends Message {

    private byte[] bytes ;
    private int len ;
    String content;

    public MessagePost(){
        super((short) 5);
        content = "";
        bytes = new byte[1 << 10];
        len = 0;
    }

    public boolean decode(byte nextByte) {
        if (nextByte == '\0') {
            content = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return true;
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

    public String getContent() {
        return content;
    }
}
