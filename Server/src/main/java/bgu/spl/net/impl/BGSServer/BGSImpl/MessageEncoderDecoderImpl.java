package bgu.spl.net.impl.BGSServer.BGSImpl;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGSServer.Messages.*;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    List<Byte> bytes = new LinkedList<>();
    Message currentMessage;

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (bytes.size() == 0) {
            bytes.add(nextByte);

        }

        else {
            if (bytes.size() == 1) {
                bytes.add(nextByte);
                currentMessage = createNewMessage();
                if (currentMessage instanceof MessageLogOut || currentMessage instanceof MessageUserList) {
                    bytes = new LinkedList<>();
                    return currentMessage;
                }
            }
            else {
                if (currentMessage.decode(nextByte)) {
                    bytes = new LinkedList<>();
                    return currentMessage;
                }
            }
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        byte[] arr = message.encode();
        return arr;
    }

    private Message createNewMessage(){
        byte[] arr = new byte[2];
        arr[0] = bytes.get(0);
        arr[1] = bytes.get(1);
        short result = (short)((arr[0] & 0xff) << 8);
        result += (short)(arr[1] & 0xff);

        switch (result)
        {
            case 1:
                return new MessageRegister();

            case 2:
                return new MessageLogin();

            case 3:
                return new MessageLogOut();

            case 4:
                return new MessageFollow();

            case 5:
                return new MessagePost();

            case 6:
                return new MessagePM();

            case 7:
                return new MessageUserList();

            case 8:
                return new MessageSTAT();

        }
        return null;// the input is valid so wont return null
    }
}
