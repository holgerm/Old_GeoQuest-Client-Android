CSS: github.css

# Specifying Layout Parameters #

Layout specifications are given by variables whose names refer to the displayed objects. There are some basic types of displayed objects:

* Display
* Image
* Text
* Button
* TODO (more to come, like list container)

For each displayed object type layout parameters can be stated on different levels:

| Level | Object Types | Meaning | How specified | 
|  ------ |------ |------ |------ |
| Default | Each | For each object type for all quests on all portals | Predefined by QuestMill  Client by the default product definition. | 
| Portal | All | For all kinds of objects in all quests on the portal |  Defined in the portal specific product definition. Should be entered in the portal admin area and delivered in quest file in game node as xml element  "layout". | 
| Portal | Given type, e.g. "Text" | For objects of the given type on the portal | (same as above) | 
| Quest |All | For all kinds of objects in the given quest |  Entered in the quest editor among settings. Delivered in quest file in game node as xml element  "layout".|
| Quest |Given type, e.g. "Image" |  For objects of the given type in the given quest  |(same as above) |
| Page | All | For all objects in the page |  Entered in the quest editor on the page. Delivered in quest file in page node as xml element "layout".|
| Page | Given type, e.g. "Button"  |  For objects of the given type in the page | (same as above)  |
| Object  (TODO not yet possible) | Implicitly given | Only for the given object |   Entered in the quest editor with the object definition. Delivered in quest file in object node as xml element "layout"|  

For actually performing the layout, the parameters are looked up in the reverse order of the table above. I.e. first, it is looked for object-specific values and if not found for page-wide object-type-specific and so on, until if nothing else is found the predefined default value is used.1


# Structure of Layout Variable Names #

The layout variable names are composed of three parts:

1. object reference (e.g. "image" for all images)
2. layout elements (e.g. ".margin.top")
3. layout attributes (e.g. "size")

![Structure of layout variable names](images/layoutOptionNames.jpg)

# Layout Elements #

All displayed objects that occupy a certain area on the screen have some basic layout options in common: they have a *margin*, a *border* and a *padding*. All of these are optional and might be invisible. In this case only the content is shown and there is no distance (not even one pixel) to their neighbors on the screen.

The following image depicts the five basic screen estate parts each displayed object occupies:

* *area* - the whole screen estate the object occupies
* *border* - a visible frame around the visible object
* *margin* - a space on the outer side between the border and the neighbors
* *padding* - a space on the inner side between the border and the visible content of this object
* *content* - the screen space that is actually covered by the visible object

![Area, margin, border, padding, and content](images/marginBorderPadding.jpg)

## Segments of Margin and Padding

To allow for a flexible layout, *margin* and *padding* are divided into eight segments: the four edges and the four corners which can be specified either separately or  as (partly overlapping) edges:

* *.margin.tl (top-left corner only)
* *.margin.tm (top-middle edge only)
* *.margin.tr (top-right corner only)
* *.margin.rm (middle-right edge only)
* *.margin.br (bottom-right corner only)
* *.margin.bm (bottom-middle edge only)
* *.margin.bl (bottom-left corner only)
* *.margin.lm (middle-left edge only)

## Convenience Segments of Margin and Paddingf

![Segments of margin and *padding*](images/margins.jpg)

* *.margin.all (covers all eight segments)
* *.margin.top (covers the three top segments)
* *.margin.right (covers the three right segments)
* *.margin.bottom (covers the three bottom segments)
* *.margin.left (covers the three left segments)

![Convenience segments for margin and padding](images/marginsConvenience.jpg)

## Attributes of Margin and Padding ##

The layout elements margin and padding (and all their segment variants) offer the same attributes:

| Attribute Name | Values | Meaning |  
|  ------	| ------	| ------	|  
| size | int | Width either in pixel or in per mille of screen size |  
| background | 
