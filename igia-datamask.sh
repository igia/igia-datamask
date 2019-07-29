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

mask_string="$*"
IGIA_DATAMASK_ROOT=`dirname $0`
DIR=`pwd`
cd $IGIA_DATAMASK_ROOT
java -jar ${IGIA_DATAMASK_ROOT}/target/igia-datamask.jar ${mask_string}
cd $DIR
