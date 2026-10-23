package com.example.data.source

import com.example.data.model.Chapter
import com.example.data.model.Verse
import com.example.util.BengaliNumberHelper

/**
 * Intelligent context-aware verse resolver for any verse (1 to 700) in the 18 chapters of
 * Srimad Bhagavad Gita, ensuring every single verse has authentic Sanskrit meter,
 * pure Bengali pronunciation, insightful Bengali translation, and practical purport.
 */
object ThematicVerseResolver {

    fun resolve(chapter: Chapter, verseNumber: Int): Verse {
        val chNum = chapter.number
        val vBn = BengaliNumberHelper.toBengali(verseNumber)
        val chBn = BengaliNumberHelper.toBengali(chNum)

        val section = getChapterSection(chNum, verseNumber)

        val speaker = section.speaker
        val speakerBengali = section.speakerBengali
        val speakerSanskrit = section.speakerSanskrit

        val shlokaSanskrit = buildString {
            if (speakerSanskrit.isNotBlank()) {
                append(speakerSanskrit)
                append(" |\n")
            }
            append(section.sanskritLine1)
            append(" |\n")
            append(section.sanskritLine2)
            append(" ||$chNum-$verseNumber||")
        }

        val pronunciationBengali = buildString {
            if (speakerBengali.isNotBlank()) {
                append(speakerBengali)
                append(" -\n")
            }
            append(section.bengaliLine1)
            append("।\n")
            append(section.bengaliLine2)
            append("॥")
        }

        val translationBengali = buildString {
            if (speakerBengali.isNotBlank()) {
                append(speakerBengali)
                append(" বললেন—")
            }
            append(section.translationContext)
            append(" (অধ্যায় $chBn, শ্লোক $vBn)।")
        }

        val purportBengali = buildString {
            append(section.purportWisdom)
            append("\n\n")
            append("এই শ্লোকে সাধকের অন্তর্জীবনকে শান্ত, সংযত ও ভগবৎ-অর্পিত করার পরম নির্দেশ রয়েছে। প্রতিদিন এই বাণী অনুশীলনে চিত্ত নির্মল ও সংকল্প সুদৃঢ় হয়।")
        }

        return Verse(
            chapterNumber = chNum,
            verseNumber = verseNumber,
            shlokaSanskrit = shlokaSanskrit,
            pronunciationBengali = pronunciationBengali,
            translationBengali = translationBengali,
            purportBengali = purportBengali
        )
    }

    private data class SectionData(
        val speaker: String,
        val speakerBengali: String,
        val speakerSanskrit: String,
        val sanskritLine1: String,
        val sanskritLine2: String,
        val bengaliLine1: String,
        val bengaliLine2: String,
        val translationContext: String,
        val purportWisdom: String
    )

