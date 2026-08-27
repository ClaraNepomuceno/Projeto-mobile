package com.example.projeto.ui.transactions.form;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.projeto.R;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.Category;
import com.example.projeto.data.model.Goal;
import com.example.projeto.data.model.Transaction;
import com.example.projeto.databinding.FragmentAddTransactionBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddTransactionFragment extends Fragment {
    private FragmentAddTransactionBinding binding;
    private int transactionId = -1;
    private Transaction currentTransaction;
    private Goal currentLinkedGoal;
    
    private List<String> categoryNames = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;

    private List<Goal> availableGoals = new ArrayList<>();
    private List<String> goalNames = new ArrayList<>();
    private ArrayAdapter<String> goalAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            transactionId = getArguments().getInt("transactionId", -1);
        }

        setupCategorySpinner();
        setupGoalSpinner();

        if (transactionId != -1) {
            binding.textTitle.setText("Editar Transação");
            binding.btnDelete.setVisibility(View.VISIBLE);
            loadTransactionData();
        }

        binding.btnAddCategory.setOnClickListener(v -> showNewCategoryDialog());
        binding.btnSave.setOnClickListener(v -> saveTransaction());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());

        return binding.getRoot();
    }

    private void loadTransactionData() {
        AppDatabase.getInstance(getContext()).transactionDao().getTransactionById(transactionId).observe(getViewLifecycleOwner(), t -> {
            if (t != null && currentTransaction == null) {
                currentTransaction = t;
                binding.editValue.setText(String.valueOf(t.value));
                binding.editDescription.setText(t.description);
                binding.spinnerCategory.setText(t.category, false);
                if (t.type.equals("INCOME")) {
                    binding.radioIncome.setChecked(true);
                } else {
                    binding.radioExpense.setChecked(true);
                }

                if (t.linkedGoalId != null) {
                    AppDatabase.getInstance(getContext()).goalDao().getGoalById(t.linkedGoalId).observe(getViewLifecycleOwner(), goal -> {
                        if (goal != null) {
                            currentLinkedGoal = goal;
                            binding.spinnerGoal.setText(goal.name, false);
                        }
                    });
                }
            }
        });
    }

    private void setupCategorySpinner() {
        categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
        binding.spinnerCategory.setAdapter(categoryAdapter);
        
        binding.spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            binding.layoutCategory.setError(null);
        });

        AppDatabase.getInstance(getContext()).categoryDao().getAll().observe(getViewLifecycleOwner(), categories -> {
            categoryNames.clear();
            for (Category c : categories) categoryNames.add(c.name);
            categoryAdapter.notifyDataSetChanged();
        });
    }

    private void setupGoalSpinner() {
        goalAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, goalNames);
        binding.spinnerGoal.setAdapter(goalAdapter);

        AppDatabase.getInstance(getContext()).goalDao().getAll().observe(getViewLifecycleOwner(), goals -> {
            availableGoals.clear();
            goalNames.clear();
            goalNames.add("Nenhuma Meta"); // Opção padrão
            for (Goal g : goals) {
                availableGoals.add(g);
                goalNames.add(g.name);
            }
            goalAdapter.notifyDataSetChanged();
        });
    }

    private void showNewCategoryDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_category, null);
        TextInputEditText editName = view.findViewById(R.id.edit_category_name);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group_category_type);

        new AlertDialog.Builder(getContext())
                .setTitle("Nova Categoria")
                .setView(view)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String name = editName.getText().toString();
                    String type = radioGroup.getCheckedRadioButtonId() == R.id.radio_category_income ? "INCOME" : "EXPENSE";

                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "O nome da categoria é obrigatório", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Executors.newSingleThreadExecutor().execute(() -> {
                        SharedPreferences prefs = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
                        int userId = prefs.getInt("user_id", -1);
                        
                        Category newCat = new Category(userId, name, "#00E676", type, "ic_menu_agenda", 0.0);
                        AppDatabase.getInstance(getContext()).categoryDao().insert(newCat);
                        
                        getActivity().runOnUiThread(() -> {
                            binding.spinnerCategory.setText(name, false);
                            binding.layoutCategory.setError(null);
                        });
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void saveTransaction() {
        String valStr = binding.editValue.getText().toString();
        String desc = binding.editDescription.getText().toString();
        String category = binding.spinnerCategory.getText().toString();
        String type = binding.radioIncome.isChecked() ? "INCOME" : "EXPENSE";
        String selectedGoalName = binding.spinnerGoal.getText().toString();

        binding.layoutCategory.setError(null);
        
        if (valStr.isEmpty()) {
            Toast.makeText(getContext(), "Preencha o valor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category.isEmpty()) {
            binding.layoutCategory.setError("A categoria é obrigatória");
            Toast.makeText(getContext(), "A categoria é obrigatória", Toast.LENGTH_SHORT).show();
            return;
        }

        double value = Double.parseDouble(valStr);
        if (value > 50000000.0) {
            Toast.makeText(getContext(), "O valor máximo permitido é R$ 50.000.000,00", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer linkedGoalId = null;
        
        // Encontrar a meta selecionada
        Goal selectedGoal = null;
        if (!selectedGoalName.equals("Nenhuma Meta")) {
            for (Goal g : availableGoals) {
                if (g.name.equals(selectedGoalName)) {
                    selectedGoal = g;
                    linkedGoalId = g.id;
                    break;
                }
            }
        }

        final Goal finalSelectedGoal = selectedGoal;
        final Integer finalLinkedGoalId = linkedGoalId;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getContext());
            
            if (transactionId == -1) {
                // Inserção
                Transaction t = new Transaction(value, desc, System.currentTimeMillis(), category, type, "Carteira", "", finalLinkedGoalId);
                db.transactionDao().insert(t);

                if (finalSelectedGoal != null) {
                    if (type.equals("INCOME")) {
                        finalSelectedGoal.currentAmount += value;
                    } else {
                        finalSelectedGoal.currentAmount -= value;
                    }
                    db.goalDao().update(finalSelectedGoal);
                }
            } else {
                // Edição
                // 1. Reverter impacto na meta antiga
                if (currentLinkedGoal != null) {
                    if (currentTransaction.type.equals("INCOME")) {
                        currentLinkedGoal.currentAmount -= currentTransaction.value;
                    } else {
                        currentLinkedGoal.currentAmount += currentTransaction.value;
                    }
                    db.goalDao().update(currentLinkedGoal);
                }

                // 2. Atualizar transação
                currentTransaction.value = value;
                currentTransaction.description = desc;
                currentTransaction.category = category;
                currentTransaction.type = type;
                currentTransaction.linkedGoalId = finalLinkedGoalId;
                db.transactionDao().update(currentTransaction);

                // 3. Aplicar impacto na meta nova
                if (finalSelectedGoal != null) {
                    // Se for a mesma meta, precisamos recarregar pois a `currentLinkedGoal` foi atualizada no passo 1
                    Goal goalToUpdate = finalSelectedGoal;
                    if (currentLinkedGoal != null && finalSelectedGoal.id == currentLinkedGoal.id) {
                         // Como estamos no mesmo thread e usamos `db.goalDao().update`, 
                         // a instância `finalSelectedGoal` pode estar desatualizada se for a mesma
                         goalToUpdate.currentAmount = currentLinkedGoal.currentAmount;
                    }
                    
                    if (type.equals("INCOME")) {
                        goalToUpdate.currentAmount += value;
                    } else {
                        goalToUpdate.currentAmount -= value;
                    }
                    db.goalDao().update(goalToUpdate);
                }
            }

            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Transação salva!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            });
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(getContext())
                .setTitle("Excluir Transação")
                .setMessage("Deseja realmente excluir esta transação?")
                .setPositiveButton("Sim", (dialog, which) -> deleteTransaction())
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteTransaction() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getContext());
            
            // Reverter impacto na meta
            if (currentTransaction.linkedGoalId != null && currentLinkedGoal != null) {
                if (currentTransaction.type.equals("INCOME")) {
                    currentLinkedGoal.currentAmount -= currentTransaction.value;
                } else {
                    currentLinkedGoal.currentAmount += currentTransaction.value;
                }
                db.goalDao().update(currentLinkedGoal);
            }

            db.transactionDao().delete(currentTransaction);

            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Transação excluída!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).popBackStack();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}