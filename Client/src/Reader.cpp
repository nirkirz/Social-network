//
// Created by shim@wincs.cs.bgu.ac.il on 12/28/18.
//

#include "Reader.h"
#include <boost/algorithm/string.hpp>
#include <connectionHandler.h>
using namespace std;

Reader::Reader(ConnectionHandler* handler, bool shouldTerminate) : _connectionHandler(handler)
        , _validToLogOut(_validToLogOut), shouldTerminate(false){}

void  Reader::operator()() {
    string message;
    while(!shouldTerminate){
        char bytesArr[2];
        bool opTaken = _connectionHandler->getBytes(bytesArr, 2);
        if(opTaken) {
            short result = (short) ((bytesArr[0] & 0xff) << 8);
            result += (short) (bytesArr[1] & 0xff);
            string s = "";
            if (result == 9)//notification
            {
                s += "NOTIFICATION ";
                char type[1];
                bool boolType =_connectionHandler->getBytes(type, 1);
                if(boolType) {
                    if (*type == '1')
                        s += "Public ";
                    else
                        s += "PM ";
                    string str;
                    bool postingUser = _connectionHandler->getFrameAscii(str, '\0');
                    if (postingUser) {
                        s += str.substr(0, str.length()-1) + " ";
                        string str2;
                        bool content = _connectionHandler->getFrameAscii(str2, '\0');
                        if (content) {
                            s += str2.substr(0, str2.length()-1);
                        }
                    }
                }
            }
            else if (result == 10)//ACK
            {
                s += "ACK ";
                char bytes[2];
                bool Ack = _connectionHandler->getBytes(bytes, 2);
                if(Ack) {
                    short num = (short) ((bytes[0] & 0xff) << 8);
                    num += (short) (bytes[1] & 0xff);
                    s += to_string(num); //Type of ACK Message
                    if (num == 4 | num == 7){//FOLLOW OR USERLIST
                        char bytesNumOfUsers[2];
                        bool numOfUserFlag = _connectionHandler->getBytes(bytesNumOfUsers, 2);
                        if(numOfUserFlag) {
                            short numOfUser = (short) ((bytesNumOfUsers[0] & 0xff) << 8);
                            numOfUser += (short) (bytesNumOfUsers[1] & 0xff);
                            s += " " +to_string(numOfUser); //num of users
                            for(unsigned int i=0; i<numOfUser ; i++){
                                string username;
                                bool content = _connectionHandler->getFrameAscii(username, '\0');
                                if (content){
                                    s += " " +username.substr(0, username.length()-1);
                                }
                            }
                        }
                    }
                    else if(num == 8){//STAT
                        char bytesStat[6];
                        bool STAT = _connectionHandler->getBytes(bytesStat, 6);
                        if(STAT){
                            short NumPosts = (short) ((bytesStat[0] & 0xff) << 8);
                            NumPosts += (short) (bytesStat[1] & 0xff);
                            short NumFollowers = (short) ((bytesStat[2] & 0xff) << 8);
                            NumFollowers += (short) (bytesStat[3] & 0xff);
                            short NumFollowing = (short) ((bytesStat[4] & 0xff) << 8);
                            NumFollowing += (short) (bytesStat[5] & 0xff);
                            s += " " +to_string(NumPosts);
                            s += " " + to_string(NumFollowers);
                            s += " " + to_string(NumFollowing);
                        }
                    }
                    else if (num == 3){ //LOGOUT
                        shouldTerminate = true;
                    }
                    else if (num == 2) //ACK FOR LOGIN
                        _validToLogOut = true;


                }
            }
            else if (result == 11)//ERROR
            {
                s += "ERROR ";
                char bytes[2];
                bool Error = _connectionHandler->getBytes(bytes, 2);
                if(Error) {
                    short errorNum = (short) ((bytes[0] & 0xff) << 8);
                    errorNum += (short) (bytes[1] & 0xff);
                    s += to_string(errorNum);
                }
            }
            cout << s << endl;

        }
    }
}