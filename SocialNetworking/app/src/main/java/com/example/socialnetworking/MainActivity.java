package com.example.socialnetworking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;


    private ImageButton AddNewPostButton;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, PostsRef;
    private ArrayList<Posts> postsArrayList;
    private Recycler_Adapter recyclerAdapter;
    private Context mContext;




    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference();

        postsArrayList = new ArrayList<>();



        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        postList = findViewById(R.id.all_users_post_list);

        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);



        ClearAll();
        GetImageFromFirebase();


        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);


        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("fullname")) {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }

                    if (dataSnapshot.hasChild("profileimage")) {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);

                    }


                    else {
                        Toast.makeText(MainActivity.this, "Profile Name do not exists...", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);

                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });


        //DisplayAllUsersPosts();

    }

    private void GetImageFromFirebase() {


        Query query = PostsRef.child("Posts");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearAll();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Posts posts = new Posts();

                    posts.setPostimage(snapshot.child("postimage").getValue().toString());
                    posts.setDate(snapshot.child("date").getValue().toString());
                    posts.setTime(snapshot.child("time").getValue().toString());
                    posts.setDescription(snapshot.child("description").getValue().toString());
                    posts.setFullname(snapshot.child("fullname").getValue().toString());
                    posts.setProfileimage(snapshot.child("profileimage").getValue().toString());

                    postsArrayList.add(posts);

                }
                recyclerAdapter = new Recycler_Adapter(getApplicationContext(), postsArrayList);
                postList.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void ClearAll(){

        if (postsArrayList != null){
            postsArrayList.clear();

            if (recyclerAdapter!=null){
                recyclerAdapter.notifyDataSetChanged();

            }


        }

        else {

            postsArrayList = new ArrayList<>();
        }
    }

/*
    private void DisplayAllUsersPosts()
    {
        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(PostsRef, Posts.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_posts_layout, parent, false);

                return new PostsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(PostsViewHolder viewHolder, int position, Posts model) {



                viewHolder.setFullname(model.getFullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());

                Picasso.get().load(model.getProfileimage()).into(viewHolder.proImg);


            }
        };
        adapter.startListening();
        postList.setAdapter(adapter);
    }




    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public ImageView postImag;

        CircleImageView proImg = itemView.findViewById(R.id.post_profile_image);




        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            postImag = (ImageView) mView.findViewById(R.id.post_image);
        }

        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context ctx1,  String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postimage).into(PostImage);
        }
    }



*/


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }

    /*public void DisplayAllUsersPosts() {


        FirebaseRecyclerOptions<Posts> firebaseRecyclerOptions =new FirebaseRecyclerOptions.Builder<Posts>
                ().setQuery(PostsRef,Posts.class)
                .build();

        firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Posts, PostsHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull PostsHolder holder, int position, @NonNull Posts model) {
                holder.setPosts(model);

            }

            @NonNull
            @Override
            public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout,parent,false);
                return  new PostsHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseRecyclerAdapter!= null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }



    public static class PostsHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile_image;
        private ImageView post_image;
        private TextView Description;
        private TextView Date,Time, Has_Updated_Post,Has_Username;


        public PostsHolder(View itemView) {
            super(itemView);

            Date=itemView.findViewById(R.id.post_date);
            Time=itemView.findViewById(R.id.post_time);

            Has_Username=itemView.findViewById(R.id.post_user_name);
            Description=itemView.findViewById(R.id.post_description);
            profile_image=itemView.findViewById(R.id.post_profile_image);
            post_image=itemView.findViewById(R.id.post_image);
        }




    } */


/*
    private void DisplayAllUsersPosts(){
        PostsRef=FirebaseDatabase.getInstance().getReference();

        FirebaseRecyclerOptions<Posts> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Posts>
                ().setQuery(PostsRef, Posts.class)
                .build();

        FirebaseRecyclerAdapter<Posts, PostsHolder>
                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull PostsHolder holder, int position, @NonNull Posts model) {
                holder.setPosts(model);

            }

            @NonNull
            @Override
            public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new PostsHolder(view);
            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }








    public static class PostsHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile_image;
        private ImageView post_image;
        private TextView Description;
        private TextView Date,Time, Has_Updated_Post,Has_Username;


        public PostsHolder(View itemView) {
            super(itemView);

            Date=itemView.findViewById(R.id.post_date);
            Time=itemView.findViewById(R.id.post_time);

            Has_Username=itemView.findViewById(R.id.post_user_name);
            Description=itemView.findViewById(R.id.post_description);
            profile_image=itemView.findViewById(R.id.post_profile_image);
            post_image=itemView.findViewById(R.id.post_image);
        }

        public  void setPosts(Posts posts){

            String users_name=posts.getFullname();
            Has_Username.setText(users_name);
            String users_description=posts.getDescription();
            Description.setText(users_description);
            String users_date=posts.getDate();
            Date.setText(" " +users_date);
            String users_time=posts.getTime();
            Time.setText("" +users_time);
            String users_image=posts.getProfileimage();
            Picasso.get().load(users_image).into(post_image);
            String users_posts_image=posts.getPostimage();
            Picasso.get().load(users_posts_image).into(profile_image);



        }




*/

    //  }


    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }



    private void CheckUserExistence() {

        final String current_user_id = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(current_user_id)){

                    SendUserToSetupActivity();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity() {

        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();


    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "FriendsList", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;


            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;



            case R.id.nav_logout:

                mAuth.signOut();
                SendUserToLoginActivity();

                break;

        }
    }

}