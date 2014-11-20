Deutsche Warenwirtschaft Open Source
====================================

Most of the Development and User Documentation starts here. We are trying to put all information into
the java code as javadoc or referencing html files.

Overview
--------

The software is a classic client server application. The server part is based on the Java EE 6 Specs and uses multiple
data sources. The client is a fat Swing Client, which is constantly merged to JavaFX. The connection is made through
classic remote interfaces.

The application has two working modes. Either the server part (dwoss-server) is deployed in an EE 6 Server including
the full hibernate persistence layer and the client connects via jndi naming and remote interfaces.
Or both components are run together on the client with a embedded EE 6 Server.
The typical deployment is the [Tomee Server](http://tomee.apache.org).

Architecture
------------

In the core the application has a simple layer model.

1. Library
2. EE (for the Server), UI (for the Client)
	1. Core
		- Non Persistence Projects
	2. Persistence
		- Projects, that supply JPA Entities, or other persistence data.
		- Only one data source per project.
	2. Extended
		- Projects that use more than one persistence source

There are also some more rules to consider, which we were not yet able to represent.
See [DWOSS-27](http://overload.ahrensburg.gg-net.de/jira/browse/DWOSS-27) for more details.

### Default Naming ###

**GroupId**: eu.ggnet.dwoss

**ArtifactId**: dwoss-"layer"-"sublayer"-"project" (Hint: The sublayer might not exist)

 - Some layers do not have a sublayer.
 - Incubator projects have the same groupId, but might have a completely different artifact architecture.

**Package tree**: eu.ggnet.dwoss."project"

 - Represent each main project. To get more information start looking into these first.
 - If a project stretches over multiple layers, the same package tree can be used.
 - The developer has to ensure, that only one ```package-info.java``` exists.
 - The validation of overlapping class names is happening in the assembly projects,
   so it's recommended that the developer watches out for this also.

#### Typical Packages, Classes and Methods with their Nature ####

Here are typical names of packages, classes and method, which imply some nature. Most of these are not self invented
but seen in other projects and reused.

 - \*.api | Public API, for now *dwoss-ee-core-common* contains the public API of a multiple projects. May change in
   the future.
 - \*.entity | Persistent Entities. Entity classes are named like their intention (e.g. Person, Order, Customer)
 - \*.assist | Assist Classes
	- \*.assist.gen | Generators for Entities.
 - \*.eao | Entity Access Objects. Classes here will never modify the entity or the persistence unit.
	- ‹Entity›Eao | Each Eao is the accessor for one primary entity, hence the name. Contains one or more queries
	  in context of this entity.
		- find\* | Returns one or more entities. Most of these methods have version with (int start, int amount)
		  for paging requests. Methods will never fail. Single finds return one instance or null. Multi finds will
		  return a collection.
		- count\* | Returns amount of entities
 - *.emo | Entity Modification Objects. These classes might change existing entity instances or create new ones.
	- ‹Entity›Emo | Each Emo is a simple to complex modifier of one primary entity. Contains one or more queries
	  and partial business logic in context of one entity.
		- request\* | Either finds a requested instance or if non exists, creates one.

#### Local and Remote Service Names ####

The main communication protocol between client and server are the Java EE Remote Service interfaces. A service, which is
intended for remote usage has always a remote interface which name represents its intention alone.

_e.g._ UnitOverviewer, WorkflowManager, ReportExporter.

The implementation reuses the interface name and extends it with Operation or Bean. This allows our actual remote discovery
through interface usage to work. See {@link eu.ggnet.saft.core.Client} for more details.


#### Ui Class and Method Names ####

 - Model-View-Controller Implementations:
	- Full: \*View, \*Model, \*Controller (Special Case, if the Model is another class: Create Empty Model with comment)
	- Embedded (All in one Class): \*ViewCask
 - CRUD Implementations:
	- Operation/View for creating,building,first time usage of something: Create\*
	- Operation/View for modifying, editing, updating of something: Update\*

Implicite Knowledge
-------------------

Things we know, but haven't written down yet.

 - Why a lose coupled component must not cause an event out of an event
 - The heavy usage of enums, which looks like a dependency connection of lose coupled components
 - Choice of multiple data sources
 - JDBC Exception on MySQL DBs but not on HSQLDBs
 - toString, xxxFormater and getName
 - Sample Client Zip with launch4j and win32 jre8

