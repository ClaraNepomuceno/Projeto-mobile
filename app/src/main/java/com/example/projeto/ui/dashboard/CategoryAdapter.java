package com.example.projeto.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projeto.databinding.ItemDashboardCategoryBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryItem> items = new ArrayList<>();

    public void setItems(List<CategoryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDashboardCategoryBinding binding = ItemDashboardCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        holder.binding.textCategoryName.setText(item.name);
        holder.binding.textCategoryValue.setText(String.format(Locale.getDefault(), "R$ %.2f", item.value));
        holder.binding.progressCategory.setProgress(item.percent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CategoryItem {
        String name;
        double value;
        int percent;

        public CategoryItem(String name, double value, int percent) {
            this.name = name;
            this.value = value;
            this.percent = percent;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDashboardCategoryBinding binding;
        ViewHolder(ItemDashboardCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}