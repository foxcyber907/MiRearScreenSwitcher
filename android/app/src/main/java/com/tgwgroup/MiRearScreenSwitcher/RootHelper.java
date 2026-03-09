package com.tgwgroup.MiRearScreenSwitcher;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Root权限检查工具类
 * 用于检查KernelSU/Magisk提供的root权限是否可用
 */
public class RootHelper {
    private static final String TAG = "RootHelper";
    private static Boolean cachedRootAvailable = null;

    /**
     * 检查root权限是否可用
     * @return true如果su可用且能获得root权限
     */
    public static boolean isRootAvailable() {
        if (cachedRootAvailable != null) {
            return cachedRootAvailable;
        }
        cachedRootAvailable = checkRoot();
        return cachedRootAvailable;
    }

    /**
     * 强制重新检查root权限（清除缓存）
     */
    public static boolean recheckRoot() {
        cachedRootAvailable = null;
        return isRootAvailable();
    }

    private static boolean checkRoot() {
        try {
            ProcessBuilder pb = new ProcessBuilder("su", "-c", "id");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()), 1024
            );
            String line = reader.readLine();
            reader.close();

            int exitCode = process.waitFor();

            if (exitCode == 0 && line != null && line.contains("uid=0")) {
                Log.d(TAG, "✓ Root available");
                return true;
            } else {
                Log.w(TAG, "✗ Root not available, exit=" + exitCode + ", output=" + line);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Root check failed", e);
            return false;
        }
    }
}
