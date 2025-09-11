package com.eyadalalimi.students.ui.activity.home;

import android.os.Bundle;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContent(R.layout.page_home, getString(R.string.title_home), R.id.nav_home);
    }
}
