package com.example.onlineclassquery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;

public class QueryClassByTeacher extends Activity {
    private HttpQueryClass httpQueryClass;
    private Teacher teacher;
    private int[][] courses = ConstantVars.courseSchedule;
    private GestureDetector gestureDetector;
    private Spinner semesterspinner;
    private ImageView codeImg;
    private String semesterValue;
    private EditText validateCodeValue;
    private List<Curriculum> curriculumList;
    private ViewFlipper viewFlipper;

    //数据库相关
    private MyDbHelper myDbHelper;
    private DbUtils dbUtils;
    private boolean first = false;

    public QueryClassByTeacher() {
        httpQueryClass = new HttpQueryClass();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_queryclassbyteacher);

        myDbHelper = new MyDbHelper(getApplicationContext(), "Courses.db", null, ConstantVars.version);
        dbUtils = new DbUtils(myDbHelper);

        //获取teacher对象
        Intent intent = getIntent();
        teacher = (Teacher) intent.getSerializableExtra("teacher");

        //在Title栏显示老师的名字和工号
        TextView textView = (TextView) findViewById(R.id.titleName);
        textView.setText(teacher.toString());

        //下拉列表
        semesterspinner = (Spinner) findViewById(R.id.semesters);
        initSemesterSpinner();
        getSemesterValue();

        //校验码
        codeImg = (ImageView) findViewById(R.id.validateCodeImg);
        initImgCode();
        codeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initImgCode();
            }
        });
        validateCodeValue = (EditText) findViewById(R.id.validateCodeText);
        validateCodeValue.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] data = getResources().getString(R.string.login_only_can_input).toCharArray();
                return data;
            }
        });

        //滑动显示该老师的课表，从周一到周日
        viewFlipper = (ViewFlipper) findViewById(R.id.curriculum);
        gestureDetector = new GestureDetector(this, new MyGestureListener(this, viewFlipper));//手势识别
        gestureDetector.setIsLongpressEnabled(true);
        viewFlipper.setLongClickable(true);
    }

    //从网络或主服务器获取课表
    //获取课表的流程
    public void getClass(View view) {
        final String id = teacher.getId();
        final String semester = semesterValue;
        final String imgCodeStr = validateCodeValue.getText().toString();
        new Thread() {
            @Override
            public void run() {
                if (curriculumList == null || curriculumList.size() == 0) {
                    //1.查看本地数据库是否已保存
                    curriculumList = dbUtils.getCourse(teacher.getId());
                    if (curriculumList == null || curriculumList.size() == 0) {
                        //若本地数据库没有，则通过网络查询服务器是否已保存
                        curriculumList = httpQueryClass.getDataByServer(semester, id, "query", "");
                        //若服务器的数据库中也没有保存，则客户端从学校网站爬取数据
                        //用来显示，同时也发送到服务器
                        if (curriculumList == null || curriculumList.size() == 0) {
                            curriculumList = httpQueryClass.getDataByWebSite(semester, id, imgCodeStr);
                            Gson gson = new Gson();
                            Intent intent = new Intent(QueryClassByTeacher.this, CourseToServerService.class);
                            intent.putExtra("data", gson.toJson(curriculumList));
                            intent.putExtra("semester", semester);
                            intent.putExtra("id", id);
                            startService(intent);
                        }
                    }
                }
                if (curriculumList != null && curriculumList.size() != 0) {
                    Log.i("lyjserver", "QueryClassByTeacher getClass curriculumList.size=" + curriculumList.size());
                }
                viewFlipper.post(new Runnable() {
                    @Override
                    public void run() {
                        initCurriculums();
                        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                gestureDetector.onTouchEvent(motionEvent);
                                return true;
                            }
                        });
                    }
                });
            }
        }.start();
    }

    //将课表保存在本地数据库中
    public void saveCourse(View view) {
        new Thread() {
            @Override
            public void run() {
                storeCourse();
                semesterspinner.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryClassByTeacher.this, "Save Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    private void storeCourse() {
        SQLiteDatabase readData = myDbHelper.getReadableDatabase();
        Cursor cursor = readData.rawQuery("select * from courses where teacher_id = " + teacher.getId(), null);
        //如果本地数据库中已经存在，则直接返回
        if (!cursor.moveToFirst()) {
            dbUtils = new DbUtils(myDbHelper);
            SQLiteDatabase writeData = dbUtils.getWriteData();
            String id = teacher.getId();
            int course_day = 1;
            String add_course = "insert into courses(teacher_id,course_seq,course_day,course_content) values(?,?,?,?)";
            if (curriculumList != null && curriculumList.size() != 0 && !first) {
                first = !first;
                for (Curriculum c : curriculumList) {
                    String course_content = c.getFirstLesson();
                    writeData.execSQL(add_course, new String[]{id, "1", course_day + "", course_content});
                    course_content = c.getSecondLesson();
                    writeData.execSQL(add_course, new String[]{id, "2", course_day + "", course_content});
                    course_content = c.getThirdLesson();
                    writeData.execSQL(add_course, new String[]{id, "3", course_day + "", course_content});
                    course_content = c.getForthLesson();
                    writeData.execSQL(add_course, new String[]{id, "4", course_day + "", course_content});
                    course_content = c.getFifthLesson();
                    writeData.execSQL(add_course, new String[]{id, "5", course_day + "", course_content});
                    course_day++;
                }
            }
        }
    }

    private void initCurriculums() {
        /** lesson * day
         *  mon_first_lesson    tue_first_lesson  ... ...
         *  mon_second_lesson   tue_second_lesson ... ...
         *  ... ...
         * */
        try {
            int day = 0, lesson = 0;
            Iterator<Curriculum> it = curriculumList.iterator();
            while (it.hasNext()) {
                Curriculum curriculum = it.next();
                TextView textView = (TextView) findViewById(courses[lesson][day]);
                textView.setText(curriculum.getFirstLesson());
                textView = (TextView) findViewById(courses[lesson + 1][day]);
                textView.setText(curriculum.getSecondLesson());
                textView = (TextView) findViewById(courses[lesson + 2][day]);
                textView.setText(curriculum.getThirdLesson());
                textView = (TextView) findViewById(courses[lesson + 3][day]);
                textView.setText(curriculum.getForthLesson());
                textView = (TextView) findViewById(courses[lesson + 4][day]);
                textView.setText(curriculum.getFifthLesson());
                day++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("lyjserver", "QueryClassByTeacher initCurriculums java.lang.NullPointerException,maybe imgCode is null");
            Toast.makeText(QueryClassByTeacher.this, "imgCode is null", Toast.LENGTH_SHORT).show();
        }

    }

    //根据用户选择的学期获取相应的请求字段(Sel_XNXQ)的值
    private void getSemesterValue() {
        semesterspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                semesterValue = ConstantVars.semestersIndex[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //加载校验码
    private void initImgCode() {
        new Thread() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapFactory.decodeFile(httpQueryClass.getValidateCodeImg());
                codeImg.post(new Runnable() {
                    @Override
                    public void run() {
                        codeImg.setImageBitmap(bitmap);
                    }
                });

            }
        }.start();
    }

    //加载下拉列表的数据供用户选择
    private void initSemesterSpinner() {
        String[] tmpsem = getResources().getStringArray(R.array.semestersText);
        ArrayAdapter<String> tmpadapter = new ArrayAdapter<String>(QueryClassByTeacher.this, android.R.layout.simple_list_item_1, tmpsem);
        semesterspinner.setAdapter(tmpadapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_queryclassbyteacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
