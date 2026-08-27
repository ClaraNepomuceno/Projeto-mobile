package com.example.projeto.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.User;
import com.example.projeto.data.remote.MySqlHelper;
import com.example.projeto.databinding.FragmentRegisterBinding;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String email = binding.editEmail.getText().toString();
            String pass = binding.editPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            MySqlHelper.checkEmailExists(email, new MySqlHelper.QueryCallback<>() {
                @Override
                public void onSuccess(Boolean exists) {
                    if (exists) {
                        showToast("E-mail já cadastrado!");
                    } else {
                        registerUser(name, email, pass);
                    }
                }

                @Override
                public void onError(Exception e) {
                    // Fallback to local check if remote fails
                    Executors.newSingleThreadExecutor().execute(() -> {
                        User localUser = AppDatabase.getInstance(getContext()).userDao().getUserByEmail(email);
                        if (localUser != null) {
                            showToast("E-mail já cadastrado localmente!");
                        } else {
                            registerUser(name, email, pass);
                        }
                    });
                }
            });
        });

        binding.textLogin.setOnClickListener(v -> {
            NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
        });

        return binding.getRoot();
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> 
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void registerUser(String name, String email, String pass) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        Object[] params = {name, email, pass};

        MySqlHelper.executeUpdate(sql, params, new MySqlHelper.QueryCallback<>() {
            @Override
            public void onSuccess(Integer result) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    User user = new User(name, email, pass);
                    try {
                        AppDatabase.getInstance(getContext()).userDao().register(user);
                        User savedUser = AppDatabase.getInstance(getContext()).userDao().getUserByEmail(email);
                        int userId = savedUser != null ? savedUser.id : -1;

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                saveSession(userId, name, email);
                                Toast.makeText(getContext(), "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
                            });
                        }
                    } catch (Exception e) {
                        showToast("Erro ao sincronizar dados locais.");
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                    showToast("E-mail já cadastrado no servidor!");
                } else {
                    // Tenta salvar localmente se o servidor falhar por outros motivos (offline)
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            User user = new User(name, email, pass);
                            AppDatabase.getInstance(getContext()).userDao().register(user);
                            User savedUser = AppDatabase.getInstance(getContext()).userDao().getUserByEmail(email);
                            int userId = savedUser != null ? savedUser.id : -1;

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    saveSession(userId, name, email);
                                    Toast.makeText(getContext(), "Salvo localmente (Servidor offline)", Toast.LENGTH_SHORT).show();
                                    NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
                                });
                            }
                        } catch (Exception ex) {
                            showToast("Erro ao cadastrar: e-mail já existe.");
                        }
                    });
                }
            }
        });
    }

    private void saveSession(int userId, String name, String email) {
        SharedPreferences prefs = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
        prefs.edit()
                .putInt("user_id", userId)
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