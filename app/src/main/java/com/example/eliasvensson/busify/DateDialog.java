/**
 * @author Elias Svensson and David Genelov
 * @version 1.0, 2016-05-04
 * @since 1.0
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
import java.util.GregorianCalendar;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // Variable that stores which EditText-object the date will be written to
    private EditText textToChange;

    /** Constructor that assigns which EditText-object the date will be written to
     * @param view The EditText-object that the date will be written to
     */
    public DateDialog(View view) {

        if (view instanceof EditText)
            textToChange = (EditText) view;
        else
            throw new IllegalArgumentException("The view is not an EditText view");

    }

    /** Sets the current date as default for a dialog
     * @return A new instance of DatePickerDialog with May 24th as default date.
     * @param savedInstanceState The current state of the application
     *
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, 2016, 5, 24);

        // Set the max and min dates for the DatePicker, to guarantee that there is a file
        // that corresponds to the chosen date
        // Dates set to the week 18-24th May(!) to demonstrate one week of functionality
        // on the presentation on May 24th
        GregorianCalendar minDate = new GregorianCalendar(2016, 4, 18);
        GregorianCalendar maxDate = new GregorianCalendar(2016, 4, 24);
        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        return dialog;
    }

    /** Sets the selected date to a String and sets the text of a view to the date
     * @param view The view to update the text with the date
     * @param year The selected year
     * @param month The selected month (integers ranging from 0 to 11, 0 being January)
     * @param day The selected day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Show the selected date in the text box
        String date = String.format("%4d - %02d - %02d", year, (month + 1), day);
        textToChange.setText(date);

    }

}