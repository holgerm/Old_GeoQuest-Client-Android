CSS: github.css

**Items are not yet implemented! This documentation is currently only a draft specification of the upcoming feature.**

# Life Cycle of Items #

Typically an items gets created at game start and is placed in some location. Typical locations include the player's standard inventory and all game hotspots. Items that are located at some hotspot can be picked up by the user when he is within the hotspot area. Items that the player carries can be dropped at hotspots. Eventually an item is removed completely, e.g. by consuming it somehow or destroying it.

## Declare an Item Type ##

There is an implicitly declared standard item type ("StandardItem") with standard behavior.

You can optionally declare your own item types. You can declare standard behavior for all items of that type directly within its type declaration. These declarations have to be done statically. 



## Creating Items Statically at Game Startup ##

Use the CreateItem action to create new items.

## Picking Up an Item ##

For all interaction with items we mostly use the item inspector, a tool that can be either started automatically through an event or manually by the user. The item inspector offers access to all currently accessible items. This includes all items that are in the standard user inventory as well as all items that are located at an active hotspot in whose range the user currently is. 

### Default Behavoir ###

Default behavior includes that you get offered to pick up all items that lie at any hotspot that you enter. I.e. by default the hotspot's onEnter event triggers an onAccess event on the item. This in turn by fault executes an action that offers you to pick the item, wich moves it from the hotspot to your standard inventory.

Later on you will be able to explicitly define similar and also different behavior.


## Dropping an Item Down ##

### Default Behavior ###

There is also a default behavior for dropping an item down. By default you can drop any item which is in your inventory when you are at a hotspot. This means that we have a notion of your personal location in terms of being in range of certain hotspots (which might be several hotspots at the same time). 

In this situation you will be presented a dialog which lets you drop each item you have in one of the hotspots you currently are at.



## Using Items Together ##

A very powerful way of using items is to declare behavior which is based on the combination of multiple items.

