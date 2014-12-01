# StartAndExitScreen #

This mission will be renamed to "FullScreenImage".

| Name | Meaning | Values | Default Value |
|:--|:--|:--|:--
| **Necessary Attributes** ||||
| id | Identification String for this mission | String (unique within this quest) | --- |
| image | relative path to image (png, jpg or gif) or animation file (zip) | String Path | --- |
| **Optional Attributes** ||||
| duration | how long image is shown (in milliseconds) | Number | 5000 ms|
|  | wait until user touches screen | "interactive" | |
|  | wait until animation has completed (only with animations that are not looping) | "animation" | |
| loop | is the animation looping? (only used with animation) | Boolean | true|

