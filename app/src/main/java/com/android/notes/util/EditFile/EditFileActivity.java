package com.android.notes.util.EditFile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.notes.R;
import com.android.notes.util.ChooseFile.ChooseFileActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditFileActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.ok)
    ImageView ok;
    @BindView(R.id.del)
    ImageView del;
    @BindView(R.id.titleBar)
    LinearLayout titleBar;
    @BindView(R.id.addPic)
    ImageView addPic;
    @BindView(R.id.editTitle)
    EditText editTitle;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.editContent)
    EditText editContent;

    String path;
    String getRealTitle, getTitle, getContent, status, title,fileTitle;
    String content = "";
    Uri selectedImage;
    final int IMAGE_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_file);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        editContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        path = this.getExternalFilesDir("Notes").getAbsolutePath();

        Intent get = this.getIntent();
        Bundle getBundle = get.getExtras();
        if (getBundle != null) {
            status = "1";
            getTitle = getBundle.getString("title");
            getContent = getBundle.getString("content");
            getRealTitle = getBundle.getString("realTitle");
            openFile();
        }
    }

    //開起舊檔
    public void openFile() {
        try {
            editTitle.setText(getTitle);
            editContent.setText(getContent);

            Bitmap read_pic = BitmapFactory.decodeFile(path + "/" + getRealTitle + ".png");
            if (read_pic != null) {
                addPic.setImageBitmap(read_pic);
            }
        } catch (Exception e) {
            Log.v("ERROR", "" + e);
        }
    }

    @OnClick(R.id.ok)
    public void okBtnClicked() {
        content = editContent.getText().toString();
        title = editTitle.getText().toString();
        if (status == "1") {
            File filePath = new File(path, getRealTitle + ".txt");
            filePath.delete();
        }
        //判斷是否有內容
        if (content == "" && title == "") {
            back();
        } else if (content != "" || title != "") {
            if (title.equals("")) {
                noTitle();
            }
            try {
                fileTitle = title;
                fileTitle = fileTitle.replace("\\"," ");
                fileTitle = fileTitle.replace("/"," ");
                fileTitle = fileTitle.replace("<"," ");
                fileTitle = fileTitle.replace(">"," ");
                fileTitle = fileTitle.replace("“"," ");
                fileTitle = fileTitle.replace(":"," ");
                fileTitle = fileTitle.replace("?"," ");
                fileTitle = fileTitle.replace("|"," ");
                fileTitle = fileTitle.replace("*"," ");
                File file = new File(path + "/" + fileTitle + time() + ".txt");

                FileOutputStream writeContent = new FileOutputStream(file);
                writeContent.write("*TiTle*\n".getBytes());
                writeContent.write(title.getBytes());
                writeContent.write("\n*ConTent*\n".getBytes());
                writeContent.write(content.getBytes());
                writeContent.close();
                savePic();
                back();
            } catch (Exception e) {
                Log.v("ERROR","" + e);
            }
        }
    }


    //無標題時
    public void noTitle() {
        File[] files = new File(path).listFiles();
        int q = 1;
        for (int i = 0; i < files.length; i++) {
            title = "未命名" + String.format("%3d", q).replace(" ", "0");
            int a = files[i].getName().lastIndexOf(" --");
            if (a != -1) {
                if (title.equals(files[i].getName().substring(0, a))) {
                    q = q + 1;
                    title = "未命名" + String.format("%3d", q).replace(" ", "0");
                }
            }
        }
    }

    //儲存圖片
    public void savePic() {
        try {
            if (selectedImage != null) {
                int byteread = 0;
                InputStream inStream = getContentResolver().openInputStream(selectedImage);  //讀取圖片
                FileOutputStream fs = new FileOutputStream(path + "/" + fileTitle + time() + ".png");//輸出圖片
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            File oldPic = new File(path, getRealTitle + ".png");
            if (oldPic.exists()) {
                oldPic.renameTo(new File(path + "/" + fileTitle + time() + ".png"));
            }
        } catch (Exception e) {
            Log.v("ERROR","" + e);
        }
    }

    //新增圖片
    @OnClick(R.id.addPic)
    public void onViewClicked() {
        Choose choose = new Choose(EditFileActivity.this);
        View view = LayoutInflater.from(EditFileActivity.this).inflate(R.layout.pic_btn, null);
        choose.showAtLocation(view, Gravity.CENTER, 0, 0);
        //強制隱藏鍵盤
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    //取得被選取圖片URI
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            File picFilePath = new File(path, getRealTitle + ".png");
            picFilePath.delete();
            selectedImage = data.getData(); //獲取系統返回的照片的Uri
            Cursor cursor = getContentResolver().query(selectedImage,
                    null, null, null, null);//從系統表中查詢指定Uri對應的照片
            cursor.moveToFirst();
            cursor.close();
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedImage, "r");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            addPic.setImageBitmap(image);
        } catch (Exception e) {
            System.out.println("選取圖片錯誤");
        }
    }


    @OnClick(R.id.del)
    public void delClicked() {
        if(status != "1"){
            back();
        }else{
            File oldFile = new File(path,getRealTitle + ".txt");
            oldFile.delete();
            File old_pic = new File(path,getRealTitle + ".png");
            if(old_pic != null){
                old_pic.delete();
            }
            Intent it = new Intent(EditFileActivity.this,ChooseFileActivity.class);
            startActivity(it);
            finish();
            Toast.makeText(EditFileActivity.this,"刪除：" + getTitle, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.back)
    public void backClicked() {
        back();
    }


    public class Choose extends PopupWindow {
        View view;
        @BindView(R.id.delBtn)
        Button delBtn;
        @BindView(R.id.pickBtn)
        Button pickBtn;
        @BindView(R.id.popLayout)
        LinearLayout popLayout;

        public Choose(Context mContext) {
            this.view = LayoutInflater.from(mContext).inflate(R.layout.pic_btn, null);
            ButterKnife.bind(this, view);


            if (addPic.getDrawable().getCurrent().getConstantState().equals(getResources().getDrawable(R.drawable.none).getConstantState())) {
                delBtn.setVisibility(View.INVISIBLE); // 隱藏
                pickBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.radius3));
            }

            // 外部可點擊
            this.setOutsideTouchable(true);
            // mMenuView添加OnTouchListener監聽判斷獲取觸屏位置如果在選擇框外面則銷毀彈出框
            this.view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    int height = view.findViewById(R.id.popLayout).getTop();
                    int y = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height) {
                            dismiss();
                        }
                    }
                    return true;
                }
            });

            this.setContentView(this.view);
            // 窗口高和寬填滿
            this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
            this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
            // 設置彈出窗體可點擊
            this.setFocusable(true);
            // 背景色
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            this.setBackgroundDrawable(dw);
            // 彈出窗体的動畫
            this.setAnimationStyle(R.style.take_photo_anim);
        }

        @OnClick(R.id.pickBtn)
        public void pickBtnClicked() {
            Intent pick = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pick, IMAGE_REQUEST_CODE);
            // 銷毀彈出框
            dismiss();
        }

        @OnClick(R.id.delBtn)
        public void delBtnClicked() {
            if (status == "1") {
                File delPic = new File(path, getRealTitle + ".png");
                delPic.delete();
                addPic.setImageDrawable(getResources().getDrawable(R.drawable.none));
            } else {
                addPic.setImageDrawable(getResources().getDrawable(R.drawable.none));
            }
            // 銷毀彈出框
            dismiss();
        }
    }

    //當前時間
    public String time() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(" --yyyy-MM-dd hh-mm-ss");
        String date = sDateFormat.format(new Date());
        return date;
    }

    public void back() {
        Intent it = new Intent(EditFileActivity.this, ChooseFileActivity.class);
        startActivity(it);
        finish();
    }

    //返回鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
