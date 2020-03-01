package bgu.spl.net.impl.BGSServer.Messages;

public class MessageNotification extends Message {

    private char notificationType; //0 is PM, 1 is public(post)
    private String postingUser;
    private String content;
    private byte[] bytes ;
    private int len;

    public MessageNotification(char notificationType, String postingUser, String content){
        super((short) 9);
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
        bytes = new byte[1 << 10];
        len = 0;
    }

    @Override
    public boolean decode(byte nextByte) {
        return true; //**TODO ??
    }

    @Override
    public byte[] encode() {
        byte[] bytesOp = new byte[3];
        short num = 9; //adding the opcode
        bytesOp[0] = (byte)((num >> 8) & 0xFF);
        bytesOp[1] = (byte)(num & 0xFF);
        //adding the notification type
        bytesOp[2] =(byte)(notificationType & 0xFF);
        len =3;
        byte[] pu = postingUser.getBytes();
        byte[] cont = content.getBytes();
        bytes = new byte[3+pu.length+cont.length+2];

        System.arraycopy(bytesOp, 0, bytes,0, bytesOp.length);
        System.arraycopy(pu, 0, bytes,bytesOp.length, pu.length);
        len = bytesOp.length+pu.length;
        bytes[len] = '\0';

        System.arraycopy(cont, 0, bytes,len+1, cont.length);
        len = len + 1 + cont.length;
        bytes[len] = '\0';
//
//        if (bytes.length-3 < pu.length)
//            bytes = Arrays.copyOf(bytes, bytes.length+pu.length);
//        for(int i=0; i< pu.length; i++) {
//            bytes[i+len] = pu[i];
//        }
//        len = len +pu.length;
//
//        if (bytes.length-len < cont.length+2) //adding 2 times '\0'
//            bytes = Arrays.copyOf(bytes, bytes.length+cont.length+2);
//        bytes[len] = '\0'; //adding \0 after posting user
//        for(int i=0; i< cont.length; i++) {
//            bytes[i+len+1] = cont[i];
//        }
//        len = len + cont.length + 2;
//        bytes[len] = '\0'; //adding \0 at the end.
        return bytes;
    }

}

