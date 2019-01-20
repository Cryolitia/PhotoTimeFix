package photoTimeFix

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.lincaiqi.PhotoTimeFix.R

class Fragment2 : Fragment() {

    private lateinit var editor : SharedPreferences.Editor
    private lateinit var coreK: CoreK

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_2,parent,false)
        coreK = CoreK(context!!)
        val preferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = preferences.edit()
        coreK.initFragment(view,preferences,editor)
        return view
    }

}
