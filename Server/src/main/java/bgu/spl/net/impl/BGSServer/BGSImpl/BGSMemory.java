package bgu.spl.net.impl.BGSServer.BGSImpl;

import bgu.spl.net.impl.BGSServer.Messages.Message;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class BGSMemory {


    public BGSMemory() {
    }

    private ConcurrentHashMap<String, String> usersMap = new ConcurrentHashMap<>(); //username , password
    private ConcurrentHashMap<String, User> registeredUsers = new ConcurrentHashMap<>();
    private List<String> registeredUsersNames = new LinkedList<>();
    private ConcurrentHashMap<String, Integer> loggedInUsers=new ConcurrentHashMap<>(); //connectionId, username

    public boolean containsUser(String username){
        return (usersMap.containsKey(username));
    }

    public ConcurrentHashMap<String, Integer> getLoggedInUsers() {
        return loggedInUsers;
    }

    public boolean addUser(String username, String password, int connectionId){
        if(usersMap.putIfAbsent(username,password) == null) {
            registeredUsers.put(username, new User(username, password));
            synchronized (registeredUsersNames) {
                registeredUsersNames.add(username);
            }
            return true;
        }
        return false;
    }

    public void logInUser(int connectionId, String username){
        loggedInUsers.put(username, connectionId);
    }

    public boolean containsLogged(String username){
        return (loggedInUsers.containsKey(username));
    }

    public String containsLogged(int connectionId){
        String s = null;
        for (Map.Entry<String, Integer> entry : loggedInUsers.entrySet()) {
            if (entry.getValue() == connectionId){
                s = entry.getKey();
                break;
            }
        }
        return s;
    }

    public ConcurrentHashMap<String, User> getRegisteredUsers() {
        return registeredUsers;
    }

    public int getConnectionIdFromLogInUser(String username){
        return loggedInUsers.get(username);
    }

    public void logOutUser(String username){
        loggedInUsers.remove(username);
    }

    public List<String> getRegList(){
        return registeredUsersNames;
    }

    public List<String> addFollowing(String username, List<String> userList){
        List<String> retList = new LinkedList<>();
        User curr = registeredUsers.get(username); //the user to add
        for (String key : userList) {
            if (registeredUsers.get(key) != null){ //there is user with that name to follow

                if (!curr.getFollowing().contains(key)) {
                    curr.getFollowing().add(key); //add the user to the following list
                    retList.add(key);
                    registeredUsers.get(key).getFollowers().add(curr.getName());
                }
            }
        }
        return retList;
    }

    public List<String> removeFollowing(String username, List<String> userList) {
        List<String> retList = new LinkedList<>();
        User curr = registeredUsers.get(username);
        for (String key : userList) {
            if (registeredUsers.get(key) != null) { //there is user with that name to follow
                if (curr.getFollowing().contains(key)) {
                    curr.getFollowing().remove(key); //remove the user to the following list
                    retList.add(key);
                    registeredUsers.get(key).getFollowers().remove(curr.getName());
                }
            }
        }
        return retList;
    }

    public void savePost(String username, Message msg){
        registeredUsers.get(username).getPostedMessages().add(msg);
    }

    public BlockingQueue<Message> getWaitingNotifications(String username){
        return registeredUsers.get(username).getWaitingNotifications();
    }

    public List<Integer> getSTAT(String username){
        List<Integer> l = new LinkedList<>();
        l.add(registeredUsers.get(username).getPostedMessages().size());
        l.add(registeredUsers.get(username).getFollowers().size());
        l.add(registeredUsers.get(username).getFollowing().size());
        return l;
    }

}