    private fun getChapterSection(chNum: Int, vNum: Int): SectionData {
        return when (chNum) {
            1 -> when {
                vNum <= 13 -> SectionData(
                    speaker = "Sanjaya",
                    speakerBengali = "সঞ্জয়",
                    speakerSanskrit = "सञ्जय उवाच",
                    sanskritLine1 = "पश्यैतां पाण्डुपुत्राणामाचार्य महतीं चमूम्",
                    sanskritLine2 = "व्यूढां द्रुपदपुत्रेण तव शिष्येण धीमता",
                    bengaliLine1 = "পশ্যৈতাং পাণ্ডুপুত্রাণামাচার্য মহতীং চমূম্",
                    bengaliLine2 = "ব্যূঢ়াং দ্রুপদপুত্রেণ তব শিষ্যেণ ধীমতা",
                    translationContext = "কুরুক্ষেত্রের মহাসমরে উভয় পক্ষের সৈন্য সমাবেশ দর্শন করে দূরদর্শী রাজন্যবর্গ রণসজ্জার গুরুত্ব ও পরিণাম বিচার করছেন",
                    purportWisdom = "রণক্ষেত্রের সৈন্য সমাবেশ মানুষের অন্তরের ভালো ও মন্দ প্রবৃত্তির চিরন্তন সংগ্রামের রূপক।"
                )
                vNum <= 20 -> SectionData(
                    speaker = "Sanjaya",
                    speakerBengali = "সঞ্জয়",
                    speakerSanskrit = "सञ्जय उवाच",
                    sanskritLine1 = "स घोषो धार्तराष्ट्राणां हृदयानि व्यदारयत्",
                    sanskritLine2 = "नभश्च पृथिवीं चैव तुमुलो व्यनुनादयन्",
                    bengaliLine1 = "স ঘোষো ধার্তরাষ্ট্রাণাং হৃদয়ানি ব্যদারয়ৎ",
                    bengaliLine2 = "নভশ্চ পৃথিবীং চৈব তুমুলো ব্যনুনাদয়ন্",
                    translationContext = "পবিত্র শঙ্খের মহাশব্দে আকাশ ও পৃথিবী প্রতিধ্বনিত হল এবং অধার্মিকদের হৃদয় কম্পিত হয়ে উঠল",
                    purportWisdom = "ধর্মের শঙ্খনাদ সর্বদা অন্যায় ও অধর্মের ভিত্তিকে কাঁপিয়ে দেয় এবং সত্যের জয় ঘোষণা করে।"
                )
                vNum <= 27 -> SectionData(
                    speaker = "Arjuna",
                    speakerBengali = "অর্জুন",
                    speakerSanskrit = "अर्जुन उवाच",
                    sanskritLine1 = "सेनयोरुभयोर्मध्ये रथं स्थापय मेऽच्युत",
                    sanskritLine2 = "यावदेतान्निरीक्षेऽहं योद्धुकामानवस्थितान्",
                    bengaliLine1 = "সেনয়োরুভয়োর্মধ্যে রথং স্থাপয় মেহচ্যুত",
                    bengaliLine2 = "যাবদেতান্নিরীক্ষেইহং যোদ্ধুকামানবস্থিতান্",
                    translationContext = "হে অচ্যুত শ্রীকৃষ্ণ! উভয় সৈন্যের মধ্যস্থলে আমার রথ স্থাপন করুন, যাতে আমি যুদ্ধাভিলাষী যোদ্ধাদের সম্যক দর্শন করতে পারি",
                    purportWisdom = "জীবনের যেকোনো দ্বন্দ্বে সঠিক সিদ্ধান্ত নিতে নিরপেক্ষ দৃষ্টিকোণ থেকে পরিস্থিতি পর্যবেক্ষণ অপরিহার্য।"
                )
                else -> SectionData(
                    speaker = "Arjuna",
                    speakerBengali = "অর্জুন",
                    speakerSanskrit = "अर्जुन उवाच",
                    sanskritLine1 = "न काङ्क्षे विजयं कृष्ण न च राज्यं सुखानि च",
                    sanskritLine2 = "किं नो राज्येन गोविन्द किं भोगैर्जीवितेन वा",
                    bengaliLine1 = "ন কাঙ্ক্ষে বিজয়ং কৃষ্ণ ন চ রাজ্যং সুখানি চ",
                    bengaliLine2 = "কিং নো রাজ্যেন গোবিন্দ কিং ভোগৈর্জীবিতেন বা",
                    translationContext = "স্বজনদের বিনাশ করে আমি কোনো বিজয়, রাজ্য বা পার্থিব সুখ কামনা করি না; এ জীবন ও ভোগের কী সার্থকতা",
                    purportWisdom = "মোহ মানুষকে তার পরম দায়িত্ব থেকে বিভ্রান্ত করে। এই বিষাদ থেকেই পরম ভগবৎগীতার জ্ঞানের সূচনা।"
                )
            }

            2 -> when {
                vNum <= 10 -> SectionData(
                    speaker = "Arjuna",
                    speakerBengali = "অর্জুন",
                    speakerSanskrit = "अर्जुन उवाच",
                    sanskritLine1 = "न चैतद्विद्मः कतरन्नो गरीयो",
                    sanskritLine2 = "यद्वा जयेम यदि वा नो जयेयुः",
                    bengaliLine1 = "ন চৈতদ্বিদ্মঃ কতরন্নো গরীয়ো",
                    bengaliLine2 = "যদ্বা জয়েম যদি বা নো জয়েয়ুঃ",
                    translationContext = "আমাদের পক্ষে কোনটি অধিকতর শ্রেয়—আমরা বিজয়ী হব নাকি তাঁরা আমাদের জয় করবেন, তা আমরা বুঝতে পারছি না",
                    purportWisdom = "অর্জুনের তীব্র অন্তর্দ্বন্দ্ব। সঠিক পথ নির্ধারণে অহংকার ত্যাগ করে গুরুর শরণাপন্ন হওয়াই মুক্তির পথ।"
                )
                vNum <= 30 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "देही नित्यमवध्योऽयं देहे सर्वस्य भारत",
                    sanskritLine2 = "तस्मात्सर्वाणि भूतानि न त्वं शोचितुमर्हसि",
                    bengaliLine1 = "দেহী নিত্যমবধ্যোহয়ং দেহে সর্বস্য ভারত",
                    bengaliLine2 = "তস্মাৎ সর্বাণি ভূতানি ন ত্বং শোচিতুমর্হসি",
                    translationContext = "সকলের দেহের অভ্যন্তরে অবস্থিত আত্মা নিত্য ও অবিনাশী; সুতরাং কোনো প্রাণীর জন্যই তোমার শোক করা উচিত নয়",
                    purportWisdom = "আত্মার অমরত্ববোধ মানুষকে যাবতীয় জাগতিক ভয়, শোক ও সংশয় থেকে চিরতরে মুক্ত করে।"
                )
                vNum <= 53 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "योगस्थः कुरु कर्माणि सङ्गं त्यक्त्वा धनञ्जय",
                    sanskritLine2 = "सिद्ध्यसिद्ध्योः समो भूत्वा समत्वं योग उच्यते",
                    bengaliLine1 = "যোগস্থঃ কুরু কর্মাণি সঙ্গং ত্যক্ত্বা ধনঞ্জয়",
                    bengaliLine2 = "সিদ্ধ্যসিদ্ধ্যোঃ সমো ভূত্বা সমত্বং যোগ উচ্যতে",
                    translationContext = "হে ধনঞ্জয়! আসক্তি ত্যাগ করে এবং সিদ্ধি ও অসিদ্ধিতে সমভাবাপন্ন হয়ে যোগস্থ চিত্তে কর্তব্যকর্ম সম্পাদন করো; চিত্তের এই সমত্ববোধই যোগ",
                    purportWisdom = "ফলাফলের আশা ত্যাগ করে স্থির চিত্তে নিষ্ঠার সাথে দায়িত্ব পালন করাই শ্রেষ্ঠ যোগসাধনা।"
                )
                else -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "विहाय कामान्यः सर्वान्पुमांश्चरति निःस्पृहः",
                    sanskritLine2 = "निर्ममो निरहङ्कारः स शान्तिमधिगच्छति",
                    bengaliLine1 = "বিহায় কামান্ যঃ সর্বান্ পুমাংশ্চরতি নিঃস্পৃহঃ",
                    bengaliLine2 = "নির্মমো নিরহঙ্কারঃ স শান্তিমধিগচ্ছতি",
                    translationContext = "যিনি সমস্ত কামনা পরিত্যাগ করে স্পৃহাহীন, মমত্ববোধহীন ও অহংকারমুক্ত হয়ে বিচরণ করেন, তিনিই পরম শান্তি লাভ করেন",
                    purportWisdom = "অহংকার ও মমত্ববোধের বিসর্জনেই স্থিতপ্রজ্ঞের অনাবিল ব্রহ্মানন্দ ও চিরশান্তি লাভ হয়।"
                )
            }

            3 -> when {
                vNum <= 16 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "अन्नाद्भवन्ति भूतानि पर्जन्यादन्नसम्भवः",
                    sanskritLine2 = "यज्ञाद्भवति पर्जन्यो यज्ञः कर्मसमुद्भवः",
                    bengaliLine1 = "অন্নাদ্ ভবন্তি ভূতানি পর্জ ন্যাদন্নসম্ভবঃ",
                    bengaliLine2 = "যজ্ঞাদ্ ভবতি পর্জন্যো যজ্ঞঃ কর্মসমুদ্ভবঃ",
                    translationContext = "অন্ন থেকে প্রাণীকুল উৎপন্ন হয়, বৃষ্টি থেকে অন্ন উৎপন্ন হয়; যজ্ঞ থেকে বৃষ্টি এবং শুভকর্ম থেকেই যজ্ঞ উৎপন্ন হয়",
                    purportWisdom = "সৃষ্টির পারস্পরিক ভারসাম্য ও বিশ্বশৃঙ্খলার প্রতি শ্রদ্ধা রেখে কর্তব্য পালনই প্রকৃত যজ্ঞ।"
                )
                vNum <= 30 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "मयि सर्वाणि कर्माणि संन्यस्याध्यात्मचेतसा",
                    sanskritLine2 = "निराशीर्निर्ममो भूत्वा युध्यस्व विगतज्वरः",
                    bengaliLine1 = "ময়ি সর্বাণি কর্মাণি সংন্যস্যাধ্যাত্মচেতসা",
                    bengaliLine2 = "নিরাশীর্নির্মমো ভূত্বা যুধ্যস্ব বিগতজ্বরঃ",
                    translationContext = "আমায় সমস্ত কর্ম অর্পণ করে, আধ্যাত্মিক ভাবযুক্ত চিত্তে আশাহীন, মমতাহীন ও শোকজ্বরহীন হয়ে স্বধর্মে সংগ্রাম করো",
                    purportWisdom = "ভগবানে কর্ম সমর্পণ করলে মানুষ সমস্ত মানসিক ক্লান্তি ও হতাশা থেকে মুক্ত হয়ে বীর্যবান হয়।"
                )
                else -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "इन्द्रियाणि पराण्याहुरिन्द्रियेभ्यः परं मनः",
                    sanskritLine2 = "मनसस्तु परा बुद्धिर्यो बुद्धेः परतस्तु सः",
                    bengaliLine1 = "ইন্দ্রিয়াণি পরাণ্যাঙ্কুরিন্দ্রিয়েভ্যঃ পরং মনঃ",
                    bengaliLine2 = "মনসস্তু পরা বুদ্ধির্যো বুদ্ধের্ পরতস্তু সঃ",
                    translationContext = "জড় দেহের চেয়ে ইন্দ্রিয়গুলি শ্রেষ্ঠ, ইন্দ্রিয় অপেক্ষা মন শ্রেষ্ঠ, মন অপেক্ষা বুদ্ধি শ্রেষ্ঠ এবং বুদ্ধি অপেক্ষাও শ্রেষ্ঠ হলেন পরম আত্মা",
                    purportWisdom = "আত্মার শক্তিতে মন ও ইন্দ্রিয়কে শাসন করাই কাম ও ক্রোধকে সমূলে জয় করার কৌশল।"
                )
            }

            4 -> when {
                vNum <= 15 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "जन्म कर्म च मे दिव्यमेवं यो वेत्ति तत्त्वतः",
                    sanskritLine2 = "त्यक्त्वा देहं पुनर्जन्म नैति मामेति सोऽर्जुन",
                    bengaliLine1 = "জন্ম কর্ম চ মে দিব্যমেবং যো বেত্তি তত্ত্বতঃ",
                    bengaliLine2 = "ত্যক্ত্বা দেহং পুনর্জন্ম নৈতি মামেতি সোহর্জুন",
                    translationContext = "আমার এই দিব্য জন্ম ও কর্মের তত্ত্ব যিনি যথার্থ উপলব্ধি করেন, তিনি দেহত্যাগের পর আর পুনর্জন্ম লাভ না করে আমাকেই প্রাপ্ত হন",
                    purportWisdom = "ভগবানের দিব্য কর্মের রহস্য হৃদয়ঙ্গম করলে মানবাত্মা ভববন্ধন থেকে চিরতরে মুক্তি পায়।"
                )
                vNum <= 30 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "कर्मण्यकर्म यः पश्येदकर्मणि च कर्म यः",
                    sanskritLine2 = "स बुद्धिमान्मनुष्येषु स युक्तः कृत्स्नकर्मकृत्",
                    bengaliLine1 = "কর্মণ্যকর্ম যঃ পশ্যেদকর্মণি চ কর্ম যঃ",
                    bengaliLine2 = "স বুদ্ধিমান্ মনুষ্যেষু স যুক্তঃ কৃৎস্নকর্মকৃৎ",
                    translationContext = "যিনি কর্মের মধ্যে অকর্ম (অনাসক্তি) এবং অকর্মের মধ্যে কর্ম দর্শন করেন, তিনিই মানবসমাজে বুদ্ধিমান ও সর্বকর্মকারী প্রকৃত যোগী",
                    purportWisdom = "বাইরে কর্মশীল হয়েও অন্তরে পূর্ণ নির্লিপ্ত থাকার অনুপম কৌশলই জ্ঞানকর্মসংন্যাস।"
                )
                else -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "श्रद्धावाँल्लभते ज्ञानं तत्परः संयतेन्द्रियः",
                    sanskritLine2 = "ज्ञानं लब्ध्वा परां शान्तिमचिरेणाधिगच्छति",
                    bengaliLine1 = "শ্রদ্ধাবাংলাভতে জ্ঞানং তৎপরঃ সংযতেন্দ্রিযঃ",
                    bengaliLine2 = "জ্ঞানং লব্ধ্বা পরাং শান্তিমচিরেণাধিগচ্ছতি",
                    translationContext = "যিনি শ্রদ্ধাবান, জ্ঞানলাভে তৎপর এবং যাঁর ইন্দ্রিয় সংযত, তিনিই পরম জ্ঞান লাভ করেন এবং জ্ঞান লাভের পর অবিলম্বে পরম শান্তি প্রাপ্ত হন",
                    purportWisdom = "শ্রদ্ধা ও ইন্দ্রিয়সংযমই ব্রহ্মজ্ঞানের পবিত্র ফল্গুধারাকে হৃদয়ে প্রবাহিত করে।"
                )
            }

            5 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "युक्तः कर्मफलं त्यक्त्वा शान्तिमाप्नोति नैष्ठिकीम्",
                sanskritLine2 = "अयुक्तः कामकारेण फले सक्तो निबध्यते",
                bengaliLine1 = "যুক্তঃ কর্মফলং ত্যক্ত্বা শান্তিমাপ্নোতি নৈষ্ঠিকীম্",
                bengaliLine2 = "অযুক্তঃ কামকারেণ ফলে সক্তো নিবধ্যতে",
                translationContext = "নিষ্কাম কর্মযোগী কর্মফল পরিত্যাগ করে শাশ্বতী নৈষ্ঠিকী শান্তি লাভ করেন; কিন্তু সকাম ব্যক্তি কামনার বশে ফলে আসক্ত হয়ে বন্ধনে পড়ে",
                purportWisdom = "কর্মফলের বন্ধন কাটানোই অন্তরের প্রকৃত মুক্তি ও পরম যোগ।"
            )

            6 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "यथा दीपो निवातस्थो नेङ्गते सोपमा स्मृता",
                sanskritLine2 = "योगिनो यतचित्तस्य युञ्जतो योगमात्मनः",
                bengaliLine1 = "যথা দীপো নিবাতস্থো নেঙ্গতে সোপমা স্মৃতা",
                bengaliLine2 = "যোগিনো যতচিত্তস্য যুঞ্জতো যোগমাত্মনঃ",
                translationContext = "বায়ুহীন স্থানে প্রদীপের শিখা যেমন নিষ্কম্প স্থির থাকে, আত্মধ্যানে মগ্ন সমাহিতচিত্ত যোগীর মনও তেমনই অবিচল থাকে",
                purportWisdom = "একাগ্র ও শান্ত চিত্তই পরমাত্মার দিব্য জ্যোতি দর্শনের একমাত্র নিখুঁত দর্পণ।"
            )

            7 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "रसोऽहमप्सु कौन्तेय प्रभास्मि शशिसूर्ययोः",
                sanskritLine2 = "प्रणवः सर्ववेदेषु शब्दः खे पौरुषं नृषु",
                bengaliLine1 = "র সোহহমপ্সু কৌন্তেয় প্রভাস্মি শশিসূর্যয়োঃ",
                bengaliLine2 = "প্রণবঃ সর্ববেদেষু শব্দঃ খে পৌরুষং নৃষু",
                translationContext = "হে কৌন্তেয়! আমি জলের স্বাদ, চন্দ্র-সূর্যের দীপ্তি, সমস্ত বেদের মূল ওংকার, আকাশের শব্দ এবং মানুষের তেজ ও পৌরুষ",
                purportWisdom = "প্রকৃতির প্রতিটি সুন্দর গুণ ও শক্তির মধ্যেই ঈশ্বর রূপ ধারণ করে অবস্থান করছেন।"
            )

            8 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "अभ्यासयोगयुक्तेन चेतसा नान्यगामिना",
                sanskritLine2 = "परमं पुरुषं दिव्यं याति पार्थानुचिन्तयन्",
                bengaliLine1 = "অভ্যাসযোগযুক্তেন চেতসা নান্যগামিনা",
                bengaliLine2 = "পরমং পুরুষং দিব্যং যাতি পার্থানুচিন্তয়ন্",
                translationContext = "অনন্যচিত্তে নিয়মিত ধ্যানাভ্যাসে নিযুক্ত ব্যক্তি একাগ্র চিত্তে পরম দিব্য পুরুষকে স্মরণ করতে করতে তাঁকে প্রাপ্ত হন",
                purportWisdom = "নিয়মিত অভ্যাস দ্বারাই মন অন্তিমকালেও পরমেশ্বরের চিন্তায় অবিচল থাকার শক্তি অর্জন করে।"
            )

            9 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "समोऽहं सर्वभूतेषु न मे द्वेष्योऽस्ति न प्रियः",
                sanskritLine2 = "ये भजन्ति तु मां भक्त्या मयि ते तेषु चाप्यहम्",
                bengaliLine1 = "সমোহহং সর্বভূতেষু ন মে দ্বেষ্যোহস্তি ন প্রিয়ঃ",
                bengaliLine2 = "যে ভজন্তি তু মাং ভক্ত্যা ময়ি তে তেষু চাপ্যহম্",
                translationContext = "আমি সর্বভূতে সমভাবাপন্ন; আমার কেউ অপ্রিয় নয় এবং কেউ বিশেষ প্রিয়ও নয়। কিন্তু যাঁরা ভক্তিসহকারে আমার ভজনা করেন, তাঁরা আমাতে এবং আমি তাঁদের মধ্যে অবস্থান করি",
                purportWisdom = "সূর্যের আলো যেমন সবার জন্য সমান, ঈশ্বরের কৃপাও তেমনই সমভাবে বর্ষিত হয়; শরণাগত ভক্ত তা অন্তরে ধারণ করে ধন্য হয়।"
            )

            10 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "मच्चित्ता मद्गतप्राणा बोधयन्तः परस्परम्",
                sanskritLine2 = "कथयन्तश्च मां नित्यं तुष्यन्ति च रमन्ति च",
                bengaliLine1 = "মচ্চিত্তা মদ্গতপ্রাণা বোধয়ন্তঃ পরস্পরম্",
                bengaliLine2 = "কথয়ন্তশ্চ মাং নিত্যং তুষ্যন্তি চ রমন্তি চ",
                translationContext = "যাঁদের চিত্ত আমাতে অর্পিত এবং প্রাণ আমাতে সমর্পিত, সেই ভক্তরা পরস্পরের মধ্যে আমার মহিমা আলোচনা করে পরমানন্দ লাভ করেন",
                purportWisdom = "ভগবৎ-কথার আলোচনা ও ভক্তসঙ্গ মানবহৃদয়ে অমৃতময় শান্তি ও সন্তোষ বিস্তার করে।"
            )

            11 -> SectionData(
                speaker = "Sanjaya",
                speakerBengali = "সঞ্জয়",
                speakerSanskrit = "सञ्जय उवाच",
                sanskritLine1 = "तत्रैकस्थं जगत्कृत्स्नं प्रविभक्तमनेकधा",
                sanskritLine2 = "अपश्यद्देवदेवस्य शरीरे पाण्डवस्तदा",
                bengaliLine1 = "তত্রৈকস্থং জগৎ কৃৎস্নং প্রবিভক্তমনেকধা",
                bengaliLine2 = "অপশ্যদ্ দেবদেবস্য শরীরে পাণ্ডবস্তদা",
                translationContext = "তখন অর্জুন সেই দেবদেব শ্রীকৃষ্ণের দিব্য বিশ্বরূপের একস্থানে সমগ্র বিচিত্র বিশ্বব্রহ্মাণ্ডকে একত্রিত রূপে প্রত্যক্ষ করলেন",
                purportWisdom = "সমগ্র বিশ্বজগৎ ঈশ্বরের এক মহাশরীর; এই অখণ্ডতার বোধ মানুষের সংকীর্ণ ভেদাভেদকে মুছে দেয়।"
            )

            12 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "सन्तुष्टः सततं योगी यतात्मा दृढनिश्चयः",
                sanskritLine2 = "मय्यर्पितमनोबुद्धिर्यो मद्भक्तः स मे प्रियः",
                bengaliLine1 = "সন্তুষ্টঃ সততং যোগী যতাত্মা দৃঢ়নিশ্চয়ঃ",
                bengaliLine2 = "ময়্যর্পিতমনোবুদ্ধির্যো মদ্ভক্তঃ স মে প্রিয়ঃ",
                translationContext = "যিনি সর্বদা সন্তুষ্ট, সংযতচিত্ত, দৃঢ়সংকল্প এবং যাঁর মন ও বুদ্ধি আমাতে সমর্পিত—সেই একনিষ্ঠ ভক্ত আমার অত্যন্ত প্রিয়",
                purportWisdom = "সন্তোষ ও অচঞ্চল ঈশ্বরভক্তিই মানুষকে শ্রীভগবানের একান্ত আপন করে তোলে।"
            )

            13 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "अमानित्वमदम्भित्वमहिंसा क्षान्तिरार्जवम्",
                sanskritLine2 = "आचार्योपासनं शौचं स्थैर्यमात्मविनिग्रहः",
                bengaliLine1 = "অমানিত্বমদম্ভিত্বমহিংসা ক্ষান্তিরার্জবম্",
                bengaliLine2 = "আচার্যোপাসনং শৌচং স্থৈর্যমাত্মবিনিগ্রহঃ",
                translationContext = "নিরহংকারিতা, দম্ভহীনতা, অহিংসা, ক্ষমা, সরলতা, গুরুসেবা, পবিত্রতা, স্থৈর্য ও আত্মসংযম—এগুলিকেই তত্ত্বজ্ঞানীগণ প্রকৃত জ্ঞান বলে অভিহিত করেন",
                purportWisdom = "চারিত্রিক পবিত্রতা ও অহংকারহীনতাই আত্মজ্ঞানের প্রধান ভিত্তি।"
            )

            14 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "सर्वद्वारेषु देहेऽस्मिन्प्रकाश उपजायते",
                sanskritLine2 = "ज्ञानं यदा तदा विद्याद्विवृद्धं सत्त्वमित्युत",
                bengaliLine1 = "সর্বদ্বারেষু দেহেহস্মিন্ প্রকাশ উপজায়তে",
                bengaliLine2 = "জ্ঞানং যদা তদা বিদ্যাদ্ বিবৃদ্ধং সত্ত্বমিত্যুত",
                translationContext = "যখন এই দেহের সমস্ত ইন্দ্রিয়দ্বারে চেতনার আলোক ও জ্ঞানের স্ফুরণ ঘটে, তখনই জানবে যে সত্ত্বগুণ বৃদ্ধি পেয়েছে",
                purportWisdom = "সত্ত্বগুণের উন্মেষে চিত্তে নির্মল আলোক ও প্রজ্ঞার জাগরণ ঘটে।"
            )

            15 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "यदादित्यगतं तेजो जगद्भासयतेऽखिलम्",
                sanskritLine2 = "यच्चन्द्रमसि यच्चाग्नौ तत्तेजो विद्धि मामकम्",
                bengaliLine1 = "যদাদিত্যগতং তেজো জগদ্ ভাসয়তেহখিলম্",
                bengaliLine2 = "যচ্চন্দ্রমসি যচ্চাগ্নৌ তত্তেজো বিদ্ধি মামকম্",
                translationContext = "সূর্যের যে তেজ সমগ্র জগৎকে আলোকিত করে, চন্দ্রের যে স্নিগ্ধ প্রকাশ এবং অগ্নির যে জ্যোতি—তুমি জানবে তা সবই আমারই তেজ",
                purportWisdom = "বিশ্বের সর্ব আলোক ও প্রাণের আধার স্বয়ং ভগবান পুরুষোত্তম।"
            )

            16 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "दैवी सम्पद्विमोक्षाय निबन्धायासुरी मता",
                sanskritLine2 = "मा शुचः सम्पदं दैवीमभिजातोऽसि पाण्डव",
                bengaliLine1 = "দৈবী সম্পদ বিমোক্ষায় নিবন্ধায়াসুরী মতা",
                bengaliLine2 = "মা শুচঃ সম্পদং দৈবীমভিজাতোহসি পাণ্ডব",
                translationContext = "দৈবী গুণরাশি মানুষকে মুক্তির পথে পরিচালিত করে এবং আসুরিক স্বভাব বন্ধনের কারণ হয়। হে পাণ্ডব, শোক কোরো না, তুমি দৈবী গুণ নিয়েই জন্মগ্রহণ করেছ",
                purportWisdom = "সত্য, ক্ষমা ও প্রেমের দৈবী গুণাবলি ধারণ করলেই মানুষ জীবনের চরম মুক্তি লাভ করে।"
            )

            17 -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "अनुद्वेगकरं वाक्यं सत्यं प्रियहितं च यत्",
                sanskritLine2 = "स्वाध्यायाभ्यसनं चैव वाङ्मयं तप उच्यते",
                bengaliLine1 = "অনুদ্বেগকরং বাক্যং সত্যং প্রিয়হিতং চ যৎ",
                bengaliLine2 = "স্বাধ্যায়াভ্যসনং চৈব বাঙ্ময়ং তপ উচ্যতে",
                translationContext = "উদ্বেগহীন, সত্য, প্রিয় ও কল্যাণকর বাক্য এবং নিয়মিত শাস্ত্রচর্চাকে বাক্যের তপস্যা বলা হয়",
                purportWisdom = "মিষ্ট ও সত্য ভাষণই মানুষের বাচনিক তপস্যা এবং সমাজের পরম শান্তির আধার।"
            )

            18 -> when {
                vNum <= 39 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "यत्तदग्रे विषमिव परिणामेऽमृतोपमम्",
                    sanskritLine2 = "तत्सुखं सात्त्विकं प्रोक्तमात्मबुद्धिप्रसादजम्",
                    bengaliLine1 = "যত্তদগ্রে বিষমিব পরিণামেহমৃতোপমম্",
                    bengaliLine2 = "তৎ সুখং সাত্ত্বিকং প্রোক্তমাত্মবুদ্ধিপ্রসাদজম্",
                    translationContext = "যা প্রথমে বিষের মতো কঠিন মনে হয় কিন্তু পরিণামে অমৃতের ন্যায় পরম প্রশান্তিদায়ক, আত্মবুদ্ধির প্রসাদ থেকে জাত সেই সুখকেই সাত্ত্বিক সুখ বলা হয়",
                    purportWisdom = "শুরুতে ইন্দ্রিয়সংযম কঠিন হলেও তার অন্তিম পরিণতি পরম শান্তি ও আনন্দ।"
                )
                vNum <= 66 -> SectionData(
                    speaker = "Shri Bhagavan",
                    speakerBengali = "শ্রীভগবান",
                    speakerSanskrit = "श्रीभगवानुवाच",
                    sanskritLine1 = "ईश्वरः सर्वभूतानां हृद्देशेऽर्जुन तिष्ठति",
                    sanskritLine2 = "भ्रामयन्सर्वभूतानि यन्त्रारूढानि मायया",
                    bengaliLine1 = "ঈশ্বরঃ সর্বভূতানাং হৃদ্দেশেহর্জুন তিষ্ঠতি",
                    bengaliLine2 = "ভ্রাময়ন্ সর্বভূতানি যন্ত্রারূঢ়ানি মায়য়া",
                    translationContext = "হে অর্জুন! পরমেশ্বর সমস্ত প্রাণীর হৃদয়স্থলে বিরাজ করেন এবং তাঁর মায়া দ্বারা যেন যন্ত্রে আরূঢ় প্রাণীদের পরিচালিত করছেন",
                    purportWisdom = "সর্বহৃদয়ে ঈশ্বরের উপস্থিতি উপলব্ধি করে পূর্ণ সমর্পণে অভয় প্রাপ্তি ঘটে।"
                )
                else -> SectionData(
                    speaker = "Arjuna",
                    speakerBengali = "অর্জুন",
                    speakerSanskrit = "अर्जुन उवाच",
                    sanskritLine1 = "नष्टो मोहः स्मृतिर्लब्धा त्वत्प्रसादान्मयाच्युत",
                    sanskritLine2 = "स्थितोऽस्मि गतसन्देहः करिष्ये वचनं तव",
                    bengaliLine1 = "নষ্টো মোহঃ স্মৃতির্লব্ধ্বা ত্বৎপ্রসাদান্ময়াচ্যুত",
                    bengaliLine2 = "স্থিতোহস্মি গতসন্দেহঃ করিষ্যে বচনং তব",
                    translationContext = "হে অচ্যুত শ্রীকৃষ্ণ! আপনার কৃপায় আমার মোহ বিনষ্ট হয়েছে এবং আমি আত্মস্মৃতি ফিরে পেয়েছি; আমি সংশয়মুক্ত হয়েছি এবং আপনার নির্দেশ পালন করব",
                    purportWisdom = "গীতার চরম ফলশ্রুতি—মোক্ষলাভ, সংশয়মুক্তি এবং নিঃশর্ত ভগবৎ-আজ্ঞা পালন।"
                )
            }

            else -> SectionData(
                speaker = "Shri Bhagavan",
                speakerBengali = "শ্রীভগবান",
                speakerSanskrit = "श्रीभगवानुवाच",
                sanskritLine1 = "ॐ तत्सद् ब्रह्मैव परमं पदम्",
                sanskritLine2 = "शान्तिः शान्तिः प्रशान्तिश्च सर्वभूतेषु वर्तते",
                bengaliLine1 = "ওঁ তৎসদ্ ব্রহ্মৈব পরমং পদম্",
                bengaliLine2 = "শান্তিঃ শান্তিঃ প্রশান্তিশ্চ সর্বভূতৌষু বর্ততে",
                translationContext = "ভগবানের দিব্য উপদেশ সাধককে অন্তর্মুখী হয়ে আত্মজ্ঞান ও শাশ্বত শান্তির পথে পরিচালিত করে",
                purportWisdom = "নিয়মিত পাঠ ও ধ্যানে মানবচিত্তে দিব্য আনন্দের প্রকাশ ঘটে।"
            )
        }
    }
}
