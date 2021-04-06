package com.hijridatepicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.eltohamy.materialhijricalendarview.CalendarDay;
import com.github.eltohamy.materialhijricalendarview.MaterialHijriCalendarView;
import com.github.eltohamy.materialhijricalendarview.OnDateSelectedListener;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

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
        customizeHijriCalendarView(getActivity(), widget, args, mOnExceptionListener);
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

    public static void customizeHijriCalendarView(Context mContext, MaterialHijriCalendarView widget, Bundle args, HijriDatePickerAndroidModule.OnExceptionListener mOnExceptionListener) {

        UmmalquraCalendar ummalquraCalendar = new UmmalquraCalendar();

        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_DATE)) {
            try {
                ummalquraCalendar.setTimeInMillis(args.getLong(HijriDatePickerAndroidModule.ARG_DATE));
                widget.setDateSelected(ummalquraCalendar, true);
            } catch (Exception e) {
                mOnExceptionListener.onException(HijriDatePickerAndroidModule.ERROR_PARSING_OPTIONS,
                        "Exception happened while parsing " + HijriDatePickerAndroidModule.ARG_DATE + ", details: " + e.getMessage());
                widget.setSelectedDate(new UmmalquraCalendar());
            }
        } else {
            widget.setSelectedDate(new UmmalquraCalendar());
        }

        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_WEEK_DAY_LABELS)) {
            try {
                ArrayList<String> stringArrayList = args.getStringArrayList(HijriDatePickerAndroidModule.ARG_WEEK_DAY_LABELS);
                if (stringArrayList != null) {
                    String[] stringArray = Arrays.copyOf(stringArrayList.toArray(), stringArrayList.toArray().length, String[].class);
                    widget.setWeekDayLabels(stringArray);
                }
            } catch (Exception e) {
                mOnExceptionListener.onException(HijriDatePickerAndroidModule.ERROR_PARSING_OPTIONS,
                        "Exception happened while parsing " + HijriDatePickerAndroidModule.ARG_DATE + ", details: " + e.getMessage());
            }
        }

        //TODO: customize the Dialog UI for more options
        HijriDatePickerMode mode = HijriDatePickerMode.DEFAULT;
        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_MODE) && args.getString(HijriDatePickerAndroidModule.ARG_MODE) != null) {
            try {
                mode = HijriDatePickerMode.valueOf(args.getString(HijriDatePickerAndroidModule.ARG_MODE).toUpperCase(Locale.US));
            } catch (Exception e) {
                mOnExceptionListener.onException(HijriDatePickerAndroidModule.ERROR_PARSING_OPTIONS,
                        "Exception happened while parsing " + HijriDatePickerAndroidModule.ARG_MODE + ", details: " + e.getMessage());
            }
        }
        switch (mode) {
            case NO_ARROWS:
                widget.setLeftArrowMask(null);
                widget.setRightArrowMask(null);
                break;
            case DEFAULT:
                widget.setLeftArrowMask(ContextCompat.getDrawable(mContext, R.drawable.mcv_action_previous));
                widget.setRightArrowMask(ContextCompat.getDrawable(mContext, R.drawable.mcv_action_next));
                break;
            default:
                widget.setLeftArrowMask(ContextCompat.getDrawable(mContext, R.drawable.mcv_action_previous));
                widget.setRightArrowMask(ContextCompat.getDrawable(mContext, R.drawable.mcv_action_next));
                break;
        }

        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_MINDATE)) {
            try {
                ummalquraCalendar.setTimeInMillis(args.getLong(HijriDatePickerAndroidModule.ARG_MINDATE));
                widget.setMinimumDate(ummalquraCalendar);
                widget.setShowOtherDates(MaterialHijriCalendarView.SHOW_ALL);
            } catch (Exception e) {
                mOnExceptionListener.onException(HijriDatePickerAndroidModule.ERROR_PARSING_OPTIONS,
                        "Exception happened while parsing " + HijriDatePickerAndroidModule.ARG_MINDATE + ", details: " + e.getMessage());
            }
        }

        if (args != null && args.containsKey(HijriDatePickerAndroidModule.ARG_MAXDATE)) {
            try {
                ummalquraCalendar.setTimeInMillis(args.getLong(HijriDatePickerAndroidModule.ARG_MAXDATE));
                widget.setMaximumDate(ummalquraCalendar);
                widget.setShowOtherDates(MaterialHijriCalendarView.SHOW_ALL);
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


    /**
     * Date picker modes
     */
    public enum HijriDatePickerMode {
        DEFAULT,
        NO_ARROWS
    }


}

