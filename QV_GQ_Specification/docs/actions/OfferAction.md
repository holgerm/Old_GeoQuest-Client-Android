CSS: github.css


# Offer an Action #

This action offers an action to the user, e.g. accessible via a button or menu item which the client app can show him during the game.

The action which is offered 

	<mission id="a_1" ... >
	  ...
		<onAccess>
			<rule>
				<if>
					<state name="drop" state="dropped" />
				</if>
				<then>
					<action type="OfferAction" name="take" icon="drawable/take.png">
						<action type="SetState" object="item:Turmfalke" name="drop" value="taken"></action>
						<action type="MoveItem" to="player" id="item:Turmfalke"></action>
					</action>
				</then>
			</rule>
		</onAccess>
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
