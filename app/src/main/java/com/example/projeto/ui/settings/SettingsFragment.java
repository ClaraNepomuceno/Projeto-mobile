package com.example.projeto.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.User;
import com.example.projeto.data.remote.MySqlHelper;
import com.example.projeto.databinding.FragmentSettingsBinding;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private User currentUser;
    private SharedPreferences sessionPrefs;
    private SharedPreferences themePrefs;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        
        sessionPrefs = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        themePrefs = getActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);

        loadUserData();
        setupThemeToggle();

        binding.btnSaveSettings.setOnClickListener(v -> saveChanges());

        return binding.getRoot();
    }

    private void loadUserData() {
        String email = sessionPrefs.getString("user_email", "");
        if (email.isEmpty()) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            currentUser = AppDatabase.getInstance(getContext()).userDao().getUserByEmail(email);
            if (currentUser != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.editSettingsName.setText(currentUser.name);
                    binding.editSettingsEmail.setText(currentUser.email);
                });
            }
        });
    }

    private void setupThemeToggle() {
        boolean isDarkMode = themePrefs.getBoolean("dark_mode", false);
        binding.switchDarkMode.setChecked(isDarkMode);

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            themePrefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? 
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void saveChanges() {
        if (currentUser == null) return;

        String newName = binding.editSettingsName.getText().toString();
        String newEmail = binding.editSettingsEmail.getText().toString();
        String newPassword = binding.editSettingsPassword.getText().toString();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(getContext(), "Nome e Email são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldEmail = currentUser.email;
        currentUser.name = newName;
        currentUser.email = newEmail;
        if (!newPassword.isEmpty()) {
            currentUser.password = newPassword;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Update Room
            AppDatabase.getInstance(getContext()).userDao().update(currentUser);

            // 2. Update MySQL
            String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE email = ?";
            Object[] params = {currentUser.name, currentUser.email, currentUser.password, oldEmail};
            
            MySqlHelper.executeUpdate(sql, params, new MySqlHelper.QueryCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            updateSession(currentUser.name, currentUser.email);
                            Toast.makeText(getContext(), "Configurações salvas!", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            updateSession(currentUser.name, currentUser.email);
                            Toast.makeText(getContext(), "Salvo localmente (erro ao sincronizar)", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        });
    }

    private void updateSession(String name, String email) {
        sessionPrefs.edit()
                .putString("user_name", name)
                .putString("user_email", email)
                .apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}