package com.example.projeto.ui.transactions;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projeto.data.model.Transaction;
import com.example.projeto.databinding.ItemTransactionBinding;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        holder.binding.textDesc.setText(t.description);
        holder.binding.textCategory.setText(t.category);
        
        String prefix = t.type.equals("INCOME") ? "+ " : "- ";
        holder.binding.textValue.setText(prefix + "R$ " + String.format("%.2f", t.value));
        holder.binding.textValue.setTextColor(t.type.equals("INCOME") ? Color.parseColor("#00E676") : Color.parseColor("#FF5252"));

        holder.binding.imgEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTransactionBinding binding;
        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}