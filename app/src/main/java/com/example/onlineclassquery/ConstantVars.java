package com.example.onlineclassquery;

/**
 * Created by admin on 2015/12/1.
 */
public class ConstantVars {
    public static final String[] semestersIndex = {"20150","20141","20140","20131","20130","20121","20120","20111","20110","20101","20100"};

    //Monday ~ Sunday first lesson,int [] Resources
    public static final int[] firstLesson ={R.id.mon_first,R.id.tue_first,R.id.wen_first,R.id.thu_first,R.id.fri_first,R.id.sat_first,R.id.sun_first};

    //Monday ~ Sunday second lesson,int [] Resources
    public static final int[] secondLesson ={R.id.mon_second,R.id.tue_second,R.id.wen_second,R.id.thu_second,R.id.fri_second,R.id.sat_second,R.id.sun_second};

    //Monday ~ Sunday third lesson,int [] Resources
    public static final int[] thirdLesson ={R.id.mon_third,R.id.tue_third,R.id.wen_third,R.id.thu_third,R.id.fri_third,R.id.sat_third,R.id.sun_third};

    //Monday ~ Sunday fourth lesson,int [] Resources
    public static final int[] fourthLesson ={R.id.mon_fourth,R.id.tue_fourth,R.id.wen_fourth,R.id.thu_fourth,R.id.fri_fourth,R.id.sat_fourth,R.id.sun_fourth};

    //Monday ~ Sunday fifth lesson,int [] Resources
    public static final int[] fifthLesson ={R.id.mon_fifth,R.id.tue_fifth,R.id.wen_fifth,R.id.thu_fifth,R.id.fri_fifth,R.id.sat_fifth,R.id.sun_fifth};

    //Monday ~ Sunday
    public static final int[][] courseSchedule = {firstLesson,secondLesson,thirdLesson,fourthLesson,fifthLesson};
}
