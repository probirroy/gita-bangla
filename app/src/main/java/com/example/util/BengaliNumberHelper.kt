package com.example.util

/**
 * Utility helper for converting between Bengali numerals (০-৯) and ASCII digits (0-9),
 * as well as formatting numbers into Bengali script.
 */
object BengaliNumberHelper {

    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    /**
     * Converts an integer into Bengali digits (e.g. 47 -> "৪৭", 2 -> "২").
     */
    fun toBengali(number: Int): String {
        return toBengali(number.toString())
    }

    /**
     * Converts a Long number into Bengali digits.
     */
    fun toBengali(number: Long): String {
        return toBengali(number.toString())
    }

    /**
     * Converts any string containing ASCII digits (0-9) to Bengali digits (০-৯).
     */
    fun toBengali(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Normalizes Bengali numerals (০-৯) in a string to standard ASCII digits (0-9).
     * For example, "২.৪৭" -> "2.47", "অধ্যায় ৪" -> "অধ্যায় 4".
     */
    fun normalizeBengaliToAscii(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            when (ch) {
                '০' -> sb.append('0')
                '১' -> sb.append('1')
                '২' -> sb.append('2')
                '৩' -> sb.append('3')
                '৪' -> sb.append('4')
                '৫' -> sb.append('5')
                '৬' -> sb.append('6')
                '৭' -> sb.append('7')
                '৮' -> sb.append('8')
                '৯' -> sb.append('9')
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Returns true if the character is a Bengali digit ('০'..'৯') or ASCII digit ('0'..'9').
     */
    fun isDigitOrBengaliDigit(ch: Char): Boolean {
        return ch in '0'..'9' || ch in '০'..'৯'
    }
}
