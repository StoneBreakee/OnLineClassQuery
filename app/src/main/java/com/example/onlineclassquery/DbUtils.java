package com.example.onlineclassquery;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2015/12/2.
 */
public class DbUtils {
    private SQLiteDatabase readData;
    private SQLiteDatabase writeData;
    private List<Map<String, String>> teacherDatabaseMap;

    public DbUtils(MyDbHelper myDbHelper) {
        readData = myDbHelper.getReadableDatabase();
        writeData = myDbHelper.getWritableDatabase();
        teacherDatabaseMap = new ArrayList<Map<String, String>>();
    }

    public List<Map<String, String>> getTeacherMap() {
        return teacherDatabaseMap;
    }

    public void queryAllTeachers() {
        try {
            String queryTeacher = "select id, name from teachers";
            Cursor cursor = readData.rawQuery(queryTeacher, null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, String> tmp = new HashMap<String, String>();
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    tmp.put("id", id);
                    //解决中文乱码
                    //                    byte[] namebyte = cursor.getBlob(cursor.getColumnIndex("name"));
                    //                    String name = new String(namebyte,"gb2312");
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.i("lyj", "name=" + name);
                    tmp.put("name", name);
                    teacherDatabaseMap.add(tmp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SQLiteDatabase getWriteData() {
        return writeData;
    }

    public List<Curriculum> getCourse(String id) {
        try {
            int course_seq_id = 1;
            List<Curriculum> curList = new ArrayList<Curriculum>();
            Curriculum curriculum = new Curriculum();
            String query_course = "select teacher_id,course_seq ,course_day,course_content from courses where teacher_id = ? order by course_day,course_seq";
            Cursor cursor = readData.rawQuery(query_course, new String[]{id});
            if (cursor.moveToFirst()) {
                do {
                    String course_seq = cursor.getString(cursor.getColumnIndex("course_seq"));
                    String course_content = cursor.getString(cursor.getColumnIndex("course_content"));
                    setCourseSeq(course_seq,curriculum,course_content);
                    if(course_seq_id == 5){
                        curList.add(curriculum);
                        course_seq_id = 0;
                    }
                    course_seq_id++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            return curList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setCourseSeq(String course_seq,Curriculum curriculum,String content){
        if (course_seq.equals("1")) {
            curriculum.setFirstLesson(content);
        }else if (course_seq.equals("2")){
            curriculum.setSecondLesson(content);
        }else if (course_seq.equals("3")){
            curriculum.setThirdLesson(content);
        }else if (course_seq.equals("4")){
            curriculum.setForthLesson(content);
        }else if (course_seq.equals("5")){
            curriculum.setFifthLesson(content);
        }
    }

}
