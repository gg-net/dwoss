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

Der einfachste Weg um einen ersten Eindruck zu bekommen, ist die Sample Implementation. Diese arbeitet mit in Memory Datenbanken und generierten Beispieldaten

Benötigt wird:
- JDK 8_u25 oder neuer (Zen Java Plugin benötigt sehr neue Version)
- Maven 3.x

Checkout and Build:

 1. https://github.com/gg-net/dwoss.git
 2. Im Hauptverzeichniss: mvn clean install -Pdevelopment
 3. Unter assembly/server: mvn clean install tomee:run -Pserver-sample,tomee-1.7.0
 4. Unter assembly/client-remote: mvn clean install jfx:run -Pclient-sample

User admin/admin hat alle Rechte, User user/user hat minimale Rechte.

Projekt Resourcen
-----------------

Die meisten Information werden in der Java Dokumentation verarbeitet.
**[Project und Java Dokumentation](http://deutschewarenwirtschaft.de/site/apidocs/index.html)**

Weitere relevante Systeme:

- Wiki: [GG-Net Confluence](https://confluence.cybertron.global/display/DWOSS)
- Blog: [GG-Net Blog](https://confluence.cybertron.global/display/DWOSS/Deutsche+Warenwirtschaft+Blog)
- Issue Tracker: [GG-Net Jira](https://jira.cybertron.global)
- Build Server: [GG-Net Bamboo](https://bamboo.cybertron.global)

English version
===============

This is the open source version of the Deutsche Warenwirtschaft by [GG-Net GmbH](http://gg-net.de).
It is licensed unter the GNU General Public License v3. Copyright [GG-Net GmbH](http://gg-net.de) - Oliver Günther.

The software is an internal project of the [GG-Net GmbH](http://gg-net.de) used to implement all processes of a 'Used IT-Goods Sales Operation'. It is still in use and actively developed.

Project Resources
-----------------

Most of the Project information are merged into the javadoc.
**[Project und Java Documentation](http://deutschewarenwirtschaft.de/site/apidocs/index.html)**

Also sources of information:

- Wiki: [GG-Net Confluence](https://confluence.cybertron.global/display/DWOSS)
- Blog: [GG-Net Blog](https://confluence.cybertron.global/display/DWOSS/Deutsche+Warenwirtschaft+Blog)
- Issue Tracker: [GG-Net Jira](https://jira.cybertron.global)
- Build Server: [GG-Net Bamboo](https://bamboo.cybertron.global)
