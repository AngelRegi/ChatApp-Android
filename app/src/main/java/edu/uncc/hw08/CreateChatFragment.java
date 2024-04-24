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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import edu.uncc.hw08.databinding.FragmentCreateChatBinding;
import edu.uncc.hw08.databinding.FragmentMyChatsBinding;


public class CreateChatFragment extends Fragment {

    FragmentCreateChatBinding binding;
    private FirebaseAuth mAuth;
    ArrayList<User> users = new ArrayList<>();
    UserAdapter adapter;
    User receiver;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateChatFragment newInstance(String param1, String param2) {
        CreateChatFragment fragment = new CreateChatFragment();
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
        binding = FragmentCreateChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("New Chat");
        binding.buttonSubmit.setOnClickListener(v -> {
            String message = binding.editTextMessage.getText().toString();
            if(message.isEmpty()) {
                Toast.makeText(getContext(), "Please enter the chat message", Toast.LENGTH_SHORT).show();
                return;
            }
            if(receiver == null) {
                Toast.makeText(getContext(), "Please select a user to chat", Toast.LENGTH_SHORT).show();
                return;
            }
            createChat();

        });
        binding.buttonCancel.setOnClickListener(v -> {
            mListener.goBackToChats();
        });
        ListView listView  = binding.listView;
        adapter = new UserAdapter(getActivity(), R.layout.users_row_item, users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, listview, position, id) -> {
            receiver = users.get(position);
            binding.textViewSelectedUser.setText(receiver.getName());

        });
        getUsers();
    }
    void createChat() {
        HashMap<String, Object> chat = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        chat.put("senderId", mAuth.getCurrentUser().getUid());
        chat.put("senderName", mAuth.getCurrentUser().getDisplayName());
        chat.put("receiverId", receiver.getUid());
        chat.put("receiverName", receiver.getName());
        ArrayList<Object> messages = new ArrayList<>();
        Date postDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        String strDate = formatter.format(postDate);
        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("senderId", mAuth.getCurrentUser().getUid());
        messageMap.put("senderName", mAuth.getCurrentUser().getDisplayName());
        messageMap.put("receiverId", receiver.getUid());
        messageMap.put("receiverName", receiver.getName());
        messageMap.put("creationDate", strDate);
        messageMap.put("message", binding.editTextMessage.getText().toString());
        messageMap.put("messageId", UUID.randomUUID().toString());
        messages.add(messageMap);
        chat.put("messages", messages);

        chat.put("lastSentMsg",binding.editTextMessage.getText().toString());
        chat.put("lastSentDate", strDate);
        chat.put("creationDate", strDate);

        db.collection("chats").add(chat)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            Log.d("demo", "onSuccess: created");
                            //binding.editTextPostText.setText("");
                            mListener.goBackToChats();

                        } else {
                            Toast.makeText(getActivity(), "Error Creating the Chat Session", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
   void getUsers()  {
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       mAuth = FirebaseAuth.getInstance();

       db.collection("users")
               .addSnapshotListener(new EventListener<QuerySnapshot>() {
                   @Override
                   public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                       users.clear();
                       for (QueryDocumentSnapshot document : value) {
                           if (mAuth.getCurrentUser().getUid().equals(document.getString("uid"))) {
                                continue;
                           }
                           users.add(new User(document.getString("name"), document.getString("uid"), document.getString("email"), document.getBoolean("isLoggedIn")));

                           adapter.notifyDataSetChanged();
                       }
                   }
               });
   }

   CreateChatListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateChatListener) context;
    }

    interface CreateChatListener {
        void goBackToChats();
    }
}