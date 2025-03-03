package com.abclab.abcereports

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTabHost
import androidx.lifecycle.lifecycleScope
import com.abclab.abcereports.databinding.ReportPendingBinding
import com.abedatasolutions.ereports.core.common.datetime.LocalDatePattern
import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.reports.ReportsApi
import com.abedatasolutions.ereports.core.models.reports.ReportStatus
import com.abedatasolutions.ereports.core.models.reports.ReportsQuery
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.ext.android.inject

class ReportPendingActivity : Fragment() {
    private lateinit var binding: ReportPendingBinding
    private val listData = ArrayList<ResultData?>()
    private var lastIdx = 0
    private var rowAdded = 0
    private var aa: ReportArrayAdapter? = null
    private val gc: GlobalClass by lazy {
        requireContext().applicationContext as GlobalClass
    }
    private val api by inject<ReportsApi>()

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.find_report_result_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        if (info.position < listData.size - 1) {
            val d = listData[info.position]
            when (item.itemId) {
                R.id.findRptResultFindRelated -> {
                    gc.findReportFilters.PatientName = d!!.patientName

                    val tabHost = requireView().parent.parent.parent as FragmentTabHost
                    gc.forceFindResult = true
                    tabHost.currentTab = 2
                    return true
                }

                R.id.findRptResultDownload -> {
                    gc.reportNo = d!!.reportNo
                    DownloadFile(requireActivity())
                    return true
                }

                else -> return super.onContextItemSelected(item)
            }
        } else {
            return super.onContextItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized)
            binding = ReportPendingBinding.inflate(inflater, container, false)
        registerForContextMenu(binding.reportPendingLV)

        binding.reportPendingLV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(
                view: AbsListView,
                scrollState: Int
            ) {
                val threshold = 1
                val count = binding.reportPendingLV.count

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (binding.reportPendingLV.lastVisiblePosition >= count - threshold) {
                        populateData()
                    }
                }
            }

            override fun onScroll(
                view: AbsListView, firstVisibleItem: Int,
                visibleItemCount: Int, totalItemCount: Int
            ) {
            }
        })
        binding.reportPendingLV.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                if (position < listData.size - 1) {
                    val d = listData[position]
                    gc.reportNo = d!!.reportNo
                    DownloadFile(requireActivity())
                }
            }
        return binding.root
    }

    fun populateData() {
        viewLifecycleOwner.lifecycleScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Logger.recordException(throwable)
                gc.hideProgress()
            }
        ) {
            gc.showProgress(activity, "Loading Data", "Please Wait")
            try {
                try {
                    val reports = withContext(Dispatchers.IO){
                        api.getReports(
                            ReportsQuery(
                                startRowIndex = listData.takeUnless {
                                    it.isEmpty()
                                }?.lastIndex ?: 0,
                                maxRows = 10,
                                status = ReportStatus.PARTIAL
                            )
                        )
                    }
                    if (reports.isNotEmpty()){
                        if (listData.isNotEmpty()) {
                            listData.removeAt(listData.lastIndex)
                        }
                        reports.forEach { report ->
                            val nData = ResultData()
                            nData.reportNo = report.accession
                            nData.patientName = report.patientName
                            nData.gender = report.gender
                            nData.reportDate = report.reportDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.let {
                                LocalDatePattern.Soap.formatter.format(it.date)
                            } ?: ""
                            nData.status = report.status.name
                            listData.add(nData)
                        }
                        val lData = ResultData()
                        lData.reportNo = "LAST"
                        lData.patientName = ""
                        lData.gender = ""
                        lData.reportDate = ""
                        lData.status = ""
                        listData.add(lData)
                    }
                } catch (e: Exception) {
                    Log.d(getString(R.string.tag), "ERROR $e")
                }
            } catch (e: Exception) {
                Log.d(getString(R.string.tag), "ERROR $e")
            }
            updateList()
        }
    }

    private fun updateList(){
        aa = ReportArrayAdapter(activity, listData)
        binding.reportPendingLV.adapter = aa
        gc.hideProgress()
        if (listData.size > 0) {
            if (rowAdded > 0) {
                binding.reportPendingLV.post { binding.reportPendingLV.setSelection(lastIdx - rowAdded - 1) }
                Toast.makeText(
                    activity,
                    getString(R.string.menuLongPressOption),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    activity,
                    getString(R.string.noAddRecordFound),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(activity, getString(R.string.noRecordFound), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (lastIdx == 0) {
            populateData()
        } else if (listData.isNotEmpty()) {
            aa = ReportArrayAdapter(activity, listData)
            binding.reportPendingLV.adapter = aa
        }
    }
}