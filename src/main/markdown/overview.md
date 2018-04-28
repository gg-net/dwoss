Deutsche Warenwirtschaft Open Source
====================================

Most of the development and user documentation starts here. We are trying to put
all information into the java code as javadoc or referencing html files.

Overview
--------

The software is a classic client server application. The server part is based on
the Java EE 7 Specs and uses multiple data sources. The client is a fat Swing and
JavaFx Client. The connection is made through remote ejb invocations.

Architecture
------------

In the core the application has a simple layer model.

1. Library
2. API 
    - Only plain java projects. No Enitys or EJBs. Supplies Interfaces,Annotations and Values 
      for data exchange between projects.
    - Projects may depend on other API projects. Make sure no cycles happen.
3. EE (for the Server), UI (for the Client) -> layer
	1. Core -> sublayer
		- Non Persistence Projects, APIs.
                - This layer is a little bit grubby. Some project depend on other projects in 
                  this layer. This may be cleaned up in the future.
	2. Persistence
		- Projects, that supply JPA Entities, or other persistence data.
		- Only one data source per project.
	2. Extended
		- Projects that use more than one persistence source
4. No module name may stretch over a layer.

There are also some more rules to consider, which we were not yet able to represent.
See the developed Graph in
 [DWOSS-27](https://jira.cybertron.global/browse/DWOSS-27) for more details.

### Default Naming ###

**GroupId**: eu.ggnet.dwoss

**ArtifactId**: dwoss-"layer"-"sublayer"-"project"

 - Some layers do not have a sublayer.
 - Incubator projects have the same groupId, but might have a completely
   different artifact architecture.

**Package tree**: eu.ggnet.dwoss."project".("layer")

 - If a project stretches over multiple layers, the layer must be in the package tree.
    - Most project violate this rule right now [DWOSS-133](https://jira.cybertron.global/browse/DWOSS-137]). But everything new must honor it at least.
 - The validation of overlapping class names is happening in the assembly projects,
   so it's recommended that the developer watches out for this.

Examples:

 - progress (only in one layer)
    - dwoss-ee-core-progress : eu.ggnet.dwoss.progress.(ee)
 - redtape (everythere)
    - dwoss-ee-persistence-redtape : eu.ggnet.dwoss.redtape.ee
    - dwoss-ee-extended-redtape    : eu.ggnet.dwoss.redtapext.ee
    - dwoss-web-extended-redtape    : eu.ggnet.dwoss.redtapext.web
    - dwoss-web-persistence-redtape    : eu.ggnet.dwoss.redtape.web
    - dwoss-ui-persistence-redtape : eu.ggnet.dwoss.redtape.ui
    - dwoss-ui-extended-redtape    : eu.ggnet.dwoss.redtapext.ui

#### Typical Packages, Classes and Methods with their Nature ####

Here are typical names of packages, classes and method, which imply some nature.
Most of these are not self invented but seen in other projects and reused.

 - \*.api | Public API, for now *dwoss-ee-core-common* contains the public API
   of a multiple projects. May change in the future.
 - \*.entity | Persistent Entities. Entity classes are named like their intention
   (e.g. Person, Order, Customer)
 - \*.assist | Assist Classes
	- \*.assist.gen | Generators for Entities.
 - \*.eao | Entity Access Objects. Classes here will never modify the entity or
   the persistence unit.
	- ‹Entity›Eao | Each Eao is the accessor for one primary entity, hence
          the name. Contains one or more queries in context of this entity.
		- find\* | Returns one or more entities. Most of these methods
                  have version with (int start, int amount) for paging requests.
                  Methods will never fail. Single finds return one instance or null.
                  Multi finds will return a collection.
		- count\* | Returns amount of entities
 - *.emo | Entity Modification Objects. These classes might change existing
   entity instances or create new ones.
	- ‹Entity›Emo | Each Emo is a simple to complex modifier of one primary
          entity. Contains one or more queries and partial business logic in
          context of one entity.
		- request\* | Either finds a requested instance or if non exists,
                  creates one.
 - *.op | Operations. Businesslogic that ist more that a "simple" emo
 - *.cap | ActionFactories, MetaActions and eveything that connects to the ui
   Framework via services.

#### Local and Remote Service Names ####

The main communication protocol between client and server are the Java EE Remote
Service interfaces. A service, which is intended for remote usage has always
a remote interface which name represents its intention alone.

_e.g._ UnitOverviewer, WorkflowManager, ReportExporter.

The implementation reuses the interface name and extends it with Operation or Bean.
This allows our actual remote discovery through interface usage to work.
See {@link eu.ggnet.saft.core.Client} for details on how these are discovered on the
client side.

Each persistence Project may supply a "Project"Agent. This is the simplest way to get
to entites from the remote side. The Agent implementation should consit mostly of
findXXX methodes and supply fetch eager versions.

#### Ui Class and Method Names ####

- UI Implementations (MVC, MVP, mixed, missing):
	- \*Model, \*View, \*Controller, \*Presenter
		- Controller and Presenter are syndonymes, just to represent the pattern
		- Model may be missing.
		- View may be a description file (fxml)
	- Embedded (All in one Class): \*View or \*ViewCask
- CRUD Implementations:
	- Operation/View for creating,building,first time usage of something: Create\*
	- Operation/View for modifying, editing, updating of something: Update\*

Implicite Knowledge
-------------------

Things we know, but haven't written down yet.

 - Why a lose coupled component must not cause an event out of an event
 - The heavy usage of enums, which looks like a dependency connection of lose
   coupled components
 - Choice of multiple data sources
 - JDBC Exception on MySQL DBs but not on HSQLDBs
 - toString, xxxFormater and getName
 - Sample Client Zip with launch4j and win32 jre8
 - New Persistence Concept (one persistence.xml for every project and final server-app)
 - Fadeout of Local and Sample Client
 - Transition to tomee 7 and wildfly
 - http://stackoverflow.com/questions/40818396/unable-to-build-hibernate-sessionfactory-exception-from-nowhere
 - http://stackoverflow.com/questions/39410183/hibernate-5-2-2-no-persistence-provider-for-entitymanager
 - In Hiberante 5.2 the hibernate-entitymanager is obsolete. everything is in core
 - TransactionAttribute(Requires New) on every generator.
 - Wildfly Remote needs ApplicationRealm User https://www.schoenberg-solutions.de/roller/arndtsBlog/entry/remote-zugriff-wildfly-10-teil4
 - persistenc.xml -> <property name="hibernate.id.new_generator_mappings" value="false" /> , since 5.x hibernate uses other default key generator.
   This returns to old behavior
