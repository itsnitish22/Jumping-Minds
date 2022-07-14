package com.example.jumpingminds.database

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jumpingminds.databinding.FragmentSavedNewsBinding
import com.example.jumpingminds.news.NewsViewModel

class SavedNews : Fragment() {
    private lateinit var binding: FragmentSavedNewsBinding
    private var adapter: RecyclerView.Adapter<SavedNewsAdapter.ViewHolder>? = null //adapter
    private lateinit var viewModel: NewsViewModel //viewmodel
    private val newsList: ArrayList<NewsEntity> = arrayListOf() //news
    private lateinit var progressDialog: ProgressDialog //progress dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        viewModel =
            ViewModelProvider(requireActivity())[NewsViewModel::class.java] //setting view model
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireContext()) //setting recycler view
        progressDialog = ProgressDialog(requireContext()) //setting progress bar

        //getting the news from db
        viewModel.newsListFromDB?.observe(requireActivity(), Observer { it ->
            Log.i("NoteLogging", it.toString())
            putNewsIntoArray(it)
        })

        return binding.root
    }

    //putting the news into List
    private fun putNewsIntoArray(news: List<NewsEntity>?) {
        if (news != null) {
            for (i in 0 until news.size) {
                newsList.add(news[i])
            }
        }
        showInRecyclerView(newsList)
    }

    //showing news in recycler view
    private fun showInRecyclerView(newsList: java.util.ArrayList<NewsEntity>) {
        adapter = SavedNewsAdapter(newsList)
        binding.recyclerview.adapter = adapter
    }

}