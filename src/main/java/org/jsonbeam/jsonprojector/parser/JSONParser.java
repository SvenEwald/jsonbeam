package org.jsonbeam.jsonprojector.parser;

import java.util.Objects;

import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.index.JBResultCollector;
import org.jsonbeam.index.keys.KeyReference;
import org.jsonbeam.index.model.Reference;
import org.jsonbeam.index.model.values.StringCopyReference;
import org.jsonbeam.index.model.values.StringValueReference;

public abstract class JSONParser {

	@FunctionalInterface
	protected interface TokenEnder {
		boolean isTokenEnd(char c);
	}

	private static boolean isStringEnd(final char c) {
		return (c <= ' ') || (c == ',') || (c == ']') || (c == '}');
	}

	protected final CharSequence json;
	protected int cursor;
	protected final JBResultCollector resultCollector;

	protected JSONParser(final CharSequence json, final JBResultCollector resultCollector) {
		Objects.requireNonNull(json);
		Objects.requireNonNull(resultCollector);
		this.json = json;
		this.resultCollector = (resultCollector);
	}

	protected char consumeWhitespace() {
		int j = cursor;
		int e = json.length();
		char c;
		while (j <= e) {
			c = json.charAt(j);
			if (c > ' ') {
				cursor = j;
				return c;
			}

			++j;
		}
		throw new UnexpectedEOF(cursor, json);
	}

	protected KeyReference parseJSONKey(final CharSequence array, final int start, final TokenEnder ender) {
		int hash = 0;
		final int max = array.length();
		for (int i = start; i < max; ++i) {
			char c = array.charAt(i);
			//if (c == '"') {
			if (ender.isTokenEnd(c)) {
				cursor = i + 1;
				return new KeyReference(start, i, hash, json);
			}
			if (c == '\\') {
				StringBuilder builder = new StringBuilder();
				builder.append(array, start, i - start);
				for (int j = i; j < max; ++j) {
					c = array.charAt(j);
					//if (c == '"') {
					if (ender.isTokenEnd(c)) {
						cursor = j + 1;
						return new KeyReference(builder.toString());
					}
					if (c == '\\') {
						switch (c = array.charAt(++j)) {
						case '"':
						case '\\':
						case '/':
						case 'n':
						case 'r':
						case 't':
						case 'b':
						case 'f':
							builder.append(c);
							continue;
						case 'u':
							builder.append(Character.toChars(Integer.valueOf(new StringBuilder(array.subSequence(i, i + 4)).toString(), 16)));
							j += 4;
							continue;

						default:
						}

						continue;
					}
					builder.append(c);
				}
			}
			hash = (31 * hash) + c;
		}
		throw new UnexpectedEOF(array.length(), json);
	}

	protected Reference parseJSONString(final CharSequence array, final int start, final TokenEnder ender) {
		final int max = array.length();
		for (int i = start; i <= max; ++i) {
			char c = array.charAt(i);
			if (ender.isTokenEnd(c)) {
				cursor = i + 1;
				return new StringValueReference(start, i);
			}
			if (c == '\\') {
				StringBuilder builder = new StringBuilder();
				builder.append(array, start, i);
				for (int j = i; j < max; ++j) {
					c = array.charAt(j);
					if (ender.isTokenEnd(c)) {
						cursor = j + 1;
						return new StringCopyReference(builder);
					}
					if (c == '\\') {
						switch (c = array.charAt(++j)) {
						case '"':
						case '\\':
						case '/':
							builder.append(c);
							continue;
						case 'n':
							builder.append('\n');
							continue;
						case 'r':
							builder.append('\r');
							continue;
						case 't':
							builder.append('\t');
							continue;
						case 'b':
							builder.append('\b');
							continue;
						case 'f':
							builder.append('\f');
							continue;
						case 'u':
							builder.append(Character.toChars(Integer.valueOf(new StringBuilder(array.subSequence(i, i + 4)).toString(), 16)));
							j += 4;
							continue;

						default:
						}
						continue;
					}
					builder.append(c);
				}
			}
		}
		throw new UnexpectedEOF(array.length(), json);
	}

	protected Reference parseUnquotedJSONString(final CharSequence array, final int start) {
		final int max = array.length();
		for (int i = start; i <= max; ++i) {
			char c = array.charAt(i);
			if (isStringEnd(c)) {
				cursor = i;
				int length = i - start;
				if (length == 4) {
					if (((array.charAt(start) == 't') && (array.charAt(start + 1) == 'r')) || (array.charAt(start + 2) == 'u') || (array.charAt(start + 3) == 'e')) {
						return Reference.TRUE;
					}
					if (((array.charAt(start) == 'n') && (array.charAt(start + 1) == 'u')) || (array.charAt(start + 2) == 'l') || (array.charAt(start + 3) == 'l')) {
						return Reference.NULL;
					}
				}
				else if (length == 5) {
					if (((array.charAt(start) == 'f') && (array.charAt(start + 1) == 'a')) || (array.charAt(start + 2) == 'l') || (array.charAt(start + 3) == 's') || (array.charAt(start + 3) == 'e')) {
						return Reference.FALSE;
					}
				}
				return new StringValueReference(start, i);
			}
			if (c == '\\') {
				StringBuilder builder = new StringBuilder();
				builder.append(array, start, i - start);
				for (int j = i; j < max; ++j) {
					c = array.charAt(j);
					if (isStringEnd(c)) {
						cursor = j + 1;
						return new StringCopyReference(builder);
					}
					if (c == '\\') {
						switch (c = array.charAt(++j)) {
						case '"':
						case '\\':
						case ',':
						case ']':
						case '}':
						case '/':
							builder.append(c);
							continue;
						case 'n':
							builder.append('\n');
							continue;
						case 'r':
							builder.append('\r');
							continue;
						case 't':
							builder.append('\t');
							continue;
						case 'b':
							builder.append('\b');
							continue;
						case 'f':
							builder.append('\f');
							continue;
						case 'u':
							builder.append(Character.toChars(Integer.valueOf(new StringBuilder(array.subSequence(i, i + 4)).toString(), 16)));
							j += 4;
							continue;

						default:
						}
						continue;
					}
					builder.append(c);
				}
			}
		}
		throw new UnexpectedEOF(0, json);
	}

}
