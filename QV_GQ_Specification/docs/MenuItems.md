CSS: github.css


# Menu Items #

**Menu items are not yet implemented. This documentation is only a specification of the upcoming feature.**

The following table gives an overview about all features regarding menu items supported in GeoQuest. An example is given here:

	<menuItem activity="true" priority="50" icon="some/path/or/URI"
		title="TestItem" showText="false" id="1234" show="always">
		<rule>
			<onSelect>
				<action>...</action>
			</onSelect>
		</rule>
	</menuItem>

Regardless of the way the item is shown, when the user manually selects it, the given actions in the rule among onSelect will be executed.

| Attribute Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identifier of the menu item | String (unique among all menuItems stored in the DB) | generated |
| title | Shown title of the Item | String (really short please) | --- |
| **Optional Attributes** ||||
| activity | shown and enabled versus hidden| boolean | true |
| icon | file path to the icon or URL| String (path or URI) | --- |
| priority | The higher it is the easier the item is accessible, i.e. the more up in the list it is shown. | int (we typically use 0, ..., 100) | 50 |
| show |Should the item be shown separately? Alternatively it is shown in the overflow list, which has to be brought up by the user manually. | { always, if_room, never } | if_room |
| showText | Should the title be shown as text additionally to the icon, if given? (In case no icon is given, the title is always shown.)| boolean | false |

