package com.example.elessar.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by elessar on 6/24/17.
 */

public class CustomListAdapter extends SimpleAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String,String>> arrayListwithData;
    private HashMap<Integer,Boolean> isSelected;

    public CustomListAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.arrayListwithData=data;
        this.context=context;
        isSelected=new HashMap<>();
        iniData();
        inflater.from(context);


    }
    private void iniData(){
        for(int i=0;i<arrayListwithData.size();i++){
            isSelected.put(i,false);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //TODO:solve the checkBox problem
        ViewHolder holder=null;
        if(convertView==null){
            //get the view  holder
            holder=new ViewHolder();
            //inflate the convertView
            convertView=LayoutInflater.from(context).inflate(R.layout.sensor_each,parent,false);
            //convertView=inflater.inflate(R.layout.sensor_each,parent,true);
            holder.sensorImage=(ImageView)convertView.findViewById(R.id.sensorImage);
            holder.sensorName=(TextView)convertView.findViewById(R.id.sensorName);
            holder.sensorChecked=(CheckBox)convertView.findViewById(R.id.sensorCheck);
            //set tag for the convert view
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.sensorName.setText(arrayListwithData.get(position).get("name"));
        holder.sensorImage.setImageResource(Integer.valueOf(arrayListwithData.get(position).get("image")));
        holder.sensorChecked.setChecked(isSelected.get(position));
        return convertView;
    }
    public HashMap<Integer,Boolean> getIsSelected(){
        return this.isSelected;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}

