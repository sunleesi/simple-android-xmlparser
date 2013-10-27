package com.begentgroup.xmlparser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;

class XMLGenerator {
	static HashMap<Class,ClassInfoTable> tables = new HashMap<Class,ClassInfoTable>();
	static FieldInfo sObjectInfo = new FieldInfo();
	static FieldInfo sPrimitiveInfo = new FieldInfo();
	static {
		sObjectInfo.fieldType = Utils.CLASS_OBJECT;
		sPrimitiveInfo.fieldType = Utils.CLASS_PRIMITIVE;
	}
	
	String result;
	
	public XMLGenerator(String element, Object object) {
		int type = Utils.getClassType(object.getClass());
		FieldInfo info = new FieldInfo();
		if (type == Utils.CLASS_ARRAY || type == Utils.CLASS_COLLECTION) return;
		info.fieldType = type;
		result = generatingXML(element, object, info);
	}
	
	public String generatingXML(String element, Object object, FieldInfo info) {
		StringBuilder sb = new StringBuilder();
		FieldInfo childInfo = null;
				
		switch (info.fieldType) {
		case Utils.CLASS_PRIMITIVE :
			sb.append("<"+element+">" + object + "</"+element+">\n\r");
			break;
		case Utils.CLASS_ARRAY :
			if (info.componentType == Utils.CLASS_ARRAY || info.componentType == Utils.CLASS_COLLECTION) return "";
			
			if (info.componentType == Utils.CLASS_PRIMITIVE) {
				childInfo = sPrimitiveInfo; 
			} else if (info.componentType == Utils.CLASS_OBJECT) {
				childInfo = sObjectInfo;
			}
			
			int length = Array.getLength(object);
			for (int i = 0; i < length; i++) {
				Object value = Array.get(object, i);
				sb.append(generatingXML(element, value, childInfo));
			}
			break;
		case Utils.CLASS_COLLECTION :
			if (info.componentType == Utils.CLASS_ARRAY || info.componentType == Utils.CLASS_COLLECTION) return "";
			if (info.componentType == Utils.CLASS_PRIMITIVE) {
				childInfo = sPrimitiveInfo; 
			} else if (info.componentType == Utils.CLASS_OBJECT) {
				childInfo = sObjectInfo;
			}
			Collection collection = (Collection)object;
			for (Object value : collection) {
				sb.append(generatingXML(element, value, childInfo));
			}
			break;
		case Utils.CLASS_OBJECT :
			ClassInfoTable table = tables.get(object.getClass());
			if (table == null) {
				table = makeClassInfoTable(object.getClass());
				tables.put(object.getClass(), table);
			}
			sb.append("<"+element);
			for (Field f : table.fields) {
				FieldInfo cinfo = table.fieldInfos.get(f.getName());
				if (cinfo.annotationType == FieldInfo.ANNOTATION_TYPE_ATTRIBUTE && cinfo.fieldType == Utils.CLASS_PRIMITIVE) {
					Object v = null;
					try {
						v = f.get(object);
						if (v != null) {
							sb.append(" " + f.getName() + "= \"" + v + "\"");
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			sb.append(">\n\r");

			for (Field f : table.fields) {
				FieldInfo cinfo = table.fieldInfos.get(f.getName());
				if (cinfo.annotationType != FieldInfo.ANNOTATION_TYPE_ATTRIBUTE) {
					Object v = null;
					try {
						v = f.get(object);
						if (v != null) {
							sb.append(generatingXML(f.getName(), v, cinfo));
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			sb.append("</" + element + ">\n\r");
			break;
		}
		return sb.toString();
	}
	
	private ClassInfoTable makeClassInfoTable(Class<? extends Object> clazz) {
		ClassInfoTable table = new ClassInfoTable();
		table.fields = clazz.getDeclaredFields();
		for (Field field : table.fields) {
			FieldInfo info = new FieldInfo();
			info.f = field;
			info.fieldType = Utils.getClassType(field.getType());
			if (field.getAnnotation(Attribute.class) != null) {
				info.annotationType = FieldInfo.ANNOTATION_TYPE_ATTRIBUTE;
			} else if (field.getAnnotation(Element.class) != null) {
				info.annotationType = FieldInfo.ANNOTATION_TYPE_ELEMENT;
			}
				
			switch (info.fieldType) {
			case Utils.CLASS_ARRAY :
				info.component = field.getType().getComponentType();
				info.componentType = Utils.getClassType(info.component);
				break;
			case Utils.CLASS_COLLECTION :
				info.component = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
				info.componentType = Utils.getClassType(info.component);
				break;
			}
			table.fieldInfos.put(field.getName(), info);
		}
		return table;
	}

	@Override
	public String toString() {
		return result;
	}
}
