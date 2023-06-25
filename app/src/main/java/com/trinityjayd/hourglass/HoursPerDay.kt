package com.trinityjayd.hourglass

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.trinityjayd.hourglass.dbmanagement.AnalyticsData

class HoursPerDay : AppCompatActivity() {

    private var data = AnalyticsData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours_per_day)

        val barChart = findViewById<BarChart>(R.id.barChart)

        // Customize chart appearance
        barChart.description.isEnabled = false
        val chartBGColor = ContextCompat.getColor(this, R.color.light_background)
        barChart.setBackgroundColor(chartBGColor)
        barChart.setTouchEnabled(true)
        barChart.setPinchZoom(true)
        barChart.legend.textSize = 14f
        barChart.legend.textColor = Color.WHITE
        barChart.setExtraOffsets(10f, 10f, 10f, 15f)


        // Customize X-axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 14f
        xAxis.granularity = 1f

        // Customize Y-axis
        val yAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.textColor = Color.WHITE
        yAxis.textSize = 14f

        // Customize right Y-axis (optional)
        barChart.axisRight.isEnabled = false

        AnalyticsData().getGoals { goals ->
            val minimumGoal = goals.first
            val maximumGoal = goals.second

            // Update the axis minimum and maximum values here
            barChart.axisLeft.axisMinimum = 0f
            barChart.axisLeft.axisMaximum = maximumGoal

            val minimumGoalLine = LimitLine(minimumGoal, "Minimum Goal Per Day")
            minimumGoalLine.textColor = Color.WHITE
            minimumGoalLine.textSize = 14f
            minimumGoalLine.lineWidth = 2f
            val minLineColor = ContextCompat.getColor(this, R.color.pink_square)
            minimumGoalLine.lineColor = minLineColor

            val maximumGoalLine = LimitLine(maximumGoal, "Maximum Goal Per Day")
            maximumGoalLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
            maximumGoalLine.textColor = Color.WHITE
            maximumGoalLine.textSize = 14f
            maximumGoalLine.lineWidth = 2f
            val maxLineColor = ContextCompat.getColor(this, R.color.teal_400)
            minimumGoalLine.lineColor = maxLineColor


            yAxis.addLimitLine(minimumGoalLine)
            yAxis.addLimitLine(maximumGoalLine)
        }

        // Set data and display the chart
        generateBarData { barData ->
            barChart.data = barData
            val xAxisLabels = data.getDayLabels()
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            xAxis.labelCount = xAxisLabels.size
            barChart.invalidate()
        }

        val homeImageView = findViewById<ImageView>(R.id.homeImageView)
        homeImageView.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    private fun generateBarData(callback: (BarData) -> Unit) {
        data.hoursPerDay("05/06/2023", "10/06/2023") { userEntries ->
            val entries = ArrayList<BarEntry>()
            for (i in userEntries.indices) {
                entries.add(BarEntry(i.toFloat(), userEntries[i]))
            }

            val barDataSet = BarDataSet(entries, "Time Per Day hh:mm")
            val barColor = ContextCompat.getColor(this, R.color.blue_300)
            barDataSet.color = barColor
            barDataSet.valueTextColor = Color.WHITE


            val barData = BarData(barDataSet)
            callback(barData)
        }
    }

}
