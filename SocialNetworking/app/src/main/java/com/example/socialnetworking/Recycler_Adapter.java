package com.example.socialnetworking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Recycler_Adapter extends RecyclerView.Adapter<Recycler_Adapter.ViewHolder> {

    private static final String Tag = "RecyclerView";

    private Context mContext;
    private ArrayList<Posts> postssList;

    public Recycler_Adapter(Context mContext, ArrayList<Posts> postssList) {
        this.mContext = mContext;
        this.postssList = postssList;
    }




    @NonNull
    @Override
    public Recycler_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);

        return new ViewHolder(view);





    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.date.setText(postssList.get(position).getDate());
        holder.time.setText(postssList.get(position).getTime());
        holder.description.setText(postssList.get(position).getDescription());
        holder.has_Username.setText(postssList.get(position).getFullname());



        Glide.with(mContext).load(postssList.get(position).getPostimage()).into(holder.imageView);
        Glide.with(mContext).load(postssList.get(position).getProfileimage()).into(holder.profileView);

    }

    @Override
    public int getItemCount() {


        return postssList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView;
        CircleImageView profileView;
        TextView date,time, description,has_Username;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.post_image);
            date=itemView.findViewById(R.id.post_date);
            time=itemView.findViewById(R.id.post_time);

            has_Username=itemView.findViewById(R.id.post_user_name);
            description=itemView.findViewById(R.id.post_description);
            profileView=itemView.findViewById(R.id.post_profile_image);


        }
    }

}
