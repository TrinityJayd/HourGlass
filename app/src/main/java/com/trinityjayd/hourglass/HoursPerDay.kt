package com.trinityjayd.hourglass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

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
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        // Customize Y-axis
        val yAxis = lineChart.axisLeft
        yAxis.setDrawGridLines(true)

        // Customize right Y-axis (optional)
        lineChart.axisRight.isEnabled = false

        // Set data and display the chart
        lineChart.data = generateLineData() // Implement this method to generate LineData
        lineChart.invalidate()
    }

    private fun generateLineData(): LineData {
        //val entries = ArrayList<Entry>()

        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val hoursWorked = doubleArrayOf(6.5, 7.2, 5.8, 8.1, 6.9, 4.5, 7.0)
        val minimumGoal = 5.0
        val maximumGoal = 8.0

        val entries = ArrayList<Entry>()

        for (i in hoursWorked.indices) {
            entries.add(Entry(i.toFloat(), hoursWorked[i].toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "Daily Hours Worked")
        lineDataSet.color = ColorTemplate.COLORFUL_COLORS[0]
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        return lineData
    }

}