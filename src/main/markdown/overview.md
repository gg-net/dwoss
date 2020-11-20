Deutsche Warenwirtschaft Open Source
====================================

Most of the development and user documentation starts here. We are trying to put
all information into the java code as javadoc or referencing html files.

Overview
========

The software is a classic client server application. The server part is based on
the Java EE 8 Specs and uses multiple data sources. The client is a fat Swing and
JavaFx Client. The connection is made through remote ejb invocations.

Project root package and maven groupid: "eu.ggnet.dwoss"
Project: dwoss

Server
------

The server is designed as a deployable war, optimized for the wildfly server. The basis is found
under assembly/server. For the final product database definitions and an implementation of mandator is
needed. 

A sample implementation is found under assembly/server-sample. The war can be deployed to an empty wildfly (V18) and
will generate sample data for an in memory database.

Client
------

The client is designed as a classic fat jar running on a desktop operating system. There has been some work to run the client
via jpro.one but it is in an early stage. It can be found under assembly/client.

The client uses javafx and swing elements bridged through saft. It leverages a CDI backend (SeContainer) and uses RemoteEJB connections
for communication with the server. 

### Swing, JavaFx and Saft

The usage of swing and javafx all together has historical reasons. The application development started in 2008. At that time 
Swing was the best choice for a fat client. JavaFx 2 was released in 2011. Till then, a huge amount of Ui's was already up and 
running. But all the features of javafx and the nature of this project (also to be a playground for some technics) motivated the
integration of javafx components and later the development of saft as a bridging api/framework.

### CDI on the Client

The CDI integration started 2019. This happened with the long term target jpro.one client in mind. One limitation or feature of jpro.one
is, that everything runs in one JVM. This changes the behavior of static values, methods and singletons as they are available through out
all client sessions. CDI with the SeContainer allows the startup of multiple containers in one jvm. And features like @Singleton provide 
the expected functionality of a classic self-designed singleton. 

CDI also has so many cool features (like Events and Interceptors) that simplify the development on the client side.

It has to be noted that the auto discovery of the SeContainer needs to be disabled as most of the server classes are available on 
the client side. The splitting of data objects, remote interfaces and server implementation would be a tremendous effort and is not
considered yet.

### RemoteEjbs

A typical application of today would probably use something like jackson -> json/xml -> jackson for client server communication.
But in the beginning of the project remote ejb connections where chosen and most of the jpa entity models where exposed 
(used in the client). A transformation would be a tremendous effort and is not considered yet. 
Newly developed components try to hide the entity model but all the connections are still done via remote ejbs. 

The only drawbacks so far are:
- A client dependency to the wildfly server
- Some weird serialization/deserialization problems. (e.g. TreeMap)
- Usage of different jdks on client and server side.

Architecture
============

An architecture must be testable (e.g. Archunit/Mavenresolver) even if no test is written, but it is possible and may happen any time.
Everything that is not testable should be defined as guidelines, good style. But it _may_ be violated.
And above all an architecture must make sense. Splitting modules into artifacts without a technical or functional requirement
is not useful and must be avoided.
(e.g. the remote interfaces for EJB clients. If entities are exposed, expose the hole ejb to the client. An extra layer which only contains
remote interfaces and entities but not the implementation of the ejbs, even if it isn't used on the client side, doesn't make sense.
If only a small amount of information should be exposed, design a public api)

