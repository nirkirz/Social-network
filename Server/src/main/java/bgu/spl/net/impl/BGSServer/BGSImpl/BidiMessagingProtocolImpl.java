package bgu.spl.net.impl.BGSServer.BGSImpl;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Messages.*;
import bgu.spl.net.srv.BlockingConnectionHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private ConnectionImpl connections = new ConnectionImpl();
    private int connectionId;
    private BGSMemory memory;
    private boolean shouldTerminate;
    private Object LockLogOut;

    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/

    public BidiMessagingProtocolImpl(BGSMemory memory){ //REMEMBER TO SEND IN THE SERVER.serve() the BGSmemory in the constructor
        this.memory = memory;
    }


    public void start(int connectionId, Connections connections) {
        this.connections = (ConnectionImpl)connections;
        this.connectionId = connectionId;
        this.shouldTerminate = false;
        LockLogOut = new Object();
    }

    @Override
    public void process(Message message) {
        Message retMsg;
        if (message instanceof MessageRegister)
            retMsg = processRegister((MessageRegister)message);
        else {
            if (message instanceof MessageLogin)
                retMsg = processLogIn((MessageLogin) message);
            else {
                if (message instanceof MessageLogOut)
                    retMsg = processLogOut((MessageLogOut) message);
                else {
                    if (message instanceof MessageUserList)
                        retMsg = processUserList((MessageUserList) message);
                    else {
                        if (message instanceof MessageFollow)
                            retMsg = processFollow((MessageFollow) message);
                        else {
                            if (message instanceof MessagePost)
                                retMsg = processPost((MessagePost) message);
                            else {
                                if (message instanceof MessagePM)
                                    retMsg = processPM((MessagePM) message);
                                else
                                    retMsg = processSTAT((MessageSTAT) message);
                            }
                        }
                    }
                }
            }

        }
        connections.send(connectionId, retMsg);
        if (retMsg instanceof MessageLogOut) {
            try {
                ((BlockingConnectionHandler)connections.getConectionMap().get(connectionId)).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return true if the connection should be terminated
     */
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private Message processRegister(MessageRegister message){
        Message retMsg = new MessageError(message);
        String s = memory.containsLogged(connectionId);
        if(s == null) { //checking if is not loggedIn
            if (!memory.containsUser(message.getName()) && memory.addUser(message.getName(), message.getPassword(), connectionId)) {
                //if the user is not registered already && successfully added
                retMsg = new MessageACK(message);
            }
        }
        return retMsg;
    }

    private Message processLogIn(MessageLogin message) { //if log in - receive pm and post with @this and post that he follows
        Message retMsg;
        if (!memory.containsUser(message.getName())) //if the user is not registered
            retMsg = new MessageError(message);
        else {
            synchronized (memory.getRegisteredUsers().get(message.getName())) { //sync on the username object in the registeredMap
                if (!memory.containsLogged(message.getName()) && (memory.containsLogged(connectionId) == null)
                        && (message.getPassword().equals(memory.getRegisteredUsers().get(message.getName()).getPassword()))) {
                    //this user is not logged in, and the connectionId is not connected to otherUser and the password is correct
                    retMsg = new MessageACK(message);
                    memory.logInUser(connectionId, message.getName());
                    BlockingQueue<Message> queue = memory.getWaitingNotifications(message.getName());
                    while (!queue.isEmpty()) {
                        connections.send(connectionId, queue.poll());
                    }

                } else //the user is registered but already logged in
                    retMsg = new MessageError(message);
            }
        }
        return retMsg;
    }
    private Message processLogOut(MessageLogOut message){
        Message retMsg;
        String s = memory.containsLogged(connectionId);
        if(s != null) { //checking if is loggedIn
            //synchronized (memory.getRegisteredUsers().get(s)){ //sync on the user object
            memory.logOutUser(s);
            retMsg = new MessageACK(message);
            shouldTerminate = true;
            //}
        }
        else
            retMsg = new MessageError(message);
        return retMsg;
    }

    private Message processFollow(MessageFollow message) {
        Message retMsg = new MessageError(message);
        List<String> l;
        String s = memory.containsLogged(connectionId);
        if(s != null) { //checking if is loggedIn
            if (message.getIsFollow())
                l = memory.addFollowing(s, message.getUserNameList());
            else
                l = memory.removeFollowing(s, message.getUserNameList());
            if(!l.isEmpty()) { //un/followed at least 1 user
                message.setNumOfUsers(l.size());
                message.setUserNameList(l);
                retMsg = new MessageACK(message);
            }
        }
        return retMsg;
    }

    private Message processPost(MessagePost message){
        Message retMsg = new MessageError(message);
        List<String> lnames = new LinkedList<>(); //marking the users that already got notification for the post, and avoiding double sending
        String username = memory.containsLogged(connectionId); //the username of the poster
        if(username != null) {//checking if is loggedIn
            memory.savePost(username, message);
            //Handling followers
            for (String key : memory.getRegisteredUsers().get(username).getFollowers()) { //username followers list
                //synchronized (memory.getRegisteredUsers().get(key)) { //sync on the follower user object
                    lnames.add(key); //adding to list
                    SendNotification(key, username, message); //if online - send now, else, add to the waiting list
                //}
            }
            //Handling the post content and tagged users and checking for double notification sending
            String content = message.getContent();
            String tempuser = "";
            while (content.contains("@")) {
                content = content.substring(content.indexOf("@") + 1);
                if (content.contains(" ")) {
                    tempuser = content.substring(0, content.indexOf(' ')); //0 == @, thats why 1
                    content = content.substring(content.indexOf(' '));
                } else { //no more @ in content
                    tempuser = content;
                    content = "";
                }

                if (memory.getRegisteredUsers().containsKey(tempuser)) {//checking if the tagged user is registered
                    if (!memory.getRegisteredUsers().get(username).getFollowers().contains(tempuser) && !lnames.contains(tempuser)) {//checking if the tagged user is not following and already received notification
                        //synchronized (memory.getRegisteredUsers().get(tempuser)) { //sync on the tagged user object only if he is registered and didnt received the notification so far
                            lnames.add(tempuser);
                            SendNotification(tempuser, username, message);
                        //}
                    }
                }
            } //while loop
            retMsg = new MessageACK(message);
        }
        return retMsg;
    }

    private void SendNotification(String userToSend, String Sender, MessagePost message){//if the userToSend is online - send now, else, add to his the waiting list
        if (memory.getLoggedInUsers().containsKey(userToSend))
            connections.send(memory.getConnectionIdFromLogInUser(userToSend), (new MessageNotification('1', Sender, message.getContent())));
        else
            try {
                memory.getRegisteredUsers().get(userToSend).getWaitingNotifications().put(new MessageNotification('1', Sender, message.getContent()));
            } catch (InterruptedException e) {
            }

    }
    private Message processPM(MessagePM message){
        Message retMsg = new MessageError(message);
        String postingUser = memory.containsLogged(connectionId);
        if(postingUser != null) {//checking if is loggedIn
            if (memory.containsUser(message.getReceiver())) { //checking if the receiver is registered
                retMsg = new MessageACK(message);
                //synchronized (memory.getRegisteredUsers().get(message.getReceiver())) { //sync on the receiver object
                    if (memory.containsLogged(message.getReceiver())) { //if the receiver is logged in
                        connections.send(memory.getConnectionIdFromLogInUser(message.getReceiver()), (new MessageNotification('0', postingUser, message.getContent())));
                    } else //else, add to waiting
                    {
                        try {
                            memory.getRegisteredUsers().get(message.getReceiver()).getWaitingNotifications().put(new MessageNotification('0',postingUser, message.getContent()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                //} //end sync
            }
        }
        return retMsg;
    }

    private Message processSTAT(MessageSTAT message){
        Message retMsg = new MessageError(message);
        String username = memory.containsLogged(connectionId);
        if(username != null) {//checking if is loggedIn
            if(memory.containsUser(message.getName())){ //checking if there is a username with that name
                List<Integer> l = memory.getSTAT(message.getName());
                message.setNumOfPosts(l.get(0));
                message.setNumOfFollowers(l.get(1));
                message.setNumOfFollowing(l.get(2));
                retMsg = new MessageACK(message);
            }
        }
        return retMsg;
    }

    private Message processUserList(MessageUserList message){
        Message retMsg;
        String s = memory.containsLogged(connectionId);
        if(s != null) { //checking if is loggedIn
            message.setUserNameList(memory.getRegList()); //added sorted registered user list
            retMsg = new MessageACK(message);
        }
        else
            retMsg = new MessageError(message);
        return retMsg;
    }
}
