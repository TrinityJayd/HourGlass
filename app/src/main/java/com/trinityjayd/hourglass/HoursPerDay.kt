package com.trinityjayd.hourglass

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.trinityjayd.hourglass.dbmanagement.AnalyticsData
import java.util.Calendar

class HoursPerDay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours_per_day)

        val lineChart = findViewById<LineChart>(R.id.lineChart)

            // Customize chart appearance
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        // Customize X-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(getDays())
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        // Customize Y-axis
        val yAxis = lineChart.axisLeft
        yAxis.setDrawGridLines(true)

        // Customize right Y-axis (optional)
        lineChart.axisRight.isEnabled = false

        // Set data and display the chart
        lineChart.data = generateLineData() // Implement this method to generate LineData

        AnalyticsData().getGoals { goals ->
            val minimumGoal = goals.first.toFloat()
            val maximumGoal = goals.second.toFloat()

            // Update the axis minimum and maximum values here
            lineChart.axisLeft.axisMinimum = minimumGoal
            lineChart.axisLeft.axisMaximum = maximumGoal
        }



        lineChart.invalidate()

        val homeImageView = findViewById<ImageView>(R.id.homeImageView)
        homeImageView.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    private fun generateLineData(): LineData {
        val data = AnalyticsData()

        val entries = ArrayList<Entry>()
        data.hoursPerDay("Start", "End") { userEntries ->
            for (i in userEntries.indices) {
                entries.add(Entry(i.toFloat(), userEntries[i].toFloat()))
            }

        }

        val lineDataSet = LineDataSet(entries, "Hours Per Day")
        lineDataSet.color = ColorTemplate.COLORFUL_COLORS[0]
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawValues(false)

        return LineData(lineDataSet)
    }

    private fun getDays(): ArrayList<String> {
        val data = AnalyticsData()
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val datePair = data.getWeekDates(currentDate)

        return data.getDayNames(datePair)
    }


}