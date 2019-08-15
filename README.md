# Remote Server

This application is using the java-remote-object (https://github.com/vladaeloaiei/byte-remote-object) framework to expose an object through which a client interact with the hosting machine.

The server is written in Java and uses JNI for calling C++ code for the audio volume control.

The screen sharing is done using pipeline architecture which provides 20-30 FPS.

An implementation for client for Android can be found at: https://github.com/vladaeloaiei/remote-client

## Features
- assure single client connection active at a time
- real time screen sharing
- keyboard and mouse control
- multi file transfer
- audio volume control
- option to choose the communication protocol (TCP/UDP)
