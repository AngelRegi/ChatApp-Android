package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentMyChatsBinding;

public class MyChatsFragment extends Fragment {

    FragmentMyChatsBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<ChatSession> chatSessions = new ArrayList<>();
    ChatAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyChatsFragment newInstance(String param1, String param2) {
        MyChatsFragment fragment = new MyChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Chats");
        binding.buttonLogout.setOnClickListener(v -> {
            updateUserLoginStatus();

        });

        binding.buttonNewChat.setOnClickListener(v -> {
            mListener.openNewChat();
        });

        ListView listView  = binding.listView;
        adapter = new ChatAdapter(getActivity(), R.layout.my_chats_list_item, chatSessions);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, listview, position, id) -> {
            mListener.openChat(chatSessions.get(position));

        });
        getMyChatSessions();
    }

    void getMyChatSessions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        db.collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        chatSessions.clear();

                        for (QueryDocumentSnapshot document : value) {
                            if(currentUserId.equals(document.getString("senderId")) || currentUserId.equals(document.getString("receiverId")) ) {
                                ChatSession chat = new ChatSession();
                                chat.creationDate = document.getString("creationDate");
                                chat.senderId = document.getString("senderId");
                                chat.receiverId = document.getString("receiverId");
                                chat.senderName = document.getString("senderName");
                                chat.receiverName = document.getString("receiverName");
                                ArrayList<Object> msgs = (ArrayList<Object>) document.get("messages");
                                chat.messages = msgs;
                                chat.lastSentMsg = document.getString("lastSentMsg");
                                chat.lastSentDate = document.getString("lastSentDate");
                                chat.chatId = document.getId();

                                chatSessions.add(chat);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
    void updateUserLoginStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put("isLoggedIn", false);
        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots) {
                            if(mAuth.getCurrentUser().getUid().equals(document.getString("uid"))) {

                                db.collection("users")
                                        .document(document.getId())
                                        .update(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                mListener.logout();
                                             }
                                        });
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo", "onFailure: ");
                    }
                });


    }
    MyChatsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (MyChatsListener) context;
    }

    interface MyChatsListener {
        void logout();
        // void goToCreateForumFragment();
       void openNewChat();
       void openChat(ChatSession chatSession);
    }
}