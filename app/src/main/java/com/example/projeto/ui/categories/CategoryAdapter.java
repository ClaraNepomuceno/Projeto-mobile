package com.example.projeto.ui.categories;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projeto.data.model.Category;
import com.example.projeto.databinding.ItemCategoryBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onEditClick(Category category);
        void onDelete(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category c = categories.get(position);
        holder.binding.textCategoryName.setText(c.name);
        
        String typeStr = c.type.equals("INCOME") ? "Receita" : "Despesa";
        if (c.budgetLimit > 0) {
            typeStr += " • Limite: R$ " + String.format(Locale.getDefault(), "%.2f", c.budgetLimit);
        }
        holder.binding.textCategoryType.setText(typeStr);
        
        // Carregar ícone
        try {
            int iconRes = Integer.parseInt(c.iconName);
            holder.binding.imgCategoryIcon.setImageResource(iconRes);
        } catch (Exception e) {
            holder.binding.imgCategoryIcon.setImageResource(android.R.drawable.ic_menu_agenda);
        }

        // Colorir o círculo
        Drawable background = ContextCompat.getDrawable(holder.itemView.getContext(), com.example.projeto.R.drawable.circle_background);
        if (background != null) {
            background.mutate().setColorFilter(Color.parseColor(c.color), PorterDuff.Mode.SRC_IN);
            holder.binding.iconContainer.setBackground(background);
        }

        holder.binding.imgEdit.setOnClickListener(v -> listener.onEditClick(c));
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(c));
        
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(c);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;
        ViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}