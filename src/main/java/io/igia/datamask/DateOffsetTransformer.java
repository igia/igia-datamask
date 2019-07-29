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

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// adds a constant number of days to every value in the column
public class DateOffsetTransformer extends Transformer {

	static final Logger log = Logger.getLogger(DateOffsetTransformer.class);
	
	public enum ParamKey 
	{ 
	    SIMPLE_DATE_FORMAT; 
	} 

	static class DateTimeMatcher {
		String name;
		String formatString;
		SimpleDateFormat format;

		DateTimeMatcher(String name, String formatString) {
			this.name = name;
			this.formatString = formatString;
			this.format = new SimpleDateFormat(this.formatString);
		}
	}

	// order of following list is important, date string may match to multiple patterns, more lenient patterns should be later
	// consider externalizing this configuration so users can add allowable formats for new situations without recompile
	protected static final DateTimeMatcher[] dateTimeMatchList = {

			new DateTimeMatcher("Midnight-Time",           "'00:00:00'"),                    // "00:00:00"
			new DateTimeMatcher("Date-Midnight-Time",      "yyyy-MM-dd'T00:00:00'"),         // "2016-12-22T00:00:00"
			new DateTimeMatcher("Date-Time",               "yyyy-MM-dd'T'HH:mm:ss"),         // "2017-02-01T10:33:06"
			new DateTimeMatcher("Time",                    "HH:mm:ss"),                      // "23:07:02"
			new DateTimeMatcher("Hour-Minute",             "HH:mm"),                         // "23:07"
			new DateTimeMatcher("ISO8601-UTC-Z",           "yyyy-MM-dd'T'HH:mm:ssZ"),        // "2009-08-31T00:00:00Z"
			new DateTimeMatcher("ISO8601-UTC-Precision-Z", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"),    // "2017-02-01T10:33:06.647-07:00"
			new DateTimeMatcher("ISO8601-UTC-Nano",        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"), // "2017-02-01T10:33:06.647123-07:00"
			new DateTimeMatcher("Date",                    "yyyy-MM-dd"),                    // "2017-01-17"
			new DateTimeMatcher("Date-Time-Compress",      "yyyyMMddHHmm"),                  // "201702011633"
			new DateTimeMatcher("Date-Time-Compress2",     "yyyyMMddHHmmss"),                // "20170201163355"
			new DateTimeMatcher("MM/dd/yyyy",              "MM/dd/yyyy"),                    // "01/17/2017"
			new DateTimeMatcher("yyyyMMdd",	  			   "yyyyMMdd"),                      // "20121130"

			// This is added to Support 16 digit date in Visit List and Visit details Service 
			new DateTimeMatcher("Date-Time-Compress3",     "yyyyMMddHHmmssSS"),              // "2017020116335570"

			// This is added to Support MM/dd/yyyy HH:mm date format in LKV Service
			new DateTimeMatcher("MM/dd/yyyy HH:mm",	       "MM/dd/yyyy HH:mm")               // "11/30/2012 10:11"
	};

	private static final int SECONDS_PER_DAY = 60 * 60 * 24;
	protected static final int RANDOM_RANGE_LOW = 10 * SECONDS_PER_DAY;
	protected static final int RANDOM_RANGE_HIGH = 365 * SECONDS_PER_DAY;
	protected static final int RANDOM = new SecureRandom().nextInt(RANDOM_RANGE_HIGH - RANDOM_RANGE_LOW) + RANDOM_RANGE_LOW;
	protected int secondsOffset; // number of seconds to offset

	public DateOffsetTransformer() {
		super();
		this.secondsOffset = RANDOM;
		log.debug("DateOffsetTransformer.secondsOffset="+secondsOffset);
	}

	DateOffsetTransformer(String xpath, String xpathTypeString, String rule, Map<String, String> params) {
		super(xpath, xpathTypeString, rule, params);
		this.secondsOffset = RANDOM;
		log.debug("DateOffsetTransformer.secondsOffset="+secondsOffset);
	}

	@Override
	public String mask(String value) {
		DateTimeMatcher dtm;
		if (value==null || value.length()==0) return "";		
		
		// determine matching format type
		// if provided, use date format param, else check pre-defined format list
		dtm = null;
		String simpleDateFormat = this.getParams().get(ParamKey.SIMPLE_DATE_FORMAT.name());
		if (simpleDateFormat != null && !simpleDateFormat.isEmpty()) {
			if(isValidDate(value, simpleDateFormat)) {
				dtm = new DateTimeMatcher(simpleDateFormat, simpleDateFormat);
				log.warn("Using configured date format '"+simpleDateFormat+"'");
			} else {
				log.warn("WARNING: Invalid configured date format'"+simpleDateFormat+"' for '"+value+"'.");
			}
		}else {		
			for (DateTimeMatcher m : dateTimeMatchList) {
				if(isValidDate(value, m.formatString)) {
					dtm = m;
					log.trace("Matched '"+value+"' as '"+m.formatString+"' ("+m.name+")");
					break;
				}
			}
		}

		if (dtm!=null) {
			// transform based on match, or remove
			log.debug("Matched value '"+value+"' to format '"+dtm.name+"'; deidentifying");
			//changed to joda-time due to formatting issues with SimpleDateFormat
			//cannot use Java 8 date handling due to bug in millisecond pattern match, fixed in Java 9
			//https://stackoverflow.com/questions/22588051/is-java-time-failing-to-parse-fraction-of-second
			DateTimeFormatter dtf = DateTimeFormat.forPattern(dtm.formatString);
			DateTime dateTime = dtf.parseDateTime(value);
			dateTime = offset(dateTime, secondsOffset);
			return dateTime.toString(dtf);
		} else {
			// no match found, so remove the PHI
			log.warn("WARNING: did not find a datetime format match for '"+value+"'; removing.");
			return "";
		}
	}

	protected DateTime offset(DateTime dateTime, int secondsOffset) {
		return dateTime.minusSeconds(secondsOffset);
	}
	
	protected boolean isValidDate(String dateToValidate, String pattern){
	    try {
	        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
	        fmt.parseDateTime(dateToValidate);
	    	log.trace("Trying '"+pattern+"' true");
	    } catch (Exception e) {
	    	log.trace("Trying '"+pattern+"' false");
	        return false;
	    }
	    return true;
	}
}
