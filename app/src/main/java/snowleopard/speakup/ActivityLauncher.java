package snowleopard.speakup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by chintandani on 31/10/17.
 */

public class ActivityLauncher extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=4000;
    private ImageView mImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mImg = (ImageView) findViewById(R.id.LauncherImage);
        mImg.setImageResource(R.drawable.app_name);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(ActivityLauncher.this,ListViewActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);

    }
}
