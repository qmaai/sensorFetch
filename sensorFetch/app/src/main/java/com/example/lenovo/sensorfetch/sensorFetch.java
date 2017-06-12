package com.example.lenovo.sensorfetch;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.example.lenovo.sensorfetch.R.id.action;

public class sensorFetch extends AppCompatActivity implements SensorEventListener{
    private ListView sensorList;
    private SensorManager manager;
    private List<Sensor> availableSensors;
    private Field[] drawables;
    private final R.drawable drawableResources = new R.drawable();
    private CustomListAdpater myAdapter;
    private ArrayList<HashMap<String,String>> listForAdapter;
    private ArrayList<String> sensorNameList;
    private Calendar calendar;
    private SimpleDateFormat format=new SimpleDateFormat("mmdd_HHMMSS");
    private HashMap<String,ArrayList<Object>> sensorFileHolder=new HashMap<String,ArrayList<Object>>();
    File root;
    File dir;
    private int userTime=10000;
    private FileWriter writer;
    //private FileOutputStream outPutStream;
    private Button selectAll;
    private Button cancel;
    private Button reverseSelect;

    @Override
    //initialise the manager and the sensorList
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_fetch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectAll=(Button)findViewById(R.id.selectAll);
        cancel=(Button)findViewById(R.id.cancel);
        reverseSelect=(Button)findViewById(R.id.selectReverse);

        manager=(SensorManager) getSystemService(SENSOR_SERVICE);
        availableSensors=manager.getSensorList(Sensor.TYPE_ALL);

