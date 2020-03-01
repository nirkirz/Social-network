package bgu.spl.net.impl.BGSServer.Messages;

public class MessageACK extends Message {

    private Message msg;
    private byte[] bytes ;
    //private int len;

    public MessageACK(Message msg){

        super((short) 10);
        this.msg=msg;
        //bytes = new byte[1 << 10];
        //len = 0;
    }
    @Override
    public boolean decode(byte nextByte) {
        return true; //**TODO ??
    }

    @Override
    public byte[] encode() {
        String s = "";
        byte[] bytes;
        byte[] bytesOp = new byte[4];
        short num = 10; //ACK OPCODE
        bytesOp[0] = (byte)((num >> 8) & 0xFF);
        bytesOp[1] = (byte)(num & 0xFF);
        short num2 = (short) msg.getNumMsg(); //Msg OPCODE
        bytesOp[2] = (byte)((num2 >> 8) & 0xFF);
        bytesOp[3] = (byte)(num2 & 0xFF);
        if(msg instanceof MessageFollow){
            byte[] bytesUsers = new byte[2];
            short numOfUsers = (short) ((MessageFollow) msg).getNumOfUsers();
            bytesUsers[0] = (byte)((numOfUsers >> 8) & 0xFF);
            bytesUsers[1] = (byte)(numOfUsers & 0xFF);
            for (int i=0; i<(((MessageFollow) msg).getUserNameList().size()); i++){
                s = s +(((MessageFollow) msg).getUserNameList().get(i))+ '\0';
            }
            byte[] bytesString = s.getBytes();
            bytes = addArrays(bytesString, bytesOp, bytesUsers);

        }
        else if(msg instanceof MessageUserList){
            byte[] bytesUsers = new byte[2];
            short numOfUsers = (short) ((MessageUserList) msg).getUserNameList().size();
            bytesUsers[0] = (byte)((numOfUsers >> 8) & 0xFF);
            bytesUsers[1] = (byte)(numOfUsers & 0xFF);
            for (int i=0; i<(((MessageUserList) msg).getUserNameList().size()); i++){
                s = s +(((MessageUserList) msg).getUserNameList().get(i))+ '\0';
            }
            byte[] bytesString = s.getBytes();
            bytes = addArrays(bytesString, bytesOp, bytesUsers);
        }
        else if(msg instanceof MessageSTAT){
            byte[] bytesUsers = new byte[6];
            short numOfPosts = (short)((MessageSTAT) msg).getNumOfPosts();
            short numOfFollowers = (short)((MessageSTAT) msg).getNumOfFollowers();
            short NumOfFollowing = (short)((MessageSTAT) msg).getNumOfFollowing();
            bytesUsers[0] = (byte)((numOfPosts >> 8) & 0xFF);
            bytesUsers[1] = (byte)(numOfPosts & 0xFF);
            bytesUsers[2] = (byte)((numOfFollowers >> 8) & 0xFF);
            bytesUsers[3] = (byte)(numOfFollowers & 0xFF);
            bytesUsers[4] = (byte)((NumOfFollowing >> 8) & 0xFF);
            bytesUsers[5] = (byte)(NumOfFollowing & 0xFF);
            bytes = new byte[bytesUsers.length+6];
            System.arraycopy(bytesOp, 0, bytes,0, bytesOp.length);
            System.arraycopy(bytesUsers, 0, bytes,bytesOp.length, bytesUsers.length);
            //need to add /0?
        }
        else
            bytes = bytesOp;
        return bytes;
    }

    private byte[] addArrays(byte[] bytesString, byte[] bytesOp, byte[] bytesUsers){
        bytes = new byte[bytesString.length+6];
        System.arraycopy(bytesOp, 0, bytes,0, bytesOp.length);
        System.arraycopy(bytesUsers, 0, bytes,bytesOp.length, bytesUsers.length);
        System.arraycopy(bytesString, 0, bytes,bytesOp.length+bytesUsers.length, bytesString.length);
        return bytes;
    }


}
