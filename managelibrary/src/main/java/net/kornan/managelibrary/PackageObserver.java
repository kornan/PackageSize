package net.kornan.managelibrary;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.app.ActivityManagerCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by KORNAN on 2016/1/25.
 */
public class PackageObserver {
    private PackageListener packageListener;
    private Context context;

    public PackageObserver(Context context) {
        this.context = context;
    }

    public interface PackageListener {
        /**
         * IPackageStatsObserver.Stub of onGetStatsCompleted
         *
         * @param packageStats PackageStats
         * @param succeeded    boolean
         */
        void onGetStatsCompleted(PackageStats packageStats, boolean succeeded);

        void onRemoveUserDataCompleted(String packageName, boolean succeded);

        void onRemoveCacheDataCompleted(String packageName, boolean succeded);
    }

    /**
     * Response onGetStatsCompleted
     *
     * @param packageListener PackageListener
     */
    public void setPackageListener(PackageListener packageListener) {
        this.packageListener = packageListener;
    }

    /**
     * Get Package size info
     * You need to add a 'android.permission.GET_PACKAGE_SIZE' permission;
     * response PackageListener.onGetStatsCompleted
     *
     * @param packageName PackageName
     * @throws Exception
     */
    public void getPacakgeSize(String packageName) throws Exception {
        if (packageName != null) {
            PackageManager pm = context.getPackageManager();
            PackageSizeObserver pkgSize = new PackageSizeObserver();
            try {
                Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, packageName, pkgSize);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    /**
     * Clear Application User Data
     * response PackageListener.onRemoveCompleted
     */
    public void clearUserData(String packageName) throws Exception {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            am.clearApplicationUserData();
//        } else {
            try {

                ClearUserDataObserver cud = new ClearUserDataObserver();
                Method clearApplicationUserData = am.getClass().getMethod("clearApplicationUserData",String.class,IPackageDataObserver.class);
                clearApplicationUserData.invoke(am, packageName,cud);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
//        }
    }

    class PackageSizeObserver extends IPackageStatsObserver.Stub {
        /**
         * @param pStats    android.content.pm.PackageStats
         * @param succeeded boolean
         * @throws RemoteException
         */
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            if (packageListener != null)
                packageListener.onGetStatsCompleted(pStats, succeeded);
        }
    }

    class ClearUserDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeded) throws RemoteException {
            if (packageListener != null)
                packageListener.onRemoveUserDataCompleted(packageName, succeded);
        }
    }

    class ClearCacheObServer extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeded) throws RemoteException {
            if (packageListener != null)
                packageListener.onRemoveCacheDataCompleted(packageName, succeded);
        }
    }
}
