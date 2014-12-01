CSS: github.css


# Game Element - Global Attributes #

The outer most xml element (game) contains global attributes.

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| name | Name of this game | String |--- |
| xmlformat | Version number for the used XMLSpecification (must be 5 is not used currently) | Integer | --- |
| **Optional Attributes** ||||
| bg | Background image used in every mission where no background is specified | String Path |--- (black screen color)  |
| bgcolor | Background color used in every mission where no background is specified | Color |--- (black screen color)  |
| uistyle | [UI style](UIStyles.md) that should be used throughout the game |"Web" | "Default" - the standard native UI for each activity type |


# Pages and their Attributes #

## Media Presentation, Story Telling ##

| Page Type | Short Description |  
|:--|:-- |  
| [NPCTalk] (./missions/NPCTalk.md)| A dialog scene, where an image is shown at the top and a sequence of text paragraphs which are revealed one by one after clicking a proceed button. This can be used to mimic dialogs or monologues or simply deiplay illustrated textual information. There is build in audio support for the text paragraphs. |  
| [StartAndExitScreen](./missions/StartAndExitScreen.md) | Shows an image (covering the full screen). Useful as Start Screen or to show media embedded narration, e.g. comics. |

## Creativity, Users Create Media ##

| Mission Type | Short Description |  
|:--|:-- |  
| [AudioRecord] (./missions/AudioRecord.md)| User records an audio file, e.g. for creating an interview or recording environmental sounds. |  
| [ImageCapture] (./missions/ImageCapture.md)| User  takes a photograph. |  


## Orientation and Location ##

| Mission Type | Short Description |  
|:--|:-- |  
| [MapGoogle] (./missions/MapGoogle.md)|Show a map or satellite view using Google Map. Hotspots and own location are supported. |  
| [MapOSM] (./missions/MapOSM.md)|Show a map using Open Street Map. Hotspots and own location are supported. Routing and offline map is supported. |  



## MapOSM ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| **Optional Attributes** ||||
| zoomlevel | Initial zoomlevel of the shown map | Number (1, . . ., 18) | 18 |


## Any Question - MultipleChoiceQuestion and TextQuestion ##

The following attributes are the same for all question mission, i.e. MultipleChoiceQuestion and TextQuestion. Each of these question types has different additional attributes that you find in the following specific subsections.

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| **Optional Attributes** ||||
| loopUntilSuccess | Can loop until the question has been answered correctly | Boolean | false |
| bg | Background image used in every mode (i.e. *question*, *correct* or *wrong reply*) if no more specific is given | String Path |Different default images used depending on actual mode  |
| bgQuestion | Background image used in *question* mode (i.e. *correct* or *wrong*)  if no more specific is given | String Path | Default image shows a blue question mark on black background |
| bgOnReply | Background image used in any reply mode | String Path | Different default images used depending on actual mode  |
| bgOnWrongReply | Background image used in mode *wrong reply*  | String Path | Default image shows a red cross on black background |
| bgOnCorrectReply | Background image used in mode *correct reply*  | String Path | Default image shows a blue exclamation mark on black background |

## MultipleChoiceQuestion - Additional Attributes ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Optional Attributes** ||||
| shuffle | Order of answers can be randomized | Boolean | false |
| nextbuttontext | Text shown in button to proceed to next mission in quest | String (short please) | Defaults in en and de |

For each multiple choice question you must specify a set of answers which are presented to the user. The answers are xml elements with name (\<answer>) contained within the mission element. Each answer element has the following attributes:

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Optional Attributes** ||||
| nextbuttontext | Text shown in proceed button in the following reply mode after this answer has been chosen | String (short please) | Defaults in en and de (TODO klären ???) |


## TextQuestion - Additional Attributes ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| question | Question text | String | --- |
| **Optional Attributes** ||||
| prompt | Helping text that is shown before anything is entered in the answer field  | String | Defaults in de and en (e.g. "Enter your answer here ...") |
| replyOnCorrect | Reaction text shown when correct answer given  | String | Defaults in de and en |
| replyOnWrong | Reaction text shown when wrong answer given  | String | Defaults in de and en |  

Answers are defined in XML elements like this:

    <mission ...>
    	<answers>
    		<answer>Answer_Definition_1</answer>
    		...
    		<answer>Answer_Definition_n</answer>  		
    	</answers>
    </mission>

Each answer definition is a regular expression. An answer is correct when it matches one of the given answer definitions. In the simplest case you can just give all valid answers as explicit text. 