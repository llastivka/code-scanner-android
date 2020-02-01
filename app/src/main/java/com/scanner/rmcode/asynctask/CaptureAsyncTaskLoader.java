package com.scanner.rmcode.asynctask;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public class CaptureAsyncTaskLoader extends AsyncTaskLoader<byte[]> {

    public CaptureAsyncTaskLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public byte[] loadInBackground() {
        return new byte[0];
    }
}
