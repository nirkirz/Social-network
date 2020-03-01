package bgu.spl.net.impl.BGSServer.Messages;

public abstract class Message {
    short numMsg;

    public Message(short numMsg){this.numMsg=numMsg;}

    public abstract boolean decode(byte nextByte);
    public abstract  byte[] encode();
    public short getNumMsg(){return numMsg;}

}
