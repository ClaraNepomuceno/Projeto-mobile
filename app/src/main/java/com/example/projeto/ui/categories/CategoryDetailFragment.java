package com.example.projeto.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.Transaction;
import com.example.projeto.databinding.FragmentCategoryDetailBinding;
import com.example.projeto.ui.transactions.TransactionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryDetailFragment extends Fragment {
    private FragmentCategoryDetailBinding binding;
    private TransactionAdapter adapter;
    private String categoryName;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryDetailBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName");
        }

        binding.textCategoryTitle.setText(categoryName);

        adapter = new TransactionAdapter();
        binding.recyclerCategoryTransactions.setAdapter(adapter);

        loadTransactions();

        return binding.getRoot();
    }

    private void loadTransactions() {
        AppDatabase.getInstance(getContext()).transactionDao().getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            List<Transaction> filtered = new ArrayList<>();
            double total = 0;
            for (Transaction t : transactions) {
                if (t.category != null && t.category.equals(categoryName)) {
                    filtered.add(t);
                    total += t.value;
                }
            }
            adapter.setTransactions(filtered);
            binding.textTotalSpent.setText(String.format(Locale.getDefault(), "Total: R$ %.2f", total));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}