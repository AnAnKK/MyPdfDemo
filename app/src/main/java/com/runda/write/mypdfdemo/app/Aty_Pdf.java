package com.runda.write.mypdfdemo.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.artifex.mupdf.viewer.MuPDFCore;
import com.runda.write.mypdfdemo.R;
import com.runda.write.mypdfdemo.app.adapter.PageAdapter;
import com.runda.write.mypdfdemo.app.pdf.ComposeSignatureToPdf;
import com.runda.write.mypdfdemo.app.pdf.PdfReaderView;
import com.runda.write.mypdfdemo.app.pdf.SignatureView;
import com.runda.write.mypdfdemo.app.util.FileUtils;
import com.runda.write.mypdfdemo.app.util.LogUtils;
import com.runda.write.mypdfdemo.app.util.SPUtils;
import com.runda.write.mypdfdemo.app.util.ShareFileUtils;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Aty_Pdf extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_title_left)
    ImageView ivTitleLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_title)
    RelativeLayout llTitle;
    @BindView(R.id.readerView)
    PdfReaderView readerView;
    @BindView(R.id.signatureView)
    SignatureView signatureView;
    @BindView(R.id.btn_sign)
    ImageView btnSign;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_sign)
    RelativeLayout rlSign;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.rl_clear)
    RelativeLayout rlClear;
    @BindView(R.id.imageView4)
    ImageView imageView4;
    @BindView(R.id.rl_compose)
    RelativeLayout rlCompose;
    @BindView(R.id.rl_top)
    LinearLayout rlTop;
    @BindView(R.id.rl_parent)
    LinearLayout rlParent;

    private String cachePath = Environment.getExternalStorageDirectory()+"/11/";
    private String current_pdf;
    private File file;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_pdf);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        current_pdf = intent.getStringExtra("pdf_address");
        fileName = FileUtils.getFileNameByFilePath(current_pdf);
        if("".equals(fileName)){
            Toast.makeText(Aty_Pdf.this, "文件错误", Toast.LENGTH_SHORT).show();
            return;
        }

        initView();
        initClick();
        deleteCacheFile();
    }

    private void deleteCacheFile() {
        FileUtils.deleteDirWihtFile(new File(cachePath));
    }


    private void initClick() {
        rlSign.setOnClickListener(this);
        rlCompose.setOnClickListener(this);
        rlClear.setOnClickListener(this);
        tvTitleRight.setOnClickListener(this);
    }

    private void initView() {
        tvTitle.setText("pdf阅读签名");
        llTitle.setBackgroundColor(getResources().getColor(R.color.color_gray));
        tvTitle.setTextColor(Color.WHITE);
        ivTitleLeft.setVisibility(View.VISIBLE);
        tvTitleRight.setText("发送");
        tvTitleRight.setTextColor(Color.WHITE);
        tvTitleRight.setVisibility(View.VISIBLE);
        ivTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FileUtils.putAssetsToSDCard(this, "pdf_file", Environment.getExternalStorageDirectory().getPath());
        initPdfReadView();
    }

    ProgressDialog progressDialog;
    String originalPdf;
    String nextPdf;
    String curPdf;
    int curComposeNum = 0; //第几次合成
    MuPDFCore muPDFCore;
    SavePdfTask savePdfTask;

    private void initPdfReadView() {
        originalPdf = current_pdf;
        try {
            muPDFCore = new MuPDFCore(originalPdf);//要显示的PDF的文件路径
            readerView.setAdapter(new PageAdapter(this, muPDFCore));
            readerView.setDisplayedViewIndex(0);//要显示的PDF的页数
        } catch (Exception e) {
            Toast.makeText(Aty_Pdf.this, "此文件路径无效", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_sign:
                signature();
                break;
            case R.id.rl_clear:
                signatureView.clear();
                break;
            case R.id.rl_compose://合成图片！
                composeSignature();
                break;
            case R.id.tv_title_right:
                String path_tu_ya = SPUtils.getInstance(Aty_Pdf.this).getString("path_tu_ya", "");
                ShareFileUtils.shareFile(Aty_Pdf.this, path_tu_ya);
        }
    }

    /**
     * 合成签名到指定的PDF文档
     */
    class SavePdfTask extends AsyncTask {
        ComposeSignatureToPdf savePdf;

        public SavePdfTask(ComposeSignatureToPdf savePdf) {
            this.savePdf = savePdf;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            savePdf.addText();
            return null;
        }

        //主线程
        @Override
        protected void onPostExecute(Object o) {
            progressDialog.dismiss();
            progressDialog = ProgressDialog.show(Aty_Pdf.this, null, "合成成功，正在载入，...");
            curComposeNum++; //合成后，涂鸦次数+1
            try {
                muPDFCore = new MuPDFCore(nextPdf);//显示合成后的PDF
                readerView.setAdapter(new PageAdapter(Aty_Pdf.this, muPDFCore));
                readerView.setmScale(1.0f);
                readerView.setDisplayedViewIndex(readerView.getDisplayedViewIndex());
                progressDialog.dismiss();
                originalPdf = nextPdf;
                LogUtils.e("-----------合成后的显示nextPdf --- " + nextPdf);
                LogUtils.e("==================================================================================== ");
            } catch (Exception e) {
                LogUtils.e("合成后的显示nextPdf显示错误 ---- " + e);
                e.printStackTrace();
                Toast.makeText(Aty_Pdf.this, "合成后的显示nextPdf显示错误", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //原文件和最后一份合成的文档不删除
        for (int i = 0; i < curComposeNum; i++) {
            File file = new File(originalPdf.substring(0, originalPdf.length() - 4) + i + ".pdf");
            if (file.exists()) {
                file.delete();
            }
        }
        deleteCacheFile();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            keyBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void keyBack() {
        if (null != savePdfTask && !savePdfTask.isCancelled())
            savePdfTask.cancel(true);
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
        Aty_Pdf.this.finish();
    }


    private void signature() {
        if (signatureView.getVisibility() == View.GONE) {
            //可编辑状态
            signatureView.setVisibility(View.VISIBLE);
            rlCompose.setVisibility(View.VISIBLE);
            rlClear.setVisibility(View.VISIBLE);
            btnSign.setImageResource(R.drawable.icon_pdf_sign);
        } else {
            //不可编辑状态！
            signatureView.clear();
            signatureView.setVisibility(View.GONE);
            rlClear.setVisibility(View.GONE);
            btnSign.setImageResource(R.drawable.icon_pdf_signature);
        }
    }

    /**
     * 异步合成签名图片到PDF文档
     */
    private void composeSignature() {
        LogUtils.e("curComposeNum=="+curComposeNum);
        LogUtils.e("originalPdf=="+originalPdf);


        //确认存储位置和名称
        curPdf = originalPdf;
        nextPdf = cachePath+curComposeNum+"/"+fileName;

        file = new File(cachePath+curComposeNum+"/");
        if (!file.exists()) {
            file.mkdirs();
        }
        ComposeSignatureToPdf savePdf = new ComposeSignatureToPdf(curPdf, nextPdf);
        LogUtils.e("curPdf == "+curPdf+"===nextPdf==="+nextPdf);
        savePdf.setPageNum(readerView.getDisplayedViewIndex() + 1);
        savePdf.setWidthScale(1.0f * readerView.scrollX / readerView.getDisplayedView().getWidth());//缩放后，相对于左下角 宽偏移的百分比
        savePdf.setHeightScale(1.0f * readerView.scrollY / readerView.getDisplayedView().getHeight());//缩放后，相对于左下角，长偏移的百分比
        if (signatureView.getWidth() == 0 || signatureView.getHeight() == 0) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(signatureView.getWidth(), signatureView.getHeight(),
                Bitmap.Config.ARGB_8888); //签名图片
        Canvas canvas = new Canvas(bitmap);
        signatureView.draw(canvas);//一定要执行此操作，否则图片上没有内容
        //HH  设置图片大小占放大后的pdf的比例
        float widthPercent = bitmap.getWidth() / (float) readerView.getDisplayedView().getWidth();
        float heightPercent = bitmap.getHeight() / (float) readerView.getDisplayedView().getHeight();
        savePdf.setImgPercent(widthPercent, heightPercent);
        savePdf.setBitmap(bitmap);
        savePdfTask = new SavePdfTask(savePdf);
        savePdfTask.execute();
        progressDialog = ProgressDialog.show(Aty_Pdf.this, null, "正在合成...");
        unSignatureViewState();
    }

    private void unSignatureViewState() {
        signatureView.setVisibility(View.GONE);
        rlClear.setVisibility(View.GONE);
        signatureView.clear();
        btnSign.setImageResource(R.drawable.icon_pdf_signature);
    }

}
