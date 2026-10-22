package com.example.data.source

import com.example.data.model.DailyVerseCurated
import com.example.data.model.Verse
import java.util.Calendar

object DailyVerseProvider {

    private data class CuratedEntry(
        val chapterNumber: Int,
        val verseNumber: Int,
        val themeCategory: String,
        val inspirationQuote: String,
        val dailyWisdomBengali: String,
        val dailyPracticeBengali: String
    )

    private val curatedEntries = listOf(
        CuratedEntry(
            chapterNumber = 2,
            verseNumber = 47,
            themeCategory = "নিষ্কাম কর্মযোগ",
            inspirationQuote = "কর্মেই তোমার একমাত্র অধিকার, ফলাফলে আসক্তি নয়",
            dailyWisdomBengali = "কর্মের ফলাফলের আশা ত্যাগ করে সর্বোচ্চ নিষ্ঠার সাথে দায়িত্ব পালন করলে মন শান্ত থাকে এবং যেকোনো কাজের সাফল্য বহুগুণ বৃদ্ধি পায়।",
            dailyPracticeBengali = "আজকের দিনে একটি কল্যাণকর কাজ কোনো ব্যক্তিগত লাভের আশা না রেখে সম্পূর্ণ আন্তরিকতার সাথে সম্পন্ন করুন।"
        ),
        CuratedEntry(
            chapterNumber = 2,
            verseNumber = 14,
            themeCategory = "স্থিতপ্রজ্ঞতা ও সহনশীলতা",
            inspirationQuote = "সুখ-দুঃখ অনিত্য, ধৈর্যধারণই মুক্তির পথ",
            dailyWisdomBengali = "শীত ও গ্রীষ্মের মতোই জাগতিক সুখ ও দুঃখও ক্ষণস্থায়ী। জীবনের উত্থান-পতনে বিচলিত না হয়ে সমাহিত অন্তরে এগিয়ে চলাই জ্ঞানীর লক্ষণ।",
            dailyPracticeBengali = "আজ যেকোনো প্রতিকূল বা অপ্রত্যাশিত পরিস্থিতিতে ক্রোধ বা হতাশা প্রকাশ না করে শান্তচিত্তে ধৈর্য ধারণ করুন।"
        ),
        CuratedEntry(
            chapterNumber = 2,
            verseNumber = 20,
            themeCategory = "আত্মার অমরত্ব",
            inspirationQuote = "আত্মা জন্মরহিত, নিত্য, শাশ্বত ও সনাতন",
            dailyWisdomBengali = "নশ্বর দেহের পরিবর্তন হলেও আপনার মধ্যকার চিন্ময় আত্মা চির অবিনশ্বর। কোনো জাগতিক ক্ষয় বা ভয় কখনো আত্মাকে স্পর্শ করতে পারে না।",
            dailyPracticeBengali = "আজ অন্তত পাঁচ মিনিট নীরব ধ্যানে বসে সমস্ত ভয় পরিহার করে আপনার অন্তরের অমর আত্মশক্তিকে অনুভব করুন।"
        ),
        CuratedEntry(
            chapterNumber = 2,
            verseNumber = 22,
            themeCategory = "নির্ভীক রূপান্তর",
            inspirationQuote = "জীর্ণ বস্ত্র ত্যাগের মতোই আত্মার নতুন দেহ গ্রহণ",
            dailyWisdomBengali = "মানুষ যেমন পুরনো পোশাক পরিত্যাগ করে নতুন বস্ত্র পরিধান করে, তেমনি অবিনাশী আত্মা জীর্ণ দেহ ত্যাগ করে। এই বোধ জাগলে জীবনের ক্ষণস্থায়িত্বের ভয় দূর হয়।",
            dailyPracticeBengali = "আজ অতীতের পুরোনো কোনো ক্ষোভ বা আক্ষেপ ঝেড়ে ফেলে নতুন প্রত্যয়ে দিনটি উদযাপন করুন।"
        ),
        CuratedEntry(
            chapterNumber = 2,
            verseNumber = 62,
            themeCategory = "মনোসংযম ও আত্মরক্ষা",
            inspirationQuote = "আসক্তি থেকে কাম ও ক্রোধের সূচনা",
            dailyWisdomBengali = "অসংযত চিন্তাভাবনা থেকেই মোহ ও আসক্তির জন্ম হয়। চিন্তার জগতে শুভ ও পবিত্র ভাবনার চর্চা মানুষকে সর্বনাশা ক্রোধ থেকে মুক্ত রাখে।",
            dailyPracticeBengali = "আজকের দিনে অপ্রয়োজনীয় বিষয়াসক্তি বা ডিজিটাল আসক্তি থেকে নিজেকে কিছু সময় সচেতনভাবে দূরে রাখুন।"
        ),
        CuratedEntry(
            chapterNumber = 2,
            verseNumber = 63,
            themeCategory = "ক্রোধজয় ও প্রজ্ঞা",
            inspirationQuote = "ক্রোধ বিবেক নষ্ট করে, আত্মজয়ই পরম বিজয়",
            dailyWisdomBengali = "ক্রোধ মানুষের স্মৃতিভ্রম ও বুদ্ধিনাশ ঘটায়। শান্ত ও একাগ্র মনের সাধনাই জীবনের প্রতিটি পদক্ষেপে সঠিক সিদ্ধান্ত নেওয়ার চাবিকাঠি।",
            dailyPracticeBengali = "আজ যদি কোনো কারণে রাগ অনুভূত হয়, তাৎক্ষণিক কোনো প্রতিক্রিয়া না দিয়ে ৩ বার গভীর প্রশ্বাস নিয়ে শান্ত থাকুন।"
        ),
        CuratedEntry(
            chapterNumber = 3,
            verseNumber = 8,
            themeCategory = "কর্তব্যপরায়ণতা",
            inspirationQuote = "কর্মহীন অলসতা অপেক্ষা সৎ কর্ম সম্পাদন শ্রেষ্ঠ",
            dailyWisdomBengali = "নিষ্ক্রিয় বসে থাকার চেয়ে শুভকর্ম সম্পাদন সর্বদা শ্রেষ্ঠ। মানবজীবনের স্বার্থকতা নিঃস্বার্থ ও দায়িত্বশীল কর্মসাধনার মধ্যেই নিহিত।",
            dailyPracticeBengali = "আজ আপনার দিনের সবচেয়ে জরুরি ও প্রয়োজনীয় দায়িত্বটি কালক্ষেপণ না করে প্রফুল্ল চিত্তে শুরু করুন।"
        ),
        CuratedEntry(
            chapterNumber = 3,
            verseNumber = 21,
            themeCategory = "আদর্শ চরিত্র ও লোকশিক্ষা",
            inspirationQuote = "শ্রেষ্ঠ ব্যক্তির আচরণই সমাজকে পরিচালিত করে",
            dailyWisdomBengali = "আমাদের প্রতিটি সৎ ও সুন্দর আচরণ পরিবারের সদস্য এবং সমাজের জন্য প্রেরণার আলোকবর্তিকা হতে পারে। তাই সর্বদা সত্যের দৃষ্টান্ত স্থাপন করুন।",
            dailyPracticeBengali = "আজ কথায় ও ব্যবহারে এমন সদাচরণ প্রকাশ করুন যা চারপাশের মানুষদের সুন্দর কাজে উৎসাহিত করে।"
        ),
        CuratedEntry(
            chapterNumber = 4,
            verseNumber = 7,
            themeCategory = "ঐশ্বরিক আশ্বাস",
            inspirationQuote = "ধর্মের গ্লানিতে সত্যের জয় প্রতিষ্ঠায় শ্রীভগবান অবতীর্ণ হন",
            dailyWisdomBengali = "অসত্য ও অন্ধকার সাময়িক, সত্যের জয় চিরন্তন। অন্তরে যখনই হতাশা বাসা বাঁধবে, বিশ্বাস রাখুন সত্যের শক্তি সর্বদা আপনার সহায়।",
            dailyPracticeBengali = "আজ যেকোনো ন্যায় ও সততার প্রশ্নে কোনো আপস না করে সত্য ও সততার পক্ষে অবস্থান নিন।"
        ),
        CuratedEntry(
            chapterNumber = 4,
            verseNumber = 8,
            themeCategory = "সাধুরক্ষা ও ধর্মসংস্থাপন",
            inspirationQuote = "শুভশক্তির বিজয় ও অশুভের বিনাশই চিরন্তন নিয়ম",
            dailyWisdomBengali = "জগতে সৎভাব ও প্রেমের সুরক্ষা দেওয়াই পরমেশ্বরের চিরন্তন বিধান। নিজে সৎ পথে থাকলে জীবনের কোনো বাধাই আপনাকে পরাস্ত করতে পারবে না।",
            dailyPracticeBengali = "আজ কাউকে অন্যায়ভাবে ছোট হতে দেখলে তার পাশে দাঁড়িয়ে আন্তরিক সমর্থন ও সান্ত্বনা দিন।"
        ),
        CuratedEntry(
            chapterNumber = 4,
            verseNumber = 38,
            themeCategory = "দিব্য জ্ঞান ও প্রজ্ঞা",
            inspirationQuote = "আত্মজ্ঞানের সমতুল্য পবিত্রকারী জগতে আর কিছুই নেই",
            dailyWisdomBengali = "যাবতীয় অন্ধকার ও মনের সংশয় দূর করার একমাত্র আলোকবর্তিকা হলো তত্ত্বজ্ঞান। প্রতিদিন গীতা অধ্যয়ন মনের মলিনতা দূর করে দিব্য জ্যোতি এনে দেয়।",
            dailyPracticeBengali = "আজ অন্তত একটি আধ্যাত্মিক সত্য গভীরভাবে অনুধাবন করুন এবং তা নিজের জীবনে প্রয়োগ করুন।"
        ),
        CuratedEntry(
            chapterNumber = 6,
            verseNumber = 5,
            themeCategory = "আত্মউন্নয়ন ও আত্মবিশ্বাস",
            inspirationQuote = "মনই নিজের বন্ধু এবং মনই নিজের শত্রু",
            dailyWisdomBengali = "নিজেকে কখনো অবসাদগ্রস্ত বা অসহায় ভাববেন না। নিয়ন্ত্রিত ও একাগ্র মন আপনার জীবনের সর্বশ্রেষ্ঠ মিত্র ও শক্তির উৎস।",
            dailyPracticeBengali = "আজ নেতিবাচক আত্মচিন্তা সম্পূর্ণরূপে পরিহার করে আত্মবিশ্বাস ও ঈশ্বরের কৃপায় আস্থা রাখুন।"
        ),
        CuratedEntry(
            chapterNumber = 6,
            verseNumber = 26,
            themeCategory = "ধ্যান ও চিত্তস্থিরতা",
            inspirationQuote = "চঞ্চল মন যেখানেই ধাবিত হয়, সেখান থেকে আত্মায় ফিরিয়ে আনুন",
            dailyWisdomBengali = "মন স্বাভাবিকভাবেই বহিমুখী হতে পারে, কিন্তু বারবার ধৈর্য ও মমতা সহকারে মনকে শান্ত ও স্থির কেন্দ্রে ফিরিয়ে আনাই ধ্যানের প্রকৃত অনুশীলন।",
            dailyPracticeBengali = "আজ কাজের ফাঁকে অন্তত তিন মিনিট চোখ বুজে শ্রীভগবানের নাম বা ওঁ ধ্বনি স্মরণ করে মনকে সমাহিত করুন।"
        ),
        CuratedEntry(
            chapterNumber = 7,
            verseNumber = 7,
            themeCategory = "সর্বব্যাপী ঈশ্বরদর্শন",
            inspirationQuote = "সূত্রে গাঁথা মুক্তার মতো ঈশ্বর সমগ্র সৃষ্টিতে বিদ্যমান",
            dailyWisdomBengali = "সমগ্র সৃষ্টির রূপ-বৈচিত্র্যের অন্তরে এক পরম সত্য ওতপ্রোতভাবে জড়িয়ে রয়েছে। সকল প্রাণের প্রতি অহিংসা ও প্রীতিই পরমেশ্বরের শ্রেষ্ঠ বন্দনা।",
            dailyPracticeBengali = "আজ প্রতিটি জীবের প্রতি শ্রদ্ধাশীল থাকুন এবং কোনো প্রাণীকে অহেতুক কষ্ট দেওয়া থেকে বিরত থাকুন।"
        ),
        CuratedEntry(
            chapterNumber = 9,
            verseNumber = 22,
            themeCategory = "পূর্ণ শরণাগতি",
            inspirationQuote = "অনন্য ভক্তের যোগক্ষেম পরমেশ্বর স্বয়ং বহন করেন",
            dailyWisdomBengali = "যিনি কায়মনোবাক্যে নিজেকে শ্রীভগবানের চরণে নিবেদন করেন, তাঁর সমস্ত নিরাপত্তা ও পরম কল্যাণ স্বয়ং ঈশ্বর নিশ্চিত করেন।",
            dailyPracticeBengali = "আজকের সমস্ত মানসিক দুশ্চিন্তা ও ভবিষ্যতের ভয় ঈশ্বরের চরণে সমর্পণ করে নিশ্চিন্তে কর্তব্য পালন করুন।"
        ),
        CuratedEntry(
            chapterNumber = 9,
            verseNumber = 26,
            themeCategory = "সরল ভক্তি ও প্রেম",
            inspirationQuote = "ভক্তিভরে একটু ফুল বা জল দিলেও ঈশ্বর তা সাদরে গ্রহণ করেন",
            dailyWisdomBengali = "ঈশ্বর ধনের ঐশ্বর্য দেখেন না, তিনি দেখেন ভক্তের ব্যাকুলতা ও হৃদয়ের নির্মল ভক্তি। সরল ভালোবাসাই ঈশ্বরলাভের সহজতম রাজপথ।",
            dailyPracticeBengali = "আজ সকালে বা সন্ধ্যায় একান্তে একটু সময় নিয়ে নিবেদনপূর্ণ হৃদয়ে প্রভুকে প্রণাম ও ধন্যবাদ জানান।"
        ),
        CuratedEntry(
            chapterNumber = 10,
            verseNumber = 8,
            themeCategory = "সৃষ্টির পরম উৎস",
            inspirationQuote = "আমিই সমস্ত সৃষ্টির পরম উৎস, আমা থেকেই সবকিছু প্রবর্তিত",
            dailyWisdomBengali = "এই নিখিল জগতের সমস্ত সৌন্দর্য, শক্তি ও মহিমার আদি উৎস হলেন পরমাত্মা। এই সত্য জেনে ভক্ত আনন্দে আত্মহারা হন।",
            dailyPracticeBengali = "আজ চারপাশের প্রকৃতির রূপ ও স্নিগ্ধতা দেখে সেই পরম স্রষ্টার করুণা অনুভব করুন।"
        ),
        CuratedEntry(
            chapterNumber = 11,
            verseNumber = 32,
            themeCategory = "সময়ের মর্যাদা ও মহাকাল",
            inspirationQuote = "কাল পরম সংহারক, জীবনের প্রতিটি মুহূর্ত মূল্যবান",
            dailyWisdomBengali = "সময় কারো জন্য অপেক্ষা করে না। ক্ষণভঙ্গুর মানবজীবনের প্রতিটি শ্বাস-প্রশ্বাসকে সৎ কর্ম ও পরমার্থ সাধনায় নিয়োজিত করাই বুদ্ধিমত্তা।",
            dailyPracticeBengali = "আজ আলস্যে সময় অপচয় না করে অন্তত আধা ঘণ্টা সময় কোনো সৃষ্টিশীল বা আত্মউন্নয়নমূলক কাজে ব্যয় করুন।"
        ),
        CuratedEntry(
            chapterNumber = 12,
            verseNumber = 13,
            themeCategory = "করুণা ও অহিংসা",
            inspirationQuote = "দ্বেষহীন, ক্ষমাশীল ও করুণাময় ভক্তই পরমেশ্বরের প্রিয়",
            dailyWisdomBengali = "কারো প্রতি শত্রুতা না রেখে জগতের সমস্ত জীবের প্রতি ক্ষমা ও নিঃস্বার্থ ভালোবাসা প্রদর্শন করাই আধ্যাত্মিক জীবনের সর্বোচ্চ প্রাপ্তি।",
            dailyPracticeBengali = "আজ এমন একজন মানুষের সঙ্গে হাসিমুখে কথা বলুন বা উপকার করুন যার সাথে পূর্বে মনমালিন্য ছিল।"
        ),
        CuratedEntry(
            chapterNumber = 15,
            verseNumber = 15,
            themeCategory = "হৃদয়বিহারী অন্তর্যামী",
            inspirationQuote = "সবার হৃদয়ে অন্তর্যামী রূপে আমি অবস্থান করি",
            dailyWisdomBengali = "ঈশ্বর সুদূর আকাশে নন, তিনি আপনার নিজের হৃদয়ের কেন্দ্রস্থলে সাক্ষী ও আশ্রয়দাতা হিসেবে বিরাজমান। অন্তরেই তাঁর সন্ধান করুন।",
            dailyPracticeBengali = "আজ দিনে অন্তত একবার বুকের মাঝে হাত রেখে অন্তরের সেই পরম প্রিয়তমকে শ্রদ্ধাভরে স্মরণ করুন।"
        ),
        CuratedEntry(
            chapterNumber = 18,
            verseNumber = 65,
            themeCategory = "অনন্য প্রেম ও ভক্তি",
            inspirationQuote = "মন আমাতে অর্পণ করো, তুমি নিশ্চিতভাবেই আমাকে পাবে",
            dailyWisdomBengali = "চিন্তা, অনুভূতি ও কর্ম শ্রীভগবানে নিবেদন করলেই মানুষ ভবসাগরের যাবতীয় ক্লেশ অতিক্রম করে পরমানন্দ লাভ করে।",
            dailyPracticeBengali = "আজ আপনার দিনের প্রতিটি কাজের শুরু ও শেষে মনে মনে ভগবানের পবিত্র নাম জপ করুন।"
        ),
        CuratedEntry(
            chapterNumber = 18,
            verseNumber = 66,
            themeCategory = "পরম আশ্রয় ও অভয়বাণী",
            inspirationQuote = "সর্বধর্ম ত্যাগ করে একমাত্র আমার শরণ নাও, আমি তোমাকে মুক্ত করব",
            dailyWisdomBengali = "গীতার সর্বশেষ ও সর্বশ্রেষ্ঠ অভয়বাণী—ঈশ্বরকে একমাত্র আশ্রয় বানালে কোনো পাপ বা ভয় আত্মাকে স্পর্শ করতে পারে না। তিনি সর্বদা আপনার রক্ষক।",
            dailyPracticeBengali = "আজ সমস্ত ভয় ও দুর্ভাবনা মন থেকে মুছে ফেলে পরম আত্মবিশ্বাস ও আনন্দের সাথে দিনটি যাপন করুন।"
        )
    )

