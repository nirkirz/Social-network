package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.BGSImpl.BGSMemory;
import bgu.spl.net.impl.BGSServer.BGSImpl.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.BGSServer.BGSImpl.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        BGSMemory memory = new BGSMemory(); //one shared object
        int num_of_threads = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);
        Server.reactor(
                num_of_threads,
                port, //port
                () -> new BidiMessagingProtocolImpl(memory), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

//
    }
}