        setUpListView();
        sensorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ViewHolder holder=(ViewHolder)view.getTag();
                    //改变checkBox 状态
                    holder.sensorChecked.toggle();
                    //记录状态
                    myAdapter.getIsSelected().put(position,holder.sensorChecked.isChecked());
            }
        });

        root=android.os.Environment.getExternalStorageDirectory();
        dir=new File(root.getAbsolutePath()+"/SensorFetch");
        dir.mkdirs();
    }

    // the control functio for the three buttons
    public void selectAll(View view) {
        for(int i=0;i<sensorNameList.size();i++){
            myAdapter.getIsSelected().put(i,true);
        }
        dataChanged();
    }
    public void removeAll(View view){
        for(int i=0;i<sensorNameList.size();i++){
            if(myAdapter.getIsSelected().get(i)){
                myAdapter.getIsSelected().put(i,false);
            }
        }
        dataChanged();
    }
    public void reverseAll(View view) {
        for(int i=0;i<sensorNameList.size();i++){
            if(myAdapter.getIsSelected().get(i)){
                myAdapter.getIsSelected().put(i,false);
            }
            else{
                myAdapter.getIsSelected().put(i,true);
            }
        }
        dataChanged();
    }
    //for list view manipulation
    private void dataChanged(){
        myAdapter.notifyDataSetChanged();
    }
    private void setUpListView() {
        sensorList=(ListView)findViewById(R.id.sensorList);
        listForAdapter=new ArrayList<>();

        sensorNameList=new ArrayList<>();
        for(Sensor sensor: availableSensors){
            sensorNameList.add(sensor.getName().toLowerCase());
        }
        ArrayList<String> imageList=retrievingAndMatchingDrawable(sensorNameList);
        for(int i=0;i<sensorNameList.size();i++){
            String s=sensorNameList.get(i);
            HashMap<String,String> map=new HashMap<>();
            map.put("name",s);
            map.put("image",imageList.get(i));
            Log.e("TAG",map.toString());
            listForAdapter.add(map);
        }
        String[] from={"name","image"};
        int[] to={R.id.sensorName,R.id.sensorImage};
        myAdapter=new CustomListAdpater(this,listForAdapter,R.layout.sensor_each,from,to);
        sensorList.setAdapter(myAdapter);
    }
    public ArrayList<String> retrievingAndMatchingDrawable(ArrayList<String> sensorNameList){
        ArrayList<String> sensorImage=new ArrayList<>();

        Log.e("TAG","the size is "+sensorNameList.size());
        drawables = R.drawable.class.getFields();
        for(int j=0;j<sensorNameList.size();j++){
            String s=sensorNameList.get(j);
            for (Field f:drawables) {
                if (s.contains(f.getName())){
                    int i;
                    try {
                        i = f.getInt(drawableResources);
                        sensorImage.add(i+"");
                        Log.e("TAG",f.getName()+" and the id is "+i+"\nand the sensor name is "+s);
                        Log.e("TAG",sensorImage.toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            //check whether there is a matching pair. If not, use a general image;
            while(sensorImage.size()<j+1){
                sensorImage.add(R.drawable.cloud+"");
                Log.e("TAG","no matching for "+s+", so add a default image; "+sensorImage.size());
            }
            /* make use of resourceId for accessing Drawables here */

        }

        return sensorImage;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_fetch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.changeInterval){
            //try to pop up an alert dialog with the help of inflater
            LayoutInflater inflater=LayoutInflater.from(sensorFetch.this);
            View clockView=inflater.inflate(R.layout.activity_set_clock,null);
            TextView textview=(TextView)clockView.findViewById(R.id.alertText);
            textview.setText("sampling interval "+userTime+" to ");
            //use the builder to build
            AlertDialog.Builder builder=new AlertDialog.Builder(sensorFetch.this);
            builder.setView(clockView);
            final EditText userInput=(EditText)clockView.findViewById(R.id.editTextDialogUserInput);
            //set dialog message
            builder.setCancelable(false)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // get user input and set it to result
                            // edit text
                            userTime=Integer.valueOf(userInput.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        if(id== action){
            if(item.getTitle().equals("stop")){
                Toast.makeText(this,"start recording",Toast.LENGTH_SHORT).show();
                item.setIcon(R.drawable.stop);
                item.setTitle("record");
                try {
                    startRecordingData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(item.getTitle().equals("record")){
                Toast.makeText(this,"stop",Toast.LENGTH_SHORT).show();
                item.setIcon(R.drawable.start);
                item.setTitle("stop");
                try {
                    stopRecordingData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //receive the user input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        userTime=data.getExtras().getInt("period");
    }

    private void stopRecordingData() throws IOException {
        manager.unregisterListener(this);
        writer.close();
    }
    private void startRecordingData() throws IOException {
        //first get the sensors that should be recorded.
        for(int i=0;i<sensorNameList.size();i++){
            if(myAdapter.getIsSelected().get(i)){
                manager.registerListener(this,availableSensors.get(i),userTime);
            }
        }
        File file=new File(dir,format.format(Calendar.getInstance().getTime())+".csv");
        writer=new FileWriter(file);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor!=null){
            String entry=buildEntry(event);
            int position=sensorNameList.indexOf(event.sensor.getName().toLowerCase());
            if(myAdapter.getIsSelected().get(position)){
                if(writer!=null){
                    try {
                        writer.write(entry);
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.e("ERROR","the fileWriter is null");
                }
            }
            else{
                Log.e("ERROR","the sensor is not registered");
            }
        }else{
            Log.e("ERROR","the event is null");
        }
    }

    private String buildEntry(SensorEvent event) {
        String time=format.format(Calendar.getInstance().getTime())+",";
        String sensorValue=event.sensor.getName()+",";
        for(float f:event.values){
            sensorValue+=f+",";
        }
        return time+sensorValue+"\n";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterListener(this);
    }

/*
    private Sensor getSensor(View view){
        View parent=(View)view.getParent();
        TextView sensorName=(TextView)parent.findViewById(R.id.sensorName);
        Log.e("ERROR","the sensor being clicked is "+sensorName.getText().toString());
        //Log.e("ERROR","the list "+sensorNameList.toString()+"\n"+sensorNameList.contains(sensorName))
        Sensor sensor=availableSensors.get(sensorNameList.indexOf(sensorName.getText().toString()));
        return sensor;
    }
    private void updateLInkedSensorInformation(Sensor sensor) throws FileNotFoundException {
        ArrayList<Object> list = new ArrayList<>();
        list.add(sensor);
        File file=createAFile(sensor);
        list.add(file);
        //take notice! a stream can not be passed on!
        //list.add(getOutPutStream(file));
        sensorFileHolder.put(sensor.getName(),list);
        //Log.e("HELL",sensorFileHolder.toString());
    }
    public void checkSensor(View view) throws IOException {
        Boolean isChecked=((CheckBox)view).isChecked();
        Sensor sensor=getSensor(view);
        if(isChecked){
            //TODO: then start the file writing
            updateLInkedSensorInformation(sensor);
            manager.registerListener(this,sensor,3000);
        }
        else{
            //TODO:then stop the file writing
            manager.unregisterListener(this,sensor);
            String key=sensor.getName();
            Log.e("ERROR","null pointer "+sensorFileHolder);
            if(sensorFileHolder.containsKey(key)){
                FileOutputStream outPutStream=(FileOutputStream) sensorFileHolder.get(key).get(2);
                Log.e("ERROR","the outPutStream is "+ outPutStream);
                Log.e("ERROR",sensorFileHolder.toString());
                outPutStream.close();
            }
            else{
                Log.e("ERROR","it does not exit");
            }
        }
    }
    private File createAFile(Sensor sensor) {
        File path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        path.mkdirs();
        String fileName=sensor.getName()+"_"+format.format(Calendar.getInstance().getTime())+".csv";
        File file=new File(path,fileName);
        return file;
    }*/
}
