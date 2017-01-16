package com.procuratorate.app.activity;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import com.procuratorate.app.R;
import com.procuratorate.app.base.BaseActivity;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 杨绘庆 on 2016/9/2.
 */
public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_about_us);
        ButterKnife.bind(this);
        setHeadTitle("关于我们");
        setHeadSize(16);
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            tvVersion.setText("V "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
