Deutsche Warenwirtschaft Open Source
====================================

_Bei Fragen, nicht lange google, direkt an uns wenden. If there are questions, don't google too long, just ask us._
- Oliver Günther: **oliver.guenther@gg-net.de**

(English version below)

Dies ist die Open Source Version der Deutschen Warenwirtschaft der Firma [GG-Net GmbH](http://gg-net.de).
Die Software steht unter der GNU General Public License v3. Copyright [GG-Net GmbH](http://gg-net.de) - Oliver Günther.

Einleitung
----------

Es handelt sich bei dieser Software um eine interne Entwicklung der [GG-Net GmbH](http://gg-net.de) zur Abbildung der
Geschäftsprozesse des IT-Gebrauchtwarenhandels. Die Software befindet sich im täglichen Einsatz und wird aktiv weiter entwickelt. Dabei hat Sie mehrere Schwerpunkte:

**Geschäftsprozess IT-Gebrauchtwarenhandel**

In erster Hinsicht wird diese Software dazu verwendet den [Acer Sonderposten](http://acersonderposten.de) und dessen Prozesse abzubilden und dadurch zu unterstützen. Aus diesem Geschäftszweig kommen auch die meisten Anforderungen zur Weiterentwicklung. Ein weiterer Einsatz findet bei [oneado](https://oneado.de) statt.

**Entwicklung Auszubildende**

Die GG-Net GmbH bildet natürlich auch, wie viele gut Softwarehäuser in Deutschland, Entwickler selber aus. Um diese an die Arbeit in Teams und größeren Projekten zu gewöhnen, steigen Sie als erstes in diese Projekt mit ein. Über Prüfung, Entwicklung und Wartung von Tests sowie Verbesserung von Dokumentation, werden Sie nach und nach an produktive Komponenten herangeführt.

**Entwicklung Junior Developer**

Junior Developer werden an Hand dieser Platform weiterentwickelt. Sie können hier Architekturschwächen analysieren, Changeprozesse durchführen und sich mit mehr als nur Feature Implementation auseinander setzten. Auch interne Prozesse von anderen Abteilungen untersuchen Sie und evaluieren,planen und integrieren Diese.

**Experimentierplattform Senior Developer und Architekten**

Senior Developer verwenden die Software um neue Bibliotheken und Frameworks an mehr als nur Beispielen zu untersuchen und zu bewerten. Die Größe des Projektes sowie die Modularität lässt oft und schnell Lücken in interessanten Bibliotheken offenbar werden.

**Trainingsplatform DevOps**

Um einer solchen Software und den Entwicklern dahinter einen hohes Maß an Experimentierfreudigkeit zu erlauben ist eine optimale Abstimmung der Teams praktisch unabdingbar. Dadurch ist DevOps bei GG-Net stark etabliert und greift bis zum "internen" Kunden (Verkauf,Marketing,Finanz und Logistik). Multiple Deployments am Tag sind nichts ungewöhnliches und erlauben schnelle Bugfixes. Neben der üblichen kontinuierlichen Verbesserung der Prozesse findet auch hier die Ausbildung und Unterweisung neuer Techniker sowie deren Experimente mit neuen Ansätzen statt.

Getting Started
---------------

Der einfachste Weg um einen ersten Eindruck zu bekommen, ist den Beispiel-Client zu verweden. Dieser benötigt keine Installation und arbeitet Lokal mit generierten Beispieldaten.

Unter Windows:
- Downloaden und Enpacken Sie den [Beispiel-Client](http://devcon.ahrensburg.gg-net.de/bamboo/artifact/DWOSS-MASTER/shared/build-latest/DW-OSS-Zip/sample-client-bin.zip)
- Starten sie die sample-client.exe (Das JRE ist enthalten)

(Sollte der Download nich funktionieren, versuchen Sie es später noch einmal. Der Client wir automatisch aktualisiert)

Unter Linux und MacOS müssen Sie noch folgende Schritte zusätzlich durchführen:
- Installieren Sie [Java 8](http://java.com).
- Im Fenster: Doppelclick auf die sample-client.jar
- In der Konsole: java -jar sample-client.jar

Nach kurzer Zeit öffnet sich ein optionaler Registierungsdialog. Im Anschluss sehen Sie die Deutsche Warenwirtschaft mit autogennerierten Beispieldaten im Einsatz. Anmeldung ist mit jeglichen Nutzerdaten möglich, der Nutzer "test", jedes Passwort, hat alle Rechte per Standarteinstellung.

Um den Beispiel-Client selber zu bauen, ist folgendes notwendig:
 
- JDK 8_u25 (Zen Java Plugin benötigt sehr neue Version)
- Maven 3.x

Checkout and Build (mvn clean install)

 1. https://github.com/gg-net/statemachine.git (Temporär notwendig, Artifacte werden demnächst in maven central verfügbar sein)
 2. https://github.com/gg-net/lucidcalc.git (Temporär notwendig, Artifacte werden demnächst in maven central verfügbar sein)
 3. https://github.com/gg-net/dwoss.git (Für schnelleren build: mvn clean install -Pdevelopment)

In das Verzeichniss dwoss/assembly/client-sample wechseln und folgendes ausführen:
```
mvn jfx:run -Pserver-sample,client-sample
```
Projekt Resourcen
-----------------

Die meisten Information werden in der Java Dokumentation verarbeitet.
**[Project und Java Dokumentation](http://deutschewarenwirtschaft.de/site/apidocs/index.html)**

Weitere relevante Systeme:

- Wiki: [GG-Net Confluence](http://overload.ahrensburg.gg-net.de/confluence/display/DWOSS) 
- Blog: [GG-Net Blog](http://overload.ahrensburg.gg-net.de/confluence/display/DWOSS/Deutsche+Warenwirtschaft+Blog)
- Issue Tracker: [GG-Net Jira](http://overload.ahrensburg.gg-net.de/jira)
- Build Server: [GG-Net Bamboo](http://devcon.ahrensburg.gg-net.de/bamboo)

English version
===============

This is the open source version of the Deutsche Warenwirtschaft by [GG-Net GmbH](http://gg-net.de).
It is licensed unter the GNU General Public License v3. Copyright [GG-Net GmbH](http://gg-net.de) - Oliver Günther.

The software is an internal project of the [GG-Net GmbH](http://gg-net.de) used to implement all processes of a 'Used IT-Goods Sales Operation'. It is still in use and actively developed.

To get a first view on the application, use the sample client. This client starts a local server in the background and generates sample data. Please be patient, the startup takes some seconds.

MS Windows:
- Download and Extract [Sample-Client](http://devcon.ahrensburg.gg-net.de/bamboo/artifact/DWOSS-MASTER/shared/build-latest/DW-OSS-Zip/sample-client-bin.zip)
- Start the sample-client.exe (The JRE is enclosed)

(If the download is not available, please try again later. The client is rebuild as part of our automaticbamboo build 
infrastructure)

For Linux and MacOS, you need to do:
- Install [Java 8](http://java.com).
- In a window: Coubleclick on sample-client.jar
- On the console: java -jar sample-client.jar

An optional registration dialog should open. After completing it, the application is running in a local sample mode.
You can login with any user, but the user "test", any password, has all rights.

To get started you'll need:

- JDK 8_u25 ((Some lower versions and the zen-java plugin won't work))
- Maven 3.x

Checkout and Build (mvn clean install)

 1. https://github.com/gg-net/statemachine.git (Needed, till artifacts are deployed to maven central)
 2. https://github.com/gg-net/lucidcalc.git (Needed, till artifacts are deployed to maven central)
 3. https://github.com/gg-net/dwoss.git (To Speed things up: mvn clean install -Pdevelopment)

Switch to dwoss/assembly/client-sample and call:

mvn jfx:run -Pserver-sample,client-sample

Project Resources
-----------------

Most of the Project information are merged into the javadoc.
**[Project und Java Documentation](http://deutschewarenwirtschaft.de/site/apidocs/index.html)**

Also sources of information:

- Wiki: [GG-Net Confluence](http://overload.ahrensburg.gg-net.de/confluence/display/DWOSS) 
- Blog: [GG-Net Blog](http://overload.ahrensburg.gg-net.de/confluence/display/DWOSS/Deutsche+Warenwirtschaft+Blog)
- Issue Tracker: [GG-Net Jira](http://overload.ahrensburg.gg-net.de/jira)
- Build Server: [GG-Net Bamboo](http://devcon.ahrensburg.gg-net.de/bamboo)
