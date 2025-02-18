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
import com.abclab.abcereports.databinding.ReportFinalBinding
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportFinalActivity : Fragment() {
    private lateinit var binding: ReportFinalBinding
    private val listData = ArrayList<ResultData?>()
    private var lastIdx = 0
    private var rowAdded = 0
    private var aa: ReportArrayAdapter? = null
    private val gc: GlobalClass by lazy {
        requireContext().applicationContext as GlobalClass
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = activity!!.menuInflater
        inflater.inflate(R.menu.find_report_result_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        if (info.position < listData.size - 1) {
            val d = listData[info.position]
            when (item.itemId) {
                R.id.findRptResultFindRelated -> {
                    gc.findReportFilters.PatientName = d!!.patientName

                    val tabHost = view!!.parent.parent.parent as FragmentTabHost
                    gc.forceFindResult = true
                    tabHost.currentTab = 2
                    return true
                }

                R.id.findRptResultDownload -> {
                    gc.reportNo = d!!.reportNo
                    DownloadFile(activity!!)
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
            binding = ReportFinalBinding.inflate(inflater, container, false)
        registerForContextMenu(binding.reportFinalLV)
        binding.reportFinalLV.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(
                view: AbsListView,
                scrollState: Int
            ) {
                val threshold = 1
                val count = binding.reportFinalLV.count

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (binding.reportFinalLV.lastVisiblePosition >= count - threshold) {
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
        binding.reportFinalLV.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                if (position < listData.size - 1) {
                    val d = listData[position]
                    gc.reportNo = d!!.reportNo
                    DownloadFile(activity!!)
                }
            }
        return binding.root
    }

    fun populateData() {
        viewLifecycleOwner.lifecycleScope.launch {
            gc.showProgress(activity, "Loading Data", "Please Wait")
            try {
                try {
                    val result = withContext(Dispatchers.IO){
                        HttpClient(Android).use {
                            it.submitForm(
                                url = "https://www.abclab.com/eReportApple/Report/FindFinals",
                                formParameters = Parameters.build {
                                    append("branch", gc.getBranchId().toString())
                                    append("siteid", gc.siteId!!)
                                    append("username", gc.userId!!)
                                    append("hash", gc.hashCode!!)
                                    append("startrow", lastIdx.toString())
                                }
                            ).bodyAsText()
                        }
                    }
                    if (result.isNotEmpty()) {
                        gc.PopulateData(result, listData)
                    }
                    rowAdded = listData.size - lastIdx
                    lastIdx += rowAdded
                } catch (e: Exception) {
                    Log.d(getString(R.string.tag), "ERROR $e")
                }
            } catch (e: Exception) {
                Log.d(getString(R.string.tag), "ERROR $e")
            }
            updateList()
        }
    }

    private fun updateList() {
        aa = ReportArrayAdapter(activity, listData)
        binding.reportFinalLV.adapter = aa
        gc.hideProgress()
        if (listData.size > 0) {
            if (rowAdded > 0) {
                binding.reportFinalLV.post { binding.reportFinalLV.setSelection(lastIdx - rowAdded - 1) }
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
            binding.reportFinalLV.adapter = aa
        }
    }
}
