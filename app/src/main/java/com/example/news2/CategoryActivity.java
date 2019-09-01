package com.example.news2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class CategoryActivity extends Activity implements CompoundButton.OnCheckedChangeListener{

    private static final int numOfCategories = 11;

    private int[] checkboxIds = new int[numOfCategories];
    private CheckBox[] checkboxs = new CheckBox[numOfCategories];
    private boolean[] selected = new boolean[numOfCategories];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_category);
        TypedArray checkbox_array = this.getResources().obtainTypedArray(R.array.checkbox_array);
        selected = getIntent().getBooleanArrayExtra("selected");
        for(int i=0;i<numOfCategories;i++){
            checkboxIds[i] = checkbox_array.getResourceId(i,0);
            checkboxs[i] = (CheckBox) findViewById(checkboxIds[i]);
            checkboxs[i].setChecked(selected[i]);
            checkboxs[i].setOnCheckedChangeListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int i=0;i<numOfCategories;i++){
            selected[i] = checkboxs[i].isChecked();
        }
        Intent intent = new Intent("broadsend.action");
        intent.putExtra("selected",selected);
        sendBroadcast(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        return;
    }
}
