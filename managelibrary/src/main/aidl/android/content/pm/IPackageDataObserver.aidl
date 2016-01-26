// IPackageDataObserver.aidl
package android.content.pm;

// Declare any non-default types here with import statements

oneway interface IPackageDataObserver {
    void onRemoveCompleted(in String packageName,boolean succeded);
}
