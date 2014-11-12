Deutsche Warenwirtschaft Open Source
====================================
(English version below)

Dies ist die Open Source Version der Deutschen Warenwirtschaft der Firma [GG-Net GmbH](http://gg-net.de).
Die Software steht unter der GNU General Public License v3. Copyright [GG-Net GmbH](http://gg-net.de) - Oliver Günther.

Es handelt sich bei dieser Software um eine interne Entwicklung der [GG-Net GmbH](http://gg-net.de) zur Abbildung der
Geschäftsporzesse des IT-Gebrauchtwarenhandels. Die Software befindet sich im täglichen Einsatz
und wird aktiv weiter entwickelt.

Aktuell gibt es noch keine einfach zu nutzenden Beispiel-Version (jar/exe). (ClassLoading issues)

Um die Software mit Beispieldaten im Einsatz zu sehen, ist folgendes notwendig:
 
- JDK 8
- Maven 3.x

Checkout and Build (mvn clean install)

 1. https://github.com/gg-net/statemachine.git (Temporär notwendig, Artifacte werden demnächst in maven central verfügbar sein)
 2. https://github.com/gg-net/lucidcalc.git (Temporär notwendig, Artifacte werden demnächst in maven central verfügbar sein)
 3. https://github.com/gg-net/dwoss.git

In das Verzeichniss dwoss/assembly/client-sample wechseln und folgendes ausführen:

mvn jfx:run -Pserver-sample,client-sample

Nach kurz Zeit öffnet sich ein optionaler Registierungsdialog. Im Anschluss sehen Sie die Deutsche Warenwirtschaft mit
autogennerierten Beispieldaten im Einsatz. Anmeldung ist mit jeglichen Nutzerdaten möglich, der Nutzer "test", jedes Passwort, hat alle Rechte per Standarteinstellung.

English version
===============

This is the open source version of the Deutsche Warenwirtschaft by [GG-Net GmbH](http://gg-net.de).
It is licensed unter the GNU General Public License v3. Copyright [GG-Net GmbH](http://gg-net.de) - Oliver Günther.

The software is an internal project of the [GG-Net GmbH](http://gg-net.de) used to implement all processes of a 'Used IT-Goods Sales Operation'. It is still in use and actively developed.

For the moment there is no sample version ready to use. (some classloading issues)

To get started you'll need:

- JDK 8
- Maven 3.x

Checkout and Build (mvn clean install)

 1. https://github.com/gg-net/statemachine.git (Needed, till artifacts are deployed to maven central)
 2. https://github.com/gg-net/lucidcalc.git (Needed, till artifacts are deployed to maven central)
 3. https://github.com/gg-net/dwoss.git

Switch to dwoss/assembly/client-sample and call:

mvn jfx:run -Pserver-sample,client-sample

An optional registration dialog should open. After completing it, the application is running in a local sample mode.
You can login with any user, but the user "test", any password, has all rights.
