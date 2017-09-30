package com.kaamel.simpletwitterclient;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kaamel.simpletwitterclient.databinding.FragmentCompseTweetDialogBinding;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTweetComposerUpdateListener} interface
 * to handle interaction events.
 * Use the {@link Comp0seTweetDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Comp0seTweetDialogFragment extends DialogFragment {

    FragmentCompseTweetDialogBinding binding;
    private static final String BODY = "body";

    // TODO: Rename and change types of parameters
    private String body;
    EditText etCompose;
    TextView tvCount;
    ImageButton ibCancel;
    Button btTweet;

    private OnTweetComposerUpdateListener mListener;

    public Comp0seTweetDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param body the body of the tweet (optional)
     * @return A new instance of fragment CompseTweetDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Comp0seTweetDialogFragment newInstance(String body) {
        Comp0seTweetDialogFragment fragment = new Comp0seTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString(BODY, body);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            body = getArguments().getString(BODY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compse_tweet_dialog, null, false);
        ibCancel = binding.ibCancel;
        etCompose = binding.etCompose;
        tvCount = binding.tvCount;
        btTweet = binding.btTweet;
        if (body != null && body.length() > 0) {
            etCompose.setText(body);
            int count = 140-body.length();
            tvCount.setText(String.valueOf(count));
            if (count <=7)
                tvCount.setTextColor(Color.RED);
            else
                tvCount.setTextColor(Color.BLACK);
        }
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = 140-etCompose.getText().length();
                tvCount.setText(String.valueOf(count));
                if (count <=7)
                    tvCount.setTextColor(Color.RED);
                else
                    tvCount.setTextColor(Color.BLACK);
            }
        });
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUpdate(-1, null);
                }
                dismiss();
            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUpdate(1, etCompose.getText().toString());
                }
                dismiss();
            }
        });
        return binding.getRoot();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onTweetButtonPressed(View view) {
        if (mListener != null) {
            mListener.onUpdate(1, etCompose.getText().toString());
        }
    }

    public void onCancellButtonPressed(View view) {
        if (mListener != null) {
            mListener.onUpdate(-1, null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTweetComposerUpdateListener) {
            mListener = (OnTweetComposerUpdateListener) context;
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
    public interface OnTweetComposerUpdateListener {
        // TODO: Update argument type and name
        void onUpdate(int status, String body);
    }
}
