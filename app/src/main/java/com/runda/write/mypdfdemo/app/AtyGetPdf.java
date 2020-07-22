package com.runda.write.mypdfdemo.app;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runda.write.mypdfdemo.R;
import com.runda.write.mypdfdemo.app.adapter.Adapter_pdf_item;
import com.runda.write.mypdfdemo.app.provider.AbstructProvider;
import com.runda.write.mypdfdemo.app.provider.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class AtyGetPdf extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_title_left)
    ImageView ivTitleLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
//    @BindView(R.id.tv_title_right)
//    TextView tvTitleRight;
    @BindView(R.id.ll_title)
    RelativeLayout llTitle;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.iv_get_pdf)
    ImageView ivGetPdf;
    private Adapter_pdf_item adapter_pdf_item;

    public static final int FILE_SELECTOR_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_get_pdf);
        ButterKnife.bind(this);
        initView();
        initClick();
    }

    private void initView() {
        tvTitle.setText("pdf文件");

    }


    private void initRecyclerView(List<String> listDate) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter_pdf_item = new Adapter_pdf_item(listDate);
        rv.setAdapter(adapter_pdf_item);
    }

    private void initClick() {
        ivGetPdf.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_get_pdf:
                Toast.makeText(AtyGetPdf.this, "获取pdf文件", Toast.LENGTH_SHORT).show();
                btnGetPdf();
                break;
        }
    }

    /**
     * 打开本地文件器
     */
    private void openFileSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_SELECTOR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECTOR_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            String path = uri.getPath().toString();
            if (path.endsWith("pdf")) {
                Intent intent = new Intent(AtyGetPdf.this, Aty_Pdf.class);
                String realFilePath = getRealFilePath(AtyGetPdf.this, uri);
                intent.putExtra("pdf_address", realFilePath);
                AtyGetPdf.this.startActivity(intent);
            } else {
                Toast.makeText(AtyGetPdf.this, "您选择得文件不是pdf！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 先实现打开本地文件中的pdf文件！
     */
    public void btnGetPdf() {
        open_loc_pdf_file();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exe_request_permission();
    }

    /**
     * 请求权限！！
     */
    private void exe_request_permission() {
        AtyGetPdfPermissionsDispatcher.requestStoragePermissionWithPermissionCheck(this);
    }


    private void open_loc_pdf_file() {
        AbstructProvider provider = new FileProvider(this);
        List<File> list = provider.getList("%.pdf");
        List<String> listDate = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String absolutePath = list.get(i).getAbsolutePath();
                listDate.add(absolutePath);
            }
            ivGetPdf.setVisibility(View.GONE);
            initRecyclerView(listDate);
        } else {
            Toast.makeText(AtyGetPdf.this, "本地没有pdf文件！", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 获取真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void requestStoragePermission() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AtyGetPdfPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void requestDecly() {
    }

}
