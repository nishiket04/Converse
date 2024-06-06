package com.nishiket.converse.view;


import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.nishiket.converse.ChatApplication;
import com.nishiket.converse.R;
import com.nishiket.converse.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = getWindow(); // get os status bar and navigation bar and screen
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView()); // gets its view and controller

        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(false); // White font color on status bar
            windowInsetsController.setAppearanceLightNavigationBars(false); // set font color white
        }
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
//        mSocket.on(Socket.EVENT_CONNECT, args -> Log.d("SocketIO", "Connected"));
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> Log.d("SocketIO", "Connection Error: " + args[0]));
//        mSocket.on(Socket.EVENT_DISCONNECT, args -> Log.d("SocketIO", "Disconnected"));
        mSocket.connect();
        binding.text.setText(""+mSocket.id());
        mSocket.on("chat message", onNewMessage);
        mSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("SocketIO", "Connected");
            JSONObject messageObject = new JSONObject();
            try {
                messageObject.put("message", "Hello");
//                Log.d("SocketIO", "Emitting message: " + messageObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("SocketIO", "Failed to create JSON object: " + e.getMessage());
            }
        });
        mSocket.emit("chat message", "45");

    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        String message;
                        try {
                            message = data.getString("message");
                            Log.d("SocketIO", "Received message: " + message);
                            // Handle the message as needed
                        } catch (JSONException e) {
                            Log.e("SocketIO", "JSON parsing error: " + e.getMessage());
                        }
                    } else {
                        Log.e("SocketIO", "Received data is not a JSONObject");
                    }
                }
            });
        }
    };
}