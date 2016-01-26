package net.kornan.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.kornan.managelibrary.AppInfo;
import net.kornan.managelibrary.PackageObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.kornan.installmanager.R;

public class MainActivity extends AppCompatActivity implements PackageObserver.PackageListener {
    RecyclerView recyclerView;
    private List<AppInfo> mlistAppInfo = null;
    private long cachesize; //缓存大小
    private long datasize;  //数据大小
    private long codesize;  //应用程序大小
    private long totalsize; //总大小
    PackageObserver packageObserver;

    static WeakReferenceHandler<MainActivity> handler= new WeakReferenceHandler<MainActivity>(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(this.getReference().get(), "" + String.valueOf(msg.obj), Toast.LENGTH_LONG).show();
        }
    };

    class CustomHandler<MainActivity> extends WeakReferenceHandler<MainActivity>{

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler.setWeakReferenceHandler(this);
        mlistAppInfo = new ArrayList<>();
        packageObserver = new PackageObserver(this);
        packageObserver.setPackageListener(this);
        init();
        queryAppInfo();
    }

    @Override
    public void onGetStatsCompleted(PackageStats packageStats, boolean succeeded) {
        cachesize = packageStats.cacheSize;
        datasize = packageStats.dataSize;
        codesize = packageStats.codeSize;
        totalsize = codesize + datasize ;

        Message msg=handler.obtainMessage();
        msg.obj=Formatter.formatFileSize(this, totalsize);
        msg.sendToTarget();
    }

    @Override
    public void onRemoveUserDataCompleted(String packageName, boolean succeded) {

    }

    @Override
    public void onRemoveCacheDataCompleted(String packageName, boolean succeded) {

    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.main_layout);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.app_item, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                AppInfo appInfo = mlistAppInfo.get(position);
                holder.text_name.setText(appInfo.getAppLabel());
                holder.img_icon.setImageDrawable(appInfo.getAppIcon());
                holder.img_icon.setTag(position);
                holder.img_icon.setOnClickListener(onClickListener);
            }

            @Override
            public int getItemCount() {
                return mlistAppInfo.size();
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AppInfo appinfo = mlistAppInfo.get((Integer) (v.getTag()));
            try {
//                packageObserver.getPacakgeSize(appinfo.getPkgName());
                packageObserver.clearUserData(appinfo.getPkgName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_icon;
        TextView text_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            img_icon = (ImageView) itemView.findViewById(R.id.img_icon);
            text_name = (TextView) itemView.findViewById(R.id.text_name);
        }
    }


    public void queryAppInfo() {
        PackageManager pm = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name;
                String pkgName = reInfo.activityInfo.packageName;
                String appLabel = (String) reInfo.loadLabel(pm);
                Drawable icon = reInfo.loadIcon(pm);

                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName, activityName));
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setAppIcon(icon);
                appInfo.setIntent(launchIntent);
                mlistAppInfo.add(appInfo);
            }
        }
    }
}
