package com.iced.alexwang.views.main;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.iced.alexwang.activities.R;

public class ViewHelper {
    public static DrawerLayout getDrawerLayout(Context context, int resourceId, RecyclerView.Adapter adapter) {
        return getDrawerLayout(context, LayoutInflater.from(context).inflate(resourceId, null), adapter);
    }
    public static DrawerLayout getDrawerLayout(Context context, View main, RecyclerView.Adapter adapter) {
        DrawerLayout layout = (DrawerLayout) LayoutInflater.from(context).inflate(R.layout.layout_drawer, null);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.main_left_drawer);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        RelativeLayout relativeLayout = (RelativeLayout) layout.findViewById(R.id.content_layout);
        relativeLayout.addView(main, new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setAdapter(adapter);
        return layout;
    }
}