    fun toBengaliDigits(number: Int): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return number.toString().map { ch ->
            if (ch in '0'..'9') bnDigits[ch - '0'] else ch
        }.joinToString("")
    }

    fun getBengaliDateString(calendar: Calendar = Calendar.getInstance()): String {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val monthNames = listOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )
        val dayNames = listOf(
            "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার"
        )

        val dayStr = toBengaliDigits(day)
        val monthStr = monthNames.getOrElse(month) { "" }
        val dayOfWeekStr = dayNames.getOrElse(dayOfWeek - 1) { "" }

        return "$dayStr $monthStr • $dayOfWeekStr"
    }

    fun getCuratedDailyVerse(calendar: Calendar = Calendar.getInstance()): DailyVerseCurated {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        return getCuratedDailyVerseForDay(dayOfYear, getBengaliDateString(calendar))
    }

    fun getCuratedDailyVerseForDay(dayOfYear: Int, dateDisplayBengali: String? = null): DailyVerseCurated {
        val index = (Math.abs(dayOfYear - 1)) % curatedEntries.size
        val entry = curatedEntries[index]
        val verse = GitaContentProvider.getVerse(entry.chapterNumber, entry.verseNumber)

        val dateText = dateDisplayBengali ?: "দিন ${toBengaliDigits(dayOfYear)}"

        return DailyVerseCurated(
            dayOfYear = dayOfYear,
            dateDisplayBengali = dateText,
            verse = verse,
            themeCategory = entry.themeCategory,
            inspirationQuote = entry.inspirationQuote,
            dailyWisdomBengali = entry.dailyWisdomBengali,
            dailyPracticeBengali = entry.dailyPracticeBengali
        )
    }

    fun getRandomCuratedVerse(): DailyVerseCurated {
        val randomIndex = curatedEntries.indices.random()
        val entry = curatedEntries[randomIndex]
        val verse = GitaContentProvider.getVerse(entry.chapterNumber, entry.verseNumber)
        val cal = Calendar.getInstance()

        return DailyVerseCurated(
            dayOfYear = cal.get(Calendar.DAY_OF_YEAR),
            dateDisplayBengali = getBengaliDateString(cal),
            verse = verse,
            themeCategory = entry.themeCategory,
            inspirationQuote = entry.inspirationQuote,
            dailyWisdomBengali = entry.dailyWisdomBengali,
            dailyPracticeBengali = entry.dailyPracticeBengali
        )
    }

    fun getTotalCuratedCount(): Int = curatedEntries.size
}
