# Changelog

## 1.1.0

* Drop support for scala.js 0.6
* Add support for scala 3.0.0-M2
* Multiple library update

## 1.0.0

* Update scala to 2.12.11
* Update scala to 2.13.2
* Update sbt-locales to 1.0.0
* Fix the default locale logic

## 0.6.0

* Support scala 2.13.1, 2.12.10, 2.11.10
* Dropped support for 2.10
* Support Scala.js 0.6.32 and 1.0.1
* Implement Locale#getISO3Country
* Implement Locale#getISO3Language
* Unification of 0.3 and 0.5 branches, it includes currency support
* Does not include the locales db, it must be built with sbt-locales
* Across the board improvements:
  * Format with scalafmt
  * Uses GH actions
  * Uses sbt-tpolecat and sbt-ci-release 

## 0.3.11-cldr33

* Scala.js 0.6.25 and 1.0.0-M4
* Scala versions 2.10.7, 2.11.13, 2.12.5 and 2.13.0-M4
* Scala native 0.3.8

## 0.3.10-cldr32

* Scala.js 0.6.22 and 1.0.0-M3

## 0.3.9-cldr32

* Scala.js 0.6.21 and 1.0.0-M2
* Scala versions 2.10.7, 2.11.12, 2.12.4 and 2.13.0-M2
* Updated to cldr32

## 0.3.8-cldr31

* Scala.js 0.6.20 and 1.0.0-M1
* Scala versions 2.11.11, 2.12.3 and 2.13.0-M2
* Support for scala-native

## 0.3.7-cldr31

* Updated to scala.js 0.6.18
* Supports scala.js 1.0.0-M1

## 0.3.5-cldr31

* Updated to scala.js 0.6.17

## 0.3.4-cldr31

* Updated to scala.js 0.6.16
* Updated to scala 2.11.11
* Updated to scala 2.12.2

## 0.3.3-cldr31

* Improved SimpleDateFormat with a partial implementation of format
* Updated to scala.js 0.6.15
* Updated CLDR to version 31

## 0.3.2-cldr30

* Updated to scala.js 0.6.14 and scala 2.12.1
* Use io.github.cquiroz organization
* Fix source map path

## 0.3.1-cldr30

* Updated to scala.js 0.6.13 and scala 2.12.0

## 0.3.0-cldr30

* Updated CLDR to version 30
* Updated to scala.js 0.6.12 and scala 2.12.0-RC1

## 0.3.0-cldr29

Provides minimal implementation of DateFormat for scala-java-time

* Add basic implementation java.text.NumberFormat
* Add basic implementation java.text.DateFormat
* Add basic implementation java.text.SimpleDateFormat

## 0.2.0-cldr29

* Add class java.text.Format
* Add class java.text.FieldPosition
* Add class java.text.CharacterIterator
* Add class java.text.AttributedCharacterIterator
* Add class java.text.ParsePosition
* Add class java.text.ParseException

## 0.1.1-cldr29

* Cross build and publish to scala 2.10.6, 2.11.8 and 2.12.0-M5

## 0.1.0-cldr29

First release of scala-java-locales:

* Partial implementation of Locale, Locale.Category and Locale.Builder classes
* Code generation of locales metadata from CLDR code
* Partial implementation of DecimalFormatSymbols with CLDR data
* Partial implementation of DateFormatSymbols with CLDR data
