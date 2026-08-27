package com.example.projeto.ui.goals.form;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.Goal;
import com.example.projeto.databinding.FragmentAddGoalBinding;
import java.util.concurrent.Executors;

public class AddGoalFragment extends Fragment {
    private FragmentAddGoalBinding binding;
    private Goal goalToEdit;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddGoalBinding.inflate(inflater, container, false);

        int idParam = -1;
        if (getArguments() != null) {
            idParam = getArguments().getInt("goalId", -1);
        }

        if (idParam != -1) {
            loadGoalData(idParam);
        }

        binding.btnSave.setOnClickListener(v -> saveGoal());
        binding.btnDelete.setOnClickListener(v -> deleteGoal());

        return binding.getRoot();
    }

    private void loadGoalData(int id) {
        AppDatabase.getInstance(getContext()).goalDao().getGoalById(id).observe(getViewLifecycleOwner(), goal -> {
            if (goal != null) {
                goalToEdit = goal;
                binding.textTitle.setText("Editar Meta");
                binding.editGoalName.setText(goal.name);
                binding.editTargetAmount.setText(String.valueOf(goal.targetAmount));
                binding.editCurrentAmount.setText(String.valueOf(goal.currentAmount));
                binding.btnDelete.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveGoal() {
        String name = binding.editGoalName.getText().toString();
        String targetStr = binding.editTargetAmount.getText().toString();
        String currentStr = binding.editCurrentAmount.getText().toString();

        if (name.isEmpty() || targetStr.isEmpty()) {
            Toast.makeText(getContext(), "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        double target = Double.parseDouble(targetStr);
        double current = currentStr.isEmpty() ? 0.0 : Double.parseDouble(currentStr);

        if (target > 50000000.0 || current > 50000000.0) {
            Toast.makeText(getContext(), "O valor máximo permitido é R$ 50.000.000,00", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (goalToEdit == null) {
                Goal newGoal = new Goal(name, target, current);
                AppDatabase.getInstance(getContext()).goalDao().insert(newGoal);
            } else {
                goalToEdit.name = name;
                goalToEdit.targetAmount = target;
                goalToEdit.currentAmount = current;
                AppDatabase.getInstance(getContext()).goalDao().update(goalToEdit);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Meta salva!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(AddGoalFragment.this).popBackStack();
                });
            }
        });
    }

    private void deleteGoal() {
        new AlertDialog.Builder(getContext())
                .setTitle("Excluir Meta")
                .setMessage("Deseja realmente excluir esta meta?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        if (goalToEdit != null) {
                            AppDatabase.getInstance(getContext()).goalDao().delete(goalToEdit);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    NavHostFragment.findNavController(AddGoalFragment.this).popBackStack();
                                });
                            }
                        }
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}