package com.begentgroup.xmlparser;

import java.lang.reflect.Field;

class FieldInfo {
	Field f;
	int fieldType = DataCollector.CLASS_NOT_FIXED;
	int componentType = DataCollector.CLASS_NOT_FIXED;
	Class component;
}
