//
// Created by shim@wincs.cs.bgu.ac.il on 12/28/18.
//


#include <boost/algorithm/string.hpp>
#include "KeyBoardSender.h"
#include <connectionHandler.h>
using namespace std;

KeyBoardSender::KeyBoardSender(ConnectionHandler* handler, bool validToLogOut) :
        _connectionHandler(handler), _validToLogOut(validToLogOut) {}

void KeyBoardSender::operator()(){
    bool logout = false;
    while (!logout){
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        char bytes[2];
        short num;
        std::vector<std::string> results;
        boost::split(results, line, [](char c){return c == ' ';});
        if (results[0] == "REGISTER" | results[0] == "LOGIN"){
            if(results[0] == "REGISTER")
                num = (short)1;
            else
                num = (short)2;
            bytes[0] = ((num >> 8) & 0xFF);
            bytes[1] = (num & 0xFF);
            _connectionHandler->sendBytes(bytes, 2);//opcode
            _connectionHandler->sendFrameAscii(results[1], '\0');//userName
            _connectionHandler->sendFrameAscii(results[2], '\0');//password
        }
        if (results[0] == "LOGOUT" | results[0] == "USERLIST"){
            if(results[0] == "LOGOUT") {
                num = (short) 3;
                if (_validToLogOut)
                    logout = true;
            }
            else
                num = (short)7;
            bytes[0] = ((num >> 8) & 0xFF);
            bytes[1] = (num & 0xFF);
            _connectionHandler->sendBytes(bytes, 2);//opcode
        }
        if (results[0] == "FOLLOW") {
            num = (short)4;
            bytes[0] = ((num >> 8) & 0xFF);
            bytes[1] = (num & 0xFF);
            _connectionHandler->sendBytes(bytes, 2);//opcode
            char a[1];
            if (results[1] == "0")
                a[0] = '0';
            else//UNFOLLOW
                a[0] = '1';
            _connectionHandler->sendBytes(a, 1); //follow/unfollow

            short numOfUsers = (short) std::stoi(results[2]);
            char bytesUsers[2];
            bytesUsers[0] = ((numOfUsers >> 8) & 0xFF);
            bytesUsers[1] = (numOfUsers & 0xFF);
            _connectionHandler->sendBytes(bytesUsers, 2);
            for(int i=3; i<results.size(); i++)//UserNameList
                _connectionHandler->sendFrameAscii(results[i], '\0');
        }
        if (results[0] == "POST") {
            num=(short)5;
            bytes[0] = ((num >> 8) & 0xFF);
            bytes[1] = (num & 0xFF);
            _connectionHandler->sendBytes(bytes, 2);//opcode
            size_t pos = 5;
            line = line.substr (pos);
            _connectionHandler->sendFrameAscii(line, '\0'); //content
        }
        if (results[0] == "PM"){
            num = (short)6;
            bytes[0] = ((num >> 8) & 0xFF);
            bytes[1] = (num & 0xFF);
            _connectionHandler->sendBytes(bytes, 2);//opcode
            _connectionHandler->sendFrameAscii(results[1], '\0');//userName
            size_t pos = 4+results[1].size();
            line = line.substr (pos);
            _connectionHandler->sendFrameAscii(line, '\0');//content
        }
        if (results[0] == "STAT"){
            num=(short)8;
            bytes[0] = ((num >> 8) & 0xFF);
            bytes[1] = (num & 0xFF);
            _connectionHandler->sendBytes(bytes, 2);//opcode
            _connectionHandler->sendFrameAscii(results[1], '\0');//userName
        }
    }

}


