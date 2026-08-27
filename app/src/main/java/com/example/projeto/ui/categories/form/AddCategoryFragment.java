package com.example.projeto.ui.categories.form;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projeto.R;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.Category;
import com.example.projeto.databinding.FragmentAddCategoryBinding;
import com.example.projeto.databinding.ItemIconPickerBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddCategoryFragment extends Fragment {
    private FragmentAddCategoryBinding binding;
    private int categoryId = -1;
    private Category currentCategory;
    private String selectedColor = "#00E676";
    private int selectedIconRes = android.R.drawable.ic_menu_agenda;
    
    private final String[] colors = {"#00E676", "#FF5252", "#2196F3", "#FFD600", "#9C27B0", "#FF9100", "#607D8B"};
    private final int[] icons = {
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_compass,
            android.R.drawable.ic_menu_directions,
            android.R.drawable.ic_menu_today,
            android.R.drawable.ic_menu_view,
            android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_info,
            android.R.drawable.ic_lock_lock
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            categoryId = getArguments().getInt("categoryId", -1);
        }

        setupColorPicker();
        setupIconPicker();
        setupPreview();

        if (categoryId != -1) {
            binding.btnSave.setText("Atualizar Categoria");
            binding.btnDelete.setVisibility(View.VISIBLE);
            loadCategoryData();
        }

        binding.btnSave.setOnClickListener(v -> saveCategory());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());

        return binding.getRoot();
    }

    private void loadCategoryData() {
        AppDatabase.getInstance(getContext()).categoryDao().getCategoryById(categoryId).observe(getViewLifecycleOwner(), category -> {
            if (category != null && currentCategory == null) {
                currentCategory = category;
                binding.editCategoryName.setText(category.name);
                binding.editBudget.setText(String.valueOf(category.budgetLimit));
                selectedColor = category.color;
                try {
                    selectedIconRes = Integer.parseInt(category.iconName);
                } catch (NumberFormatException e) {
                    selectedIconRes = android.R.drawable.ic_menu_agenda;
                }
                
                if ("INCOME".equals(category.type)) {
                    binding.toggleGroupType.check(R.id.btn_income);
                } else {
                    binding.toggleGroupType.check(R.id.btn_expense);
                }
                
                updatePreviewVisuals();
            }
        });
    }

    private void setupPreview() {
        binding.editCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.previewCard.textCategoryName.setText(s.toString().isEmpty() ? "Nome" : s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.toggleGroupType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String type = checkedId == R.id.btn_income ? "Receita" : "Despesa";
                binding.previewCard.textCategoryType.setText(type);
            }
        });

        updatePreviewVisuals();
    }

    private void updatePreviewVisuals() {
        binding.previewCard.imgCategoryIcon.setImageResource(selectedIconRes);
        Drawable bg = ContextCompat.getDrawable(getContext(), R.drawable.circle_background);
        if (bg != null) {
            bg.mutate().setColorFilter(Color.parseColor(selectedColor), PorterDuff.Mode.SRC_IN);
            binding.previewCard.iconContainer.setBackground(bg);
        }
    }

    private void setupColorPicker() {
        for (String colorHex : colors) {
            View colorView = new View(getContext());
            int size = (int) (32 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(16, 0, 16, 0);
            colorView.setLayoutParams(params);
            
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(Color.parseColor(colorHex));
            colorView.setBackground(shape);

            colorView.setOnClickListener(v -> {
                selectedColor = colorHex;
                updatePreviewVisuals();
            });
            binding.layoutColors.addView(colorView);
        }
    }

    private void setupIconPicker() {
        IconAdapter adapter = new IconAdapter(icons, iconRes -> {
            selectedIconRes = iconRes;
            updatePreviewVisuals();
        });
        binding.recyclerIcons.setAdapter(adapter);
    }

    private void saveCategory() {
        String name = binding.editCategoryName.getText().toString();
        String type = binding.toggleGroupType.getCheckedButtonId() == R.id.btn_income ? "INCOME" : "EXPENSE";
        String budgetStr = binding.editBudget.getText().toString();
        double budget = budgetStr.isEmpty() ? 0.0 : Double.parseDouble(budgetStr);

        if (budget > 50000000.0) {
            Toast.makeText(getContext(), "O valor máximo permitido é R$ 50.000.000,00", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!name.isEmpty()) {
            SharedPreferences prefs = getActivity().getSharedPreferences("Session", Context.MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);

            Executors.newSingleThreadExecutor().execute(() -> {
                if (categoryId == -1) {
                    Category c = new Category(userId, name, selectedColor, type, String.valueOf(selectedIconRes), budget);
                    AppDatabase.getInstance(getContext()).categoryDao().insert(c);
                } else if (currentCategory != null) {
                    currentCategory.name = name;
                    currentCategory.type = type;
                    currentCategory.color = selectedColor;
                    currentCategory.iconName = String.valueOf(selectedIconRes);
                    currentCategory.budgetLimit = budget;
                    AppDatabase.getInstance(getContext()).categoryDao().update(currentCategory);
                }
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Categoria salva!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(binding.getRoot()).popBackStack();
                });
            });
        } else {
            Toast.makeText(getContext(), "O nome da categoria é obrigatório", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Excluir Categoria")
                .setMessage("Deseja realmente excluir esta categoria?")
                .setPositiveButton("Sim", (dialog, which) -> deleteCategory())
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteCategory() {
        if (currentCategory != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(getContext()).categoryDao().delete(currentCategory);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Categoria excluída!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(binding.getRoot()).popBackStack();
                });
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Inner Adapter for Icons
    private static class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {
        private final int[] icons;
        private final OnIconClickListener listener;

        interface OnIconClickListener { void onIconClick(int iconRes); }

        IconAdapter(int[] icons, OnIconClickListener listener) {
            this.icons = icons;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemIconPickerBinding binding = ItemIconPickerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int iconRes = icons[position];
            holder.binding.imgIcon.setImageResource(iconRes);
            holder.itemView.setOnClickListener(v -> listener.onIconClick(iconRes));
        }

        @Override
        public int getItemCount() { return icons.length; }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ItemIconPickerBinding binding;
            ViewHolder(ItemIconPickerBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}