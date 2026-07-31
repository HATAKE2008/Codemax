package com.tyron.code.ui.settings;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.transition.MaterialSharedAxis;
import com.tyron.common.SharedPreferenceKeys;
import codemax.rm.R;
import codemax.rm.databinding.BaseTextinputLayoutBinding;

public class GitSettingsFragment extends PreferenceFragmentCompat {
  private BaseTextinputLayoutBinding binding;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
    setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.git_preferences, rootKey);
  }

  @Override
  public boolean onPreferenceTreeClick(Preference preference) {

    String key = preference.getKey();
    SharedPreferences pref = preference.getSharedPreferences();
    String user_name = pref.getString("user_name", "");
    String user_email = pref.getString("user_email", "");
    String access_token = pref.getString("git_access_token", "");
    String remote_url = pref.getString("git_remote_url", "");

    binding = BaseTextinputLayoutBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();

    switch (key) {
      case SharedPreferenceKeys.GIT_USER_NAME:
        AlertDialog dialog =
            new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(R.string.enter_full_name)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog.setOnShowListener(
            d -> {
              final Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

              binding.textinputLayout.setHint(R.string.git_user_name_title);
              EditText editText = binding.textinputLayout.getEditText();
              editText.setText(user_name);

              button.setOnClickListener(
                  v -> {
                    String name = binding.textinputLayout.getEditText().getText().toString();
                    pref.edit().putString(SharedPreferenceKeys.GIT_USER_NAME, name).apply();
                    preference.callChangeListener(name);
                    d.dismiss();
                  });
            });

        dialog.show();

        return true;

      case SharedPreferenceKeys.GIT_USER_EMAIL:
        AlertDialog dialog1 =
            new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(R.string.enter_email)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog1.setOnShowListener(
            d -> {
              final Button button = dialog1.getButton(DialogInterface.BUTTON_POSITIVE);

              binding.textinputLayout.setHint(R.string.git_user_email_title);
              EditText editText = binding.textinputLayout.getEditText();
              editText.setText(user_email);

              button.setOnClickListener(
                  v -> {
                    String email = binding.textinputLayout.getEditText().getText().toString();
                    pref.edit().putString(SharedPreferenceKeys.GIT_USER_EMAIL, email).apply();
                    preference.callChangeListener(email);
                    d.dismiss();
                  });
            });

        dialog1.show();

        return true;

      case SharedPreferenceKeys.GIT_ACCESS_TOKEN:
        AlertDialog dialog2 =
            new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(R.string.enter_access_token)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog2.setOnShowListener(
            d -> {
              final Button button = dialog2.getButton(DialogInterface.BUTTON_POSITIVE);

              binding.textinputLayout.setHint(R.string.git_access_token_title);
              EditText editText = binding.textinputLayout.getEditText();
              editText.setText(access_token);

              button.setOnClickListener(
                  v -> {
                    String token = binding.textinputLayout.getEditText().getText().toString();
                    pref.edit().putString(SharedPreferenceKeys.GIT_ACCESS_TOKEN, token).apply();
                    preference.callChangeListener(token);
                    d.dismiss();
                  });
            });

        dialog2.show();

        return true;

      case SharedPreferenceKeys.GIT_REMOTE_URL:
        AlertDialog dialog3 =
            new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .setTitle(R.string.enter_remote_url)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog3.setOnShowListener(
            d -> {
              final Button button = dialog3.getButton(DialogInterface.BUTTON_POSITIVE);

              binding.textinputLayout.setHint(R.string.git_remote_url_title);
              EditText editText = binding.textinputLayout.getEditText();
              editText.setText(remote_url);

              button.setOnClickListener(
                  v -> {
                    String url = binding.textinputLayout.getEditText().getText().toString();
                    pref.edit().putString(SharedPreferenceKeys.GIT_REMOTE_URL, url).apply();
                    preference.callChangeListener(url);
                    d.dismiss();
                  });
            });

        dialog3.show();

        return true;
    }

    return super.onPreferenceTreeClick(preference);
  }
}
