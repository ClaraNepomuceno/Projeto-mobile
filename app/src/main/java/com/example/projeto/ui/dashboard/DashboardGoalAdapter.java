package com.example.projeto.ui.dashboard;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projeto.data.model.Goal;
import com.example.projeto.databinding.ItemDashboardGoalBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardGoalAdapter extends RecyclerView.Adapter<DashboardGoalAdapter.ViewHolder> {
    private List<Goal> goals = new ArrayList<>();

    public DashboardGoalAdapter() {
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDashboardGoalBinding binding = ItemDashboardGoalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.binding.textGoalName.setText(goal.name);
        
        int progress = 0;
        if (goal.targetAmount > 0) {
            progress = (int) ((goal.currentAmount / goal.targetAmount) * 100);
        }
        holder.binding.progressGoal.setProgress(Math.min(progress, 100));
        holder.binding.textGoalPercent.setText(progress + "%");
        holder.binding.textGoalAmount.setText(String.format(Locale.getDefault(), "R$ %.0f / %.0f", goal.currentAmount, goal.targetAmount));
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDashboardGoalBinding binding;
        ViewHolder(ItemDashboardGoalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}