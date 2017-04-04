package com.hijridatepicker;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.DatePicker;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class HijriDatePickerAndroidModule extends ReactContextBaseJavaModule {
    private static final String REACT_CLASS = "HijriDatePickerAndroid";

    public static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
    public static final String ERROR_OPEN = "E_OPEN";
    public static final String ERROR_PARSING_OPTIONS = "E_PARSING_OPTIONS";
    private static final String ERROR_CONVERT = "E_CONVERT";

    private static final String ACTION_DATE_SET = "dateSetAction";
    private static final String ACTION_DISMISSED = "dismissedAction";

    private static final String FRAGMENT_TAG = "DatePickerAndroid";
    static final String ARG_DATE = "date";
    static final String ARG_MINDATE = "minDate";
    static final String ARG_MAXDATE = "maxDate";
    private static final String ARG_MODE = "mode";


    public HijriDatePickerAndroidModule(final ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put(ACTION_DATE_SET, ACTION_DATE_SET);
        constants.put(ACTION_DISMISSED, ACTION_DISMISSED);
        constants.put(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY);
        constants.put(ERROR_OPEN, ERROR_OPEN);
        constants.put(ERROR_CONVERT, ERROR_CONVERT);
        constants.put(ERROR_PARSING_OPTIONS, ERROR_PARSING_OPTIONS);
        return constants;
    }

    /**
     * Copyright (c) 2015-present, Facebook, Inc.
     * All rights reserved.
     * <p>
     * This source code is licensed under the BSD-style license found in the
     * LICENSE file in the root directory of this source tree. An additional grant
     * of patent rights can be found in the PATENTS file in the same directory.
     * Show a date picker dialog.
     *
     * @param options a map containing options. Available keys are:
     *                <p>
     *                <ul>
     *                <li>{@code date} (timestamp in milliseconds) the date to show by default</li>
     *                <li>
     *                {@code minDate} (timestamp in milliseconds) the minimum date the user should be allowed
     *                to select
     *                </li>
     *                <li>
     *                {@code maxDate} (timestamp in milliseconds) the maximum date the user should be allowed
     *                to select
     *                </li>
     *                <li>
     *                {@code mode} To set the date picker mode to 'calendar/spinner/default' , currently there's only one mode for HijriDatePicker, so this field is ignored
     *                </li>
     *                </ul>
     * @param promise This will be invoked with parameters action, year,
     *                month (0-11), day, where action is {@code dateSetAction} or
     *                {@code dismissedAction}, depending on what the user did. If the action is
     *                dismiss, year, month and date are undefined.
     */
    @ReactMethod
    public void open(@Nullable final ReadableMap options, Promise promise) {
        try {
            Activity activity = getCurrentActivity();
            if (activity == null) {
                promise.reject(ERROR_NO_ACTIVITY, "Tried to open a DatePicker dialog while not attached to an Activity");
                return;
            }
            // We want to support both android.app.Activity and the pre-Honeycomb FragmentActivity
            // (for apps that use it for legacy reasons). This unfortunately leads to some code duplication.
            if (activity instanceof android.support.v4.app.FragmentActivity) {
                android.support.v4.app.FragmentManager fragmentManager = ((android.support.v4.app.FragmentActivity) activity).getSupportFragmentManager();
                android.support.v4.app.DialogFragment oldFragment = (android.support.v4.app.DialogFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
                if (oldFragment != null) {
                    oldFragment.dismiss();
                }
                SupportHijriDatePickerDialogFragment fragment = new SupportHijriDatePickerDialogFragment();
                if (options != null) {
                    final Bundle args = createFragmentArguments(options, promise);
                    if (args == null)
                        return;
                    fragment.setArguments(args);
                }
                final DatePickerDialogListener listener = new DatePickerDialogListener(promise);
                fragment.setOnDismissListener(listener);
                fragment.setOnDateSetListener(listener);
                fragment.setOnExceptionListener(listener);
                fragment.show(fragmentManager, FRAGMENT_TAG);
            } else {
                FragmentManager fragmentManager = activity.getFragmentManager();
                DialogFragment oldFragment = (DialogFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
                if (oldFragment != null) {
                    oldFragment.dismiss();
                }
                HijriDatePickerDialogFragment fragment = new HijriDatePickerDialogFragment();
                if (options != null) {
                    final Bundle args = createFragmentArguments(options, promise);
                    if (args == null)
                        return;
                    fragment.setArguments(args);
                }
                final DatePickerDialogListener listener = new DatePickerDialogListener(promise);
                fragment.setOnDismissListener(listener);
                fragment.setOnDateSetListener(listener);
                fragment.setOnExceptionListener(listener);
                fragment.show(fragmentManager, FRAGMENT_TAG);
            }
        } catch (Exception e) {
            promise.reject(ERROR_OPEN, "Exception happened while executing open method, details: " + e.getMessage());
        }
    }

    private Bundle createFragmentArguments(ReadableMap options, Promise promise) {
        final Bundle args = new Bundle();
        try {
            if (options.hasKey(ARG_DATE) && !options.isNull(ARG_DATE)) {
                if (!parseOptionsWithKey(ARG_DATE, options, args, promise))
                    return null;
            }
            if (options.hasKey(ARG_MINDATE) && !options.isNull(ARG_MINDATE)) {
                if (!parseOptionsWithKey(ARG_MINDATE, options, args, promise))
                    return null;
            }
            if (options.hasKey(ARG_MAXDATE) && !options.isNull(ARG_MAXDATE)) {
                if (!parseOptionsWithKey(ARG_MAXDATE, options, args, promise))
                    return null;
            }
            if (options.hasKey(ARG_MODE) && !options.isNull(ARG_MODE)) {
                args.putString(ARG_MODE, options.getString(ARG_MODE));
            }
        } catch (Exception e) {
            promise.reject(ERROR_PARSING_OPTIONS, "Exception happened while parsing options, details: " + e.getMessage());
            return null;
        }
        return args;
    }

    private boolean parseOptionsWithKey(String ARG_KEY, ReadableMap options, Bundle args, Promise promise) {
        ReadableType argDateType = options.getType(ARG_KEY);
        if (ReadableType.String.equals(argDateType)) {
            try {
                long milliSeconds = 0;
                milliSeconds = (long) convertHijriDateToGregorianMilliseconds(options.getString(ARG_KEY));
                args.putLong(ARG_KEY, milliSeconds);
                return true;
            } catch (PatternSyntaxException | IndexOutOfBoundsException | NumberFormatException e) {
                promise.reject(ERROR_PARSING_OPTIONS, "Exception happened while parsing " + ARG_KEY +
                        " we only accept object of Date or String with the format \"dd-MM-yyyy\" in Hijri");
                return false;
            }
        } else if (ReadableType.Number.equals(argDateType)) {
            args.putLong(ARG_KEY, (long) options.getDouble(ARG_KEY));
            return true;
        } else {
            promise.reject(ERROR_PARSING_OPTIONS, "Exception happened while parsing " + ARG_KEY +
                    " we only accept object of Date or String with the format \"dd-MM-yyyy\" in Hijri");
            return false;
        }
    }

    /**
     * @param hijriDate must be at the format dd-MM-yyyy
     * @return milliseconds of Gregorian equivalent to the given hijriDate
     */
    public double convertHijriDateToGregorianMilliseconds(String hijriDate) {
        UmmalquraCalendar ummalquraCalendar = new UmmalquraCalendar();

        int year = Integer.parseInt(hijriDate.split("-")[2]);
        int monthOfYear = Integer.parseInt(hijriDate.split("-")[1]);
        int dayOfMonth = Integer.parseInt(hijriDate.split("-")[0]);

        ummalquraCalendar.set(UmmalquraCalendar.YEAR, year);
        ummalquraCalendar.set(UmmalquraCalendar.MONTH, monthOfYear - 1);
        ummalquraCalendar.set(UmmalquraCalendar.DAY_OF_MONTH, dayOfMonth);

        return ummalquraCalendar.getTime().getTime();
    }

    @ReactMethod
    public void convertHijriDateStrToGregorianMilliseconds(String hijriDate, Promise promise) {
        try {
            double milliseconds = convertHijriDateToGregorianMilliseconds(hijriDate);
            promise.resolve(milliseconds);
        } catch (Exception e) {
            promise.reject(ERROR_CONVERT, "Exception while executing convertHijriDateStrToGregorianMilliseconds, Details: " + e.getMessage());

        }
    }

    /**
     * @param milliseconds your gregorian date in milliseconds
     * @return hijri date at the format of "dd-MM-yyyy"
     */
    @ReactMethod
    public void convertMillisecondsToHijriDate(double milliseconds, Promise promise) {

        try {
            UmmalquraCalendar ummalquraCalendar = new UmmalquraCalendar();
            ummalquraCalendar.setTimeInMillis((long) milliseconds);
            WritableMap result = new WritableNativeMap();
            result.putInt("year", ummalquraCalendar.get(Calendar.YEAR));
            result.putInt("month", ummalquraCalendar.get(Calendar.MONTH));
            result.putInt("day", ummalquraCalendar.get(Calendar.DAY_OF_MONTH));
            promise.resolve(result);
        } catch (Exception e) {
            promise.reject(ERROR_CONVERT, "Exception while executing convertMillisecondsToHijriDate, Details: " + e.getMessage());
        }
    }


    public interface OnExceptionListener {
        void onException(String code, String message);
    }

    /**
     * Copyright (c) 2015-present, Facebook, Inc.
     * All rights reserved.
     * <p>
     * This source code is licensed under the BSD-style license found in the
     * LICENSE file in the root directory of this source tree. An additional grant
     * of patent rights can be found in the PATENTS file in the same directory.
     */
    private class DatePickerDialogListener implements DatePickerDialog.OnDateSetListener, DialogInterface.OnDismissListener, OnExceptionListener {

        private final Promise mPromise;
        private boolean mPromiseResolved = false;

        public DatePickerDialogListener(final Promise promise) {
            mPromise = promise;
        }

        @Override
        public void onDateSet(DatePicker ignored, int year, int month, int day) {
            if (!mPromiseResolved && getReactApplicationContext().hasActiveCatalystInstance()) {
                WritableMap result = new WritableNativeMap();
                result.putString("action", ACTION_DATE_SET);
                result.putInt("year", year);
                result.putInt("month", month);
                result.putInt("day", day);
                mPromise.resolve(result);
                mPromiseResolved = true;
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (!mPromiseResolved && getReactApplicationContext().hasActiveCatalystInstance()) {
                WritableMap result = new WritableNativeMap();
                result.putString("action", ACTION_DISMISSED);
                mPromise.resolve(result);
                mPromiseResolved = true;
            }
        }

        @Override
        public void onException(String code, String message) {
            if (!mPromiseResolved && getReactApplicationContext().hasActiveCatalystInstance()) {
                mPromise.reject(code, message);
                mPromiseResolved = true;
            }
        }
    }
}
