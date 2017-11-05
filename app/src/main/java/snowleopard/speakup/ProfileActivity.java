package snowleopard.speakup;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private Query mQuery;

    private ImageButton mDP;
    private Uri mImgU = null;


    private FirebaseUser mUser;
    private RecyclerView mList;
    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorage;





    private TextView mWelcome;
    public static final int GALLERY_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        mList = (RecyclerView) findViewById(R.id.list);
        //mList.hasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mUser = mAuth.getCurrentUser();
        mDP = (ImageButton) findViewById(R.id.mDP);

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mStorage  = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        mWelcome=   (TextView) findViewById(R.id.tvWelcome);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Story");
        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mQuery = mDatabase.orderByChild("Owner").equalTo(mAuth.getCurrentUser().getUid());
        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mWelcome.setText("Welcome  "+dataSnapshot.child("name").getValue().toString());
                if(dataSnapshot.child("image").getValue().toString() != "default") {
                    Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString()).into(mDP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToast("Database Error");
            }
        });
        mDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void showToast(String message)

    {

        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);

        toast.show();

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Cards_ListViewActivity , ListViewActivity.cardViewHolder> firebaserecycleradapter = new FirebaseRecyclerAdapter<Cards_ListViewActivity, ListViewActivity.cardViewHolder>(
                Cards_ListViewActivity.class,
                R.layout.list_row,
                ListViewActivity.cardViewHolder.class,
                mQuery
                ) {


            @Override
            protected void populateViewHolder(final ListViewActivity.cardViewHolder viewHolder, final Cards_ListViewActivity model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setDescription(model.getDescription());

                // mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getOwner());

                mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot owners = (dataSnapshot.child(model.getOwner()).child("name"));
                        viewHolder.setOwner(owners.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setTitle(model.getTitle());
                viewHolder.setImageUrl(getApplicationContext(), model.getImageURL());
                viewHolder.setLikeBtn(post_key);
                viewHolder.mViewStory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent viewActivity = new Intent(ProfileActivity.this, ViewStoryActivity.class);
                        viewActivity.putExtra("Key", post_key);
                        startActivity(viewActivity);
                    }
                });


            }

        };
        mList.setAdapter(firebaserecycleradapter);

    }
    public static class cardViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mLikebtn;
        TextView mNumLikes;
        ImageButton  mViewStory;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;


        public cardViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mNumLikes = (TextView) mView.findViewById(R.id.mNLike);
            mLikebtn = (ImageButton) mView.findViewById(R.id.like_btn);
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mViewStory = (ImageButton) mView.findViewById(R.id.imgCard);
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
                    if(dataSnapshot.child(post_key).hasChild("NumberOfLikes")){
                        mNumLikes.setText(dataSnapshot.child(post_key).child("NumberOfLikes").getValue().toString());}

                    /*if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikebtn.setImageResource(R.mipmap.likegray);

                    }
                    else{
                        mLikebtn.setImageResource(R.mipmap.likeblack);
                    }*/
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

            Button mOwnerbtn = (Button) mView.findViewById(R.id.owner_button);
            mOwnerbtn.setText(owner);}

        public void setImageUrl(Context ctx, String image){
            ImageButton post_image = (ImageButton) mView.findViewById(R.id.imgCard);
            Picasso.with(ctx).load(image).into(post_image);

        }
    }



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case  R.id.action_add:
                Intent mainIntent = new Intent(ProfileActivity.this, AddStoryActivity.class);
                startActivity(mainIntent);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;


            case R.id.action_logout:

//                Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
                mAuth.signOut();
//                startActivity(intent);
//                finish();
                return true;


            default:

                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            mImgU = data.getData();
            mDP.setImageURI(mImgU);
            StorageReference filepath = mStorage.child(mImgU.getLastPathSegment());
            filepath.putFile(mImgU).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri DownloadUrl = taskSnapshot.getDownloadUrl();
                    mDatabase1.child("image").setValue(DownloadUrl.toString().trim());



                }
            });
        }



    }



}