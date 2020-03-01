//
// Created by shim@wincs.cs.bgu.ac.il on 12/28/18.
//

#ifndef BOOST_ECHO_CLIENT_KEYBOARDSENDER_H
#define BOOST_ECHO_CLIENT_KEYBOARDSENDER_H


#include "connectionHandler.h"

class KeyBoardSender {

private:
    ConnectionHandler* _connectionHandler;
    bool _validToLogOut;

public:
    KeyBoardSender(ConnectionHandler* handle, bool validToLogOut);
    void operator()();

};


#endif //BOOST_ECHO_CLIENT_KEYBOARDSENDER_H
