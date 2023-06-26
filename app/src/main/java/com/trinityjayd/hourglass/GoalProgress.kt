package com.trinityjayd.hourglass

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
        home.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val analytics = AnalyticsData()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val formattedDate = dateFormat.format(currentDate)

        val thirtyDaysAgo = Calendar.getInstance()
        thirtyDaysAgo.add(Calendar.DAY_OF_YEAR, -30)
        val formattedThirtyDaysAgo = dateFormat.format(thirtyDaysAgo.time)

        var progressValue: Float
        var minGoalValue: Float
        var maxGoalValue: Float

        analytics.hoursPerDay(formattedThirtyDaysAgo.toString(), formattedDate.toString()) { userEntries ->
            progressValue = analytics.sumHoursArr(userEntries).toFloat()

            analytics.getGoals { goals ->

                val minGoalTextView = findViewById<TextView>(R.id.minGoalTextView)
                val maxGoalTextView = findViewById<TextView>(R.id.maxGoalTextView)

                minGoalTextView.text = "Daily Minimum Goal: ${goals.first}h for 30 days : "
                maxGoalTextView.text = "Daily Maximum Goal: ${goals.second}h for 30 days : "

                // Once progress value is available, retrieve goals
                analytics.getMonthlyGoals { goals ->
                    minGoalValue = goals.first
                    maxGoalValue = goals.second

                    minGoalTextView.append("${minGoalValue}h")
                    maxGoalTextView.append("${maxGoalValue}h")

                    // Create pie chart with the retrieved values
                    createPieChart(progressValue, minGoalValue, maxGoalValue)
                }
            }

        }

    }

    private fun createPieChart(progressValue: Float, minGoalValue: Float, maxGoalValue: Float) {
        val progressEntry = PieEntry(progressValue, "Current Hours")
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
                val hours = value.toInt()
                val minutes = ((value - hours) * 60).toInt()
                val adjustedHours = hours + (minutes / 60)
                val adjustedMinutes = minutes % 60
                return String.format("%d:%02d", adjustedHours, adjustedMinutes)
            }
        }

        val data = PieData(dataSet)

        val pieChart: PieChart = findViewById(R.id.pieChart)
        // Configure the chart
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)


        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 14f
        pieChart.legend.textColor = Color.WHITE
        pieChart.legend.form = Legend.LegendForm.CIRCLE
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        pieChart.legend.xEntrySpace = 10f
        pieChart.legend.yEntrySpace = 10f
        pieChart.legend.yOffset = 10f
        pieChart.legend.xOffset = 10f
        pieChart.legend.setDrawInside(false)


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