Module
------
[Wikipedia: Modul](https://de.wikipedia.org/wiki/Modul_(Software))

Components
----------

A module can consist of the following layered components. The layered order describes the allowed usage. Only for the top to the bottom may
dependencies exist.
- root package: "project-root"."module"."component"
- maven artifactid: "project"-"module"-"component"

A module comes in two forms of assemblies:
- server: deployed on an jakarta ee server, for now only wildfly is tested.
- client: deployed as desktop (swing and javafx) desktop client.

Each assembly may depend on the common subcomponent and has it's own components only used in that form of assembly.

1. **Common components** (may be used by both assemblies)
    1. **api** - public api. This is, what other modules may depend on
        - should only contain: interfaces or serializable value objects. Interfaces may be annotated with @Remote oder @Local
        - must not contain: EJBs, Webbeans, Javafx or Swing (Desktop Ui) code.
        - must not depend on any other module, even apis.
    2. **demand** - revers form of the pulic api. Things that the module wants but not implements.
        - should only contain: interfaces or serializable value objects.
        - must not contain: EJBs, Webbeans, Javafx or Swing (Desktop Ui) code.
        - must not depend on any other module, even apis.
    3. **ee** - enterprise engine, the working code.
        - should only contain: ejbs and supplementary implemented code.
        - should be: tested heaviliy
        - may only define: entity or other persistence like classes or code.
        - may contain @Remote Interfaces for (Desktop) remote client usage
        - may contain Serializable Value objects for (Desktop) remote client usage or web client usage.
        - must not contain: web or desktop ui code.
        - must not depend on other modules expect public apis.
4. **Assembly server**
    1. **web** - web ui, primefaces.
        - should contain: jsf and primefaces xhtml and controller code
        - must not contain: desktop ui code.
        - must not depend on other modules expect public apis.
5. **Assembly client**
    1. **spi** - Ui Service Api, allows the usage of other ui components (See the customer spi for an idea)
        - must not depend on other modules or public apis
        - must not expose the ee component.
        - must only contain: Interfaces and value objects.
    2. **ui** - Ui components
        - should contain javafx or swing desktop client code.
        - must not depend on other modules expect public apis and spis.

### Module dependencies

There are three modes of dependence between modules.
1. _Optional_: A module "A" depends optionally on module "B" if it declares the public api or spi of "B" as a dependency (in the maven pom.xml)
   but does not need an implementation at runtime. (e.g. user can print or mail a document. mailing is only available if an implementation of
   the public mail api is supplied at runtime. If not, the module would disable the mail button. TODO: Linked to an example)
   It is encouraged to write an comment <!-- depends optional --> in the pom.xml.

2. _Required_: A module "A" requires a module "B" if it declares the public api or spi of "B" as a dependency (in the maven pom.xml) and will
   fail at runtime if no implementation is available. (TODO: How to test this via unittesting)

3. _Bound_ (do not use anymore): A module is declared as bound if it violates the rules of component dependencies such as module "A" depends and
    uses classes defined in module "B".ee. This is forbidden in future implementations and is only described as there are still such dependencies in place.
    Also all the mandator implementations violate this restriction.

The Core Components
-------------------

The Core Components have special rules.
1. common - contains constants and interfaces
    - may be required by apis
2. system - contains interceptors, beans and libraries, very thin
    - may be bound by ee
3. widget - contains ui components, global handlers (tba)
    - may be bound by ui

X-Reactor Components
--------------------

All X-Reactor Components are maven pom projects only, which collect module components and dependencies.

GroupId: eu.ggnet.dwoss
ArtifactId: dwoss-xreactor-"group"

Most of the projects are grouped by the their component "layer" (e.g.: dwoss-xreactor-api)

X-Reactor Component Submodules and Projects
-------------------------------------------

RedTape - creation of documents, e.g. dossiers, reports
Mandator - master data of the operating business
Mandator-Sample - sample master data, used for tryout cases

Typical Packages, Classes and Methods with their Nature
-------------------------------------------------------

Here are typical names of packages, classes and method, which imply some nature.
Most of these are not self-invented but seen in other projects and reused.

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
 - *.op | Operations. Business logic that is more complex than a "simple" emo
 - *.cap | ActionFactories, MetaActions and everything that connects to the ui
   Framework via services.

### Local and Remote Service Names ####

The main communication protocol between client and server are the Java EE Remote
Service interfaces. A service, which is intended for remote usage has always
a remote interface which name represents its intention alone.

_e.g._ UnitOverviewer, WorkflowManager, ReportExporter.

The implementation reuses the interface name and extends it with Operation or Bean.
This allows our actual remote discovery through interface usage to work.

Each persistence Project may supply a "Project"Agent. This is the simplest way to get
to entities from the remote side. The Agent implementation should consist mostly of
findXXX methods and supply fetch eager versions.

### Ui Class and Method Names ####

- UI Implementations (MVC, MVP, mixed, missing):
    - \*Model, \*View, \*Controller, \*Presenter
	- Controller and Presenter are interchangeably, just to represent the pattern
	- Model may be missing.
	- \*View may be a description file (fxml)
    - Embedded (All in one Class): \*View or \*ViewCask
	- \*Manager a backing cdi bean or management class which connects some ui element and some background activity
		- e.g. LogginTimeoutManager 
- CRUD Implementations:
    - Operation/View for creating, building, first time usage of something: Create\*
    - Operation/View for modifying, editing, updating of something: Update\*

- DTO Names (mostly in the API packages):
    - Pico*
        - Smallest representation and used for id transfer
    - Simple*
        - Simple representation. Will contain more information than Pico*, but also only an API DTO

Mandator implementation
-----------------------

The mandator implementations right now depend on many components. It is encouraged that these components use only public apis but if this makes it all too
complicated the rule may be broken.

 - Incubator projects have the same groupId but might have a completely
   different artifact architecture.

DwPro Components
----------------

The dw pro components have the same architecture. They differ only in the groupid.

Architectur implemation
=======================

If a new feature is developed the following architecture questions must be answered:

1. Does the feature nature match any existing module nature then put it in that module.
2. If not, can the feature be splitted such as a part of the feature matches the nature of any existing module and the splitting feature is useful by itself.
   Then split and put the splitting feature in the existing module.
3. If not or the non matching part of a splitting, create a new module.
4. In the new module consider for every required api would make the feature sense without the implementation of the required api, than require it optional.
5. Is still something missing, create a demand.

Implicit Knowledge
-------------------

Things we know but haven't written down yet.

 - API hard bound (Kurzgedanke, ein Projekt (auch persistent) darf, andere api zwingend implementieren) .. Das muss ich noch aufschreiben
 - http://stackoverflow.com/questions/40818396/unable-to-build-hibernate-sessionfactory-exception-from-nowhere
 - http://stackoverflow.com/questions/39410183/hibernate-5-2-2-no-persistence-provider-for-entitymanager
 - TransactionAttribute(Requires New) on every generator.
 - Wildfly Remote needs ApplicationRealm User https://www.schoenberg-solutions.de/roller/arndtsBlog/entry/remote-zugriff-wildfly-10-teil4
 - persistence.xml -> <property name="hibernate.id.new_generator_mappings" value="false" /> , since 5.x hibernate uses other default key generator.
   This returns to old behavior





