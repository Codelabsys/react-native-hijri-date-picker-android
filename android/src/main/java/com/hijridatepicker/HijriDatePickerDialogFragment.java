package com.hijridatepicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.eltohamy.materialhijricalendarview.CalendarDay;
import com.github.eltohamy.materialhijricalendarview.MaterialHijriCalendarView;
import com.github.eltohamy.materialhijricalendarview.OnDateSelectedListener;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.util.Calendar;

import javax.annotation.Nullable;

/**
 * Created by Mohamed Habib on 16/10/2016.
 */
public class HijriDatePickerDialogFragment extends DialogFragment implements OnDateSelectedListener {

    @Nullable
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    @Nullable
    private DialogInterface.OnDismissListener mOnDismissListener;
    @Nullable
    private HijriDatePickerAndroidModule.OnExceptionListener mOnExceptionListener;

    Button doneBtn;
    MaterialHijriCalendarView widget;
    int dayOfMonth;
    int monthOfYear;
    int year;

    Bundle args;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_basic, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        args = getArguments();
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doneBtn = (Button) view.findViewById(R.id.done_button);
        widget = (MaterialHijriCalendarView) view.findViewById(R.id.calendarView);
        widget.setOnDateChangedListener(this);
        UmmalquraCalendar ummalquraCalendar = new UmmalquraCalendar();
        dayOfMonth = ummalquraCalendar.get(Calendar.DAY_OF_MONTH);
        monthOfYear = ummalquraCalendar.get(Calendar.MONTH);
        year = ummalquraCalendar.get(Calendar.YEAR);

        customizeHijriCalendarView(widget, args, mOnExceptionListener);
        if (widget.getSelectedDate() != null)
            onDateSelected(widget, widget.getSelectedDate(), true);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDateSetListener != null) {
                    mOnDateSetListener.onDateSet(null, year, monthOfYear, dayOfMonth);
                }
                dismiss();
            }
        });
    }

    public static void customizeHijriCalendarView(MaterialHijriCalendarView widget, Bundle args, HijriDatePickerAndroidModule.OnExceptionListener mOnExceptionListener) {

        UmmalquraCalendar ummalquraCalendar = new UmmalquraCalendar();

        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_DATE)) {
            try {
                ummalquraCalendar.setTimeInMillis(args.getLong(HijriDatePickerAndroidModule.ARG_DATE));
                widget.setDateSelected(ummalquraCalendar, true);
            } catch (Exception e) {
                mOnExceptionListener.onException(HijriDatePickerAndroidModule.ERROR_PARSING_OPTIONS,
                        "Exception happened while parsing " + HijriDatePickerAndroidModule.ARG_DATE + ", details: " + e.getMessage());
            }
        }

        //for now this calendar view has only one mode, so we wont use ARG_MODE TODO: customize the Dialog UI

        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_MINDATE)) {
            try {
                ummalquraCalendar.setTimeInMillis(args.getLong(HijriDatePickerAndroidModule.ARG_MINDATE));
                widget.setMinimumDate(ummalquraCalendar);
            } catch (Exception e) {
                mOnExceptionListener.onException(HijriDatePickerAndroidModule.ERROR_PARSING_OPTIONS,
                        "Exception happened while parsing " + HijriDatePickerAndroidModule.ARG_MINDATE + ", details: " + e.getMessage());
            }
        }

        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_MAXDATE)) {
            try {
                ummalquraCalendar.setTimeInMillis(args.getLong(HijriDatePickerAndroidModule.ARG_MAXDATE));
                widget.setMaximumDate(ummalquraCalendar);
            } catch (Exception e) {
                mOnExceptionListener.onException(HijriDatePickerAndroidModule.ERROR_PARSING_OPTIONS,
                        "Exception happened while parsing " + HijriDatePickerAndroidModule.ARG_MAXDATE + ", details: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialHijriCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        dayOfMonth = date.getDay();
        monthOfYear = date.getMonth();
        year = date.getYear();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    void setOnExceptionListener(@Nullable HijriDatePickerAndroidModule.OnExceptionListener onExceptionListener) {
        mOnExceptionListener = onExceptionListener;
    }

    void setOnDateSetListener(@Nullable DatePickerDialog.OnDateSetListener onDateSetListener) {
        mOnDateSetListener = onDateSetListener;
    }

    void setOnDismissListener(@Nullable DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }
}

