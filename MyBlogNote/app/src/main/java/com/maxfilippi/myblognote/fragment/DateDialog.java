package com.maxfilippi.myblognote.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.maxfilippi.myblognote.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Max on 1/13/17.
 *
 * Use of a custom DateDialog instead of native DatePickerDialog
 * because new constructors that require API 24+
 * old constructors are normally deprecated
 *
 */

public class DateDialog extends DialogFragment
{

    // Static class TAG for logs
    private static String TAG = "DownloaderDialog";

    // UI Elements
    private DatePicker datePicker;
    private Button buttonConfirm;
    private Button buttonCancel;

    // Interaction listener
    private OnDateInteractionListener mListener;




    //
    // FRAGMENT LIFE CYCLE
    // =========================================================================================

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Create the Alert Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate the custom view
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_fragment, null);

        // Instantiate UI Elements
        datePicker = (DatePicker) view.findViewById(R.id.dialog_date_fragment_picker);
        buttonCancel = (Button) view.findViewById(R.id.dialog_date_fragment_button_cancel);
        buttonConfirm = (Button) view.findViewById(R.id.dialog_date_fragment_button_confirm);

        // Add listeners
        buttonCancel.setOnClickListener(new OnButtonChoiceClickListener());
        buttonConfirm.setOnClickListener(new OnButtonChoiceClickListener());

        // Return the date alert dialog
        return builder.setView(view).create();
    }



    //
    // ATTACH / DETACH LISTENER
    // =========================================================================================

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnDateInteractionListener)
        {
            mListener = (OnDateInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnDateInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }



    //
    // PRIVATE FRAGMENT METHODS
    // =========================================================================================

    private Date getDate()
    {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }




    //
    // BUTTON LISTENER
    // =========================================================================================

    private class OnButtonChoiceClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View view)
        {
            Button currentButton = (Button) view;

            if(currentButton.equals(buttonConfirm))
            {
                // Check if the listener is bound to the activity
                if(mListener != null)
                {
                    // Send the delegate with current date selected
                    mListener.onDateConfirmedListener(getDate());
                }
            }

            DateDialog.this.dismiss();
        }
    }



    //
    // INTERFACE DELEGATE (LISTENER)
    // =========================================================================================

    public interface OnDateInteractionListener
    {
        void onDateConfirmedListener(Date date);
    }

}
