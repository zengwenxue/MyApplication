package com.procuratorate.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.procuratorate.app.R;
import com.procuratorate.app.adapter.MainVpAdapter;
import com.procuratorate.app.async.RequestClient;
import com.procuratorate.app.base.BaseActivity;
import com.procuratorate.app.fragment.FragmentApply;
import com.procuratorate.app.fragment.FragmentCar;
import com.procuratorate.app.fragment.FragmentDispatch;
import com.procuratorate.app.fragment.FragmentExecute;
import com.procuratorate.app.fragment.FragmentPersonal;
import com.procuratorate.app.widget.ViewPagerStop;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 杨绘庆 on 2016/8/24.
 * 主页
 */

public class MainActivity extends BaseActivity {

    @Bind(R.id.vp_main)
    ViewPagerStop vpMain;
    @Bind(R.id.rg_main)
    RadioGroup rgMain;
    private ArrayList<Fragment> frgPagers = new ArrayList<>();
    private MainVpAdapter adapter;
    private String power;
    private ProgressDialog pDialog;
    private long size;
    private Callback.Cancelable cancelable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewThis(R.layout.activity_main);
        ButterKnife.bind(this);
        ActivityManagerMe.getInstance().addActivity(this);
        ((RadioButton) (rgMain.getChildAt(0))).setChecked(true);
        vpMain.setNoScroll(true);
        initVpData();
        FragmentManager manager = getSupportFragmentManager();
        adapter = new MainVpAdapter(manager,frgPagers);
        vpMain.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        init();
    }

    private void init() {
        new Thread() {
            public void run() {
                try {
                    checkVision();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        vpMain.setOffscreenPageLimit(4);
        rgMain.setOnCheckedChangeListener(radioChangeLister);
    }

    private void checkVision() {

    }

    private RadioGroup.OnCheckedChangeListener radioChangeLister = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int raAll = rgMain.getChildCount();
            for (int i = 0; i < raAll; i++) {
                RadioButton rb = (RadioButton)rgMain.getChildAt(i);
                if (rb.isChecked()){
                    vpMain.setCurrentItem(i);
                }
            }
        }
    };
    private long exitTime = 0;

    /**
     * 双击退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                showMessage("再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                ActivityManagerMe.getInstance().finishAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initVpData() {
        FragmentApply fragmentApply = new FragmentApply();
        frgPagers.add(fragmentApply);
        FragmentCar frgCar = new FragmentCar();
        frgPagers.add(frgCar);
        FragmentDispatch frgDispatch = new FragmentDispatch();
        frgPagers.add(frgDispatch);
        FragmentExecute frgExecute = new FragmentExecute();
        frgPagers.add(frgExecute);
        FragmentPersonal frgPersonal = new FragmentPersonal();
        frgPagers.add(frgPersonal);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 获取新版本更新升级
     * @param urls
     */
    void downApk(final String urls){
        File sdcardDir = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(sdcardDir.getPath()); //sdcardDir.getPath())值为/mnt/sdcard，想取外置sd卡大小的话，直接代入/mnt/sdcard2
        long blockSize = sf.getBlockSize(); //总大小
        long availCount = sf.getAvailableBlocks(); //有效大小
        size = (availCount*blockSize)/1024/1024; //手机存储空间剩余大小MB

        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setTitle("正在下载");
        pDialog.setMessage("请稍后");
        pDialog.setProgress(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams(urls);
                params.setAutoRename(true);//自动命名
                params.setAutoResume(true);//断点续传
                //文件保存路径 sd卡 请确保有sd访问权限（这里放在了sd卡的根目录下）
                params.setSaveFilePath(Environment.getExternalStorageDirectory() + "/myapp/");
                cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onWaiting() {
                        // 等待
                        pDialog.show();
                    }
                    @Override
                    public void onStarted() {
                    }
                    @Override
                    public void onCancelled(CancelledException e) {
                    }
                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        //失败
                        showMessageNormal("失败" + throwable.getMessage());
                    }
                    @Override
                    public void onFinished() {
                        //下载（成功或者失败）
                        pDialog.cancel();
                    }
                    @Override
                    public void onLoading(long total, long current, boolean b) {
                        //加载中 current*100/total 当前进度
                        pDialog.setProgress((int) (current * 100 / total));
                        if (total >= size) {
                            pDialog.cancel();
                            cancelable.cancel();
                            showMessage("存储空间不足，请清理后更新");
                        }
                    }
                    @Override
                    public void onSuccess(File file) {
                        //下载完成,安装app
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                });
            }});
    }
}
