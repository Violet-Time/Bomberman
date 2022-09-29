# Bomberman

Bomberman is a multiplayer remake of a classic game known as Bomberman. The server is written in java using the spring boot framework. When a page is requested, a client written in JavaScript is loaded. All calculations are performed on the server, objects are sent to the client in the form of JSON. The client only sends the player's actions to the server. The interaction between the client and the server takes place using the web socket protocol.
Several game sessions can be created on the server, each session has 4 players. Sessions are collected by matchmaker. If after a certain amount of time the required number of players is not recruited, then bots are created.

Demo: http://bomberscw.herokuapp.com/
