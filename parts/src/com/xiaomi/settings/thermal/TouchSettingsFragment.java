/**
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.settings.thermal;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import com.xiaomi.settings.R;
import com.xiaomi.settings.widget.SeekBarPreference;

public class TouchSettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, OnMainSwitchChangeListener {

    private SharedPreferences mSharedPrefs;
    private SeekBarPreference mTouchSensitivity;
    private SeekBarPreference mTouchResponse;
    private SeekBarPreference mTouchResistant;
    private MainSwitchPreference mGameMode;

    private String packageName = "";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.touch_settings);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Bundle bundle = getArguments();
        String appName = "";
        if (bundle != null) {
            appName = bundle.getString("appName", "");
            packageName = bundle.getString("packageName", "");
        }

        getActivity().setTitle(getResources().getString(R.string.touch_control_title));

        mGameMode = (MainSwitchPreference) findPreference(Constants.PREF_TOUCH_GAME_MODE);
        mGameMode.addOnSwitchChangeListener(this);

        mTouchResistant = (SeekBarPreference) findPreference(Constants.PREF_TOUCH_RESISTANT);
        mTouchResponse = (SeekBarPreference) findPreference(Constants.PREF_TOUCH_RESPONSE);
        mTouchSensitivity = (SeekBarPreference) findPreference(Constants.PREF_TOUCH_SENSITIVITY);
        updateDefaults();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        if (Constants.PREF_TOUCH_GAME_MODE.equals(key)) {
            updateTouchModes(sharedPrefs.getBoolean(key, false) ? 1 : 0,
                    Constants.TOUCH_GAME_MODE);
        } else if (Constants.PREF_TOUCH_RESPONSE.equals(key)) {
            updateTouchModes(sharedPrefs.getInt(key, 0), Constants.TOUCH_RESPONSE);
        } else if (Constants.PREF_TOUCH_SENSITIVITY.equals(key)) {
            updateTouchModes(sharedPrefs.getInt(key, 0), Constants.TOUCH_SENSITIVITY);
        } else if (Constants.PREF_TOUCH_RESISTANT.equals(key)) {
            updateTouchModes(sharedPrefs.getInt(key, 0), Constants.TOUCH_RESISTANT);
        }
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        mGameMode.setChecked(isChecked);
        mTouchSensitivity.setEnabled(isChecked);
        mTouchResponse.setEnabled(isChecked);
        mTouchResistant.setEnabled(isChecked);
    }

    private void updateDefaults() {
        String[] values = getTouchValues().split(",");
        boolean modeEnabled = Integer.parseInt(values[Constants.TOUCH_GAME_MODE]) == 1;
        mGameMode.setChecked(modeEnabled);

        mTouchSensitivity.setEnabled(modeEnabled);
        mTouchResponse.setEnabled(modeEnabled);
        mTouchResistant.setEnabled(modeEnabled);

        mTouchResponse.setProgress(Integer.parseInt(values[Constants.TOUCH_RESPONSE]));
        mTouchSensitivity.setProgress(Integer.parseInt(values[Constants.TOUCH_SENSITIVITY]));
        mTouchResistant.setProgress(Integer.parseInt(values[Constants.TOUCH_RESISTANT]));
    }

    private void writeTouchValues(String modes) {
        mSharedPrefs.edit().putString(packageName, modes).apply();
    }

    public String getTouchValues() {
        String values = mSharedPrefs.getString(packageName, null);
        if (values == null || values.isEmpty()) {
            values = "0,0,0,0";
        }
        writeTouchValues(values);
        return values;
    }

    public void updateTouchModes(int value, int mode) {
        String[] values = getTouchValues().split(",");
        values[mode] = String.valueOf(value);
        String finalValues = values[Constants.TOUCH_GAME_MODE] + "," + values[Constants.TOUCH_RESPONSE] + ","
                + values[Constants.TOUCH_SENSITIVITY] + "," + values[Constants.TOUCH_RESISTANT];
        writeTouchValues(finalValues);
    }
}
