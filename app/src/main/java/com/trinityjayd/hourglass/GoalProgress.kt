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
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.trinityjayd.hourglass.dbmanagement.AnalyticsData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt


class GoalProgress : AppCompatActivity() {

    private lateinit var formattedThirtyDaysAgo: String
    private lateinit var formattedDate: String

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
        formattedDate = dateFormat.format(currentDate)

        val thirtyDaysAgo = Calendar.getInstance()
        //set the date to 30 days ago
        thirtyDaysAgo.add(Calendar.DAY_OF_YEAR, -29)
        formattedThirtyDaysAgo = dateFormat.format(thirtyDaysAgo.time)

        var progressValue: Float
        var minGoalValue: Float
        var maxGoalValue: Float


        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)
        loadingIndicator.show()

        analytics.getGoals { goals ->

            val minGoalTextView = findViewById<TextView>(R.id.minGoalTextView)
            val maxGoalTextView = findViewById<TextView>(R.id.maxGoalTextView)

            // If no goals are set, display 0h for 30 days
            if(goals.first == 0f && goals.second == 0f) {
                minGoalTextView.text = "Daily Minimum Goal: 0h for 30 days : "
                maxGoalTextView.text = "Daily Maximum Goal: 0h for 30 days : "
                createPieChart(0f, 0f, 0f)
                loadingIndicator.hide()
                return@getGoals
            }else{
                minGoalTextView.text = "Daily Minimum Goal: ${goals.first}h for 30 days : "
                maxGoalTextView.text = "Daily Maximum Goal: ${goals.second}h for 30 days : "

                analytics.getMonthlyGoals { goals ->
                    minGoalValue = goals.first
                    maxGoalValue = goals.second

                    //add the goal values for 30 days to the text views
                    minGoalTextView.append("${minGoalValue}h")
                    maxGoalTextView.append("${maxGoalValue}h")

                    analytics.hoursPerDay(
                        formattedThirtyDaysAgo,
                        formattedDate
                    ) { userEntries ->
                        progressValue = analytics.sumHoursArr(userEntries).toFloat()

                        // Create pie chart with the retrieved values
                        createPieChart(progressValue, minGoalValue, maxGoalValue)
                    }
                }
                loadingIndicator.hide()
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
                val minutes = ((value - hours) * 100).roundToInt()
                return String.format("%d:%02d", hours, minutes)
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


        pieChart.centerText = "Goal Progress\n$formattedThirtyDaysAgo - $formattedDate"
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.setCenterTextTypeface(android.graphics.Typeface.DEFAULT_BOLD)



        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.transparentCircleRadius = 0f
        pieChart.setDrawEntryLabels(false)
        pieChart.holeRadius = 70f

        // Refresh the chart
        pieChart.invalidate()
    }


}
