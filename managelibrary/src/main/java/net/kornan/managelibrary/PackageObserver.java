package net.kornan.managelibrary;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;

import java.lang.reflect.Method;

/**
 * Created by KORNAN on 2016/1/25.
 * You need to add a 'android.permission.GET_PACKAGE_SIZE' permission;
 */
public class PackageObserver extends IPackageStatsObserver.Stub {
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
     *
     * @param packageName PackageName
     * @throws Exception
     */
    public void queryPacakgeSize(String packageName) throws Exception {
        if (packageName != null) {
            PackageManager pm = context.getPackageManager();
            try {
                Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, packageName, this);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    /**
     * @param packageStats android.content.pm.PackageStats
     * @param succeeded    boolean
     * @throws RemoteException
     */
    @Override
    public void onGetStatsCompleted(final PackageStats packageStats, final boolean succeeded) throws RemoteException {
        if (packageListener != null)
            packageListener.onGetStatsCompleted(packageStats, succeeded);
    }
}
