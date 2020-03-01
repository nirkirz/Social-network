package bgu.spl.net.impl.BGSServer.Messages;

public class MessageLogOut extends Message {


    public MessageLogOut(){
        super((short) 3);
    }

    @Override
    public boolean decode(byte nextByte) {
        return true;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
