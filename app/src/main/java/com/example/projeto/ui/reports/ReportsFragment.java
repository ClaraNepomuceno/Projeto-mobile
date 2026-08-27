package com.example.projeto.ui.reports;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.projeto.data.AppDatabase;
import com.example.projeto.data.dao.TransactionDao;
import com.example.projeto.data.model.Category;
import com.example.projeto.databinding.FragmentReportsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsFragment extends Fragment {
    private FragmentReportsBinding binding;
    private Calendar currentCalendar;
    private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private final int GREEN = 0xFF00E676;
    private final int RED = 0xFFFF5252;
    
    private final Map<String, Integer> categoryColors = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        
        currentCalendar = Calendar.getInstance();
        updateMonthYearDisplay();

        // Carregar cores primeiro e então carregar os dados
        AppDatabase.getInstance(getContext()).categoryDao().getAll().observe(getViewLifecycleOwner(), categories -> {
            categoryColors.clear();
            for (Category c : categories) {
                try {
                    categoryColors.put(c.name, Color.parseColor(c.color));
                } catch (Exception e) {
                    categoryColors.put(c.name, Color.GRAY);
                }
            }
            loadReportData();
        });

        binding.btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateMonthYearDisplay();
            loadReportData();
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateMonthYearDisplay();
            loadReportData();
        });
        
        return binding.getRoot();
    }

    private void updateMonthYearDisplay() {
        String label = monthYearFormat.format(currentCalendar.getTime());
        binding.textMonthYear.setText(String.format("%s%s", label.substring(0, 1).toUpperCase(), label.substring(1)));
    }

    private void loadReportData() {
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long start = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long end = cal.getTimeInMillis();

        AppDatabase db = AppDatabase.getInstance(getContext());

        // Resumo (Somas Totais)
        db.transactionDao().getTotalIncomeInRange(start, end).observe(getViewLifecycleOwner(), income -> {
            double val = income != null ? income : 0.0;
            binding.textTotalIncome.setText(String.format(Locale.getDefault(), "R$ %.2f", val));
            updateBalanceSummary();
        });

        db.transactionDao().getTotalExpenseInRange(start, end).observe(getViewLifecycleOwner(), expense -> {
            double val = expense != null ? expense : 0.0;
            binding.textTotalExpense.setText(String.format(Locale.getDefault(), "R$ %.2f", val));
            updateBalanceSummary();
        });

        // Ganhos
        db.transactionDao().getIncomeByCategoryInRange(start, end).observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                setupPieChart(binding.pieChartIncome, list, "Ganhos", GREEN);
                binding.pieChartIncome.setVisibility(View.VISIBLE);
                binding.barChartIncome.setVisibility(View.VISIBLE);
                binding.textNoDataIncome.setVisibility(View.GONE);
            } else {
                binding.pieChartIncome.setVisibility(View.GONE);
                binding.barChartIncome.setVisibility(View.GONE);
                binding.textNoDataIncome.setVisibility(View.VISIBLE);
            }
        });
        db.transactionDao().getDailyCategorySpendingInRange("INCOME", start, end).observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) setupStackedBarChart(binding.barChartIncome, list, GREEN);
        });

        // Gastos
        db.transactionDao().getSpendingByCategoryInRange(start, end).observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                setupPieChart(binding.pieChartExpense, list, "Gastos", RED);
                binding.pieChartExpense.setVisibility(View.VISIBLE);
                binding.barChartExpense.setVisibility(View.VISIBLE);
                binding.textNoDataExpense.setVisibility(View.GONE);
            } else {
                binding.pieChartExpense.setVisibility(View.GONE);
                binding.barChartExpense.setVisibility(View.GONE);
                binding.textNoDataExpense.setVisibility(View.VISIBLE);
            }
        });
        db.transactionDao().getDailyCategorySpendingInRange("EXPENSE", start, end).observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) setupStackedBarChart(binding.barChartExpense, list, RED);
        });
    }

    private void updateBalanceSummary() {
        try {
            String incStr = binding.textTotalIncome.getText().toString().replace("R$ ", "").replace(",", ".");
            String expStr = binding.textTotalExpense.getText().toString().replace("R$ ", "").replace(",", ".");
            double income = Double.parseDouble(incStr);
            double expense = Double.parseDouble(expStr);
            double balance = income - expense;
            binding.textBalance.setText(String.format(Locale.getDefault(), "Saldo do Período: R$ %.2f", balance));
            binding.textBalance.setTextColor(balance >= 0 ? GREEN : RED);
        } catch (Exception ignored) {}
    }

    private void setupPieChart(PieChart chart, List<TransactionDao.CategorySpending> spendingList, String centerText, int textColor) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        
        boolean isExpense = centerText.equalsIgnoreCase("Gastos");
        
        for (int i = 0; i < spendingList.size(); i++) {
            TransactionDao.CategorySpending s = spendingList.get(i);
            entries.add(new PieEntry((float) s.value, s.name));
            
            if (isExpense) {
                // Paleta de vermelhos para despesas
                int alpha = 255 - (i * (150 / Math.max(1, spendingList.size())));
                colors.add(Color.argb(alpha, 255, 82, 82));
            } else {
                colors.add(getCategoryColor(s.name));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(textColor);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new CurrencyFormatter());

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.setCenterText(centerText);
        chart.setCenterTextColor(textColor);
        chart.setCenterTextSize(16f);
        chart.setHoleRadius(50f);
        
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYEntrySpace(5f);
        l.setWordWrapEnabled(true);
        
        chart.animateY(800);
        chart.invalidate();
    }

    private void setupStackedBarChart(BarChart chart, List<TransactionDao.DailyCategorySpending> dailyList, int textColor) {
        if (dailyList.isEmpty()) {
            chart.clear();
            return;
        }

        // Agrupar por data
        Map<Long, Map<String, Double>> groupedByDate = new LinkedHashMap<>();
        List<String> uniqueCategories = new ArrayList<>();
        
        for (TransactionDao.DailyCategorySpending s : dailyList) {
            if (!groupedByDate.containsKey(s.date)) {
                groupedByDate.put(s.date, new HashMap<>());
            }
            Map<String, Double> dayMap = groupedByDate.get(s.date);
            if (dayMap != null) {
                dayMap.put(s.name, s.value);
            }
            if (!uniqueCategories.contains(s.name)) uniqueCategories.add(s.name);
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        
        int xIndex = 0;
        for (Map.Entry<Long, Map<String, Double>> dateEntry : groupedByDate.entrySet()) {
            float[] values = new float[uniqueCategories.size()];
            for (int i = 0; i < uniqueCategories.size(); i++) {
                Double val = dateEntry.getValue().get(uniqueCategories.get(i));
                values[i] = val != null ? val.floatValue() : 0f;
            }
            entries.add(new BarEntry(xIndex++, values));
            xLabels.add(dayFormat.format(dateEntry.getKey()));
        }

        List<Integer> colors = new ArrayList<>();
        boolean isExpense = textColor == RED;
        
        for (int i = 0; i < uniqueCategories.size(); i++) {
            if (isExpense) {
                int alpha = 255 - (i * (150 / Math.max(1, uniqueCategories.size())));
                colors.add(Color.argb(alpha, 255, 82, 82));
            } else {
                colors.add(getCategoryColor(uniqueCategories.get(i)));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(textColor);
        dataSet.setValueTextSize(8f);
        dataSet.setValueFormatter(new CurrencyFormatter());

        BarData data = new BarData(dataSet);
        chart.setData(data);
        
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8f);

        chart.getAxisLeft().setValueFormatter(new CurrencyFormatter());
        chart.getAxisLeft().setTextSize(8f);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(800);
        chart.invalidate();
    }

    private static class CurrencyFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.format(Locale.getDefault(), "R$ %.2f", value);
        }
    }

    private int getCategoryColor(String categoryName) {
        Integer color = categoryColors.get(categoryName);
        return color != null ? color : (categoryName.equalsIgnoreCase("Geral") ? GREEN : Color.GRAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}