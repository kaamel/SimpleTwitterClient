package com.kaamel.simpletwitterclient.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.databinding.FragmentCompseTweetDialogBinding;

import static com.kaamel.simpletwitterclient.fragments.ComposeTweetDialogFragment.OnTweetComposerUpdateListener.STATUS_CANCEL;
import static com.kaamel.simpletwitterclient.fragments.ComposeTweetDialogFragment.OnTweetComposerUpdateListener.STATUS_SAVE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTweetComposerUpdateListener} interface
 * to handle interaction events.
 * Use the {@link ComposeTweetDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeTweetDialogFragment extends DialogFragment {

    FragmentCompseTweetDialogBinding binding;
    private static final String BODY = "body";

    private String body;
    EditText etCompose;
    TextView tvCount;
    ImageButton ibCancel;
    Button btTweet;

    private OnTweetComposerUpdateListener mListener;

    public ComposeTweetDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param body the body of the tweet (optional)
     * @return A new instance of fragment CompseTweetDialogFragment.
     */
    public static ComposeTweetDialogFragment newInstance(String body) {
        ComposeTweetDialogFragment fragment = new ComposeTweetDialogFragment();
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
                    String body = etCompose.getText().toString().trim();
                    if (body.length() > 0) {
                        displayCancelOptions(body);
                    }
                    else {
                        cancelTweet();
                        dismiss();
                    }
                }
            }
        });

        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
                dismiss();
            }
        });
        return binding.getRoot();
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

    private void saveTweet() {
        if (mListener != null) {
            String body = etCompose.getText().toString().trim();
            if (body.length()>0)
                mListener.onUpdate(STATUS_SAVE, body);
        }
    }

    private void tweet() {
        if (mListener != null) {
            String body = etCompose.getText().toString().trim();
            if (body.length()>0)
                mListener.onUpdate(OnTweetComposerUpdateListener.STATUS_TWEET, body);
        }
    }

    private void cancelTweet() {
        if (mListener != null)
            mListener.onUpdate(STATUS_CANCEL, null);
    }

    void displayCancelOptions(String body) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("You can come back and continue if you save. Otherwise they will be discarded")
                .setTitle("Save the text?");

        // Add the buttons
        builder.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked SAVE button
                saveTweet();
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.discard
                , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancelTweet();
                dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        public static final int STATUS_CANCEL=-1;
        public static final int STATUS_SAVE=0;
        public static final int STATUS_TWEET=1;
        void onUpdate(int status, String body);
    }
}
