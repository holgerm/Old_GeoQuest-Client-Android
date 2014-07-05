CSS: github.css

# Hotspots #

Hotspots represent geopositions which can interact with the quest. They are for example used to play certain media when a player reaches a certain geoposition. 

Hotspots can be declared either

- as single hotspots
- as sets of hotspots

## Single Hotspots ##

The following table shows attributes and elements of hotspot declarations:

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identifier | String |--- |
| latlong | latitude and longitude | Combination of two coordinates separated by a semicolon | --- |
| radius | radius | integer | --- |
| **Optional Attributes** ||||
| img | marker that is shown on navigation tools, e.g. maps | String Path |Portal standard icon or if not given GeoQuest standard hotspot icon |
| initialVisibility | is this hotspot initially visible on navigation tools?| Boolean |true  |
| initialActivity | does this hotspot initially trigger events? |Boolean | true |
| **Elements** ||||
| onEnter | triggers when the player enters the radius around this hotspots coordinates | [rules](Rules.md) |--- |
| onLeave | triggers when the player leaves the radius around this hotspots coordinates | [rules](Rules.md)  |--- |
| onTap | triggers when the player enters the radius around this hotspots coordinates | [rules](Rules.md)  |--- |

Here is an example of the declaration of a single hotspot:

	<hotspot id="HS_Tag_5" latlong="50.938904,6.9325" radius="30"
		initialVisibility="false" initialActivity="false" img="icons/geoIcon.png">
	  <onEnter>
	    <rule>
	      <if>
	        <missionState id="M_Tag_5_Intro" state="new"></missionState>
	      </if>
	      <action type="SetHotspotVisibility" id="HS_Tag_5" visible="false" />
	      <action type="SetHotspotActivity" id="HS_Tag_5" mode="false" />
	      <action type="SetHotspotActivity" id="HS_Tag_6" mode="true" />
	      <action type="SetHotspotVisibility" id="HS_Tag_6" visible="true" />
	      <action type="StartMission" id="M_Tag_5_Intro" />
	    </rule>
	  </onEnter>
	</hotspot>


## Sets of Hotspots ##

Sometimes one needs to deal with dynamical sets of hotspots, e.g. when one wants to show all starting points of a set of quests on a map.

### Declaring a Hotspot Set ###

Here is an example of a declaration of a set of hotspots:

	<hotspotset id="lernorte" radius="25" img="icon/geoIcon.png">
	  <onEnter>...</onEnter>
	  <onTap>...</onTap>
	  <onLeave>...</onLeave>
	</hotspotset>

This declaration only specifies what holds for *all* hotspots in the set, i.e. they all have the radius of 25m, the same marker (img) and the react in the same way on tap, enter and leave events.

### Populating a Hotspot Set ###

The next step is how to populate the declared set of hotspots. There are some actions manipulating a hotspot set, e.g. [AddHotspotToSet, ClearHotspotSet](Actions.md). 

In practise more actions will be involved. In the following example when the game starts the hotspot set lernorte is populated with all starting points of quests taken from the server portal no. 81 i.e. "BAG Spielmobile":

	<game>
	  <onStart>
	    <rule>
	      <action type="GatherQuestsInSet" src="portal:81" var="lernortquests"/>
	      <action type="ClearSet" var="lernorte"/>
	      <action type="ForEachElementInSet" set="lernortquests" iterator="currentquest">
	        <action type="CreateHotspot" var="currentHotspot" hotspotName="@currentquest.name" hotspotRadius="25" hotspotMarker="@currentquest.icon" hotspotInitialVisibility="true" hotpostInitialActivity="true" hotspotLatlong="@currentquest.startlocation"/>
	        <action type="CreateAction" var="currentStartAction" actionType="StartMission" actionID="@currentquest.id" actionStartmode="downloadIfNeeded"/>
	        <action type="AddAction" action="currentStartAction" trigger="onTap" holder="@currentHotspot"/>
	        <action type="AddToSet" element="@currentHotspot" set="@lernorte">
	      </action>
	        <onTap>
	          <rule>
	            <action type="StartQuest" quest="@each.name" start="downloadIfNeeded">
	          </rule>
	      </action>
	    </rule>
	  </onStart>
	</game>

This dynamic declarations create at runtime hotspots that are similar to the following static definition (where *@quest* refers to one of the quests found on the server portal):

	<hotspot name="@quest.name" radius="25" initialVisibility="true" initialActivity="true" marker="@quest.icon">
	  <onTap>
	    <rule>
	    </rule>
	  <rule>
	      



	          

These hotspots are then available for any map etc. within the quest.

### Usage of Hotspot Sets and Difference to Single Hotspots ###

Visibility and activity can be altered for hotspot sets in the same way as for single hotspots by using their id in the according actions (SetHostpotActivity, SetHotspotVisbility). You cannot access single hotspots that are elements of an hotspot set. *For hotspot sets you can only alter all contained hotspots at once*.