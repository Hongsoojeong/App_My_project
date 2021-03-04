package com.example.Activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MemoActivity<PrefUtil> extends AppCompatActivity {

    public static boolean check = false;

    TextView selectDate;
    // 날씨 입력하는 textView 를 클릭하면 datePicker 액티비티로 넘어가므로
    PrefUtil prefUtil;
    ImageView image;
    TextView saveBtn;
    TextView backBtn;
    EditText content;

    Switch breakfast_switch;
    Switch lunch_switch;
    Switch dinner_switch;

    SharedPreferences sharedPreferences; // 앱 내 데이터를 저장할 객체
    SharedPreferences.Editor editor; // 앱 내 데이터를 수정할 객체

    int REQUEST_IMAGE_CODE = 1001;

    @SuppressLint("WrongViewCast")
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);


        sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
        editor = sharedPreferences.edit();


        breakfast_switch = (Switch) findViewById(R.id.breakfast_switch);
        lunch_switch = (Switch) findViewById(R.id.lunch_switch);
        dinner_switch = (Switch) findViewById(R.id.dinner_switch);



        backBtn = findViewById(R.id.back);
        content = findViewById(R.id.content);
        selectDate = findViewById(R.id.go_to_datePicker);


        saveBtn = (TextView) findViewById(R.id.save);
        image = (ImageView) findViewById(R.id.upload_image);



        boolean bfSwitch = sharedPreferences.getBoolean("isBreakfastOn",false);
        boolean lcSwitch = sharedPreferences.getBoolean("isLunchOn",false);
        boolean dnSwitch = sharedPreferences.getBoolean("isDinnerOn",false);


        breakfast_switch.setChecked(bfSwitch);
        lunch_switch.setChecked(lcSwitch);
        dinner_switch.setChecked(dnSwitch);


        String title = sharedPreferences.getString("memoTitle", "");
        Log.d("LOGTAG/onCreate",title);

        String content = sharedPreferences.getString("memoContent", "");
        Log.d("LOGTAG/onCreate",content);

        this.content.setText(content);
        selectDate.setText(title);




//스위치 중복 상태를 방지하기 위해서 if문을 설정. setChecked를 이용해서 만약 얘가 setChecked 상태다.. 하면
        //다른 setChecked가 된 스위치들은 꺼놓도록 하는 것이다.
        breakfast_switch.setOnCheckedChangeListener((view,b)->{
                    if (b){
                        lunch_switch.setChecked(false);
                        dinner_switch.setChecked(false);
                    }
                }
        );
        lunch_switch.setOnCheckedChangeListener((view,b)->{
                    if (b){
                        breakfast_switch.setChecked(false);
                        dinner_switch.setChecked(false);
                    }
                }
        );

        dinner_switch.setOnCheckedChangeListener((view, b)->{
                    if (b){
                        breakfast_switch.setChecked(false);
                        lunch_switch.setChecked(false);
                    }
                }
        );



        saveBtn.setOnClickListener(view -> {

            Toast.makeText(getApplicationContext(), "Save is Completed", Toast.LENGTH_SHORT).show();
            String contentTrim = this.content.getText().toString().trim();
            String titleTrim= selectDate.getText().toString().trim();

            Log.d("save.setonClickListener", selectDate.getText().toString().trim());


            if (!titleTrim.equals("")&&!contentTrim.equals("")) {
                memo(titleTrim, contentTrim);
            }
            else{
                Toast.makeText(getApplicationContext(), "Please Enter All value", Toast.LENGTH_SHORT).show();
            }
    });


        image.setOnClickListener(view -> {
            openGallery();
        });

        backBtn.setOnClickListener(view -> {
            finish();
        });


    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, 101);
    }




    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    InputStream instream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    image.setImageBitmap(imgBitmap);
                    instream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                selectDate.setText(data.getStringExtra("memoTitle"));
            }
        }

    }


    private void memo(String title, String content) {
        //날짜 저장
        editor.putString("memoTitle", title).commit();
        Log.d("LOGTAG/LISTACTIVITY", sharedPreferences.getString("memoTitle",""));
        // 내용 저장
        editor.putString("memoContent", content).commit();
        Log.d("LOGTAG/LISTACTIVITY", sharedPreferences.getString("memoContent",""));

        //스위치의 상태 저장
        editor.putBoolean("isBreakfastOn",breakfast_switch.isChecked()).commit();
        editor.putBoolean("isLunchOn",lunch_switch.isChecked()).commit();
        editor.putBoolean("isDinnerOn", dinner_switch.isChecked()).commit();
    }


    private void getData(){
        Intent diaryIntent = getIntent();
        selectDate.setText(diaryIntent.getStringExtra("title"));
        content.setText(diaryIntent.getStringExtra("content"));
    }




    public void onClick(View v){
        switch(v.getId()){
            case R.id.go_to_datePicker:
                Intent picker = new Intent(this, PickerActivity.class);
                startActivityForResult(picker, 1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

}

