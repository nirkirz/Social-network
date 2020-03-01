package bgu.spl.net.impl.BGSServer.BGSImpl;

import bgu.spl.net.impl.BGSServer.Messages.Message;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class User {

    private String name;
    private String password;
    private LinkedBlockingQueue<String> following;
    private LinkedBlockingQueue<String> followers;
    private BlockingQueue<Message> waitingNotifications;
    private List<Message> postedMessages;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.following = new LinkedBlockingQueue<>();
        this.followers = new LinkedBlockingQueue<>();
        waitingNotifications = new LinkedBlockingQueue<>();
        postedMessages = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LinkedBlockingQueue<String> getFollowing() {
        return following;
    }

    public void setFollowing(LinkedBlockingQueue<String> following) {
        this.following = following;
    }

    public LinkedBlockingQueue<String> getFollowers() {
        return followers;
    }

    public void setFollowers(LinkedBlockingQueue<String> followers) {
        this.followers = followers;
    }

    public BlockingQueue<Message> getWaitingNotifications() {
        return waitingNotifications;
    }

    public void setWaitingNotifications(BlockingQueue<Message> waitingNotifications) {
        this.waitingNotifications = waitingNotifications;
    }

    public List<Message> getPostedMessages(){
        return postedMessages;
    }
}
