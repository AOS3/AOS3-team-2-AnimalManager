package com.lion.a066ex_animalmanager

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.transition.MaterialSharedAxis
import com.lion.a066ex_animalmanager.databinding.ActivityMainBinding
import com.lion.a066ex_animalmanager.fragment.InputFragment
import com.lion.a066ex_animalmanager.fragment.MainFragment
import com.lion.a066ex_animalmanager.fragment.ReplaceFragment
import com.lion.a066ex_animalmanager.fragment.ShowFragment
import com.lion.a066ex_animalmanager.util.FragmentName

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    // 확인할 권한들
    val permissionList = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_MEDIA_LOCATION,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_MEDIA_IMAGES
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestPermissions(permissionList, 0)
        // 첫 화면을 설정한다.
        replaceFragment(FragmentName.MAIN_FRAGMENT, false, null)
    }

    // 프래그먼트를 교체하는 함수
    fun replaceFragment(fragmentName: FragmentName, isAddtoBackStack: Boolean, databundle: Bundle?){
        // 프래그먼트 객체
        val newFragment = when(fragmentName){
            FragmentName.MAIN_FRAGMENT -> MainFragment()
            FragmentName.INPUT_FRAGMENT -> InputFragment()
            FragmentName.SHOW_FRAGMENT -> ShowFragment()
            FragmentName.REPLACE_FRAGMENT -> ReplaceFragment()
        }

        // bundle 객체가 null이 아니라면
        if (databundle != null) {
            newFragment.arguments = databundle
        }

        // 프래그먼트 교체
        supportFragmentManager.commit {
            newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
            newFragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
            newFragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

            replace(R.id.fragmentContainerView, newFragment)
            if (isAddtoBackStack){
                addToBackStack(fragmentName.str)
            }
        }
    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: FragmentName){
        supportFragmentManager.popBackStack(fragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}