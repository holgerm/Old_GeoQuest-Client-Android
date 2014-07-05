CSS: github.css


# Model-View Collaboration for NPCTalk #

## Collaboration  ##

| Use Case | Model (NPCTalk) | Direction | View / UI | Comments |
|  ------	| ------	| :------:	| ------	|  ----- |
| Create the view | in onCreate() the activity calls UIFactory.createUI() | --------->  | createContentView() |  Called before anything is shown. Prepares the view and
| Initialize the view | in onCreate() activity calls NPCTalkUI.init()| --------->  | showNextDialogItem() |  UI should show the content of the first dialog item. |

 
## Contracts ##

The following contracts are both 

1. an **obligation** for developers who implement new subtypes of NPCTalkUI, i.e. they must provide automated tests to prove that the contracts are fulfilled
1. a **guarantee** for developers who use any implemented UI style for NPCTalk

### Initialization ###

During initialization starting with the onCreate() method of the activity there is a general protocol between model and UI that is always obeyed:

1. the model initializes all relevant data variables (e.g. for NPCTalk these are dialogItemIterator, nrOfDialogItems, indexOfCurrentDialogItem=0)
2. it calls createUI() on the UI which calls the UIs constructor. 

### NPCTalk Protocol ###

1. create and initialize UI
	1. set button text
	2. set image
	2. display first dialog item text

### What the Model Guarantees ###

| Contract | Tests |  
|  ------	| ------	|  
| The models onCreate calls the UIs constructor | TODO |
| When the model calls the UI constructor in its onCreate() method all relevant model data variables are already initialized and can be accessed. | TODO |  


### What the UI Guarantees ###

| Contract | Tests |  
|  ------	| ------	|  
| createContentView() returns a useable view for setContentView in the activity. E.g. is contains a view with id *outerview*. | TODO |  
| The constructor calls init() to initialize the UI after it has been created. this will often be done as the last statement in the constructor. | TODO |
