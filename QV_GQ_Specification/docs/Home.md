CSS: github.css


# Documentation for Quest Authors #

Each quest is made up of *triggers* and *activities*. The first trigger is the *start of the game*.  This typically starts an activity that shows a start screen and plays some audio to welcome the player.

Further quest elements are *hotspots* (geolocations) and virtual *items*.

## Rules ##

Rules drive the quests at runtime. The are build up with three parts:

1. Event
2. Conditions
3. Actions

Events  are for example:

- Starting the quest
- Reaching or leaving a hotspot
- Starting or ending an activity
- Success or failure during an activity

Before an events triggers actions optional conditions will be checked. Such condition allow you to define state dependent behavior within your quest.

[You find further documentation on events and conditions here](Rules.md).


## Actions ##

Actions contribute the dynamic part to quests. They occur in response to triggers. Many actions have side effects, i.e. the change game state. Some actions involve the user directly (e.g. by showing message), some do their work silently and influence the game flow indirectly (e.g. changing variables in the background).

The set of actions is large and continuously growing. Examples are:

- show a message to the user
- start another interaction
- change the state of a hotspot (hide, activate)

[You find further documentation on actions here](Actions.md).


## Pages ##

Pages (formerly called missions) are coherent interactions that the user performs with the device, often on screen, sometimes involving other device features such as the camera. The whole quest can be perceived as a flow of missions.

Examples for missions are:

- Display a start screen together with optional sound
- Play a video and offer controls
- Ask a question and receive the answer
- Ask the user to take a photo and store it
- Ask the user to scan a QR code
- Show a map and some hotspots on it

[You find further documentation on pages here](Pages.md).

## Layout ##

[You find documentation about layout options for pages here](Layout.md).


## Hotspots ##

Hotspots represent geolocations in a quest. They are made up by a coordinate and a radius around it. Each hotspot has a marker icon associated that is used by maps. 

Hotspots keep state:

- has the player already visited that hotspot?
- is the hotspot currently visible, i.e. will it be shown on maps?
- is the hotspot currently active, i.e. will it trigger onEnter and onLeave events?

[You find further documentation on hotspots here](Hotspots.md).

## MenuItems ##

Menu items can be used to offer game specific interaction in the standard menu of the device (e.g. access to Help or a Map etc.). They can be configured with some attributes and contain a sequence of actions that are executed when the item is selected manually by the user.

[You find further documentation on menu items here](MenuItems.md).

## Items ##

Items are virtual objects that can be collected and used in diverse ways within the games.

[You find further documentation on items here](Items.md).


## Start Your Quest ##

### Via QR Code ###

You can create QR Codes so that scanning these codes will start a certain quest using the GeoQuest App. This assumes that the GeoQuest app is already installed on the device.

The QR Code must have the following content:

	geoquest://qeevee.org:9091/game/download/QUEST_ID

where QUEST_ID is the id of the quest you want to start. You can easily get this "link" from the online editor: just copy the download link for the quest and replace "https://" by "geoquest://" and your quest will be started as soon as you scan the qr code with any usual scanner app.

### Autostart ###

You can autostart a given quest directly when your GeoQuest app is started. Currently we have to compile this option into the app by setting the "autostartquest" option in the configuration file (gq.conf) which is included in the assets folder. When doing so we recommend to also include the quest with the app, i.e. put its zip file into an "included" folder in the assets directory.