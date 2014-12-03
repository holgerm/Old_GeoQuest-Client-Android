CSS: github.css

# StartAndExitScreen #

This mission will be renamed to "FullScreenImage".

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| image | relative path to image (png, jpg or gif) or animation file (zip) | String Path | --- |
| **Optional Attributes** ||||
| duration | how long image is shown (in seconds) | Number | ---|
|  | wait until user touches screen | "interactive" | This is the default|
|  | wait until animation has completed (only with animations that are not looping) | "animation" | true |
| loop | is the animation looping? (only used with animation) | Boolean | true|

## Animations ##

To use animations instead of a single static image, you can give the path to a zip file as image attribute. This zip must contain all frames which will be played in alphabetical order by filenames. 

Duration and animations is somewhat tricky.  Here are some typical uses cases:

| What you want| How you achieve it |  
|  ------	| ------	|  
| Animation loops endlessly until the player touches the screen. | loop = true; duration = interactive |  
| Animation runs through once and stops then. Wait then until the player touches the screen. | loop = false; duration = interactive |  
| Animation runs through only once and leave then automatically | loop = false; duration = animation |  
| Page is left after a certain period | duration = number (in seconds) |

In case you specify loop = true and duration = animation the looping is ignored and the page is left when the animation is completed for the first time.

When you specify an image (and no animation) but give duration = animation the image is shown until the player touches the screen, i.e. like you had stated duration = interactive.
