package snowleopard.speakup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by chintandani on 31/10/17.
 */

public class ActivityLauncher extends Activity {
    private static int SPLASH_TIME_OUT=3000;
    private ImageView mImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launcher);
        mImg = (ImageView) findViewById(R.id.LauncherImage);
        mImg.setImageResource(R.drawable.app_name_p);
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
