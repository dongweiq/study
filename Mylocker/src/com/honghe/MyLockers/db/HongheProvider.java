package com.honghe.MyLockers.db;

import com.base.db.DBConfig;
import com.base.db.IssContentProvider;
import com.base.db.IssDBFactory;
import com.honghe.MyLockers.bean.LockersBean;
import com.honghe.MyLockers.bean.LockersDetailBean;


public class HongheProvider extends IssContentProvider {

    @Override
    public void init() {
        DBConfig config = new DBConfig.Builder()
                                        .setName("mylocker.db")
                                        .setVersion(1)
                                        .setAuthority("com.honghe.mylocker")
                                        .addTatble(LockersBean.class)
                                        .addTatble(LockersDetailBean.class)
                                        .build();
		IssDBFactory.init(getContext(), config);
    }

}
