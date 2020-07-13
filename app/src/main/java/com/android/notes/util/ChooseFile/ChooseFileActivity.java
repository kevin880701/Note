package com.android.notes.util.ChooseFile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.android.notes.R;
import com.android.notes.util.EditFile.EditFileActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.util.Arrays.sort;

public class ChooseFileActivity extends AppCompatActivity {

    @BindView(R.id.appTitle)
    TextView appTitle;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.add)
    ImageButton add;

    String path;
    File folder;
    ArrayList<String> titleList = new ArrayList<String>();
    ArrayList<String> contentList = new ArrayList<String>();
    ArrayList<String> realTitleList = new ArrayList<String>();
    ArrayList<String> picList = new ArrayList<String>();
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        folder = new File(this.getExternalFilesDir("Notes").getAbsolutePath());
        Log.v("123",this.getExternalFilesDir("Notes").getAbsolutePath());
        path = this.getExternalFilesDir("Notes").getAbsolutePath();

        cretFoder();
        file();

        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new MyAdapter();
        list.setAdapter(adapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        Bundle bundle = new Bundle();

        // 建立ViewHolder
        class ViewHolder extends RecyclerView.ViewHolder {
            // 宣告元件
            @BindView(R.id.img)
            ImageView img;
            @BindView(R.id.fileTitle)
            TextView fileTitle;


            ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it = new Intent(ChooseFileActivity.this, EditFileActivity.class);
                        bundle.putString("realTitle", realTitleList.get(getAdapterPosition()));
                        bundle.putString("title", titleList.get(getAdapterPosition()));
                        bundle.putString("content", contentList.get(getAdapterPosition()));
                        it.putExtras(bundle);
                        startActivity(it);
                        finish();
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.fileTitle.setText(titleList.get(position));
            if(picList.get(position) == "none"){
                holder.img.setImageDrawable(getResources().getDrawable(R.drawable.none));
            }else{
                Bitmap bitmap = BitmapFactory.decodeFile(picList.get(position));
                holder.img.setImageBitmap(bitmap);
            }
        }

        @Override
        public int getItemCount() {
            return titleList.size();
        }
    }

    //創建資料夾
    public void cretFoder() {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void file() {
        //檔案篩選
        FilenameFilter nameFilter = new FilenameFilter() {
            private String[] filter = {
                    "txt"
            };

            @Override
            public boolean accept(File dir, String filename) {
                for (int i = 0; i < filter.length; i++) {
                    if (filename.indexOf(filter[i]) != -1 & filename.indexOf("--") != -1)
                        return true;
                }
                return false;
            }
        };

        File[] files = folder.listFiles(nameFilter);
        sort(files);
        if (files.length != 0) {
            for (int i = 0; i < files.length; i++) {
                String line;
                String content = "";
                try {
                    FileInputStream openTxt = new FileInputStream(files[i]);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(openTxt)); // 建立一個物件，它把檔案內容轉成計算機能讀懂的語言
                    while ((line = reader.readLine()) != null) {
                        content = content + line + "\n";
                    }
                    int a = content.lastIndexOf("*TiTle*\n");
                    int b = content.lastIndexOf("\n*ConTent*\n");
                    int c = content.length();
                    titleList.add(content.substring(a+8,b));
                    contentList.add(content.substring(b+11,c));

                }catch (Exception e) {
                    Log.v("ERROR","" + e);
                }

                int dot = files[i].getName().length();
                realTitleList.add(files[i].getName().substring(0, dot-4));

                if (new File(path + "/" + realTitleList.get(i) + ".png").exists()) {
                    picList.add(path + "/" + realTitleList.get(i) + ".png");
                } else {
                    picList.add("none");
                }
            }
        }
    }

    //新增文檔
    @OnClick(R.id.add)
    public void onClick() {
        Intent it = new Intent(ChooseFileActivity.this, EditFileActivity.class);
        startActivity(it);
        finish();
    }
}
