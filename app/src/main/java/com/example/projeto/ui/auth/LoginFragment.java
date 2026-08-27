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
import com.example.projeto.R;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.User;
import com.example.projeto.data.remote.MySqlHelper;
import com.example.projeto.databinding.FragmentLoginBinding;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString();
            String pass = binding.editPassword.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                // 1. Tentar Login no MySQL
                String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                Object[] params = {email, pass};
                
                MySqlHelper.query(sql, params, rs -> {
                    if (rs.next()) {
                        User user = new User(rs.getString("name"), rs.getString("email"), rs.getString("password"));
                        user.id = rs.getInt("id");
                        return user;
                    }
                    return null;
                }, new MySqlHelper.QueryCallback<User>() {
                    @Override
                    public void onSuccess(User remoteUser) {
                        if (remoteUser != null) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                try {
                                    // Sincronizar dados remotos para o Room
                                    User localUser = AppDatabase.getInstance(getContext()).userDao().getUserByEmail(remoteUser.email);
                                    if (localUser != null) {
                                        // Preserva o ID local para evitar quebrar chaves estrangeiras locais
                                        remoteUser.id = localUser.id;
                                    }
                                    AppDatabase.getInstance(getContext()).userDao().register(remoteUser);
                                    
                                    proceedToDashboard(remoteUser.id, remoteUser.name, remoteUser.email);
                                } catch (Exception e) {
                                    showToast("Erro ao sincronizar dados locais.");
                                    loginLocally(email, pass);
                                }
                            });
                        } else {
                            // Se não achou no remoto, tenta no local
                            loginLocally(email, pass);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        // Se der erro de conexão, tenta localmente
                        loginLocally(email, pass);
                    }
                });
            });
        });

        binding.textRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.nav_register);
        });

        binding.textForgotPassword.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.nav_forgot_password);
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

    private void loginLocally(String email, String pass) {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = AppDatabase.getInstance(getContext()).userDao().login(email, pass);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user != null) {
                        proceedToDashboard(user.id, user.name, user.email);
                    } else {
                        Toast.makeText(getContext(), "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void proceedToDashboard(int userId, String userName, String userEmail) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                SharedPreferences prefs = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
                prefs.edit()
                        .putInt("user_id", userId)
                        .putString("user_name", userName)
                        .putString("user_email", userEmail)
                        .apply();
                NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.nav_dashboard);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}