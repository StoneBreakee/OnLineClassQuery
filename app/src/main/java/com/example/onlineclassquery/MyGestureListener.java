package com.example.onlineclassquery;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

/**
 * Created by admin on 2015/12/1.
 */
public class MyGestureListener implements GestureDetector.OnGestureListener {
    private Context context;
    private ViewFlipper viewFlipper;
    public MyGestureListener(Context context,ViewFlipper viewFlipper){
        this.context = context;
        this.viewFlipper = viewFlipper;
    }
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionStart, MotionEvent motionEnd, float v, float v1) {
        if (motionEnd.getX() - motionStart.getX() > 40){
            //turn right
            turnRight(viewFlipper,context);
        }else if(motionStart.getX() - motionEnd.getX() > 40){
            //turn left
            turnLeft(viewFlipper,context);
        }
        return true;
    }

    private void turnLeft(ViewFlipper viewFlipper,Context context){
        Animation anim_in = AnimationUtils.loadAnimation(context, R.anim.left_in);
        Animation anim_out = AnimationUtils.loadAnimation(context,R.anim.left_out);
        viewFlipper.setInAnimation(anim_in);
        viewFlipper.setOutAnimation(anim_out);
        viewFlipper.showPrevious();
    }

    private void turnRight(ViewFlipper viewFlipper,Context context){
        Animation anim_in = AnimationUtils.loadAnimation(context,R.anim.right_in);
        Animation anim_out = AnimationUtils.loadAnimation(context,R.anim.right_out);
        viewFlipper.setInAnimation(anim_in);
        viewFlipper.setOutAnimation(anim_out);
        viewFlipper.showNext();
    }

}
