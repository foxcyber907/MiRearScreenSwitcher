package com.tgwgroup.MiRearScreenSwitcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Root模式下的TaskService宿主
 * 作为本地绑定Service运行，返回TaskService的IBinder
 * TaskService内部通过su执行特权命令
 */
public class RootTaskService extends Service {
    private static final String TAG = "RootTaskService";
    private final TaskService taskService = new TaskService();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "✓ RootTaskService created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "✓ RootTaskService bound");
        return taskService;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "✗ RootTaskService unbound");
        return true; // 允许rebind
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "✓ RootTaskService rebound");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "✗ RootTaskService destroyed");
    }

    /**
     * 检查设备是否已获取root权限（KernelSU/Magisk）
     * @return true=已root
     */
    public static boolean isRootAvailable() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            String line = reader.readLine();
            reader.close();
            int exitCode = process.waitFor();
            boolean isRoot = exitCode == 0 && line != null && line.contains("uid=0");
            Log.d(TAG, "Root check: exitCode=" + exitCode + ", output=" + line + ", isRoot=" + isRoot);
            return isRoot;
        } catch (Exception e) {
            Log.e(TAG, "Root check failed", e);
            return false;
        }
    }
}
