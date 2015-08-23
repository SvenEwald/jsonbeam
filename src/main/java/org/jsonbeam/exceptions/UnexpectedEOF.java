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

public class UnexpectedEOF extends ParseErrorException {

	private static final long serialVersionUID = 161429987728291878L;

	public UnexpectedEOF(final int cursor, final CharSequence json) {
		super(cursor, "Unexpected end of input at pos " + cursor + "'" + makePrintable(json.subSequence(Math.max(0, cursor - 20), cursor)) + "'");
	}

	public UnexpectedEOF(final int cursor) {
		super(cursor, "Unexpected end of input at pos " + cursor);
	}
}
