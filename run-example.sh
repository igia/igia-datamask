#!/bin/bash
#
# This Source Code Form is subject to the terms of the Mozilla Public License, v.
# 2.0 with a Healthcare Disclaimer.
# A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
# be found under the top level directory, named LICENSE.
# If a copy of the MPL was not distributed with this file, You can obtain one at
# http://mozilla.org/MPL/2.0/.
# If a copy of the Healthcare Disclaimer was not distributed with this file, You
# can obtain one at the project website https://github.com/igia.
#
# Copyright (C) 2018-2019 Persistent Systems, Inc.
#

echo "Igia Data Masker"
echo "  Type=xml"
echo "  Config=src/test/resources/fhir/patient/config.xml"
echo "  Input file=src/test/resources/fhir/patient/patient-response.xml"
echo "  Schema=src/main/test/resources/fhir/schema-xsd/patient.xsd"
echo "  Output=src/test/resources/fhir/apitent/patient-masked.xml"
./igia-datamask.sh \
  --mask=type:xml,config:src/test/resources/fhir/patient/config.xml,in:src/test/resources/fhir/patient/patient-response.xml,schema:src/test/resources/fhir/schema-xsd/patient.xsd,out:src/test/resources/fhir/patient/patient-masked.xml

echo
echo "Here are the changes:"
diff -w src/test/resources/fhir/patient/patient-response.xml src/test/resources/fhir/patient/patient-masked.xml
