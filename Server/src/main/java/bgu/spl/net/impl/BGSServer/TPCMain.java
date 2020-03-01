package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.BGSImpl.BGSMemory;
import bgu.spl.net.impl.BGSServer.BGSImpl.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.BGSServer.BGSImpl.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {
        BGSMemory memory = new BGSMemory(); //one shared object
        int port = Integer.parseInt(args[0]);
// you can use any server...
        Server.threadPerClient(
                port, //port
                () -> new BidiMessagingProtocolImpl(memory), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

    }
}
