package com.phdata.ddos.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.{Matcher, Pattern}

import com.phdata.ddos.entity.AccessLog

import scala.util.control.Exception._


@SerialVersionUID(100L)
class AccessLogParser extends Serializable {

    private val ddd = "\\d{1,3}"                      // at least 1 but not more than 3 times (possessive)
    private val ip = s"($ddd\\.$ddd\\.$ddd\\.$ddd)?"  // like `123.456.7.89`
    private val client = "(\\S+)"                     // '\S' is 'non-whitespace character'
    private val user = "(\\S+)"
    private val dateTime = "(\\[.+?\\])"              // like `[21/Jul/2009:02:48:13 -0700]`
    private val request = "\"(.*?)\""                 // any number of any character, reluctant
    private val status = "(\\d{3})"
    private val bytes = "(\\S+)"                      // this can be a "-"
    private val referer = "\"(.*?)\""
    private val agent = "\"(.*?)\""
    private val regex = s"$ip $client $user $dateTime $request $status $bytes $referer $agent"
    private val p = Pattern.compile(regex)
    

    def parseRecord(record: String): Option[AccessLog] = {
        val matcher = p.matcher(record)
        if (matcher.find) {
            Some(buildAccessLog(matcher))
        } else {
            None
        }
    }

    def parseRecordReturningNullObjectOnFailure(record: String): AccessLog = {
        val matcher = p.matcher(record)
        if (matcher.find) {
            buildAccessLog(matcher)
        } else {
            AccessLogParser.nullObjectAccessLog
        }
    }
    
    private def buildAccessLog(matcher: Matcher) = {
        AccessLog(
            matcher.group(1),
            matcher.group(2),
            matcher.group(3),
            AccessLogParser.parseDateField(matcher.group(4)),
            matcher.group(5),
            matcher.group(6),
            matcher.group(7),
            matcher.group(8),
            matcher.group(9))
    }
}

object AccessLogParser {

    def apply(): AccessLogParser = new AccessLogParser()

    val nullObjectAccessLog = AccessLog("", "", "", None, "", "", "", "", "")
    
    def parseRequestField(request: String): Option[(String, String, String)] = {
        val arr = request.split(" ")
        if (arr.size == 3) Some((arr(0), arr(1), arr(2))) else None
    }

    def parseDateField(field: String): Option[Timestamp] = {
        val dateRegex = "\\[(.*?) .+]"
        val datePattern = Pattern.compile(dateRegex)
        val dateMatcher = datePattern.matcher(field)
        if (dateMatcher.find) {
                val dateString = dateMatcher.group(1)
                // HH is 0-23; kk is 1-24
                val dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH)

            val date = dateFormat.parse(dateString)
                val timestamp = new Timestamp(date.getTime)
                allCatch.opt(timestamp)  // return Timestamp[Date]
            } else {
            None
        }
    }

}



