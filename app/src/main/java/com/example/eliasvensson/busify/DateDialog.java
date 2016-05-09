/**
 * @author Elias Svensson and David Genelov
 * @version 1.0, 2016-05-04
 * This class was made with the aid of tutorials available at
 * http://developer.android.com/guide/topics/ui/controls/pickers.html#DatePicker
 *
 * DialogFragments are typically used to display a dialog window floating on top of its activity
 * window. DateDialog extends DialogFragment, and the dialog window in this particular case
 * appears as a "DatePicker", which lets the user pick a date from a calendar.
 *
 * Furthermore, when a date is selected by a user, the method "OnDateSet" defined in the interface
 * OnDateSetListener is called. This method saves the specified date as a String, and sets it as
 * the text for a previously defined view.
 */

package com.example.eliasvensson.busify;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // Variable that stores which EditText-object  the date will be written to
    private EditText textToChange;

    /** Constructor that assigns which EditText-object the date will be written to
     * @param view The EditText-object that the date will be written to
     */
    public DateDialog(View view){
        textToChange =(EditText)view;

    }

    /** Sets the current date as default for a dialog
     * @return A new instance of DatePickerDialog with todays date as default
     * @param savedInstanceState The current state of the application
     *
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        //Set the max date for the datepicker to current date
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
        return dialog;
    }

    /** Sets the selected date to a String and sets the text of a view to the date
     * @param view The view to update the text with the date
     * @param year The selected year
     * @param month The selected month
     * @param day The selected day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
        String date=day+"-"+(month+1)+"-"+year;
        textToChange.setText(date);

    }

}