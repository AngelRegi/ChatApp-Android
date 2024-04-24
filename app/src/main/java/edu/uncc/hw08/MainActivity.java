package edu.uncc.hw08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements MyChatsFragment.MyChatsListener, CreateChatFragment.CreateChatListener, ChatFragment.ChatListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new MyChatsFragment())
                .commit();
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void openNewChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateChatFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openChat(ChatSession chatSession) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ChatFragment.newInstance(chatSession))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goBackToChats() {
        getSupportFragmentManager().popBackStack();
    }
}