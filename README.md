    sensorFetch

An android app that listens to sensory data from cellphone.

Through clock adjustments, users could view and modify current sampling interval. Integer is the only valid input. The actual sampling interval is often smaller than the one being set. (See at https://developer.android.com/reference/android/hardware/SensorManager.html#registerListener android.hardware.SensorEventListener, android.hardware.Sensor, int). Note too small a sampling interval consumes much memory of cellphone thus initial value is set to 10000, a roughly 2 second/event per sensor. Start/Stop trigger the recording or the stop process. Specific sensors could be toggled with buttons in list. Only sensors selected shall be activated.

Each Start-Stop trial log data to root\sdcard\SensorFetch directory on CSV format.

    sensorFetch 2.0
 
SensorFetch2.0 remember the perferred sensor to be checked and toggle and check them automatically on the list view. It locks choice button when data is being recorded, perventing unintended changes. When the trail is finished, a pop up menu will indicate whether data is recorded successfully. The sampling interval is set to 0.2 read/seconds.

Users are required to log in with name before recordind. Each user's data will be stored in a different folder, named aftre the upper case of the user name. Each file is named after the time of recording and the user name.

SensorFetch2.0 also provide prepocessing of sensor event data, deleting all false values for easier processing after data gathering.
