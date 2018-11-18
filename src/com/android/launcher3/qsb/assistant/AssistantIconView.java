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

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.android.launcher3.LauncherTab;
import com.android.launcher3.R;

import com.android.systemui.shared.system.ActivityManagerWrapper;

public class AssistantIconView extends View {

    private Drawable mIcon;
    private boolean mHasGoogle;

    public AssistantIconView(Context context) {
        this(context, null);
    }

    public AssistantIconView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AssistantIconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        loadViews();
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        loadViews();
    }

    public void loadViews() {
        mHasGoogle = hasPackage(LauncherTab.SEARCH_PACKAGE);
        setOnClickListener(mHasGoogle ? new AssistantIconViewClickListener(this) : null);
        mIcon = mHasGoogle ? getContext().getResources().getDrawable(R.drawable.ic_poodle_color) : 
                getContext().getResources().getDrawable(R.drawable.ic_poodle);
        if (mIcon != null) {
            int intrinsicWidth = mIcon.getIntrinsicWidth() / 2;
            int intrinsicHeight = mIcon.getIntrinsicHeight() / 2;
            int width = getWidth() / 2;
            int height = getHeight() / 2;
            mIcon.setBounds(width - intrinsicWidth, height - intrinsicHeight, width + intrinsicWidth, height + intrinsicHeight);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (mIcon != null) {
            mIcon.draw(canvas);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void startAssistant(View view) {
        if (!mHasGoogle) return;
        AssistantSearch assistantSearch = new AssistantSearch(getContext());
        boolean z;
        ContentResolver contentResolver = assistantSearch.mContext.getContentResolver();
        String assistantComponent = Secure.getString(contentResolver, "assistant");
        boolean z2 = false;
        if (TextUtils.isEmpty(assistantComponent)) {
            Object str = null;
            String voiceComponent = Secure.getString(contentResolver, "voice_interaction_service");
            if (TextUtils.isEmpty(voiceComponent)) {
                ResolveInfo resolveActivity = assistantSearch.mContext.getPackageManager().resolveActivity(new Intent("android.intent.action.ASSIST"), 65536);
                if (resolveActivity != null) {
                    str = "com.google.android.googlequicksearchbox";
                    voiceComponent = resolveActivity.resolvePackageName;
                } else {
                    z = false;
                }
            } else {
                str = "com.google.android.googlequicksearchbox";
                voiceComponent = ComponentName.unflattenFromString(voiceComponent).getPackageName();
            }
            z = str.equals(voiceComponent);
        } else {
            z = "com.google.android.googlequicksearchbox".equals(ComponentName.unflattenFromString(assistantComponent).getPackageName());
        }
        if (z) {
            z2 = ActivityManagerWrapper.getInstance().showVoiceSession(null, null, 5);
        }
        if (z2) {
            return;
        }
        assistantSearch.launchSearch("android.intent.action.VOICE_ASSIST");
    }

    private boolean hasPackage(String pkgName) {
        try {
            ApplicationInfo ai = getContext().getPackageManager()
                    .getApplicationInfo(pkgName, 0);
            return ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
