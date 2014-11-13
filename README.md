Deutsche Warenwirtschaft Open Source
====================================
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

Aktuell gibt es noch keine einfach zu nutzende Beispiel-Version (jar/exe). (ClassLoading issues)

Um die Software mit Beispieldaten im Einsatz zu sehen, ist folgendes notwendig:
 
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
Nach kurz Zeit öffnet sich ein optionaler Registierungsdialog. Im Anschluss sehen Sie die Deutsche Warenwirtschaft mit
autogennerierten Beispieldaten im Einsatz. Anmeldung ist mit jeglichen Nutzerdaten möglich, der Nutzer "test", jedes Passwort, hat alle Rechte per Standarteinstellung.

Projekt Resourcen
-----------------

Wiki: [GG-Net Confluence](http://overload.ahrensburg.gg-net.de/confluence/display/DWOSS)
Blog: [GG-Net Blog](http://overload.ahrensburg.gg-net.de/confluence/display/DWOSS/Deutsche+Warenwirtschaft+Blog)
Issue Tracker: [GG-Net Jira](http://overload.ahrensburg.gg-net.de/jira)
Build Server: [GG-Net Bamboo](http://devcon.ahrensburg.gg-net.de/bamboo)

English version
===============

This is the open source version of the Deutsche Warenwirtschaft by [GG-Net GmbH](http://gg-net.de).
It is licensed unter the GNU General Public License v3. Copyright [GG-Net GmbH](http://gg-net.de) - Oliver Günther.

The software is an internal project of the [GG-Net GmbH](http://gg-net.de) used to implement all processes of a 'Used IT-Goods Sales Operation'. It is still in use and actively developed.

For the moment there is no sample version ready to use. (some classloading issues)

To get started you'll need:

- JDK 8_u25 ((Some lower versions and the zen-java plugin won't work))
- Maven 3.x

Checkout and Build (mvn clean install)

 1. https://github.com/gg-net/statemachine.git (Needed, till artifacts are deployed to maven central)
 2. https://github.com/gg-net/lucidcalc.git (Needed, till artifacts are deployed to maven central)
 3. https://github.com/gg-net/dwoss.git (To Speed things up: mvn clean install -Pdevelopment)

Switch to dwoss/assembly/client-sample and call:

mvn jfx:run -Pserver-sample,client-sample

An optional registration dialog should open. After completing it, the application is running in a local sample mode.
You can login with any user, but the user "test", any password, has all rights.
