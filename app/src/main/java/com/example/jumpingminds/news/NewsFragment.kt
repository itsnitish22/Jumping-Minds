package com.example.jumpingminds.news

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jumpingminds.R
import com.example.jumpingminds.api.models.Articles
import com.example.jumpingminds.api.models.News
import com.example.jumpingminds.databinding.FragmentNewsBinding

class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding //binding
    private var adapter: RecyclerView.Adapter<NewsAdapter.ViewHolder>? = null //adapter
    private lateinit var viewModel: NewsViewModel //viewmodel
    private val newsList: ArrayList<Articles> = arrayListOf() //news
    private lateinit var progressDialog: ProgressDialog //progress dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false) //setting up binding
        viewModel =
            ViewModelProvider(requireActivity())[NewsViewModel::class.java] //setting view model
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireContext()) //setting recycler view
        progressDialog = ProgressDialog(requireContext()) //setting progress bar

        setHasOptionsMenu(true)

        //calling the function to GET news from API
        viewModel.getNews()
        //showing the progess dialog
        showProgressDialog("Getting News")

        //observing the changes in the variable which holds data, once we receive data, it is shown in recycler view
        viewModel.receivedNews.observe(requireActivity(), Observer { news ->
            if (news != null) {
                putNewsIntoArray(news)
                viewModel.saveFetchedNewsToDB(newsList)
            }
            if (binding.swipeRefreshLayout.isRefreshing)
                binding.swipeRefreshLayout.isRefreshing = false
        })

        //observing the status of job to hide the progress bar
        viewModel.jobCompleted.observe(requireActivity(), Observer { status ->
            if (status)
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
        })

        //swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getNews()
        }

        return binding.root
    }

    //options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //handling the press of sign out button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.restore -> moveToSavedNews()
            else -> super.onOptionsItemSelected(item)
        }
    }

    //moving to savedNewsFragment
    private fun moveToSavedNews(): Boolean {
        val action = NewsFragmentDirections.actionNewsFragmentToSavedNews()
        findNavController().navigate(action)
        return true
    }

    //putting data received in an array for sending to the adapter
    private fun putNewsIntoArray(news: News?) {
        if (news != null) {
            for (i in 0 until news.articles.size) {
                Log.i("NewsFragment", news.articles[i].toString())
                newsList.add(news.articles[i])
            }
            showInRecyclerView(newsList)
        }
    }

    //showing data in recycler view
    private fun showInRecyclerView(newsList: ArrayList<Articles>) {
        adapter = NewsAdapter(newsList)
        binding.recyclerview.adapter = adapter
    }

    //function to show progress dialog
    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

}