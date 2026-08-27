package com.example.projeto.ui.goals;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projeto.data.model.Goal;
import com.example.projeto.databinding.ItemGoalBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {
    private List<Goal> goals = new ArrayList<>();
    private OnGoalClickListener listener;

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
    }

    public GoalAdapter(OnGoalClickListener listener) {
        this.listener = listener;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoalBinding binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        
        holder.binding.textCurrent.setText(String.format(Locale.getDefault(), "Atual: R$ %.2f (%d%%)", goal.currentAmount, progress));
        holder.binding.textTarget.setText(String.format(Locale.getDefault(), "Meta: R$ %.2f", goal.targetAmount));

        holder.binding.imgEdit.setOnClickListener(v -> {
            if (listener != null) listener.onGoalClick(goal);
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemGoalBinding binding;
        ViewHolder(ItemGoalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}