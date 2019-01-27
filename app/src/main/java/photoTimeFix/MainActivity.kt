package photoTimeFix

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import tech.lincaiqi.PhotoTimeFix.R
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFragmentList : MutableList<Fragment>
    private lateinit var mTitleList : MutableList<String>
    private lateinit var coreK : CoreK
    private lateinit var preferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        preferences = getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        coreK = CoreK(this)
        if (preferences.getBoolean("ifFirst",true)) {
            coreK.showAbout()
            editor.putBoolean("ifFirst",false)
            editor.putInt("mode",R.id.radioButton)
            editor.apply()
        }
        initTitle()
        initFragment()
        mVp.adapter = PagerAdapter(supportFragmentManager, mFragmentList,mTitleList)
        mTb.setupWithViewPager(mVp)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.info,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        coreK.showAbout()
        return true
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
        mFragmentList.add(Fragment2())
    }

}
