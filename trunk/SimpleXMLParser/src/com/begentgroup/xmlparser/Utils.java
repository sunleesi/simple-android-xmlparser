package com.begentgroup.xmlparser;

import java.util.Collection;

class Utils {

	public static final int CLASS_NOT_FIXED = -1;
	public static final int CLASS_PRIMITIVE = 0;
	public static final int CLASS_ARRAY = 1;
	public static final int CLASS_COLLECTION = 2;
	public static final int CLASS_OBJECT = 3;

	public static int getClassType(Class clazz) {
		if (clazz.isPrimitive() || 
			clazz == Integer.class || clazz == Long.class || clazz == Short.class ||
			clazz == String.class || 
			clazz == Character.class || clazz == Boolean.class ||
			clazz == Float.class || clazz == Double.class) {
			return CLASS_PRIMITIVE;
		} else if (clazz.isArray()) {
			return CLASS_ARRAY;
		} else if (Collection.class.isAssignableFrom(clazz)) {
			return CLASS_COLLECTION;
		} 
		return CLASS_OBJECT;
	}
}
