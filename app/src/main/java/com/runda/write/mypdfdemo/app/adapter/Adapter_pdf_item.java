package com.runda.write.mypdfdemo.app.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.runda.write.mypdfdemo.app.Aty_Pdf;
import com.runda.write.mypdfdemo.R;
import com.runda.write.mypdfdemo.app.holder.ViewHolder_Item;

import java.io.File;
import java.util.List;


/**
 *
 * @Description:
 * @Author:         An_K
 * @CreateDate:     2020/7/21 15:07
 * @Version:        1.0
 */

public class Adapter_pdf_item extends BaseQuickAdapter<String, ViewHolder_Item> {

    public Adapter_pdf_item(List<String> data) {
        super(R.layout.item_pdf_rv, data);
    }

    @Override
    protected void convert(final ViewHolder_Item holder, final String s) {
        final View convertView = holder.getConvertView();
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvPath = convertView.findViewById(R.id.tv_path);
        File tempFile = new File(s.trim());
        String fileName = tempFile.getName();
        tvName.setText(fileName);
        tvPath.setText(s);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Aty_Pdf.class);
                intent.putExtra("pdf_address", s);
                mContext.startActivity(intent);
            }
        });
    }

}
