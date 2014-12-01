# StartAndExitScreen #

This mission will be renamed to "FullScreenImage".

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| image | relative path to image (png, jpg or gif) or animation file (zip) | String Path | --- |
| **Optional Attributes** ||||
| duration | how long image is shown (in milliseconds) | Number | ---|
|  | wait until user touches screen | "interactive" | This is the default|
|  | wait until animation has completed (only with animations that are not looping) | "animation" | true |
| loop | is the animation looping? (only used with animation) | Boolean | true|

## Animations ##

To use animations instead of a single static image, you can give the path to a zip file as image attribute. This zip must contain all frames which will be played in alhabetical order by filenames. 
