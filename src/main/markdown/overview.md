Deutsche Warenwirtschaft Open Source
====================================

Most of the Development and User Documentation starts here. We are trying to put all information into 
the java code as javadoc or referencing html files.

Architecture
------------

In the core the application has a simple layer model. 

1. Library
2. EE
	1. Core
		- Non Persistence Projects
	2. Persistence 
		- Projects, that supply JPA Entities, or other persistence data.
		- Only one data source per project.
	2. Extended 
		- Projects that use more than one persistence source

The same on the client side, but instead of EE we use UI. But there are also some more rules to consider, we were not yet able to represent. 
See [DWOSS-27](http://overload.ahrensburg.gg-net.de/jira/browse/DWOSS-27)

### Default Naming ###

GroupId: eu.ggnet.dwoss

ArtifactId: dwoss-"layer"-"sublayer"-"project" (Hint: The sublayer might not exist)

 - Some layers do not have a sublayer.
 - Incubator projects have the same groupId, but might have a completely different artifact architecture.

Package tree: eu.ggnet.dwoss."project"

 - If a project stretches over multiple layers, the same package tree can be used.
 - The developer has to ensure, that only one ```package-info.java``` exists.
 - The validation of overlapping class names is happening in the assembly projects,
   so it's recommended that the developer watches out for this also.

Package with nature:

 - \*.api | Public API, for now in the *dwoss-ee-core-common* Contains the public API of a multiple projects. May change in the future.
 - \*.entity | JPA entities. Entity classes are named like their intension (e.g. Person, Order, Customer)
 - \*.assist | Assist Classes
	- \*.assist.gen | Generators for Entities.
 - \*.eao | JPA Entity Access Objects 
	- ‹Entity›Eao : Methods Return: Entities or primitive datatypes (e.g. boolean for existence)
          Characteristics: Contains one or more queries in context of the Entity
          Persistence: No change on state of persistence connections
          Note: Exactly one persistence connection is wrapped
	- *.emo | JPA Entity Modification Objects. These classes actually may change existing or create new instances.

Ui Class and Method Names

 - Model-View-Controller Implementations:
	- Full: \*View, \*Model, \*Controller (Special Case, if the Model is another class: Create Empty Model with comment)
	- Embedded (All in one Class): \*ViewCask
 - CRUD Implementations:
	- Operation/View for creating,building,first time usage of something: Create\*
	- Operation/View for modifying, editing, updating of something: Update\*

Implicite Knowledge
-------------------

Things we know, but haven't written down yet.

 - Emo and request Methods
 - Why a lose coupled component must not cause an event out of an event
 - The heavy usage of enums, which looks like a dependency connection of lose coupled components
 - Choice of multiple data sources
 - JDBC Exception on MySQL DBs but not on HSQLDBs
 - toString, xxxFormater and getName

