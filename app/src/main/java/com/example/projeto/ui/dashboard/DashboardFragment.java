package com.example.projeto.ui.dashboard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.projeto.R;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.databinding.FragmentDashboardBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private CategoryAdapter categoryAdapter;
    private DashboardGoalAdapter goalAdapter;

    private double currentIncome = 0;
    private double currentExpense = 0;
    private List<com.example.projeto.data.model.Goal> currentGoals = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        
        // Setup Categorias
        categoryAdapter = new CategoryAdapter();
        binding.recyclerCategories.setAdapter(categoryAdapter);

        // Setup Metas (Horizontal)
        goalAdapter = new DashboardGoalAdapter();
        binding.recyclerGoals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerGoals.setAdapter(goalAdapter);

        AppDatabase db = AppDatabase.getInstance(getContext());

        // Observar Receitas
        db.transactionDao().getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            currentIncome = income != null ? income : 0.0;
            binding.textIncome.setText(String.format(Locale.getDefault(), "R$ %.2f", currentIncome));
            updateBalance();
        });

        // Observar Despesas
        db.transactionDao().getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            currentExpense = expense != null ? expense : 0.0;
            binding.textExpense.setText(String.format(Locale.getDefault(), "R$ %.2f", currentExpense));
            updateBalance();
        });

        // Observar Gastos por Categoria Real
        db.transactionDao().getSpendingByCategory().observe(getViewLifecycleOwner(), spendingList -> {
            if (spendingList != null && !spendingList.isEmpty()) {
                List<CategoryAdapter.CategoryItem> items = new ArrayList<>();
                double totalExp = 0;
                for (com.example.projeto.data.dao.TransactionDao.CategorySpending s : spendingList) totalExp += s.value;

                for (com.example.projeto.data.dao.TransactionDao.CategorySpending s : spendingList) {
                    int percent = (int) ((s.value / totalExp) * 100);
                    items.add(new CategoryAdapter.CategoryItem(s.name, s.value, percent));
                }
                categoryAdapter.setItems(items);
            }
        });

        // Observar Metas
        db.goalDao().getAll().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null) {
                currentGoals = goals;
                goalAdapter.setGoals(goals);
                updateBalance();
            }
        });

        // Navegação por clique nos cards
        binding.textIncome.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_transactions));
        binding.textExpense.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_transactions));

        return binding.getRoot();
    }

    private void updateBalance() {
        double total = currentIncome - currentExpense;
        binding.textTotalBalance.setText(String.format(Locale.getDefault(), "R$ %.2f", total));
        binding.textTotalBalance.setTextColor(total >= 0 ? 0xFF00E676 : 0xFFFF5252);
        updateDetailedBalance();
    }

    private void updateDetailedBalance() {
        if (currentGoals == null || currentGoals.isEmpty()) {
            binding.textBalanceBreakdown.setVisibility(View.GONE);
            return;
        }

        double reservedInGoals = 0;
        for (com.example.projeto.data.model.Goal g : currentGoals) {
            reservedInGoals += g.currentAmount;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();

        // Linha Reservado
        appendStyledLine(builder, "• Reservado em metas: ", 
            String.format(Locale.getDefault(), "R$ %.2f", reservedInGoals), 
            0xFF757575);

        binding.textBalanceBreakdown.setText(builder);
        binding.textBalanceBreakdown.setVisibility(View.VISIBLE);
    }

    private void appendStyledLine(SpannableStringBuilder builder, String label, String value, int color) {
        builder.append(label);
        int start = builder.length();
        builder.append(value);
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(color), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}