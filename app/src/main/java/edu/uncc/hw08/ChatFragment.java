package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentChatBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CHAT_SESSION = "ARG_CHAT_SESSION";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename and change types of parameters
    private ChatSession mChatSession;
    ArrayList<Object> mChatMessages = new ArrayList<>();

    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance(ChatSession chatSession) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHAT_SESSION, chatSession);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChatSession = (ChatSession) getArguments().getSerializable(ARG_CHAT_SESSION);
        }
    }

    FragmentChatBinding binding;
    MessageAdapter messageAdapter;
    ArrayList<ChatMessage> mForums = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter();
        binding.recyclerView.setAdapter(messageAdapter);
        mChatMessages = mChatSession.messages;
        messageAdapter.notifyDataSetChanged();
        binding.buttonDeleteChat.setOnClickListener(v -> {
            mChatMessages.clear();
            deleteChatMessages(mChatMessages);
        });
        binding.buttonClose.setOnClickListener(v -> {
                mListener.goBackToChats();
        });
        binding.buttonSubmit.setOnClickListener(v -> {
            String message = binding.editTextMessage.getText().toString();
            if(message.isEmpty()) {
                Toast.makeText(getContext(), "Please enter the chat message", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.editTextMessage.setText("");
            addMessageToChatSession();
        });
        if(mChatSession.getSenderId().equals(mAuth.getCurrentUser().getUid())) {
            getActivity().setTitle("Chat - " + mChatSession.getReceiverName());
        } else {
            getActivity().setTitle("Chat - " + mChatSession.getSenderName());
        }
        //getForums();
    }
    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new MessageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Object msg = mChatMessages.get(position);
            holder.setupUI(msg);
        }

        @Override
        public int getItemCount() {
            return mChatMessages.size();
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            ChatListItemBinding mBinding;
            Object mChatMessage;
            public MessageViewHolder(ChatListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Object chatMessage){
                HashMap<String , Object> mChatMessage = ((HashMap) chatMessage);


                mBinding.textViewMsgOn.setText(mChatMessage.get("creationDate") + "");
                mBinding.textViewMsgText.setText(mChatMessage.get("message") + "");
                String userUid = mAuth.getCurrentUser().getUid();

                if(mChatMessage.get("senderId").equals(userUid)) {
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                    mBinding.textViewMsgBy.setText("Me");
                } else {
                    mBinding.imageViewDelete.setVisibility(View.INVISIBLE);
                    mBinding.textViewMsgBy.setText(mChatMessage.get("senderName") + "");
                }

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(Object message: mChatMessages) {
                            HashMap<String , Object> msgMap = ((HashMap) message);
                            if(msgMap.get("messageId").equals(mChatMessage.get("messageId"))) {
                                mChatMessages.remove(message);
                                messageAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        deleteChatMessages(mChatMessages);

                    }
                });

            }
        }

    }
    void deleteChatMessages(ArrayList<Object> messages) {
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("messages", messages);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //realtime data
        db.collection("chats").document(mChatSession.getChatId())
                .update(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("demo", "onComplete: delete");
                            //getPosts();
                            messageAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Error deleting post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void addMessageToChatSession() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> messageMap = new HashMap<>();
        HashMap<String, Object> dataMap = new HashMap<>();
        messageMap.put("senderId", mAuth.getCurrentUser().getUid());
        messageMap.put("senderName", mAuth.getCurrentUser().getDisplayName());
        if(mAuth.getCurrentUser().getUid().equals(mChatSession.getSenderId())) {
            messageMap.put("receiverId", mChatSession.getReceiverId());
            messageMap.put("receiverName", mChatSession.getReceiverName());
        } else {
            messageMap.put("receiverId", mChatSession.getSenderId());
            messageMap.put("receiverName", mChatSession.getSenderName());
        }
        //messageMap.put("receiverId", receiver.getUid());
        //messageMap.put("receiverName", receiver.getName());
        Date postDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        String strDate = formatter.format(postDate);
        messageMap.put("creationDate", strDate);
        messageMap.put("message", binding.editTextMessage.getText().toString());
        messageMap.put("messageId", UUID.randomUUID().toString());
        mChatMessages.add(messageMap);
        dataMap.put("messages", mChatMessages);
        //realtime data
        db.collection("chats").document(mChatSession.getChatId())
                .update(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("demo", "onComplete: delete");
                            //getPosts();
                            messageAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Error deleting post", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    ChatListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ChatListener) context;
    }

    interface ChatListener {
        void goBackToChats();
    }
}