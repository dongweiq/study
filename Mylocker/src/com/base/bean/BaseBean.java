
package com.base.bean;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.base.db.TableColumn;
import com.google.gson.Gson;

public abstract class BaseBean<T> implements Serializable {
    private static final long serialVersionUID = -804757173578073135L;

    @TableColumn(type = TableColumn.Types.INTEGER, isPrimary = true)
    public final static String _ID = "_id";

    /**
     * 将json对象转化为Bean实例
     * 
     * @param jsonObj
     * @return
     */
    public abstract T parseJSON(JSONObject jsonObj);

    /**
     * 将Bean实例转化为json对象
     * 
     * @return
     */
    public abstract JSONObject toJSON();

    /**
     * 将数据库的cursor转化为Bean实例（如果对象涉及在数据库存取，需实现此方法）
     * 
     * @param cursor
     * @return
     */
    public abstract T cursorToBean(Cursor cursor);

    /**
     * 将Bean实例转化为一个ContentValues实例，供存入数据库使用（如果对象涉及在数据库存取，需实现此方法）
     * 
     * @return
     */
    public abstract ContentValues beanToValues();

    @SuppressWarnings("unchecked")
    public T parseJSON(Gson gson, String json) {
        return (T) gson.fromJson(json, this.getClass());
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        try {
            Class<?> c = getClass();
            Field[] fields = c.getFields();
            for (Field f : fields) {
                f.setAccessible(true);
                final TableColumn tableColumnAnnotation = f.getAnnotation(TableColumn.class);
                if (tableColumnAnnotation != null) {
                    if (tableColumnAnnotation.type() == TableColumn.Types.INTEGER) {
                        values.put(f.getName(), f.getInt(this));
                    } else if (tableColumnAnnotation.type() == TableColumn.Types.BLOB) {
                        values.put(f.getName(), (byte[]) f.get(this));
                    } else if (tableColumnAnnotation.type() == TableColumn.Types.TEXT) {
                        values.put(f.getName(), f.get(this).toString());
                    } else {
                        values.put(f.getName(), f.get(this).toString());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return values;
    }
    

}
