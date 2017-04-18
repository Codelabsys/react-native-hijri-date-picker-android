
# react-native-hijri-date-picker-android
[![Codelab](http://www.codelabsys.com/images/logo.png)](http://www.codelabsys.com/) 


#This Module version is experimental, if you found any issues, kindly submit. 

## Getting started

### Installing
   [![NPM](https://nodei.co/npm/react-native-hijri-date-picker-android.png?downloads=true&downloadRank=true&stars=true)](https://nodei.co/npm/react-native-hijri-date-picker-android/)
   
`$ npm install react-native-hijri-date-picker-android --save`

### Mostly automatic installation

`$ react-native link react-native-hijri-date-picker-android`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.HijriDatePickerAndroidPackage;` to the imports at the top of the file
  - Add `new HijriDatePickerAndroidPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-hijri-date-picker-android'
  	project(':react-native-hijri-date-picker-android').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-hijri-date-picker-android/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-hijri-date-picker-android')
  	```


## Usage
```javascript
//first include HijriDatePickerAndroid
import HijriDatePickerAndroid from "react-native-hijri-date-picker-android";


	let options = { date: new Date(), minDate: new Date(new Date().getTime() - (1 * 30 * 24 * 60 * 60 * 1000)), maxDate: new Date(new Date().getTime() + (1 * 30 * 24 * 60 * 60 * 1000)) };
    let stringOptions = { date: "27-7-1438", minDate: "25-6-1438", maxDate: "29-8-1438" };
	//mode:"no_arrows" hide the arrows at the bar of the calendar
	//weekDayLabels, override the default day labels at the calendar
	let moreOptions = { date: "27-7-1438", minDate: "25-6-1438", maxDate: "29-8-1438", mode:"no_arrows", weekDayLabels:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"]};
    //accepts option  dates with date objects or strings in the following format ['dd-MM-yyyy'] 
    HijriDatePickerAndroid.open(stringOptions).then(function (result) {
      if (result.action == HijriDatePickerAndroid.dismissedAction) {
        console.warn("Dismissed");
      } else {
        let { year, day, month } = result;
        console.warn("Hijri Date: " + day + "/" +( month + 1) + "/" + year + "/");
      }
    });
	
	//convert string Hijri date ['dd-MM-yyyy'] to a gregorian timestamp
    HijriDatePickerAndroid.convertHijriDateToGregorianDate("12-7-1438").then(function (result) {
      console.warn("Gregorian Timestamp" + JSON.stringify(result));

    });


    //convert gregorian date object to hijri {year,month,day}
    HijriDatePickerAndroid.convertGregorianDateToHijriDate(new Date()).then(function ({ year, day, month }) {
      console.warn("Hijri Date: " + day + "/" + month + 1 + "/" + year + "/");

```
  
## For IOS
 
Check out our IOS project [react-native-universal-datepicker-ios](https://github.com/Codelabsys/react-native-universal-datepicker-ios) 
  
  
## Credits

This project was made by the help of [Assem-Hafez](https://github.com/Assem-Hafez) and [Mohamed-Abbas](https://github.com/Mohamed-Abbas)
for the company we're working at [Codelabsys](http://www.codelabsys.com/)

And is based on the following projects, [material-hijri-calendarview](https://github.com/eltohamy/material-hijri-calendarview) and [ummalqura-calendar](https://github.com/msarhan/ummalqura-calendar)
