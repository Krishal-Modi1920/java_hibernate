package org.baps.api.vtms.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Hack to filter the fields based on what client needs.
 */
@Slf4j
public class FieldsUtil {
    private static final List<String> IGNORE_FIELDS = List.of("log");

    public static <E> void setFields(final E original, final List<String> fields) {
        if (original == null || fields == null || fields.size() == 0) {
            return;
        }
        final List<Field> originalFields = getAllFields(new ArrayList<Field>(), original.getClass());
        final List<String> adjustedFields = new ArrayList<>(fields);
        final Map<String, List<String>> nestedMap = new HashMap<>();
        //iterate and scan for nested objects
        for (final String name : fields) {
            if (name.indexOf(".") > 0) {
                final String[] result = name.split("\\.");
                final String fieldName = result[0];
                if (!nestedMap.containsKey(fieldName)) {
                    nestedMap.put(fieldName, new ArrayList<String>());
                    adjustedFields.add(fieldName);
                }
                nestedMap.get(fieldName).add(result[1]);
            }
        }

        for (Map.Entry<String, List<String>> entry : nestedMap.entrySet()) {
            try {
                final Field f = original.getClass().getDeclaredField(entry.getKey());
                f.setAccessible(true);
                //grab the object the field is associated with from the original object
                final Object object = f.get(original);
                setFields(object, nestedMap.get(entry.getKey()));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                log.warn("Exception in FieldsUtil for class [" + original.getClass().getName() + "] with property [" + entry.getKey()
                    + "] \n" + e.getMessage());
            }
        }

        for (final Field field : originalFields) {
            if (!adjustedFields.contains(field.getName()) && !IGNORE_FIELDS.contains(field.getName())) {
                try {
                    PropertyUtils.setProperty(original, field.getName(), null);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.warn("Exception in FieldsUtil for class [" + original.getClass().getName() + "] with property [" + field.getName()
                        + "] \n" + e.getMessage());
                }
            }
        }
    }

    private static <T> List<Field> getAllFields(final List<Field> fields, final Class<T> type) {
        List<Field> tempFields = fields;
        fields.addAll(List.of(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            tempFields = getAllFields(tempFields, type.getSuperclass());
        }

        return tempFields;
    }


    /**
     * Returns the source model with specific fields selected.
     *
     * @param <T>     The type of the source model.
     * @param source  The source model to extract fields from.
     * @param fields  Comma-separated field names to select.
     */
    public static <T> void selectFields(final T source, final String fields) {
        FieldsUtil.setFields(source, getFields(fields));
    }

    public static List<String> getFields(final String fields) {
        return !org.springframework.util.StringUtils.hasLength(fields) ? Collections.emptyList() : List.of(fields.split("\\,"));
    }
}
