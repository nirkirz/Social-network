package bgu.spl.net.impl.BGSServer.Messages;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageSTAT extends Message {

    private byte[] bytes ;
    private int len ;
    private String name;
    private int numOfPosts;
    private int numOfFollowers;
    private int numOfFollowing;


    public MessageSTAT(){
        super((short) 8);
        name = "";
        bytes = new byte[1 << 10];
        len = 0;
        numOfFollowing = -1;
    }

    public boolean decode(byte nextByte) {
        if (nextByte == '\0') {
            name = new String(bytes, 0, len, StandardCharsets.UTF_8);
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

    public int getNumOfPosts() {
        return numOfPosts;
    }

    public void setNumOfPosts(int numOfPosts) {
        this.numOfPosts = numOfPosts;
    }

    public int getNumOfFollowers() {
        return numOfFollowers;
    }

    public void setNumOfFollowers(int numOfFollowers) {
        this.numOfFollowers = numOfFollowers;
    }

    public int getNumOfFollowing() {
        return numOfFollowing;
    }

    public void setNumOfFollowing(int numOfFollowing) {
        this.numOfFollowing = numOfFollowing;
    }

    public String getName() {
        return name;
    }
}
