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
package com.android.launcher3.qsb.assistant;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import com.android.launcher3.compat.LauncherAppsCompat;

public class AssistantSearch {

    final Context mContext;

    public AssistantSearch(Context context) {
        mContext = context;
    }

    public final void launchSearch(String str) {
        try {
            mContext.startActivity(new Intent(str).addFlags(268468224).setPackage("com.google.android.googlequicksearchbox"));
        } catch (ActivityNotFoundException e) {
            LauncherAppsCompat.getInstance(mContext).showAppDetailsForProfile(new ComponentName("com.google.android.googlequicksearchbox", ".SearchActivity"), Process.myUserHandle());
        }
    }
}
