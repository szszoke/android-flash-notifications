# Flash Notifications
Turn your Android smartphone's flash into a notification light!

[![Build Status](https://travis-ci.org/szabolcsx/android-flash-notifications.svg?branch=master)](https://travis-ci.org/szabolcsx/android-flash-notifications)

This is an app which lets you use your phone's flash as a notification LED. As you might notice our phone doesn't have a notification LED but instead it has a flash light next to the selfie camera. Whenever you get a notification and your screen is turned off, the app will blink the flash light until you dismissed them all. For the sake of battery life the delay between the blinks is increased over time which means the longer you leave your phone alone the rarer a blink will occur.
### WHAT IS WORKING NOW
* As soon as you get a notification and your screen turns off the phone's selfie flash starts blinking
* The blinking is paused when you turn your screen on
* The blinking is stopped if you dismiss all of the notifications

### KNOWN ISSUES/LIMITATIONS
* Because of the way how AlarmManager is implemented no app can wake up the device when it is in dose mode more frequent than 15 minutes. This means as soon as the device enters into deep sleep my app will only be able to wake up the device and blink the flash light in every 15 minutes
* Notifications posted by music player/radio also trigger a notification - expected to be resolved in the future

### PLANS FOR FUTURE
* Prevent specific apps to use flash notifications
* Let the user select the which apps should use flash notifications
* Let the user switch between front and back facing flash
* Define blinking patterns for individual apps
* Make the settings exportable into storage or cloud
* Increase the list of supported devices

The current build is only expected to work on Samsung Galaxy J510 running Android 6.0.1.

### DISCLAIMER
This app comes freely WITHOUT ANY KIND OF WARRANTY! I don't take any responsibility for the damages it might cause (data loss/missed alarms because the battery died too fast/you being fired because you were late from work)! This is an alpha release! 
#### USE AT YOUR OWN RISK!