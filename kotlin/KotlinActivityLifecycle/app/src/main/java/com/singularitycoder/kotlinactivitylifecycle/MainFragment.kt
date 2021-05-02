package com.singularitycoder.kotlinactivitylifecycle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.singularitycoder.kotlinactivitylifecycle.databinding.FragmentMainBinding

// FOR CURRENT FRAGMENT
// 0. On App Start - onAttach, onCreate, onCreateView, onViewCreated, onActivityCreated, onStart, onResume
// 1. On Phone restart -
// 2. On Home button press - onPause, onStop
// 3. On App switcher press - onPause, onStop
// 4. On Selecting the App from App Switcher - onStart, onResume
// 5. On Back press - onPause, onStop, onDestroyView, onDestroy, onDetach
// 6. On Fragment launch (Fragment in foreground) - Fragment state and Activity state - onAttach, onCreate, onCreateView, onViewCreated, onActivityCreated, onStart, onResume
// 7. On App destroyed/closed - onDestroyView
// 8. On New App launched (New App in foreground) - onPause, onStop
// 9. On Notification drawer swiped from top - Nothing gets called.
// 10. On Fragment Added - (New Fragment on top of old Fragment) - Nothing gets called.
// 11. On Fragment Replaced - (New Fragment on top of old Fragment) - onPause, onStop, onDestroyView
// 12. On Fragment resurrected - (Replaced by a different fragment but got resumed) - onCreateView, onViewCreated, onActivityCreated, onStart, onResume
// 13. On Activity Launched - (New Activity on top of Fragment) - onPause, onStop

// FRAGMENT LIFECYCLE GIST
// 1. Fragment in foreground first time - onAttach, onCreate, onCreateView, onViewCreated, onActivityCreated, onStart, onResume
// 2. Fragment in foreground later times if added - Nothing gets called
// 2. Fragment in foreground later times if replaced - onCreateView, onViewCreated, onActivityCreated, onStart, onResume
// 3. Fragment in background - onPause, onStop
// 4. Fragment destroyed (Back Press/App clear) - onPause, onStop, onDestroyView, onDestroy, onDetach

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// Use the [MainFragment.newInstance] factory method to create an instance of this fragment.
class MainFragment : Fragment() {

    companion object {
        // Use this factory method to create a new instance of this fragment using the provided parameters.
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMainBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("View State: Fragment 1 onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("View State: Fragment 1 onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("View State: Fragment 1 onCreateView")
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("View State: Fragment 1 onViewCreated")

        if (null == activity) return

        binding.btnLaunchActivity2.setOnClickListener {
            AppUtils.launchActivity(context = context, activity = MainActivity2())
        }
        binding.btnAddFragment2.setOnClickListener {
            AppUtils.addFragment(
                activity = activity!!,
                bundle = null,
                parentLayout = R.id.container_activity_1,
                fragment = MainFragment2.newInstance("", ""),
                addOrReplace = "ADD"
            )
        }
        binding.btnReplaceFragment2.setOnClickListener {
            AppUtils.addFragment(
                activity = activity!!,
                bundle = null,
                parentLayout = R.id.container_activity_1,
                fragment = MainFragment2.newInstance("", ""),
                addOrReplace = "REPLACE"
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        println("View State: Fragment 1 onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        println("View State: Fragment 1 onStart")
    }

    override fun onResume() {
        super.onResume()
        println("View State: Fragment 1 onResume")
    }

    override fun onPause() {
        super.onPause()
        println("View State: Fragment 1 onPause")
    }

    override fun onStop() {
        super.onStop()
        println("View State: Fragment 1 onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("View State: Fragment 1 onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("View State: Fragment 1 onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        println("View State: Fragment 1 onDetach")
    }
}