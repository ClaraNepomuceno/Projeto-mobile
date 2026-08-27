package com.example.projeto.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.projeto.R;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.Transaction;
import com.example.projeto.databinding.FragmentTransactionsBinding;
import java.util.ArrayList;
import java.util.List;

public class TransactionFragment extends Fragment {
    private FragmentTransactionsBinding binding;
    private TransactionAdapter adapter;
    private String filterCategory = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        
        if (getArguments() != null) {
            filterCategory = getArguments().getString("categoryName");
            if (filterCategory != null) {
                binding.textFilterStatus.setText("Filtrando por: " + filterCategory);
                binding.textFilterStatus.setVisibility(View.VISIBLE);
            }
        }

        adapter = new TransactionAdapter();
        binding.recyclerTransactions.setAdapter(adapter);

        adapter.setOnTransactionClickListener(t -> {
            Bundle bundle = new Bundle();
            bundle.putInt("transactionId", t.id);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_add_transaction, bundle);
        });

        AppDatabase.getInstance(getContext()).transactionDao().getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (filterCategory != null) {
                List<Transaction> filteredList = new ArrayList<>();
                for (Transaction t : transactions) {
                    if (t.category != null && t.category.equals(filterCategory)) {
                        filteredList.add(t);
                    }
                }
                adapter.setTransactions(filteredList);
            } else {
                adapter.setTransactions(transactions);
            }
        });

        binding.fabAddTransaction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_add_transaction);
        });
        
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}