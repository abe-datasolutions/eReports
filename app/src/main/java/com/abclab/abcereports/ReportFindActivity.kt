package com.abclab.abcereports

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar

class ReportFindActivity : Fragment() {
    private var gc: GlobalClass? = null

    private var calendar: Calendar? = null // = Calendar.getInstance();
    private var df: SimpleDateFormat? =
        null //(SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
    private var txtReportNo: EditText? = null
    private var txtPatientName: EditText? = null
    private var txtDateFrom: EditText? = null
    private var txtDateTo: EditText? = null
    private var txtCurrent: EditText? = null
    private var radGrpGender: RadioGroup? = null
    private var btnClearDtFrom: Button? = null
    private var btnClearDtTo: Button? = null
    private var btnSearch: Button? = null
    private var btnResetFilter: Button? = null
    private var imm: InputMethodManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.report_find, container, false)
        gc = (v.context.applicationContext as GlobalClass)
        df = SimpleDateFormat.getDateInstance(
            SimpleDateFormat.SHORT,
            gc!!.locale
        ) as SimpleDateFormat
        calendar = Calendar.getInstance(gc!!.locale)
        txtReportNo = v.findViewById<View>(R.id.txtFindRptRptNo) as EditText
        txtPatientName = v.findViewById<View>(R.id.txtFindRptPatName) as EditText
        radGrpGender = v.findViewById<View>(R.id.radFindRptGender) as RadioGroup
        txtDateFrom = v.findViewById<View>(R.id.txtFindRptDateFrom) as EditText
        txtDateTo = v.findViewById<View>(R.id.txtFindRptDateTo) as EditText
        btnClearDtFrom = v.findViewById<View>(R.id.btnFindRptDateFromClear) as Button
        btnClearDtTo = v.findViewById<View>(R.id.btnFindRptDateToClear) as Button
        btnSearch = v.findViewById<View>(R.id.btnFindRptSearch) as Button
        btnResetFilter = v.findViewById<View>(R.id.btnFindRptResetFilter) as Button

        imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        txtDateFrom!!.setOnClickListener {
            imm!!.hideSoftInputFromWindow(txtDateFrom!!.windowToken, 0)
            txtCurrent = txtDateFrom
            setDate()
        }
        txtDateFrom!!.isFocusable = false
        txtDateFrom!!.isFocusableInTouchMode = false
        txtDateTo!!.setOnClickListener {
            imm!!.hideSoftInputFromWindow(txtDateTo!!.windowToken, 0)
            txtCurrent = txtDateTo
            setDate()
        }
        txtDateTo!!.isFocusable = false
        txtDateTo!!.isFocusableInTouchMode = false

        btnClearDtFrom!!.setOnClickListener {
            txtDateFrom!!.setText(
                ""
            )
        }
        btnClearDtTo!!.setOnClickListener { txtDateTo!!.setText("") }
        btnSearch!!.setOnClickListener { doSearch() }
        btnResetFilter!!.setOnClickListener { resetFilter() }
        return v
    }

    private fun doSearch() {
        val selGender = radGrpGender!!.indexOfChild(
            activity!!.findViewById(
                radGrpGender!!.checkedRadioButtonId
            )
        )
        val filter = FindReportFilters()
        txtReportNo!!.setText(txtReportNo!!.text.toString().trim { it <= ' ' })
        txtPatientName!!.setText(txtPatientName!!.text.toString().trim { it <= ' ' })

        if (txtReportNo!!.text.length > 0) {
            filter.ReportNo = txtReportNo!!.text.toString()
        }
        if (txtPatientName!!.text.length > 0) {
            filter.PatientName = txtPatientName!!.text.toString()
        }
        when (selGender) {
            1 -> filter.Gender = "M"
            2 -> filter.Gender = "F"
            else -> {}
        }

        if (txtDateFrom!!.text.length > 0) {
            try {
                filter.DateFrom = df!!.parse(txtDateFrom!!.text.toString()).time
            } catch (e: ParseException) {
                Log.d(getString(R.string.tag), "ERROR $e")
            }
        }
        if (txtDateTo!!.text.length > 0) {
            try {
                filter.DateTo = df!!.parse(txtDateTo!!.text.toString()).time
            } catch (e: ParseException) {
                Log.d(getString(R.string.tag), "ERROR $e")
            }
        }
        gc!!.findReportFilters = filter
        val i = Intent(activity, ReportFindResultActivity::class.java)
        startActivityForResult(i, 1)
    }

    private fun resetFilter() {
        txtReportNo!!.setText("")
        txtPatientName!!.setText("")
        (radGrpGender!!.getChildAt(0) as RadioButton).isChecked = true
        txtDateFrom!!.setText("")
        txtDateTo!!.setText("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(getString(R.string.tag), "Application Exit")
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val patName = data.getStringExtra("PATNAME")
            if (patName.length > 0) {
                txtPatientName!!.setText(patName)
                doSearch()
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (gc!!.forceFindResult) {
            resetFilter()
            txtPatientName!!.setText(gc!!.findReportFilters.PatientName)
            doSearch()
        }
        gc!!.forceFindResult = false
    }

    fun setDate() {
        try {
            calendar!!.time = df!!.parse(txtCurrent!!.text.toString())
        } catch (e: ParseException) {
            Log.d(getString(R.string.tag), "ERROR $e")
        }

        DatePickerDialog(
            activity, onDateSel,
            calendar!![Calendar.YEAR], calendar!![Calendar.MONTH], calendar!![Calendar.DAY_OF_MONTH]
        ).show()
    }

    var onDateSel: OnDateSetListener =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            if (txtCurrent != null) {
                val c = Calendar.getInstance()
                c[year, monthOfYear] = dayOfMonth

                txtCurrent!!.setText(DateFormat.format(gc!!.dateFormat, c.time))
            }
        }
}
