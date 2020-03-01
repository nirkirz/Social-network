package bgu.spl.net.impl.BGSServer.BGSImpl;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionImpl<T> implements Connections<T> {

    private AtomicInteger connectionCounter;
    private ConcurrentHashMap<Integer, ConnectionHandler> conectionMap;

    public ConnectionImpl() {
        connectionCounter = new AtomicInteger(0);
        conectionMap = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean send(int connectionId, T msg) {//sync- for example, handle with the case of 2 notification need to send from the server to the same user
        if(conectionMap.isEmpty())
            return false;
        else{
            if(conectionMap.containsKey(connectionId))
            {
                conectionMap.get(connectionId).send(msg);
                return true;
            }

        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (Integer key : conectionMap.keySet()) {
            conectionMap.get(key).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if(conectionMap.containsKey(connectionId))
        {
            conectionMap.remove(connectionId);
        }
    }

    public ConcurrentHashMap<Integer, ConnectionHandler> getConectionMap() {
        return conectionMap;
    }

    public synchronized int add(ConnectionHandler ch){ //only 1 connection handler at a time
        connectionCounter.getAndIncrement();
        conectionMap.put(connectionCounter.intValue(), ch);
        return connectionCounter.intValue();
    }
}
