package it.instantapps.bakingapp.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.google.android.exoplayer2.util.Util;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.utility.Utility;

public class SettingsFragment extends PreferenceFragmentCompat {

    private String mAppVersionName;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general_settings);

        prefMail();
        prefVersion();

    }

    private void prefMail(){
        final Preference mailTo = findPreference("pref_contact");
        mailTo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                int androidVersionCode = 0;
                String hwInfo = null;
                try {
                    androidVersionCode = Util.SDK_INT;
                    hwInfo = Util.MANUFACTURER + " - " + Util.MODEL;
                    mAppVersionName = new Utility(getActivity(), null).appVersionName();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String textMail = "Version: " +
                        mAppVersionName + "\n" +
                        "Model: " +
                        hwInfo + "\n" +
                        "Api: " +
                        String.valueOf(androidVersionCode)+"\n\n";

                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setType("message/rfc822");
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.app_support_mail)});
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_support_mail) + " " + getString(R.string.app_name));
                mailIntent.putExtra(Intent.EXTRA_TEXT, textMail);
                startActivity(Intent.createChooser(mailIntent, getString(R.string.text_mail_intent)));
                return true;
            }
        });

    }

    private void prefVersion(){
        final Preference prefVersion = findPreference(getString(R.string.pref_app_version));

        try {
            mAppVersionName = new Utility(getActivity(), null).appVersionName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (mAppVersionName != null) {
            prefVersion.setTitle("Version: " + mAppVersionName);
        }
    }
}