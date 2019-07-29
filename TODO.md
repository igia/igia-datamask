* It would be good to have masking transforms for
  - Images and other embedded files, replacing with some default artifact of the same mime type, optionally encoded
  - Addresses (Line1, Line2, City, State, Zipcode, etc.)

* Currently masked identifiers are not reversible, and not repeatable across runs for a given identifier.  Might be useful to optionally persist and re-load the cache in a file for repeatable/reversible data masking across runs.

* Sometimes text or identifiers have specific case formats in source files, so might be helpful to either detect and maintain these, or specify desired format in the config.  For example "JOHN DOE", should become "JULIUS CEASAR" not "Julius Ceasar".

* Would be great to have configurations to validate and de-identify standard FHIR responses as part of the platform release.
