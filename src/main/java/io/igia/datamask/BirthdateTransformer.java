/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v.
 * 2.0 with a Healthcare Disclaimer.
 * A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
 * be found under the top level directory, named LICENSE.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * If a copy of the Healthcare Disclaimer was not distributed with this file, You
 * can obtain one at the project website https://github.com/igia.
 *
 * Copyright (C) 2018-2019 Persistent Systems, Inc.
 */
package io.igia.datamask;

import org.joda.time.DateTime;
import org.joda.time.Years;


/**
 * All elements of dates (including year) indicative of age over 89 is considered PHI.
 * If patient age > 89 years, then advance birthdate by enough seconds to make age < 90. 
 * Otherwise, use same offset as base DateOffsetTransformer.
 *
 */
public class BirthdateTransformer extends DateOffsetTransformer {
	
	@Override
	protected DateTime offset(DateTime dateTime, int secondsOffset) {
		int years = getAge(dateTime);
		if(years > 89) {
			int secondsAgeOffset = 60 * 60 * 24 * 365 * (years - 89);
			return dateTime.plusSeconds(secondsAgeOffset);
		}else{
			return dateTime.minusSeconds(secondsOffset);
		}
	}
	
    // Returns age given the date of birth
	private static int getAge(DateTime dateTime) {
		
		Years age = Years.yearsBetween(dateTime, DateTime.now());
		return age.getYears();
    }

}
