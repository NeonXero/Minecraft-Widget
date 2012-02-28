package net.neonlotus.minecraft;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MobPref extends Activity implements View.OnClickListener {
    // TAG
    private static final String TAG = "Mine/MobPref";

    private LinearLayout mLinearLayout;
    private ArrayList<CheckBox> mBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mob_pref);



        Button savemob = (Button) findViewById(R.id.doneButton);
        Button selectall = (Button) findViewById(R.id.addAll);
        Button deselectall = (Button) findViewById(R.id.subAll);

        //OnClicks
        savemob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int monkey = 0;
                for (int i = 0, len = mBoxes.size(); i < len; i++) {
                    CheckBox cb = (CheckBox) mBoxes.get(i);
                    if (!cb.isChecked()) {
                        monkey++;
                    }
                }
                if (monkey == mBoxes.size()) {
                    Toast.makeText(getApplicationContext(), "Please Chose At Least One Mob", Toast.LENGTH_SHORT).show();
                } else {
                    saveSelection();
                    System.exit(0);
                }
            }
        });

        selectall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                checkAll(true);
            }
        });

        deselectall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                checkAll(false);
            }
        });


        init();

    }

    public void init() {
        mLinearLayout = (LinearLayout) findViewById(R.id.mob_pref_linear);
        mBoxes = new ArrayList<CheckBox>();
        layoutCheckboxes();
        readSelection();

    }

    public void layoutCheckboxes() {
        mLinearLayout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        Resources res = getResources();
        String[] mobz = res.getStringArray(R.array.mob_list);

        for (int i = 0, len = CONFIG.IMAGES.length; i < len; i++) {
            CheckBox cb = (CheckBox) inflater.inflate(R.layout.animal_box, mLinearLayout, false);
            cb.setText(mobz[i]);
            cb.setChecked(false);
            mBoxes.add(cb);
            mLinearLayout.addView(cb);
        }
    }


    public void checkAll(boolean checked) {
        for (int i = 0, len = mBoxes.size(); i < len; i++) {
            CheckBox cb = (CheckBox) mBoxes.get(i);
            cb.setChecked(checked);
        }
    }


    public void saveSelection() {
        String concatIds = null;
        for (int i = 0, len = mBoxes.size(); i < len; i++) {
            if (mBoxes.get(i).isChecked()) {
                // Add selection
                if (concatIds == null) {
                    concatIds = "" + i;
                } else {
                    concatIds += "_" + i;
                }
            }
        }

        if (concatIds != null) {
            SharedPreferences sp = getSharedPreferences(CONFIG.Preferences.MOBPREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CONFIG.Preferences.SELECTED_MOBS, concatIds);
            editor.commit();
        } else {
            // non selected
        }
    }

    public void readSelection() {
        SharedPreferences sp = getSharedPreferences(CONFIG.Preferences.MOBPREFS, Context.MODE_PRIVATE);
        String ids = sp.getString(CONFIG.Preferences.SELECTED_MOBS, null);
        if (ids != null) {
            String[] splitIds = ids.split("_");

            if (splitIds.length > 0) {
                for (int i = 0, len = splitIds.length; i < len; i++) {
                    Log.d(TAG, "found id: " + splitIds[i]);
                    mBoxes.get(Integer.parseInt(splitIds[i])).setChecked(true);
                }
            }
        }
    }




    public void onClick(View view) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}