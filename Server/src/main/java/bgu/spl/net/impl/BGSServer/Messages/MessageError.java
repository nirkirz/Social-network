package bgu.spl.net.impl.BGSServer.Messages;


public class MessageError extends Message {

    private byte[] bytes ;
    private Message msg;

    public MessageError(Message msg){
        super((short) 11);
        this.msg = msg;
        bytes = new byte[1 << 10];
    }

    @Override
    public boolean decode(byte nextByte) {
        return true; //**TODO ??
    }

    @Override
    public byte[] encode() {
        byte[] bytes = new byte[4];
        short num = 11; //ERROR OPCODE
        bytes[0] = (byte)((num >> 8) & 0xFF);
        bytes[1] = (byte)(num & 0xFF);
        short num2 = (short) msg.getNumMsg(); //Msg OPCODE
        bytes[2] = (byte)((num2 >> 8) & 0xFF);
        bytes[3] = (byte)(num2 & 0xFF);
        return bytes;
    }
}
