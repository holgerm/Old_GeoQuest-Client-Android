CSS: github.css


# Starting an Activity #

**This action will be renamed to StartActivity!**

This action starts a new activity. It needed to chain interactions and build the flow of any quest. 

Even for the simplest linear quests it is used in reaction to ending an activity, e.g.

	<mission id="a_1" ... >
	  ...
		<onEnd>
			<rule>
		  		<action type="StartMission" id="a_2" />
			</rule>
	  	</onEnd>
	</mission>


Here the rule in activity "a_1" starts the activity "a_2" as soon as "a_1" has ended.



	<mission id "a_2" ...>
	  ...
	</mission>

## Attributes of the StartActivity Action ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| type | Name of this Action Type | StartMission (will become StartActivity) ||
| id | Identifier of the activity to start | String (unique among activity IDs within this quest) | --- |
