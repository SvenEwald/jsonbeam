package org.jsonbeam.jsonprojector.utils;

public enum DH {
;
public static String oid(Object o) {
	return o.getClass().getSimpleName()+"@"+Integer.toString(System.identityHashCode(o),Character.MAX_RADIX);
}
}
