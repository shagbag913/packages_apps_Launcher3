/*
 * Copyright (C) 2018 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.qsb;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.Bundle;

import com.android.launcher3.Launcher;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.qsb.search.AppSearchProvider;

import java.lang.ref.WeakReference;

public class LongClickReceiver extends BroadcastReceiver {
    private static WeakReference<Launcher> bR = new WeakReference<>(null);

    public static void bq(final Launcher launcher) {
        LongClickReceiver.bR = new WeakReference<>(launcher);
    }

    public void onReceive(final Context context, final Intent intent) {
        final Launcher launcher = LongClickReceiver.bR.get();
        if (launcher != null) {
            final ComponentKey dl = AppSearchProvider.uriToComponent(intent.getData(), context);
            final LauncherActivityInfo resolveActivity = LauncherAppsCompat.getInstance(context).resolveActivity(new Intent(Intent.ACTION_MAIN).setComponent(dl.componentName), dl.user);
            if (resolveActivity == null) {
                return;
            }
            final ItemDragListener onDragListener = new ItemDragListener(resolveActivity, intent.getSourceBounds());
            onDragListener.setLauncher(launcher);
            launcher.getDragLayer().setOnDragListener(onDragListener);
            final ClipData clipData = new ClipData(new ClipDescription("", new String[] { onDragListener.getMimeType() }), new ClipData.Item(""));
            final Bundle bundle = new Bundle();
            bundle.putParcelable("clip_data", clipData);
            this.setResult(-1, null, bundle);
        }
    }
}
