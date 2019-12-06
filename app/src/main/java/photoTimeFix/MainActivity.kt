package photoTimeFix

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import tech.lincaiqi.PhotoTimeFix.R
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS : Int = 1

    private lateinit var mFragmentList : MutableList<Fragment>
    private lateinit var mTitleList : MutableList<String>
    private lateinit var coreK : CoreK
    private lateinit var preferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        preferences = getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        coreK = CoreK(this,editor,this,preferences)
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
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.info,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            return when (item.itemId) {
                R.id.more -> {
                    coreK.showAbout()
                    true
                }
                R.id.experimentalFunctionMenu -> {
                    coreK.experimentalFunction()
                    true
                }
                R.id.test -> {
                    coreK.test()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(item)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private  fun initTitle() {
        mTitleList = ArrayList()
        mTitleList.add(getString(R.string.batchOperation))
        mTitleList.add(getString(R.string.singleFileOperation))
        mTb.tabMode = TabLayout.MODE_FIXED
        mTb.addTab(mTb.newTab().setText(mTitleList[0]))
        mTb.addTab(mTb.newTab().setText(mTitleList[1]))
    }

    private  fun initFragment() {
        mFragmentList = ArrayList()
        mFragmentList.add(Fragment1())
        mFragmentList.add(Fragment2())
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    AlertDialog.Builder(this)
                            .setTitle(getString(R.string.permissionDenied))
                            .setMessage(getString(R.string.shouldHavePermission))
                            .setPositiveButton(getString(R.string.OK)) { _, _ -> exitProcess(0) }
                            .create()
                            .show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
        }
    }

}