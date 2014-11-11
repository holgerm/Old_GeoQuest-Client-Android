# Versioning #

## 0.9.2 ##

- wordticker bug fixed
- Mission State Variables work now correctly
- Hotpot variables work now
- Current Location variables work 
- Imprint is included now and reachable from everywhere in the app via the menu

## 0.9.1 ##

- bug in GetGameList fixed (had called UI from background thread)
- bug in onStart event fixed: now actions that would leave the mission are ignored in onStart.

## 0.9.0 ##

- features for a customer release on 24.10.2014, cf. Redmine for details

## 0.9.0-p3 ##

- Webpage Page: endButtonText Attribute works now
- Spinner shown while page loads
- Login in Prefs enabled to load personal non-public games

## 0.9.0-p2 ##

- VideoPlay Page: controllable Attribute works now

## 0.9.0-p1 ##

- Packages renamed and unnecessary code removed
- Mission results are now stored in $_mission_ID.result instead of $_ID.result wich is conform to the editor again
- script for product setting (cf. script/gq_set_product)
- New page type TextWithImage (based on NPCTalk) is tested and should work.

## 0.8.3.11 ##

- many Attributes (e.g. question texts) can now use variables which are automatically replaced

## 0.8.3.10 ##

- bug fixes

## 0.8.3.9 ##

- external missions completely removed 

## 0.8.3.8 ##

- new: config option portals 

## 0.8.3.7 ##

- new: config file with option autostart

## 0.8.3.4 ##

- in Autostart the quest starts directly without shortly showing any other start screen. Without autostart nothing has changed.

## 0.8.3.3 ##

- minor bugfixes

## 0.8.3.2 ##

- minor bugfixes

## 0.8.3.1 ##

- During autostart mode, dialogs and menu item title for end game are the same as for terminate app, since they behave the same way. I.e. the app terminates when you end the single autostart game.