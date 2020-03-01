package bgu.spl.net.impl.BGSServer.Messages;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageFollow extends Message {

    private Boolean isFollow ;
    private int numOfUsers ;
    private int usersCounter ;
    private List<String> userNameList ;
    private byte[] bytes;
    private int len;

    public MessageFollow(){
        super((short) 4);
        isFollow = null;
        numOfUsers = -1;
        usersCounter = 0;
        userNameList = new LinkedList<>();
        bytes = new byte[1 << 10];
        len = 0;
    }

    @Override
    public boolean decode(byte nextByte) {
        if (isFollow==null) {
            if(nextByte == '1')
                isFollow=false;
            else
                isFollow=true;
        }
        else{
            if(len==1 && numOfUsers == -1){
                bytes[len] = nextByte;
                numOfUsers = (short)((bytes[0] & 0xff) << 8);
                numOfUsers += (short) (bytes[1] & 0xff);
                bytes = new byte[1 << 10];
                len = 0;
            }
            else
            {
                if (nextByte == '\0' && numOfUsers != -1 ){
                    if(usersCounter != numOfUsers) {
                        usersCounter++;
                        userNameList.add(new String(bytes, 0,len, StandardCharsets.UTF_8));
                        bytes = new byte[1 << 10];
                        len = 0;
                    }
                }
                else {// not /0
                    if (len >= bytes.length) {
                        bytes = Arrays.copyOf(bytes, len * 2);
                    }
                    bytes[len] = nextByte;
                    len++;
                }
            }
        }
        if(usersCounter == numOfUsers) //finish decode
            return true;
        return false;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    public Boolean getIsFollow() {
        return isFollow;
    }

    public int getNumOfUsers() {
        return numOfUsers;
    }

    public int getUsersCounter() {
        return usersCounter;
    }

    public List<String> getUserNameList() {
        return userNameList;
    }

    public void setNumOfUsers(int numOfUsers) {
        this.numOfUsers = numOfUsers;
    }

    public void setUserNameList(List<String> userNameList) {
        this.userNameList = userNameList;
    }
}
