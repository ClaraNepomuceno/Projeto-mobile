package com.example.projeto.ui.goals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.projeto.R;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.model.Goal;
import com.example.projeto.databinding.FragmentGoalsBinding;

public class GoalsFragment extends Fragment implements GoalAdapter.OnGoalClickListener {
    private FragmentGoalsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoalsBinding.inflate(inflater, container, false);
        
        binding.recyclerGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        GoalAdapter adapter = new GoalAdapter(this);
        binding.recyclerGoals.setAdapter(adapter);

        AppDatabase.getInstance(getContext()).goalDao().getAll().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null && !goals.isEmpty()) {
                adapter.setGoals(goals);
                binding.textEmptyGoals.setVisibility(View.GONE);
            } else {
                adapter.setGoals(new java.util.ArrayList<>());
                binding.textEmptyGoals.setVisibility(View.VISIBLE);
            }
        });

        binding.btnAddGoal.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.nav_add_goal);
        });
        
        return binding.getRoot();
    }

    @Override
    public void onGoalClick(Goal goal) {
        Bundle bundle = new Bundle();
        bundle.putInt("goalId", goal.id);
        NavHostFragment.findNavController(this).navigate(R.id.nav_add_goal, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}