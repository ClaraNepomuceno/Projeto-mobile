package com.example.projeto.ui.categories;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.projeto.R;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.Category;
import com.example.projeto.databinding.FragmentCategoriesBinding;
import java.util.concurrent.Executors;

public class CategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    private FragmentCategoriesBinding binding;
    private CategoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        
        adapter = new CategoryAdapter(this);
        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerCategories.setAdapter(adapter);

        AppDatabase.getInstance(getContext()).categoryDao().getAll().observe(getViewLifecycleOwner(), categories -> {
            adapter.setCategories(categories);
        });

        binding.btnAddCategory.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_add_category));
        
        return binding.getRoot();
    }

    @Override
    public void onCategoryClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putString("categoryName", category.name);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_category_detail, bundle);
    }

    @Override
    public void onEditClick(Category category) {
        Bundle bundle = new Bundle();
        bundle.putInt("categoryId", category.id);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_add_category, bundle);
    }

    @Override
    public void onDelete(Category category) {
        new AlertDialog.Builder(getContext())
                .setTitle("Excluir")
                .setMessage("Deseja excluir esta categoria?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getInstance(getContext()).categoryDao().delete(category);
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