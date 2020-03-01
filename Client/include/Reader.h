//
// Created by shim@wincs.cs.bgu.ac.il on 12/28/18.
//

#ifndef BOOST_ECHO_CLIENT_READER_H
#define BOOST_ECHO_CLIENT_READER_H


#include "connectionHandler.h"

class Reader {
private:
    ConnectionHandler* _connectionHandler;
    bool shouldTerminate;
    bool _validToLogOut;

public:
    Reader(ConnectionHandler* handler, bool validToLogOut);
    void operator()();
};


#endif //BOOST_ECHO_CLIENT_READER_H
