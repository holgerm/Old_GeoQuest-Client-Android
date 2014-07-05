## NPCTalk ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | ---|
| **Optional Attributes** ||||
| image | relative path to image file | String Path | --- |
| mode | text is presented word by word (no formatting possible) | "wordticker" | this is default |
|  | complete paragraph at once (simple formatting possible: bold, italics etc.) | "chunk" | |  
| **Elements and their atttributes** ||||
| dialogItem | text paragraph | String (non-empty)| --- |
| dialogItem.audio | audio file that will be played when the text is shown | String  Path | --- |

Empty dialogItems will be ignored.

There is a separate discussion of [Model-View collaboration for NPCTalk](ModelViewCollab4NPCTalk.md).

### DialogItems ###

NPCTalk missions contain xml elements of type dialogitem. Each dialog item represents a paragraph of text that is spoken by a speaker. The dialog item xml elements do have the following attributes:

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Optional Attributes** ||||
| speaker | name of the speaker | String | --- |
| sound | audio file played when this dialog item content is shown | String Path| --- |
| nextdialogbuttontext | marking of button to move on to the next dialog item or mission if this is the last dialogitem | String | Next / Weiter |
| blocking | should the interface block while text is being uncovered or sound being played? | boolean | "true" |

