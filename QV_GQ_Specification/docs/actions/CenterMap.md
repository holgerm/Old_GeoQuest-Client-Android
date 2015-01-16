CSS: github.css


# Setting the Map Center and Zoomlevel #


This action centers the map so that it at least shows some locations. These locations can be determined by parameters and can include:

- the players current position
- the visible hotspots of the current quest
- the active hotspots of the current quest

If no parameter is checked, the map is zoomed and centered ion just the same way as it has been left when it was used the last time. This works (of course) only from the second usage on regarding the very same mission.

Default is to center the map on both the player's own position and the visible hotspots.

Here is an example shown:

	<mission id="a_1" ... >
	  ...
		<onEnd>
			<rule>
		  		<action type="CenterMap" position="true" visibleHotspots="true" activeHotspots="false"/>
			</rule>
	  	</onEnd>
	</mission>


Here the rule in activity "a_1" sets the map centering and according zoom level when "a_1" has ended. At that time the map will probably not be shown. But when at a later time the map is shown it used centers and zooms according to these parameters (if not overridden by another call to CenterMap in the meantime).


## Attributes of the CenterMap Action ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| type | Name of this Action Type | CenterMap | --- |
| position |Use the current position of the player | { "true" \| "false" } | "true"|  
| visibleHotspots |Use the currently visible hotspots | { "true" \| "false" } | "true"|  
| activeHotspots |Use the currently triggering hotspots | { "true" \| "false" } | "false"|  

As stated above, if all values are "false" the map reuses center and zoom level of the previous usage state. These states are stored in GeoQuest system variables.