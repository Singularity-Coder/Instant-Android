package com.singularitycoder.kotlinactivitylifecycle

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// Use the [MainFragment.newInstance] factory method to create an instance of this fragment.
class MainFragment2 : Fragment() {

    companion object {
        // Use this factory method to create a new instance of this fragment using the provided parameters.
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var param1: String? = null
    private var param2: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("View State: Fragment 2 onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("View State: Fragment 2 onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("View State: Fragment 2 onCreateView")
        return inflater.inflate(R.layout.fragment_main2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("View State: Fragment 2 onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        println("View State: Fragment 2 onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        println("View State: Fragment 2 onStart")
    }

    override fun onResume() {
        super.onResume()
        println("View State: Fragment 2 onResume")
    }

    override fun onPause() {
        super.onPause()
        println("View State: Fragment 2 onPause")
    }

    override fun onStop() {
        super.onStop()
        println("View State: Fragment 2 onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("View State: Fragment 2 onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("View State: Fragment 2 onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        println("View State: Fragment 2 onDetach")
    }
}