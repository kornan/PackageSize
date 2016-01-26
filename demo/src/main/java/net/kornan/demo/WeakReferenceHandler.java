package net.kornan.demo;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by KORNAN on 2016/1/26.
 * WeakReference extends Handler
 */
public abstract class WeakReferenceHandler<T> extends Handler {
    private WeakReference<T> reference;

    public WeakReference<T> getReference() {
        return reference;
    }

    public void setWeakReferenceHandler(T reference) {
        this.reference = new WeakReference<>(reference);
    }


    public WeakReferenceHandler() {
    }

    public WeakReferenceHandler(T reference) {
        this.reference = new WeakReference<>(reference);
    }
}
