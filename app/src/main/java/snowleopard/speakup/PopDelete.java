package snowleopard.speakup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Created by chintandani on 08/11/17.
 */

public class PopDelete extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_delete);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        double width = dm.widthPixels;
        double height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.23));

        cancel = (Button) findViewById(R.id.cancel_button);

        yes = (Button) findViewById(R.id.del_final);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PopDelete.this , ViewStoryActivity.class));
            }
        });
    }
    private Button yes;
    private Button cancel;
}
