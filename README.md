ClientServerMiniGame
====================

In this assignment, I will work on the application layer over UDP. The goal is to implement a system comprising of a game server and clients. The game in question is tic-tac-toe.
The assignment can be broadly divided into 3 parts. In the 1st part, I will be designing a system with a server and clients. The clients can basically login to the server, and play with other clients using the server. In the 2nd part, the clients-server system will be passed through an unreliable channel (which is provided by us) so that packets are dropped from the client to the server NOT vice-versa. This is to test whether the acknowledgement protocol devised by you is robust enough to handle packet losses. The last part which is not connected to the 2nd part is to use a proxy server (also provided by us) to connect from the client to the game server.

