package com.example.cryptochat;
import android.util.Log;

import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    WebSocketClient client;
    private Consumer<Pair<String, String>> messageConsumer;
    private Consumer<Pair<String, Long>> userConnectedConsumer;
    private Map<Long, String> nameMap = new ConcurrentHashMap<>();

    public Server(Consumer<Pair<String, String>> messageConsumer, Consumer<Pair<String, Long>> userConnectedConsumer) {
        this.messageConsumer = messageConsumer;
        this.userConnectedConsumer = userConnectedConsumer;
    }

    public void connect() {

        try {
            URI addr = new URI("ws://35.210.129.230:8881");

            client = new WebSocketClient(addr) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i("WSSERVER","Connected to server");
                }

                @Override
                public void onMessage(String json) {
                    int type = Protocol.getType(json);
                    if (type == Protocol.MESSAGE) {
                        onIncomingTextMessage(json);

                    //показать
                    } else if (type == Protocol.USER_STATUS) {
                        onStatusUpdate(json);
                        // обновился статус
                    } /* else if (type == Protocol.USER_NAME) {

                    } */
                    Log.i("WSSERVER","Received message "+json);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.i("WSSERVER","Connection closed");
                }

                @Override
                public void onError(Exception ex) {
                    Log.i("WSSERVER","Error occured:" + ex.getMessage());
                }
            };

            client.connect();
            } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private void onStatusUpdate(String json) {
        Protocol.UserStatus status = Protocol.unpackStatus(json);
        Protocol.User u = status.getUser();
        if (status.isConnected()) {
            nameMap.put(u.getId(), u.getName());
            userConnectedConsumer.accept(new Pair<String, Long>(u.getName()+" подключился к чату", Long.valueOf( nameMap.size() ) ));
        } else {
            userConnectedConsumer.accept(new Pair<String, Long>("", Long.valueOf( nameMap.size() ) ));
            nameMap.remove(u.getId());
        }
    }
    private void onIncomingTextMessage(String json) {
        Protocol.Message message = Protocol.unpackMessage(json);
        String name = nameMap.get(message.getSender());
        if (null == name) name = "Unnamed" + String.valueOf(message.getSender());
        messageConsumer.accept(new Pair<String, String>(message.getEncodedText(), name));

    }

    public void sendMessage(String messageText) {
        String json = Protocol.packMessage(new Protocol.Message(messageText));
        if (client != null && client.isOpen()) {
            client.send(json);
        }
    }

    public void sendName(String userName) {
        String json = Protocol.packName(new Protocol.UserName(userName));
        if (client != null && client.isOpen()) {
            client.send(json);
        }
    }
}
