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
package com.android.launcher3.qsb.search;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.android.launcher3.ItemInfo;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.userevent.nano.LauncherLogProto;

class LogContainerProvider extends FrameLayout implements UserEventDispatcher.LogContainerProvider {
    private final int mPredictedRank;

    public LogContainerProvider(Context context, int predictedRank) {
        super(context);
        mPredictedRank = predictedRank;
    }

    @Override
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        if (mPredictedRank >= 0) {
            targetParent.containerType = 7;
            target.predictedRank = mPredictedRank;
        } else {
            targetParent.containerType = 8;
        }
    }
}
