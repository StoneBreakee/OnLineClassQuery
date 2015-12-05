package com.example.onlineclassquery;

import java.io.Serializable;

/**
 * Created by admin on 2015/12/1.
 * 英: [kə'rɪkjʊləm]
 */
public class Curriculum implements Serializable {
    //上午 一二
    private String firstLesson;
    //上午 三四
    private String secondLesson;
    //下午 一二
    private String thirdLesson;
    //下午 三四
    private String forthLesson;
    //晚自习
    private String fifthLesson;

    public String getFirstLesson() {
        return firstLesson;
    }

    public void setFirstLesson(String firstLesson) {
        this.firstLesson = firstLesson;
    }

    public String getSecondLesson() {
        return secondLesson;
    }

    public void setSecondLesson(String secondLesson) {
        this.secondLesson = secondLesson;
    }

    public String getThirdLesson() {
        return thirdLesson;
    }

    public void setThirdLesson(String thirdLesson) {
        this.thirdLesson = thirdLesson;
    }

    public String getForthLesson() {
        return forthLesson;
    }

    public void setForthLesson(String forthLesson) {
        this.forthLesson = forthLesson;
    }

    public String getFifthLesson() {
        return fifthLesson;
    }

    public void setFifthLesson(String fifthLesson) {
        this.fifthLesson = fifthLesson;
    }
}
