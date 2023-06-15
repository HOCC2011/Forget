package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {
    ImageView button;
    ImageView info;
    EditText editText;

    EditText list;

    TextView Chinese;
    TextView Chinese_text;

    TextView English;
    TextView English_text;

    //eng=0 chi=1
    int lan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.orange_light));
        setContentView(R.layout.activity_main);

        //tast open+intent
        button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        //about open
        info = findViewById(R.id.button_about);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewAbout();
            }
        });

        //set edittext
        editText = findViewById(R.id.input);
        list = findViewById(R.id.list);

        English = findViewById(R.id.english);
        English_text = findViewById(R.id.english_text);
        Chinese_text = findViewById(R.id.chinese_text);
        Chinese = findViewById(R.id.chinese) ;

        //switch chi
        Chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.lan = 1;
                Chinese_text.setTextColor(getColor(R.color.orange_light));
                English_text.setTextColor(getColor(R.color.white));
                Chinese.setBackground(getResources().getDrawable(R.drawable.background_seleted));
                English.setBackground(getResources().getDrawable(R.drawable.background_unseleted));
            }
        });

        //switch eng
        English .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.lan = 0;
                English_text.setTextColor(getColor(R.color.orange_light));
                Chinese_text.setTextColor(getColor(R.color.white));
                English.setBackground(getResources().getDrawable(R.drawable.background_seleted));
                Chinese.setBackground(getResources().getDrawable(R.drawable.background_unseleted));
            }
        });

    }

    public void openNewActivity() {
        Intent intent = new Intent(MainActivity.this, Task.class);
        intent.putExtra("input_text", editText.getText().toString());
        intent.putExtra("language",lan);
        intent.putExtra("list",list.getText().toString());
        startActivity(intent);
    }

    public void openNewAbout() {
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);
    }

}