package com.demoapp.messenger;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;

/**
 * Created by Vinh.Tran on 11/11/16.
 */

public class ChatActivity extends AppCompatActivity {

    public static final String ACTION_CHAT_MESSAGE = "ACTION_CHAT_MESSAGE";
    public static final String ARG_CHAT_MESSAGE = "ARG_CHAT_MESSAGE";

    public static final String ACTION_CHAT_AUTHENTICATED = "ACTION_CHAT_AUTHENTICATED";
    public static final String ARG_CHAT_AUTHENTICATED = "ARG_CHAT_AUTHENTICATED";

    private TextMessage mTextMessage;
    private SlyceMessagingFragment mSlyceMessagingFragment;

    private ConnectXmpp mXmppChatService;
    private boolean mBound;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println(">>> ChatActivity -> onServiceConnected : ");
            LocalBinder binder = (LocalBinder) service;
            mXmppChatService = (ConnectXmpp) binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSlyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.messaging_fragment);
        //slyceMessagingFragment.setStyle(R.style.ChatTheme);
        mSlyceMessagingFragment.setDefaultDisplayName("VinhTT");
        mSlyceMessagingFragment.setDefaultAvatarUrl("");
        mSlyceMessagingFragment.setDefaultUserId("1");

        mTextMessage = new TextMessage();
        mTextMessage.setAvatarUrl("");
        mTextMessage.setDisplayName("Vlab");
        mTextMessage.setUserId("2");
        mTextMessage.setDate(new Date().getTime());
        mTextMessage.setSource(MessageSource.EXTERNAL_USER);

        mSlyceMessagingFragment.addNewMessage(mTextMessage);

        mSlyceMessagingFragment.setOnSendMessageListener(new UserSendsMessageListener() {
            @Override
            public void onUserSendsTextMessage(String text) {
                // TODO
                mXmppChatService.sendMessage(text);
            }

            @Override
            public void onUserSendsMediaMessage(Uri imageUri) {

            }
        });

        Intent intent = new Intent(this, ConnectXmpp.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mChatMessageReceiver, new IntentFilter(ACTION_CHAT_MESSAGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mChatMessageReceiver);
    }

    BroadcastReceiver mChatMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTextMessage.setText(intent.getStringExtra(ARG_CHAT_MESSAGE));
            mSlyceMessagingFragment.addNewMessage(mTextMessage);
        }
    };
}


