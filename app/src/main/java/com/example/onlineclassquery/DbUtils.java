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
            String queryTeacher = "select id, name from teachers where id < '0000100' order by id";
            Cursor cursor = readData.rawQuery(queryTeacher, null);
            if (cursor.moveToFirst()) {
                do {
                    Map<String, String> tmp = new HashMap<String, String>();
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    tmp.put("id", id);
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.i("lyj", "DbUtils name=" + name);
                    tmp.put("name", name);
                    teacherDatabaseMap.add(tmp);
                } while (cursor.moveToNext());
                Log.i("lyj","DbUtils teacherDatabaseMap.size()="+teacherDatabaseMap.size());
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
        /** select teacher_id,course_day,course_seq,course_content from courses
         * where teacher_id = ? order by course_day,course_seq;
         *
         *  0000711|1|1|
         *  0000711|1|2|浣撹偛涓庡仴搴封厾 [1-18鍛╙3-4鑺?锛?5鍔ㄧ墿鍖诲2 浜烘暟锛?0
         *  0000711|1|3|浣撹偛涓庡仴搴封厾 [1-18鍛╙5-6鑺?锛?5鐜颁唬鍐滀笟1 浜烘暟锛?5
         *  0000711|1|4|浣撹偛涓庡仴搴封厾 [1-18鍛╙7-8鑺?锛?5鍥灄寤虹瓚1 浜烘暟锛?1
         *  0000711|1|5|
         *
         *  0000711|2|1|
         *  0000711|2|2|
         *
         *  List Curriculum
         *  Curriculum1  1,2,3,4,5  周一
         *  Curriculum2 1，2，3，4，5 周二
         */
        try {
            List<Curriculum> curList = new ArrayList<Curriculum>();
            Curriculum curriculum;
            String query_course = "select teacher_id,course_seq ,course_day,course_content from courses where teacher_id = ? order by course_day,course_seq";
            Cursor cursor = readData.rawQuery(query_course, new String[]{id});
            for (int i = 0;i < 7;i++) {
                curriculum = new Curriculum();
                cursor.moveToNext();
                Log.i("lyjfilter", "current cursor ,day:" + cursor.getString(cursor.getColumnIndex("course_day")) + " seq:" + cursor.getString(cursor.getColumnIndex("course_seq")));
                curriculum.setFirstLesson(cursor.getString(cursor.getColumnIndex("course_content")));
                cursor.moveToNext();
                curriculum.setSecondLesson(cursor.getString(cursor.getColumnIndex("course_content")));
                cursor.moveToNext();
                curriculum.setThirdLesson(cursor.getString(cursor.getColumnIndex("course_content")));
                cursor.moveToNext();
                curriculum.setForthLesson(cursor.getString(cursor.getColumnIndex("course_content")));
                cursor.moveToNext();
                curriculum.setFifthLesson(cursor.getString(cursor.getColumnIndex("course_content")));
                curList.add(curriculum);
            }
            cursor.close();
            return curList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String,String>> getAllTeachersForFilter(){
        try {
            List<Map<String,String>> tmpMapList = new ArrayList<Map<String, String>>();
            String query_teachers = "select * from teachers order by id";
            Cursor cursor = readData.rawQuery(query_teachers,null);
            if(cursor.moveToFirst()){
                do {
                    Map<String, String> tmp = new HashMap<String, String>();
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    tmp.put("id", id);
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.i("lyj", "name=" + name);
                    tmp.put("name", name);
                    tmpMapList.add(tmp);
                }while(cursor.moveToNext());
            }
            return tmpMapList;
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
