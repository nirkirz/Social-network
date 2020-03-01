CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o  bin/KeyBoardSender.o bin/Reader.o bin/BGSclient.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/KeyBoardSender.o bin/Reader.o bin/BGSclient.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/KeyBoardSender.o: src/KeyBoardSender.cpp
	g++ $(CFLAGS) -o bin/KeyBoardSender.o src/KeyBoardSender.cpp

bin/Reader.o: src/Reader.cpp
	g++ $(CFLAGS) -o bin/Reader.o src/Reader.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
