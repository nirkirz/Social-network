#include <stdlib.h>
#include <connectionHandler.h>
#include <KeyBoardSender.h>
#include <Reader.h>
#include <thread>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
//    std::string host = "127.0.0.1";
//    short port = 7777;

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }


    bool validToLogOut = false;
    KeyBoardSender keyboard(&connectionHandler, &validToLogOut);
    Reader reader(&connectionHandler , &validToLogOut);
    std::thread th1(std::ref(keyboard));
    std::thread th2(std::ref(reader));

    th2.join();
    th1.join();

    return 0;
}
