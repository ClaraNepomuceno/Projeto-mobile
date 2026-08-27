package com.example.projeto.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projeto.databinding.FragmentForgotPasswordBinding;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);

        binding.btnSend.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Digite seu e-mail", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Instruções enviadas para " + email, Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(ForgotPasswordFragment.this).popBackStack();
        });

        binding.textBackToLogin.setOnClickListener(v -> {
            NavHostFragment.findNavController(ForgotPasswordFragment.this).popBackStack();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}