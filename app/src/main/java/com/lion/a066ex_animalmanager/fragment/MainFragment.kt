package com.lion.a066ex_animalmanager.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener

import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lion.a066ex_animalmanager.MainActivity
import com.lion.a066ex_animalmanager.databinding.FragmentMainBinding
import com.lion.a066ex_animalmanager.databinding.RecyclerRowBinding
import com.lion.a066ex_animalmanager.repository.AnimalRepository
import com.lion.a066ex_animalmanager.util.FragmentName
import com.lion.a066ex_animalmanager.viewmodel.AnimalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    var animalList = mutableListOf<AnimalViewModel>()

    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        settingToolbar()
        settingFAB()
        settingRecyclerView()
        refreshRecyclerView()
        return fragmentMainBinding.root
    }

    fun settingToolbar(){
        fragmentMainBinding.apply {
            materialToolbarMain.isTitleCentered = true
            materialToolbarMain.title = "동물 목록"
        }
    }

    fun settingFAB(){
        fragmentMainBinding.apply {
            fabMain.setOnClickListener {
                mainActivity.replaceFragment(FragmentName.INPUT_FRAGMENT, true, null)
            }
        }
    }

    fun refreshRecyclerView(){
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO){
                AnimalRepository.selectAnimalDataAll(mainActivity)
            }
            animalList = work1.await()
            fragmentMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
        }
    }

    fun settingRecyclerView(){
        fragmentMainBinding.apply {
            recyclerViewMain.adapter = RecyclerViewMainAdapter()
            recyclerViewMain.layoutManager = LinearLayoutManager(mainActivity)

            val deco = DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL)
            recyclerViewMain.addItemDecoration(deco)
        }
    }

    inner class RecyclerViewMainAdapter() : RecyclerView.Adapter<RecyclerViewMainAdapter.ViewMainViewHolder>(){
        inner class ViewMainViewHolder(var recyclerRowBinding: RecyclerRowBinding): RecyclerView.ViewHolder(recyclerRowBinding.root), OnClickListener{
            override fun onClick(v: View?) {
                val dataBundle = Bundle()
                dataBundle.putInt("animalIdx", animalList[adapterPosition].animalIdx)
                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, dataBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewMainViewHolder {
            val recyclerRowBinding = RecyclerRowBinding.inflate(layoutInflater , parent, false)
            val viewMainViewHolder = ViewMainViewHolder(recyclerRowBinding)

            recyclerRowBinding.root.setOnClickListener(viewMainViewHolder)

            return viewMainViewHolder
        }

        override fun getItemCount(): Int {
            return animalList.size
        }

        override fun onBindViewHolder(holder: ViewMainViewHolder, position: Int) {
            holder.recyclerRowBinding.textViewRowName.text = animalList[position].animalName
            holder.recyclerRowBinding.textViewRowType.text = animalList[position].animalType.str
        }
    }
}