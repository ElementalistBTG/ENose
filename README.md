# ENose - A Google 'Social Impact Funded' project
_(This Android app is part of an electronic nose (E-nose) to help those with smell impairment identify food degradation)_

In response to Google's initiative for community programs for the greater good and under the initiative and leadership of [George Soloupis](https://github.com/farmaker47), we (a team from GDG Athens members) have created an E-nose for Food quality assessment using a raspberry pi, some sensors, machine learning stuff and this app.

This app is used as a UI for displaying in a simple and straightforward way whether a food is edible or not. The data are acquired and evaluated from the raspberry pi and the result is sent over using Bluetooth.
[more info](https://medium.com/googledeveloperseurope/how-google-developers-communities-are-using-tech-for-social-good-38d5098942c9)

## More about this app:

In this app I created a simple "Bluetooth discovery interface" for easily finding and connecting to a nearby device capable of sending data (so that it can be used with other devices). 
Afterwards a secure socket is initiated with this device listening for data and displaying a respective result that is easy to understand.

The implementation is made using Kotlin and Jetpack Compose
