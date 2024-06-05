package com.nishiket.converse;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatApplication extends Application { // This is a subclass of Application which contains instace of Socket
    private Socket mSocket; // socket instance
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL); // initialize socket instance with server url
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() { // get socket instance method
        return mSocket;
    }
}
