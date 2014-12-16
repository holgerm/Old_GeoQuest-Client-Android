CSS: github.css 

# Game Element - Global Attributes #

The outer most xml element (game) contains global attributes.

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| name | Name of this game | String | --- |
| xmlformat | Version number for the used XMLSpecification (must be 5 is not used currently) | Integer | --- |
| **Optional Attributes** ||||
| bg | Background image used in every mission where no background is specified | String Path |--- (black screen color)  |
| bgcolor | Background color used in every mission where no background is specified | Color |--- (black screen color)  |


# Missions and their Attributes #

## NPCTalk ##

This mission will be renamed into "Dialog". 

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| **Optional Attributes** ||||
| image | relative path to image file | String Path | --- |
| mode | text is presented word by word (no formatting possible) | "wordticker" | this is default  |
|  | complete paragraph at once (simple formatting possible: bold, italics etc.) | "chunk" |  |

### DialogItems ###

NPCTalk missions contain xml elements of type dialogitem. Each dialog item represents a paragraph of text that is spoken by a speaker. The dialog item xml elements do have the following attributes:

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Optional Attributes** ||||
| speaker | name of the speaker | String | --- |
| sound | audio file played when this dialog item content is shown | String Path| --- |
| nextdialogbuttontext | marking of button to move on to the next dialog item or mission if this is the last dialogitem | String | Next / Weiter |
| blocking | should the interface block while text is being uncovered or sound being played? | boolean | "true"" |


## StartAndExitScreen ##

This mission will be renamed to "FullScreenImage".

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| image | relative path to image file | String Path | --- |
| **Optional Attributes** ||||
| duration | how long image is shown (in milliseconds) | Number | 5000 ms|
|  | wait until user touches screen | "interactive" | |


## AudioRecord ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| file | relative path file location for storing recorded audio | String Path | --- |
| **Optional Attributes** ||||
| task | Text that tells what to do | String | Default text in en and de given (e.g. "Your task: record a piece of audio!") |


## ImageCapture ##

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| file | relative path file location for storing recorded audio | String Path | --- |
| **Optional Attributes** ||||
| task | Text that tells what to do | String | Default text in en and de given (e.g. "Your task: take a picture!") |
| image | Path to image that is shown at beginning when task text is displayed | String Path | (no image shown if omitted) |
| uploadURL | Base URL of web site where image will be uploaded to | String URL | Does only store image locally if not given |


## MapOverview ##

This mission type will be renamed to "MapGoogle".

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| **Optional Attributes** ||||
| zoomlevel | Initial zoomlevel of the shown map | Number (1, …, 18) | 18 |
| mapkind | Kind of map images shown (e.g. map or satellite images) | { map, satellite } | map |


## OSMap ##

This mission type will be renamed to "MapOSM".

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| **Optional Attributes** ||||
| zoomlevel | Initial zoomlevel of the shown map | Number (1, …, 18) | 18 |


## Any Question - MultipleChoiceQuestion and TextQuestion ##

The following attributes are the same for all question mission, i.e. MultipleChoiceQuestion and TextQuestion. Each of these question types has different additional attributes that you find in the following specific subsections.

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
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
| question | Question text | String | --- |
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