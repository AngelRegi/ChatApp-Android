package edu.uncc.hw08;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatSession> {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public ChatAdapter(@NonNull Context context, int resource, @NonNull List<ChatSession> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_chats_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewMsgBy = convertView.findViewById(R.id.textViewMsgBy);
            viewHolder.textViewMsgText = convertView.findViewById(R.id.textViewMsgText);

            viewHolder.textViewMsgOn = convertView.findViewById(R.id.textViewMsgOn);


            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        ChatSession chat = getItem(position);
        if(mAuth.getCurrentUser().getUid().equals(chat.getSenderId())) {
            viewHolder.textViewMsgBy.setText(chat.getReceiverName());
        } else {
            viewHolder.textViewMsgBy.setText(chat.getSenderName());
        }
        viewHolder.textViewMsgText.setText(chat.getLastSentMsg());
        viewHolder.textViewMsgOn.setText(chat.getLastSentDate());

        return convertView;
    }

    public static class ViewHolder {
        TextView textViewMsgBy;
        TextView textViewMsgText;
        TextView textViewMsgOn;

    }
}
