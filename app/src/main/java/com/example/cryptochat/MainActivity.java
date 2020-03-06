package com.example.cryptochat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.$Gson$Preconditions;

public class MainActivity extends AppCompatActivity {
    Server server;
    MessageController controller;
    static String myName;
    TextView usersOnline;

    @Override
    protected void onStart() {
        super.onStart();
        server = new Server(
                new Consumer<Pair<String, String>>() {
                    @Override
                    public void accept(final Pair<String, String> pair) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                controller.addMessage(
                                    new MessageController.Message(
                                            pair.first,
                                            pair.second,
                                            false
                                    )
                                );
                            }
                        });
                    }
        },
                new Consumer<Pair<String, Long>>() {
                    @Override
                    public void accept(final Pair<String, Long> pair) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Context context = getApplicationContext();
                                usersOnline.setText(String.valueOf( pair.second) );

                                CharSequence text = pair.first;
                                if (text != "") {
                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.setGravity(Gravity.TOP, 0, 140);
                                    toast.show();
                                }
                            }
                        });
                    }
                });

        server.connect();


    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersOnline = findViewById(R.id.usersOnline);
        RecyclerView chatWindow = findViewById(R.id.chatWindow);
        controller = new com.example.cryptochat.MessageController();
        controller.setIncomingLayout(R.layout.message)
                .setOutgoingLayout(R.layout.outgoing_message)
                .setMessageTextId(R.id.messageText)
                .setUserNameId(R.id.userName)
                .setMessageTimeId(R.id.messageTime)
                .appendTo(chatWindow, this);


        final EditText chatInput = findViewById(R.id.chatInput);
        Button sendMessage = findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = chatInput.getText().toString();
                controller.addMessage(
                        new MessageController.Message(
                                text,
                                myName,
                                true
                        )
                );
                server.sendMessage( text );
                chatInput.setText("");
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name:");
        final EditText nameInput = new EditText(this);
        builder.setView(nameInput);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myName = nameInput.getText().toString();
                server.sendName(myName);
            }
        });

        builder.show();
    }
}
