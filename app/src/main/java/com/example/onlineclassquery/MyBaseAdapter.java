package com.example.onlineclassquery;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2015/12/5.
 * 降低listview的内存消耗，重复利用item的内存资源
 * 用在MainActivity中的listview上
 */
public class MyBaseAdapter extends BaseAdapter implements Filterable {

    private List<Map<String, String>> mapList;

    private Context context;

    private LayoutInflater layoutInflater;

    //添加自定义过滤
    private MyFilter myFilter;
    private List<Map<String, String>> mapListBack = new ArrayList<Map<String, String>>();
    private MyBaseAdapter myBaseAdapter;

    public MyBaseAdapter(Context context, List<Map<String, String>> mapList) {
        this.mapList = mapList;
        this.context = context;
        //this.mapListBack = this.mapList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMyBaseAdapter(MyBaseAdapter myBaseAdapter){
        this.myBaseAdapter = myBaseAdapter;
    }

    public void addView(Map<String, String> map) {
        mapList.add(map);
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Map<String, String> getItem(int i) {
        return mapList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewGroup item;
        String name = mapList.get(position).get("name");
        String id = mapList.get(position).get("id");
        if (convertView == null) {
            // java.lang.UnsupportedOperationException: addView(View, LayoutParams) is not supported in AdapterView
            //For others who have this problem and have inflated a layout in ArrayAdapter's getView,
            // set the parent parameter to null, as in view = inflater.inflate(R.layout.mapList_item, null);

            //root	Optional view to be the parent of the generated hierarchy.
            item = (ViewGroup) layoutInflater.inflate(R.layout.maplist_item, null);
            /** root为null或者attach为false时，inflate只是产生该xml资源文件的view对象
             *  否则将view添加为root view 的子view
             *
             *  在getView中只需要生成view对象即可，不要指定root view 由系统去完成listview的渲染工作
             * */

        } else {
            item = (ViewGroup) convertView;
        }
        TextView nameview = (TextView) item.findViewById(R.id.teachersName);
        TextView idview = (TextView) item.findViewById(R.id.teachersId);
        nameview.setText(name);
        idview.setText(id);
        return item;
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter();
        }
        return myFilter;
    }

    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            MyDbHelper myDbHelper = new MyDbHelper(context, "Courses.db", null, ConstantVars.version);
            DbUtils dbUtils = new DbUtils(myDbHelper);
            List<Map<String,String>> newValues = new ArrayList<Map<String, String>>();
            if(mapListBack.size() == 0)
                mapListBack = dbUtils.getAllTeachersForFilter();
            String filterString = charSequence.toString().trim().toLowerCase();
            Log.i("lyjfilter", "start text fileter");
            //如果搜索框内容为空，就恢复原数据
            if(TextUtils.isEmpty(filterString)){
                newValues = mapListBack;
            }else {
                //过滤出新数据
                Iterator<Map<String,String>> it = mapListBack.iterator();
                while(it.hasNext()){
                    Map<String,String> tmp = it.next();
                    String metaStr = tmp.get("id")+tmp.get("name");
                    if(-1 != metaStr.toLowerCase().indexOf(filterString)){
                        newValues.add(tmp);
                    }
                }
            }
            results.values = newValues;
            results.count = newValues.size();
            return results;
            //返回给publicResults调用
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            Log.i("lyjfilter","start update list");
            mapList = (List<Map<String,String>>)filterResults.values;
            if(filterResults.count > 0){
                myBaseAdapter.notifyDataSetChanged();//通知数据发生了改变
            }else {
                myBaseAdapter.notifyDataSetInvalidated();//通知数据失效
            }
        }
    }
}
