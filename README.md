GeoQuest player - Android native 
=======


# Checkout #

We are using eclipse IDE for development. After checkout you can import the following Eclipse projects into your Eclipse workspace:

- GQ_Android - the main implementation code for the android client
- GQ_Android_ExternalMissionHelper - an android library for conveniently integrating external apps as missions into GeoQuest
- GQ_Android_Utilities - an android library used by the client

These are all Eclipse android projects, hence import them appropriately in the eclipse import wizard.

Furthermore we use Robolectric for fast testing. You find the tests in a separate Eclipse project:

- GQ_Andoird_Tests_Robolectric - a Java Eclipse project

You can only import this project as Java project not as Android project from the Eclipse Import Wizard.

Within this testing project we refer to the Android libs by an Eclipse Variable ANDROID_SDK_HOME which you must define in Eclipse after checkout. You can easily do that when you follow the quick fix offered by eclipse in the errors shown in the problem view. Just set this value to your path to the android SDK (typically found by "echo $ANDROID_SDK").
