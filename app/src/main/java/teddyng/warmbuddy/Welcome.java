package teddyng.warmbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Welcome extends AppCompatActivity {

    Button welcomebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomebtn = (Button) findViewById(R.id.welcomebtn);

        welcomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, DeviceList.class));
            }
        });

        TextView textView = (TextView) findViewById(R.id.warmbuddy_title);
        Spannable word = new SpannableString("WARM ");

        word.setSpan(new ForegroundColorSpan(0xFFFF8800), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(word);
        Spannable wordTwo = new SpannableString("BUDDY");

        wordTwo.setSpan(new ForegroundColorSpan(Color.WHITE), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(wordTwo);
    }
}