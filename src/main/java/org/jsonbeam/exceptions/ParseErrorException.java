/************************************************************************
 *                                                                      *
 *  DDDD     SSSS    AAA        Daten- und Systemtechnik Aachen GmbH    *
 *  D   D   SS      A   A       Pascalstrasse 28                        *
 *  D   D    SSS    AAAAA       52076 Aachen-Oberforstbach, Germany     *
 *  D   D      SS   A   A       Telefon: +49 (0)2408 / 9492-0           *
 *  DDDD    SSSS    A   A       Telefax: +49 (0)2408 / 9492-92          *
 *                                                                      *
 *                                                                      *
 *  (c) Copyright by DSA - all rights reserved                          *
 *                                                                      *
 ************************************************************************
 *
 * Initial Creation:
 *    Author      se
 *    Created on  20.01.2015
 *
 ************************************************************************/
package org.jsonbeam.exceptions;

import java.util.stream.Collectors;

import java.text.MessageFormat;

public class ParseErrorException extends RuntimeException {

	private static final long serialVersionUID = -3259630066321455786L;

	private static boolean codePointIsPrintable(final int cp) {
		return !(Character.UnicodeBlock.SPECIALS.equals(Character.UnicodeBlock.of(cp)) || Character.isISOControl(cp));
	}

	private static String codePointToString(final int cp) {
		return codePointIsPrintable(cp) ? "'" + String.valueOf(Character.toChars(cp)) + "'" : "0x" + Integer.toHexString(cp);
	}

	private static String ensure(final int i) {
		if (i < 0) {
			throw new IllegalArgumentException();
		}
		return "";
	}

	protected static String makePrintable(final CharSequence chars) {
		return chars.codePoints().mapToObj(ParseErrorException::codePointToString).collect(Collectors.joining(","));
	}

	private static CharSequence subseq(final int middlePosition, final CharSequence json) {
		assert middlePosition < json.length();
		if (json.length() < middlePosition) {
			return "";
		}
		return json.subSequence(Math.max(0, middlePosition - 6), Math.min(json.length(), middlePosition + 6));
	}

	public ParseErrorException(final int i, final char expectedChar, final char illegalChar) {
		super("Illegal char " + makePrintable("" + illegalChar) + " at pos " + i + ". Expected " + makePrintable("" + expectedChar));
	}

	public ParseErrorException(final int i, final CharSequence expectedChars, final char illegalChar) {
		super("Illegal char " + makePrintable("" + illegalChar) + " at pos " + i + ". Expected one of " + makePrintable(expectedChars));
	}

	public ParseErrorException(final int i, final CharSequence json, final CharSequence expectedChars, final char illegalChar) {
		super(ensure(i) + "Illegal char " + makePrintable("" + illegalChar) + " at pos " + i + " '..." + subseq(i, json) + "...'" + ". Expected one of " + makePrintable(expectedChars));
	}

	public ParseErrorException(final int i, final String reason) {
		super(MessageFormat.format(reason, i));
	}

	public ParseErrorException(final int i, final String json, final char expectedChar, final char illegalChar) {
		super("Illegal char " + makePrintable("" + illegalChar) + " at pos " + i + " '..." + json.substring(Math.max(0, i - 6), Math.min(json.length(), i + 6)) + "...'" + ". Expected " + makePrintable("" + expectedChar));
	}
}