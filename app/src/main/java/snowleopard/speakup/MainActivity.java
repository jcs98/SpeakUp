package snowleopard.speakup;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLoginBtn;
    private Button mRegisterBtn;

    private SharedPreferences mPreferences;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mDatabase;



    // Session Manager Class
    ListViewActivity session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.keepSynced(true);
        mProgress= new ProgressDialog(this);

        mLoginPasswordField = (EditText) findViewById(R.id.loginpasswordField);
        mLoginEmailField = (EditText) findViewById(R.id.loginEmailField);
        mLoginBtn = (Button) findViewById(R.id.loginButton);
        mRegisterBtn = (Button) findViewById(R.id.registerButton);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLogin();

            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);

            }
        });


    }



    private void checkLogin() {

        final String email = mLoginEmailField.getText().toString().trim();
        final String password = mLoginPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgress.setMessage("Checking Login..");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        mProgress.dismiss();
                        checkUserExist();
                    }
                    else{
                        mProgress.dismiss();
                        Toast.makeText(MainActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                    }
                }

            });

        }
    }


    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {

                    Intent mainIntent = new Intent(MainActivity.this, ListViewActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                    Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                    finish();


                }
                else{
                    Intent setupIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);

                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

//    @Override
//    public void finish() {
//
//        this.finish();
//        super.finish();
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
