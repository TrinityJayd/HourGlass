package com.trinityjayd.hourglass

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.trinityjayd.hourglass.dbmanagement.AnalyticsData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class HoursPerDay : AppCompatActivity() {

    private var data = AnalyticsData()
    private lateinit var startDateButton: Button
    private lateinit var endDateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours_per_day)


        dateButtons()

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
        yAxis.granularity = 1f

        // Customize right Y-axis (optional)
        barChart.axisRight.isEnabled = false

        val loadingIndicator = findViewById<CircularProgressIndicator>(R.id.loadingIndicator)

        val refreshButton = findViewById<ImageView>(R.id.refreshButton)



        refreshButton.setOnClickListener {
            loadingIndicator.show()
            AnalyticsData().getGoals { goals ->
                val minimumGoal = goals.first
                val maximumGoal = goals.second

                if (minimumGoal == 0f && maximumGoal == 0f) {
                    Toast.makeText(this, "Please set goals to use this chart.", Toast.LENGTH_LONG)
                        .show()
                    loadingIndicator.hide()
                    return@getGoals
                } else {
                    // Update the axis minimum and maximum values here
                    barChart.axisLeft.axisMinimum = 0f
                    barChart.axisLeft.axisMaximum = maximumGoal + 1

                    val minimumGoalLine = LimitLine(minimumGoal, "Minimum Goal Per Day")
                    minimumGoalLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
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

                    // Set data and display the chart
                    generateBarData { barData ->
                        barChart.data = barData
                        val xAxisLabels = data.getDayLabels()
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                        xAxis.labelCount = xAxisLabels.size
                        barChart.invalidate()
                    }
                    loadingIndicator.hide()
                }


            }
        }

        loadingIndicator.show()
        refreshButton.callOnClick()
        loadingIndicator.hide()



        val homeImageView = findViewById<ImageView>(R.id.homeImageView)
        homeImageView.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }

    private fun generateBarData(callback: (BarData) -> Unit) {

        var startDate = startDateButton.text.toString()
        var endDate = endDateButton.text.toString()

        if (startDate == "Start Date" && endDate == "End Date") {
            data.hoursPerDay("Start", "End") { userEntries ->
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
        }else if (startDate == endDate) {
            Toast.makeText(this, "Please select a date range of 7 days.", Toast.LENGTH_LONG).show()
            return
        } else {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDateFormatted = dateFormat.parse(startDate)
            val endDateFormatted = dateFormat.parse(endDate)

            if(startDateFormatted.after(endDateFormatted)){
                Toast.makeText(this, "Please select a valid date range.", Toast.LENGTH_LONG).show()
                return
            }

            //get the difference between the two dates in days
            val difference = endDateFormatted.time - startDateFormatted.time
            val daysBetween = TimeUnit.MILLISECONDS.toDays(difference).toInt()

            if (daysBetween != 7) {
                Toast.makeText(this, "Please select a date range of 7 days.", Toast.LENGTH_LONG)
                    .show()
                return
            } else {
                data.hoursPerDay(startDate, endDate) { userEntries ->
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

    }


    //code start date and end date button
    private fun dateButtons() {
        //Code Attribution
        //Author: Coding Demos
        //Date: 22/02/2018
        //https://www.codingdemos.com/android-datepicker-button/
        startDateButton = findViewById(R.id.startDateButton)
        startDateButton.setOnClickListener(View.OnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@HoursPerDay, { datePicker, year, month, day ->
                    startDateButton.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })

        endDateButton = findViewById(R.id.endDateButton)
        endDateButton.setOnClickListener(View.OnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@HoursPerDay, { datePicker, year, month, day ->
                    endDateButton.text = day.toString() + "/" + (month + 1) + "/" + year
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        })
    }


}
