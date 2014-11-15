Deutsche Warenwirtschaft Open Source
====================================

This is the new documentation made with markdown.

Architecture
------------

_!We are still in the process of importing all architecture information!_

Default Naming:
- GroupId: eu.ggnet.dwoss
- ArtifactId: dwoss-"layer"-"sublayer"-"project" (Hint: The sublayer might not exist)
 - Some layers do not have a sublayer.
 - Incubator projects have the same groupId, but might have a completely different artifact architecture.
- package tree: eu.ggnet.dwoss."project"
 - If a project stretches over multiple layers, the same package tree can be used.
 - The developer has to ensure, that only one ```package-info.java``` exists.
 - The validation of overlapping class names is happening in the assembly projects, so it's recommended that the developer watches out for this also.

