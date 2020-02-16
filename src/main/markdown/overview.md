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

Assemblies
----------

There are two projects in der assembly folder, client and server. Both projects are templates to build your own client and server.

The _client_ works with every server, just needs url and authentication.

The _server_ needs to be extended with an implementation of the mandator features. There is a sample-server profile in der server project.
This profile adds all components of a sample mandator and some entity generation. Look into this for more information.

Architecture
============

An architecture must be testable (e.g. Archunit/Mavenresolver). Even if no test is written, but it is possible and may happen any time.
Everything, that is not testable, should be defined as guidelines, good style. But it _may_ be violated.
And above all an architecture must make sense. Splitting modules int artifacts without a technical or functional requirement
is not usefull and must be avoided.
(e.g. the remote interfaces for EJB clients. If entites are exposed, expose the hole ejb to the client. An extra layer which only contains
remote interfaces and entites, but not the implemation of the ejbs, even if it isn't used on the client side, doesn't make sense.
If only a small amount of information should be exposed, design a public api)

Module
------
[Wikipedia: Modul](https://de.wikipedia.org/wiki/Modul_(Software))

Components
----------

A module can consist of the following layered components. The layered order describes the allowed usage. Only for the top to the bottom, may
dependencies exist.
- root package: "project-root"."module"."component"
- maven artifactid: "project"-"module"-"component"

A module comes in two forms of assemblys:
- server: deployed on an jakarta ee server, for now only wildfly is tested.
- client: deployed as desktop (swing and javafx) desktop client.

Each assambly may dependen on the common subcomponent and has it's own components only used in that form of assembly.

1. **Common components** (may be used by both assemblies)
    1. **api** - public api. This is, what other modules may depend on
        - should only contain: interfaces or serializable value objects. Interfaces may be annotated with @Remote oder @Local
        - must not contail: EJBs, Webbeans, Javafx or Swing (Desktop Ui) code.
        - must not depend on any other module, even apis.
    2. **demand** - revers form of the pulic api. Things that the module wants but not implements.
        - should only contain: interfaces or serializable value objects.
        - must not contail: EJBs, Webbeans, Javafx or Swing (Desktop Ui) code.
        - must not depend on any other module, even apis.
    3. **ee** - enterprise engine, the working code.
        - should only contain: ejbs and supplementary implemented code.
        - should be: tested heaviliy
        - may only define: entity or other persistence like classes or code.
        - may contain @Remote Interfaces for (Desktop) remote client usage
        - may contain Serialzable Value objects for (Desktop) remote client usage or web client usage.
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
        - must only contail: Interfaces and value objects.
    2. **ui** - Ui componenten
        - should contail javafx or swing desktop client code.
        - must not depend on other modules expect public apis and spis.

### Module dependencies

There are three modes of dependence between modules.
1. _Optional_: A module "A" depends optionally on module "B" if it declares the public api or spi of "B" as a dependency (in the maven pom.xml)
   but does not need an implementation at runtime. (e.g. user can print or mail a document. mailing is only available if an implemation of
   the public mail api is supplied at runtime. If not, the module would disable the mail button. TODO: Linkt to an example)
   It is encouraged to write an comment <!-- depends optional --> in the pom.xml.

2. _Required_: A module "A" requires a module "B" if it declares the public api or spi of "B" as a dependency (in the maven pom.xml) and will
   fail at runtime if no implementation is available. (TODO: How to test this via unittesting)

3. _Bound_ (do not use anymore): A module "A" binds a module "B" if it violates the rules of component dependencies such as "A" depends and
    uses classes defined in "B".ee. This is forbided in future implementations and is only discribed as there are still such dependencies in place.
    Also all the mandator implementations violate this restriction.

The Core Components
-------------------

The Core Components have special rules.
1. common - contains constants and interfaces
    - may be requiered by apis
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

Typical Packages, Classes and Methods with their Nature
-------------------------------------------------------

Here are typical names of packages, classes and method, which imply some nature.
Most of these are not self invented but seen in other projects and reused.

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

### Local and Remote Service Names ####

The main communication protocol between client and server are the Java EE Remote
Service interfaces. A service, which is intended for remote usage has always
a remote interface which name represents its intention alone.

_e.g._ UnitOverviewer, WorkflowManager, ReportExporter.

The implementation reuses the interface name and extends it with Operation or Bean.
This allows our actual remote discovery through interface usage to work.

Each persistence Project may supply a "Project"Agent. This is the simplest way to get
to entites from the remote side. The Agent implementation should consit mostly of
findXXX methodes and supply fetch eager versions.

### Ui Class and Method Names ####

- UI Implementations (MVC, MVP, mixed, missing):
    - \*Model, \*View, \*Controller, \*Presenter
	- Controller and Presenter are syndonymes, just to represent the pattern
	- Model may be missing.
	- View may be a description file (fxml)
    - Embedded (All in one Class): \*View or \*ViewCask
- CRUD Implementations:
    - Operation/View for creating,building,first time usage of something: Create\*
    - Operation/View for modifying, editing, updating of something: Update\*

- DTO Names (mostly in the API packages):
    - Pico*
        - Smallest representation and used for id transfer
    - Simple*
        - Simple representation. Will contation more information that Pico*, but also only an API DTO

Mandator implementation
-----------------------

The mandator implementations right now depend on many components. It is encouraged, that these components use only pulic apis but if this makes it all too
complecated, the rule may be broken.

 - Incubator projects have the same groupId, but might have a completely
   different artifact architecture.

DwPro Components
----------------

The dw pro components have the same architecture. They differ only in the groupid.

Architectur implemation
=======================

If a new feature is developed the following architecture questions must be answerd:

1. Does the feature nature match any existing module nature, than put it in that module.
2. If not, can the feature be splitt such as a part of the feature matches the nature of any existing module and the splitt feature is useful by itself.
   Then splitt and put the splitt feature in the existing module.
3. If not or the non matching part of a splitt, create a new module.
4. In the new module consider for every required api would make the feature sense without the implemation of the required api, than require it optional.
5. Is still something missing, create an demand.

Implicite Knowledge
-------------------

Things we know, but haven't written down yet.

 - http://stackoverflow.com/questions/40818396/unable-to-build-hibernate-sessionfactory-exception-from-nowhere
 - http://stackoverflow.com/questions/39410183/hibernate-5-2-2-no-persistence-provider-for-entitymanager
 - TransactionAttribute(Requires New) on every generator.
 - Wildfly Remote needs ApplicationRealm User https://www.schoenberg-solutions.de/roller/arndtsBlog/entry/remote-zugriff-wildfly-10-teil4
 - persistence.xml -> <property name="hibernate.id.new_generator_mappings" value="false" /> , since 5.x hibernate uses other default key generator.
   This returns to old behavior
