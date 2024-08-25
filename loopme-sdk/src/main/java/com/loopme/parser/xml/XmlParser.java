package com.loopme.parser.xml;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple XML parser.
 * <p>
 * <p><b>Note:</b> in current implementation tag names are case-insensitive.
 * For example &lt;Img/&gt;, &lt;IMG/&gt;, and &lt;img/&gt; are the same tag.
 * <p>Attributes are case-sensitive.
 * For example &lt;PIC width="7in"/&gt; and &lt;PIC WIDTH="6in"/&gt; are separate attributes.
 */
public class XmlParser {

    public static <T> T parse(String xml, Class<T> classOfT) throws Exception {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new StringReader(xml));
        parser.next();
        return parseTag(parser, classOfT);
    }

    private static <T> T parseTag(XmlPullParser parser, Class<T> classOfT) throws Exception {
        T tagInstance = classOfT.newInstance();
        parseAttributes(parser, tagInstance);
        parser.next();
        parseElements(parser, tagInstance);
        parser.next();
        return tagInstance;
    }

    private static <T> void parseAttributes(XmlPullParser parser, T tagInstance) throws IllegalAccessException {
        for (Field field : tagInstance.getClass().getDeclaredFields()) {
            Attribute attribute = getAnnotation(field, Attribute.class);
            if (attribute == null) continue;
            String value = parser.getAttributeValue(
                null,
                TextUtils.isEmpty(attribute.value()) ? field.getName() : attribute.value()
            );
            if (TextUtils.isEmpty(value)) continue;
            field.setAccessible(true);
            Class fieldClass = field.getType();
            if (fieldClass.equals(String.class)) {
                field.set(tagInstance, value);
            } else if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
                field.setLong(tagInstance, Long.parseLong(value));
            } else if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)) {
                field.setInt(tagInstance, Integer.parseInt(value));
            } else if (Byte.class.equals(fieldClass) || byte.class.equals(fieldClass)) {
                field.setByte(tagInstance, Byte.parseByte(value));
            } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)) {
                field.setDouble(tagInstance, Double.parseDouble(value));
            } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)) {
                field.setFloat(tagInstance, Float.parseFloat(value));
            } else if (Boolean.class.equals(fieldClass) || boolean.class.equals(fieldClass)) {
                try {
                    field.setBoolean(tagInstance, Boolean.parseBoolean(value) || Integer.parseInt(value) == 1);
                } catch (NumberFormatException ignored) {
                    field.setBoolean(tagInstance, false);
                }
            }
        }
    }

    private static <T> void parseElements(XmlPullParser parser, T tagInstance) throws Exception {
        while (
            parser.getEventType() == XmlPullParser.START_TAG ||
            parser.getEventType() == XmlPullParser.TEXT
        ) {
            if (parser.getEventType() == XmlPullParser.TEXT) {
                for (Field field : tagInstance.getClass().getDeclaredFields()) {
                    if (getAnnotation(field, Text.class) != null) {
                        field.setAccessible(true);
                        field.set(tagInstance, parser.getText().trim());
                        break;
                    }
                }
                parser.next();
            } else {
                parseSubTag(parser, tagInstance);
            }
        }
    }

    private static <T> void parseSubTag(XmlPullParser parser, T parent) throws Exception {
        String tagName = parser.getName();
        Field tagField = null;
        for (Field field : parent.getClass().getDeclaredFields()) {
            Tag tagAnnotation = getAnnotation(field, Tag.class);
            if (tagAnnotation != null) {
                String tagValue = TextUtils.isEmpty(tagAnnotation.value()) ? field.getName() : tagAnnotation.value();
                if (tagValue.equalsIgnoreCase(tagName)) {
                    tagField = field;
                    break;
                }
            }
        }
        if (tagField == null) {
            int depth = parser.getDepth();
            while (
                parser.next() != XmlPullParser.END_TAG ||
                !parser.getName().equalsIgnoreCase(tagName) ||
                parser.getDepth() != depth
            ) { /* Continue parsing */ };
            parser.next();
            return;
        }
        tagField.setAccessible(true);
        if (!List.class.isAssignableFrom(tagField.getType())) {
            tagField.set(parent, parseTag(parser, tagField.getType()));
            return;
        }
        ParameterizedType listGenericType = (ParameterizedType) tagField.getGenericType();
        Object tag = parseTag(parser, (Class<?>) listGenericType.getActualTypeArguments()[0]);
        List list = (List) tagField.get(parent);
        if (list == null) {
            list = new ArrayList<>();
            tagField.set(parent, list);
        }
        list.add(tag);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        for (Annotation annotation : element.getDeclaredAnnotations()) {
            if (annotationType.isInstance(annotation)) return (T) annotation;
        }
        return null;
    }
}
