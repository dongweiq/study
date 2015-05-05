
package com.base.db;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used for describing the name of table. If you use this notion for
 * a class, it will visible as a table name.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name();
}
