# SAFEBAND

  With the growth of violence in Brazil, people feel increasingly insecure when carrying 
out daily activities. Therefore, this work has the objective of developing a system
that performs the transmission of the smartphone’s location of the user that reported
being at risk, through a hardware prototype. This device, developed with ESP8266 
microcontroller model ESP-01, connects to the Firebase server and changes the user’s state,
so the Android application recognizes this change and informs the added contacts that
the user is in danger, and they can access the real-time location on the map, and can
facilitate police action to counteract the situation. The application also allows the user
to manage their current state, being able to define whether it is safe or at risk and also,
with the help of Firebase, can create a new account, log in into the app and recover
the password. For these functionalities could be carried out correctly, it was necessary
to route the smatphone network to the microcontroller, since it does not have its own
network. Finally, tests of the developed prototype were performed, which proved to be
functional and effective for the defined purposes.