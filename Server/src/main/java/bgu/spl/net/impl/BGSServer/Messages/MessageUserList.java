package bgu.spl.net.impl.BGSServer.Messages;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageUserList extends Message {

    private List<String> userNameList;

    public MessageUserList() {
        super((short) 7);
    }

    public boolean decode(byte nextByte) {
        return true;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    public void setUserNameList(List<String> userNameList) { //getting sorted list by registration order
        this.userNameList = userNameList;
    }

    public List<String> getUserNameList() {
        return userNameList;
    }
}
