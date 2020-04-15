package com.android_projects.newsapipractice.Utils;

import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

public class DetailsTransition extends TransitionSet {
    private final long FADE_DEFAULT_TIME= 800;
    private final long MOVE_DEFAULT_TIME = 100;

    public DetailsTransition(){
        initEnterTransitionSet();
    }
    public DetailsTransition(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initEnterTransitionSet();
    }

    private void initEnterTransitionSet(){
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).addTransition(new ChangeImageTransform());
        setDuration(MOVE_DEFAULT_TIME);
        setStartDelay(FADE_DEFAULT_TIME);
    }


}
