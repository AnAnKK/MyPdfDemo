package com.runda.write.mypdfdemo.app.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.runda.write.mypdfdemo.R;

/**
 * @Description:
 * @Author: An_K
 * @CreateDate: 2020/7/21 14:54
 * @Version: 1.0
 */
public class ViewHolder_Item extends BaseViewHolder {

    private RelativeLayout itemRoot;
    private ImageView iv_head;
    private TextView tv_name;
    private TextView tv_path;



    public ViewHolder_Item(View view) {
        super(view);
        this.iv_head = getView(R.id.iv_head);
        this.tv_name = getView(R.id.tv_name);
        this.tv_path = getView(R.id.tv_path);
        this.itemRoot = getView(R.id.itemRoot);
    }

    public ImageView getIv_head() {
        return iv_head;
    }

    public TextView getTv_name() {
        return tv_name;
    }

    public TextView getTv_path() {
        return tv_path;
    }

    public RelativeLayout getItemRoot() {
        return itemRoot;
    }
}