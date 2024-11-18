package com.lion.a066ex_animalmanager.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
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

    // Toolbar를 구성하는 메서드
    fun settingToolbar(){
        fragmentMainBinding.apply {
            // 타이틀 중앙정렬
            materialToolbarMain.isTitleCentered = true
            // 타이틀
            materialToolbarMain.title = "동물 목록"
        }
    }

    // fab를 구성하는 메서드
    fun settingFAB(){
        fragmentMainBinding.apply {
            fabMain.setOnClickListener {
                // InputFragment로 이동한다.
                mainActivity.replaceFragment(FragmentName.INPUT_FRAGMENT, true, null)
            }
        }
    }

    // 동물 정보를 가져와 RecyclerView를 갱신하는 메서드
    fun refreshRecyclerView(){
        // 동물 정보를 가져온다.
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO){
                AnimalRepository.selectAnimalDataAll(mainActivity)
            }
            animalList = work1.await()
            // RecyclerView를 갱신한다.
            fragmentMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
        }
    }

    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentMainBinding.apply {
            // 어뎁터
            recyclerViewMain.adapter = RecyclerViewMainAdapter()
            // LayoutManager
            recyclerViewMain.layoutManager = LinearLayoutManager(mainActivity)
            // 구분선
            val deco = DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL)
            recyclerViewMain.addItemDecoration(deco)
        }
    }

    // RecyclerView의 어뎁터
    inner class RecyclerViewMainAdapter() : RecyclerView.Adapter<RecyclerViewMainAdapter.ViewMainViewHolder>(){
        // ViewHolder
        inner class ViewMainViewHolder(var recyclerRowBinding: RecyclerRowBinding): RecyclerView.ViewHolder(recyclerRowBinding.root), OnClickListener, OnLongClickListener{
            var isLongClick = false
            override fun onClick(v: View?) {
                isLongClick = false
                // 사용자가 누른 동물의 동물 번호를 담아준다.
                val dataBundle = Bundle()
                dataBundle.putInt("animalIdx", animalList[adapterPosition].animalIdx)
                // ShowFragment로 이동한다.
                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT, true, dataBundle)
            }

            override fun onLongClick(v: View?): Boolean {
                if (isLongClick == false){
                    isLongClick = true
                    CoroutineScope(Dispatchers.Main).launch {
                        val work1 = async(Dispatchers.IO){
                            AnimalRepository.selectAnimalDataByIdx(mainActivity, animalList[adapterPosition].animalIdx)
                        }
                        val animalViewModel = work1.await()

                        animalViewModel.animalImage?.let { uriString ->
                            val uri = android.net.Uri.parse(uriString)
                            recyclerRowBinding.imageViewRow.setImageURI(uri)
                        }
                    }
                } else {
                    isLongClick = false
                    recyclerRowBinding.imageViewRow.setImageResource(android.R.color.transparent)
                }
                return true
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewMainViewHolder {
            val recyclerRowBinding = RecyclerRowBinding.inflate(layoutInflater, parent, false)
            val viewMainViewHolder = ViewMainViewHolder(recyclerRowBinding)

            recyclerRowBinding.root.setOnClickListener(viewMainViewHolder)
            recyclerRowBinding.root.setOnLongClickListener(viewMainViewHolder)

            return viewMainViewHolder
        }

        override fun getItemCount(): Int {
            return animalList.size
        }

        override fun onBindViewHolder(holder: ViewMainViewHolder, position: Int) {
            holder.recyclerRowBinding.textViewRowName.text = animalList[position].animalName
            holder.recyclerRowBinding.textViewRowType.text = animalList[position].animalType.str
            holder.recyclerRowBinding.textViewRowAge.text = "${animalList[position].animalAge}살"
        }
    }
}