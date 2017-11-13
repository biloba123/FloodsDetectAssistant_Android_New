package com.lvqingyang.floodsdetectassistant_android_new.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lvqingyang.floodsdetectassistant_android_new.R;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/10/15
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */

public class ResourceView extends FrameLayout{

    private ImageView ivicon;
    private TextView tvname;

    public ResourceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view= LayoutInflater.from(context)
                .inflate(R.layout.view_resource, this);
        this.tvname = (TextView) view.findViewById(R.id.tv_name);
        this.ivicon = (ImageView) view.findViewById(R.id.iv_icon);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                        attrs,
                        R.styleable.ResourceView,
                        0, 0);

        try {
            tvname.setText(a.getString(R.styleable.ResourceView_rv_name));
            ivicon.setImageResource(a.getResourceId(R.styleable.ResourceView_rv_icon,
                    R.mipmap.ic_launcher));

        } finally {
            a.recycle();
        }
    }
}
