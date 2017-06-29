package com.exp.rb.reebot.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.exp.rb.reebot.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RBBookingManager.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RBBookingManager#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RBBookingManager extends Fragment {


    private static final String TAG = "ReeBot(RBBookingManager)";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "acesstoken";

    // TODO: Rename and change types of parameters
    private String email;
    private String acesstoken;

    private OnFragmentInteractionListener mListener;

    public RBBookingManager() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RBBookingManager.
     */
    // TODO: Rename and change types and number of parameters
    public static RBBookingManager newInstance(String param1, String param2) {
        RBBookingManager fragment = new RBBookingManager();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_PARAM1);
            acesstoken = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);

        Log.d(TAG,"Param1 : "+email + " Param2 :"+acesstoken);
        View v = localInflater.inflate(R.layout.fragment_rbbooking_manager, container, false);
        ;
        // Adapter 생성

        final ListView bookinglistview = (ListView) v.findViewById(R.id.bookinglist_list);
        RBBookingListViewAdapter bookinglistadapter = new RBBookingListViewAdapter(getContext(),bookinglistview,email,acesstoken);


        bookinglistadapter.setlistener(
        new RBBookingListViewAdapter.RemoveListener() {
            @Override
            public void onRemoveData(int position) {
                //Log.d(TAG,"onRemoveData : " + position );

            }
        });


        bookinglistview.setAdapter(bookinglistadapter);


        return v;





        //return inflater.inflate(R.layout.fragment_rbbooking_manager, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
