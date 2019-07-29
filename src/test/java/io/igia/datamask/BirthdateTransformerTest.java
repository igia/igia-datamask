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

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import io.igia.datamask.BirthdateTransformer;
import io.igia.datamask.Transformer;

public class BirthdateTransformerTest {
	
	private static final int SECONDS_PER_DAY = 60 * 60 * 24;
	protected static final int RANDOM_RANGE_LOW = 10 * SECONDS_PER_DAY;
	protected static final int RANDOM_RANGE_HIGH = 365 * SECONDS_PER_DAY;
	
	// Confirm age-related protections are included (age over 89)
	@Test
	public void testMask_Over89_MMddyyyyHHmm() {
		String text = "11/30/1920 10:11";
		String format = "MM/dd/yyyy HH:mm";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));

	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
		
		int originalAge = Years.yearsBetween(getDateTime(text, format), DateTime.now()).getYears();
		int newAge = Years.yearsBetween(getDateTime(maskedValue, format), DateTime.now()).getYears();

	    assertTrue(originalAge > 89);
	    assertTrue(newAge <= 89);
	}

	@Test
	public void testMask_Over89_yyyyMMdd() {
		String text = "19010118";
		String format = "yyyyMMdd";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));

	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
		
		int originalAge = Years.yearsBetween(getDateTime(text, format), DateTime.now()).getYears();
		int newAge = Years.yearsBetween(getDateTime(maskedValue, format), DateTime.now()).getYears();

	    assertTrue(originalAge > 89);
	    assertTrue(newAge <= 89);
	}

	@Test
	public void testMask_Over89_MM_dd_yyyy() {
		String text = "01/18/1901";
		String format = "MM/dd/yyyy";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));

	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
		
		int originalAge = Years.yearsBetween(getDateTime(text, format), DateTime.now()).getYears();
		int newAge = Years.yearsBetween(getDateTime(maskedValue, format), DateTime.now()).getYears();

	    assertTrue(originalAge > 89);
	    assertTrue(newAge <= 89);
	}
	
	// Confirm date transformations in different formats work similarly to parent class

	@Test
	public void testMaskISO8601UTCPrecisionZ() {
		String text = "2017-02-01T10:33:06.647-07:00";
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}
	
	@Test
	public void testMaskDateTimeCompress3() {
		String text = "2017020116335570";
		String format = "yyyyMMddHHmmssSSS";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}
	
	@Test
	public void testMaskUnmatched() {
		String text = "2017Jan01";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(maskedValue.isEmpty());
	}
	
	protected boolean isValidDate(String dateToValidate, String pattern){
	    try {
	        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
	        fmt.parseDateTime(dateToValidate);
	    } catch (Exception e) {
	        return false;
	    }
	    return true;
	}
	
	protected DateTime getDateTime(String dateString, String pattern) {
	    try {
	        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
	        return fmt.parseDateTime(dateString);
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	protected long getDateDiff(String first, String second, String dateFormat) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern(dateFormat);
		DateTime firstDate = dtf.parseDateTime(first);
		DateTime secondDate = dtf.parseDateTime(second);
		
		return Seconds.secondsBetween(firstDate,secondDate).getSeconds();
	}
	
	@Test
	public void testMaskLKVDateFormat() {
		String text = "11/30/2012 10:11";
		String format = "MM/dd/yyyy HH:mm";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}
	
	@Test
	public void testMask_MidnightTime() {
		String text = "00:00:00";
		String format = "'00:00:00'";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
	}

	@Test
	public void testMask_DateMidnightTime() {
		String text = "2016-12-22T00:00:00";
		String format = "yyyy-MM-dd'T00:00:00'";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_DateTime() {
		String text = "2017-02-01T10:33:06";
		String format = "yyyy-MM-dd'T'HH:mm:ss";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_HourMinute() {
		String text = "23:07";
		String format = "HH:mm";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	}

	@Test
	public void testMask_Time() {
		String text = "23:07:02";
		String format = "HH:mm:ss";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	}

	@Test
	public void testMask_ISO8601UTCZ() {
		String text = "2009-08-31T00:00:00Z";
		String format = "yyyy-MM-dd'T'HH:mm:ssZ";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_ISO8601UTCNano() {
		String text = "2017-02-01T10:33:06.647123-07:00";
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_Date() {
		String text = "2017-01-17";
		String format = "yyyy-MM-dd"; 
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_DateTimeCompress() {
		String text = "201702011633";
		String format = "yyyyMMddHHmm";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_DateTimeCompress2() {
		
		String text = "20170201103306";
		String format = "yyyyMMddHHmmss";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_MM_slash_dd_slash_yyyy() {
		String text = "02/01/2017";
		String format = "MM/dd/yyyy";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

	@Test
	public void testMask_yyyyMMdd() {
		String text = "20170201";
		String format = "yyyyMMdd";
		Transformer transformer = new BirthdateTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(isValidDate(maskedValue, format));
		
	    Long diff = getDateDiff(maskedValue, text, format);
	    assertTrue(diff != 0);
	    assertTrue(diff >= RANDOM_RANGE_LOW);
	    assertTrue(diff <= RANDOM_RANGE_HIGH);
	}

}
