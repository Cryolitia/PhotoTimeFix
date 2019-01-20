package photoTimeFix

import android.app.Activity
import android.os.Bundle
import tech.lincaiqi.photoTimeFix.R
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import  kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mFragmentList : MutableList<Fragment>
    private lateinit var mTitleList : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTitle()
        initFragment()
        mVp.adapter = PagerAdapter(supportFragmentManager, mFragmentList,mTitleList)
        mTb.setupWithViewPager(mVp)
    }

    private  fun initTitle() {
        mTitleList = ArrayList()
        mTitleList.add("批量操作")
        mTitleList.add("单文件操作")
        mTb.tabMode = TabLayout.MODE_FIXED
        mTb.addTab(mTb.newTab().setText(mTitleList[0]))
        mTb.addTab(mTb.newTab().setText(mTitleList[1]))
    }

    private  fun initFragment() {
        mFragmentList = ArrayList()
        mFragmentList.add(Fragment1())
    }

}