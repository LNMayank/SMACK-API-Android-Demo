package com.demoapp.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class ConnectXmpp extends Service implements MyXMPP.OnChatMessageServiceListener {

    private String userName;
    private String passWord;
    private MyXMPP xmpp = new MyXMPP();

    public ConnectXmpp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        System.out.println(">>> ConnectXmpp -> onBind : ");
        return new LocalBinder<ConnectXmpp>(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            userName = intent.getStringExtra("user");
            passWord = intent.getStringExtra("pwd");
            xmpp.init(userName, passWord);
            xmpp.setOnChatMessageServiceListener(this);
            xmpp.connectConnection();
        }
        return START_STICKY;
    }

    public void sendMessage(String message){
        xmpp.sendMsg(message);
    }

    @Override
    public void onDestroy() {
        xmpp.disconnectConnection();
        super.onDestroy();
    }

    @Override
    public void onLoginChatSuccess() {
        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(ChatActivity.ACTION_CHAT_AUTHENTICATED);
        intent.putExtra(ChatActivity.ARG_CHAT_AUTHENTICATED, true);
        sendBroadcast(intent);
    }

    @Override
    public void onChatMessageReceived(String message) {
        // update UI
        //Bundle up the intent and send the broadcast.
        Intent intent = new Intent(ChatActivity.ACTION_CHAT_MESSAGE);
        intent.putExtra(ChatActivity.ARG_CHAT_MESSAGE, message);
        sendBroadcast(intent);
    }
}
