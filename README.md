  sensorFetch

an android app that detects and fetches out data from sensors

the single-page app possesses several functionalities.

On the action bar, there are two icons: one clock and one start/stop icon. 
Three buttons and a listview comprises the content view.
the clock icon if clicked, will display the current sampling interval and update thesampling interval from the user input. The editext expect number only. From android Doc, the actual sampling interval is often smaller than the one being set. See at https://developer.android.com/reference/android/hardware/SensorManager.html#registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int) notice that too little a sampling interval will take up the whole memory of the cellphone. So the initial value is set at 10000, a roughly 2 second/event per sensor

the start/stop trigger the recording or the stop process

the button and the listview help user toggle the requied sensor. Only the toggles sensors will be recorded later.

the output file will be in csv format, stored in root\sdcard\SensorFetch. Each trial will result in one CSV file only. thr file can further be transmitted via other ways.

    sensorFetch 2.0
 
 Several fnuctionalities has been added to sensorFetch as well as soem removed.
 
To begin with, sensorFetch2.0 remember the perferred sensor to be checked and toggle and check them automatically on the list view.
It has also locked the listView when data is being recorded, perventing unintended changes.

When the record is done, a pop up menu will indicate whether the data is recorded successfully, guaranteeing the existence of the data record. The pop up menu is implemented based on the step detector, whose data is not recorded by the file.

Previous functionality of setting the time interval is removed, the interval is set to be 200000 microseconds.

Users are required to log in their user name before starting to record. Each user's data will be stored in a different folder, named aftre the upper case of the user name. Each file is named after the time of recording and the user name.

SensorFetch2.0 also provide prepocessing of sensor event data, deleting all false values for easier processing after data gathering.
