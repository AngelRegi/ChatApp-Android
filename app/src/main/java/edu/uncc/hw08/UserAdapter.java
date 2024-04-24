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

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.users_row_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
            viewHolder.imageViewOnline = convertView.findViewById(R.id.imageViewOnline);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        User user = getItem(position);
        Log.d("User", "getView: " + user);
        viewHolder.textViewName.setText(user.getName());
        if(user.getIsLoggedIn()) {
            viewHolder.imageViewOnline.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewOnline.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView textViewName;
        ImageView imageViewOnline;
    }
}
