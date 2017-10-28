package snowleopard.speakup;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ListViewActivity extends AppCompatActivity {

    private RecyclerView mList;
    private DatabaseReference mDatabase;
    private FloatingActionButton mAddNS;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseUser;
    private static final String TAG = "MyActivity";
    private boolean mProcessLike=false;
    private DatabaseReference mDatabaseLike;
    SharedPreferences pref;
    Context _context;
    SharedPreferences.Editor editor;
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static String post_key0;
    ImageButton mLikebtn;



    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_list_view);
        mList = (RecyclerView) findViewById(R.id.list);
        //mList.hasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Story");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mAddNS = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        mAddNS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ListViewActivity.this, AddStoryActivity.class);

                startActivity(mainIntent);
            }
        });
    }
    public void createLoginSession(String name, String email) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void checkLogin() {

        if (!this.isLoggedIn()) {

            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public boolean isLoggedIn() {

        return pref.getBoolean(IS_LOGIN, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Cards_ListViewActivity , cardViewHolder> firebaserecycleradapter = new FirebaseRecyclerAdapter<Cards_ListViewActivity, cardViewHolder>(
                Cards_ListViewActivity.class,
                R.layout.list_row,
                cardViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(final cardViewHolder viewHolder, final Cards_ListViewActivity model, int position) {

                final String post_key = getRef(position).getKey();
                viewHolder.setDescription(model.getDescription());

               // mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getOwner());

                mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot owners =(dataSnapshot.child(model.getOwner()).child("name"));
                        viewHolder.setOwner(owners.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setTitle(model.getTitle());
                viewHolder.setImageUrl(getApplicationContext(),model.getImageURL());
                viewHolder.setLikeBtn(post_key);
                post_key0=getRef(viewHolder.getLayoutPosition()).getKey();
                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessLike = true;
                        post_key0=getRef(viewHolder.getLayoutPosition()).getKey();
                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    Log.i(TAG, "MyClass.getView() — get item number " + post_key);
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;

                                    } else {
                                        mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Liked");

                                        mProcessLike = false;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                });


            }
        };
        mList.setAdapter(firebaserecycleradapter);


    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {

                    Intent mainIntent = new Intent(ListViewActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                    //Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                    finish();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public static class cardViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mLikebtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;


        public cardViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mLikebtn = (ImageButton) mView.findViewById(R.id.like_btn);
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("likes");
            mAuth=FirebaseAuth.getInstance();
            mDatabaseLike.keepSynced(true);
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.tvTitle);
            post_title.setText(title);

        }
        public void setLikeBtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override

                public void onDataChange(DataSnapshot dataSnapshot) {
                    mLikebtn.setImageResource(R.mipmap.likegray);
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikebtn.setImageResource(R.mipmap.likegray);

                    }
                    else{
                        mLikebtn.setImageResource(R.mipmap.likeblack);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setDescription(String description) {
            TextView post_desc = (TextView) mView.findViewById(R.id.tvDesc);
            post_desc.setText(description);
        }
        public void setOwner(String owner){
            TextView post_owner = (TextView) mView.findViewById(R.id.tvDescO);
            post_owner.setText(owner); }

        public void setImageUrl(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.imgCard);
            Picasso.with(ctx).load(image).into(post_image);

        }
    }
    public void viewActivity(View view){
        Intent viewActivity = new Intent(ListViewActivity.this,ViewStoryActivity.class);
        viewActivity.putExtra("Key",post_key0);
        startActivity(viewActivity);
    }
}
