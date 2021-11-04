package com.example.babblechatapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    // sharedPreference is actually
    /**
     * a Map<String, ?> data type to hold settings for later put into XML to share
     * @see <a href="https://www.geeksforgeeks.org/shared-preferences-in-android-with-examples/">Shared Preferences in Android</a>
     */
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor taking a {@link Context} instance as arguments: because we need to know all values in the current context to not violate values we don't want to change
     * @param context
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Put Boolean type of setting (settings that are monitored by Boolean) into the editor for later export to XML
     * @param key Setting name, also used for lookup
     * @param value of type Boolean which is the "core"/monitoring of this setting
     * (i.e. Setting name "Private" (keys) monitored through 2 options (values): ON or OFF
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    /**
     * Get the Boolean setting we put in
     * @param key
     * @return the Boolean value of the matching key, false if key has no value (no matching key)
     */
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Put String type of setting (settings that are monitored by String) into the editor for later export to XML
     * @param key Setting name
     * @param value value to monitor the setting value
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get the String value of the setting
     * @param key indicating the lookup key (for search)
     * @return the string value in the setting
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    /**
     * Clear all values written in preference editor of type {@link android.content.SharedPreferences.Editor}, only key remains
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
