
package com.base.db;

import java.util.ArrayList;

import com.base.bean.BaseBean;

public class DBConfig {
    final ArrayList<Class<? extends BaseBean<?>>> tableList;

    final String dbName;

    final int dbVersion;
    
    final String authority;
    
    final ArrayList<String> tableNameList;

    private DBConfig(final Builder builder) {
        tableList = builder.tableList;
        dbName = builder.dbName;
        dbVersion = builder.dbVersion;
        authority = builder.authority;
        
        tableNameList =  new ArrayList<String>();
        for(Class<? extends BaseBean<?>> c:tableList){
            String name = TableUtil.getTableName(c);
            tableNameList.add(name);
        }
    }

    public static class Builder {
        private ArrayList<Class<? extends BaseBean<?>>> tableList;

        private String dbName;

        private int dbVersion;
        
        private String authority = "com.iss.mobile";

        public Builder() {
            tableList = new ArrayList<Class<? extends BaseBean<?>>>();
        }

        public Builder setName(String name) {
            dbName = name;
            return this;
        }

        public Builder setVersion(int version) {
            dbVersion = version;
            return this;
        }

        public Builder addTatble(Class<? extends BaseBean<?>> table) {
            tableList.add(table);
            return this;
        }
        
        public Builder setAuthority(String authority){
            this.authority = authority;
            return this;
        }
        
        public DBConfig build(){
            return new DBConfig(this);
        }
    }

}
