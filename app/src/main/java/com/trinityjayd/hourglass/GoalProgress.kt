package com.trinityjayd.hourglass

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.formatter.ValueFormatter
import com.trinityjayd.hourglass.dbmanagement.AnalyticsData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class GoalProgress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_progress)

        val home = findViewById<ImageView>(R.id.homeImageView)

        val analytics = AnalyticsData()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val formattedDate = dateFormat.format(currentDate)

        val thirtyDaysAgo = Calendar.getInstance()
        thirtyDaysAgo.add(Calendar.DAY_OF_YEAR, -30)
        val formattedThirtyDaysAgo = dateFormat.format(thirtyDaysAgo.time)

        var progressValue = 0f
        var minGoalValue = 0f
        var maxGoalValue = 0f

        analytics.hoursPerDay(formattedDate.toString(), formattedThirtyDaysAgo.toString()) { userEntries ->
            progressValue = userEntries.sum()

            // Once progress value is available, retrieve goals
            analytics.getGoals { goals ->
                minGoalValue = goals.first
                maxGoalValue = goals.second

                // Create pie chart with the retrieved values
                createPieChart(progressValue, minGoalValue, maxGoalValue)
            }
        }

    }

    fun createPieChart(progressValue: Float, minGoalValue: Float, maxGoalValue: Float) {
        val progressEntry = PieEntry(progressValue, "Current Progress")
        val minGoalEntry = PieEntry(minGoalValue, "Minimum Goal")
        val maxGoalEntry = PieEntry(maxGoalValue, "Maximum Goal")

        val dataSet = PieDataSet(listOf(progressEntry, minGoalEntry, maxGoalEntry), "")

        val progressColor = ContextCompat.getColor(this, R.color.green_square)
        val minColor = ContextCompat.getColor(this, R.color.pink_square)
        val maxColor = ContextCompat.getColor(this, R.color.blue_300)

        dataSet.colors = listOf(progressColor, minColor, maxColor)
        dataSet.setDrawValues(true)
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 16f
        dataSet.valueTypeface = android.graphics.Typeface.DEFAULT_BOLD

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val hours = value.toInt() / 60
                val minutes = value.toInt() % 60
                return String.format("%02d:%02d", hours, minutes)
            }
        }

        val data = PieData(dataSet)

        val pieChart: PieChart = findViewById(R.id.pieChart)
        // Configure the chart
        pieChart.data = data
        pieChart.description.isEnabled = false


        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 16f
        pieChart.legend.textColor = Color.WHITE
        pieChart.legend.form = Legend.LegendForm.CIRCLE
        pieChart.legend.setDrawInside(true)

        pieChart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        }

        pieChart.centerText = "Goal Progress\nhh:mm"
        pieChart.setCenterTextSize(18f)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.setCenterTextTypeface(android.graphics.Typeface.DEFAULT_BOLD)

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.transparentCircleRadius = 0f
        pieChart.setDrawEntryLabels(false)
        pieChart.holeRadius = 60f

        // Refresh the chart
        pieChart.invalidate()
    }






}
