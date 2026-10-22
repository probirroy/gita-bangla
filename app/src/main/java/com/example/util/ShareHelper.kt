package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.data.model.Verse
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    fun buildVerseShareText(verse: Verse): String {
        return buildString {
            append("শ্রীমদ্ভগবদ্গীতা • অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}\n\n")
            append("শ্লোক:\n${verse.pronunciationBengali}\n\n")
            append("সরল বঙ্গানুবাদ:\n${verse.translationBengali}\n\n")
            if (verse.purportBengali.isNotBlank()) {
                append("আত্মচিন্তন:\n${verse.purportBengali}\n\n")
            }
            append("— শ্রীমদ্ভগবদ্গীতা বাংলা")
        }
    }

    fun shareVerse(context: Context, verse: Verse) {
        shareVerseText(context, verse)
    }

    fun shareVerseText(context: Context, verse: Verse) {
        val shareText = buildVerseShareText(verse)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "শ্লোক টেক্সট শেয়ার করুন").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun shareVerseWithReflection(context: Context, verse: Verse, reflectionText: String) {
        val shareText = buildString {
            append("শ্রীমদ্ভগবদ্গীতা • অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}\n\n")
            append("শ্লোক:\n${verse.pronunciationBengali}\n\n")
            append("সরল বঙ্গানুবাদ:\n${verse.translationBengali}\n\n")
            append("আমার ব্যক্তিগত চিন্তন ও উপলব্ধি:\n\"$reflectionText\"\n\n")
            append("— শ্রীমদ্ভগবদ্গীতা বাংলা")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "চিন্তন শেয়ার করুন").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun createStaticLayout(
        text: CharSequence,
        paint: TextPaint,
        width: Int,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
        spacingMult: Float = 1.35f,
        spacingAdd: Float = 0f
    ): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width.coerceAtLeast(100))
                .setAlignment(alignment)
                .setLineSpacing(spacingAdd, spacingMult)
                .setIncludePad(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, paint, width.coerceAtLeast(100), alignment, spacingMult, spacingAdd, true)
        }
    }

    /**
     * Generates a devotional high-resolution screenshot card bitmap of the whole Bengali Shlok.
     */
    fun generateVerseCardBitmap(context: Context, verse: Verse): Bitmap {
        val cardWidth = 1080
        val horizontalMargin = 72
        val contentWidth = cardWidth - (horizontalMargin * 2)

        // Paints for Bengali texts and decorative elements
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(183, 28, 28) // Deep Crimson Sacred Red
            textSize = 44f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }

        val chapterBadgePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(230, 81, 0) // Saffron
            textSize = 32f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val shlokPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(38, 28, 20) // Deep Dark Warm Charcoal
            textSize = 38f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }

        val sectionHeadingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(197, 155, 39) // Gold Accent
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val translationPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(44, 44, 44) // Charcoal
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val purportHeadingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(0, 105, 92) // Lotus Teal
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val purportPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(78, 52, 46) // Warm Brown
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(117, 117, 117)
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Layout measurements
        val titleLayout = createStaticLayout("॥ শ্রীমদ্ভগবদ্গীতা ॥", titlePaint, contentWidth, Layout.Alignment.ALIGN_CENTER)
        val chapterBadgeLayout = createStaticLayout("অধ্যায় ${verse.chapterNumber} • শ্লোক ${verse.verseNumber}", chapterBadgePaint, contentWidth, Layout.Alignment.ALIGN_CENTER)
        val shlokLayout = createStaticLayout("॥ ${verse.pronunciationBengali} ॥", shlokPaint, contentWidth, Layout.Alignment.ALIGN_CENTER, 1.4f)
        val transHeadingLayout = createStaticLayout("— সরল বঙ্গানুবাদ —", sectionHeadingPaint, contentWidth, Layout.Alignment.ALIGN_CENTER)
        val translationLayout = createStaticLayout(verse.translationBengali, translationPaint, contentWidth, Layout.Alignment.ALIGN_CENTER, 1.35f)

        val hasPurport = verse.purportBengali.isNotBlank()
        val purportHeadingLayout = if (hasPurport) createStaticLayout("— আত্মচিন্তন ও উপলব্ধি —", purportHeadingPaint, contentWidth, Layout.Alignment.ALIGN_CENTER) else null
        val purportLayout = if (hasPurport) createStaticLayout(verse.purportBengali, purportPaint, contentWidth, Layout.Alignment.ALIGN_CENTER, 1.35f) else null

        val footerLayout = createStaticLayout("— শ্রীমদ্ভগবদ্গীতা বাংলা অ্যাপ —", footerPaint, contentWidth, Layout.Alignment.ALIGN_CENTER)

        // Compute total dynamic height
        var totalHeight = 80 // Top padding
        totalHeight += titleLayout.height + 16
        totalHeight += chapterBadgeLayout.height + 40
        totalHeight += 24 // Divider 1
        totalHeight += 36
        totalHeight += shlokLayout.height + 40
        totalHeight += 24 // Divider 2
        totalHeight += 36
        totalHeight += transHeadingLayout.height + 16
        totalHeight += translationLayout.height + 40

        if (hasPurport && purportHeadingLayout != null && purportLayout != null) {
            totalHeight += purportHeadingLayout.height + 16
            totalHeight += purportLayout.height + 40
        }

        totalHeight += 24 // Divider 3
        totalHeight += 32
        totalHeight += footerLayout.height + 80 // Bottom padding
        totalHeight = totalHeight.coerceAtLeast(960)

        // Allocate bitmap and canvas
        val bitmap = Bitmap.createBitmap(cardWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background: Warm Ivory Sacred Linear Gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, totalHeight.toFloat(),
                Color.rgb(255, 253, 248), // Top: Ivory
                Color.rgb(255, 248, 235), // Bottom: Warm soft parchment
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, cardWidth.toFloat(), totalHeight.toFloat(), bgPaint)

        // Outer ornate border
        val outerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(230, 81, 0) // Saffron
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        val outerRect = RectF(24f, 24f, (cardWidth - 24).toFloat(), (totalHeight - 24).toFloat())
        canvas.drawRoundRect(outerRect, 32f, 32f, outerBorderPaint)

        // Inner golden border
        val innerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(197, 155, 39) // Gold Accent
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
        }
        val innerRect = RectF(38f, 38f, (cardWidth - 38).toFloat(), (totalHeight - 38).toFloat())
        canvas.drawRoundRect(innerRect, 22f, 22f, innerBorderPaint)

        // Decorative corner dots
        val cornerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(197, 155, 39)
            style = Paint.Style.FILL
        }
        val dotRadius = 6f
        canvas.drawCircle(52f, 52f, dotRadius, cornerDotPaint)
        canvas.drawCircle((cardWidth - 52).toFloat(), 52f, dotRadius, cornerDotPaint)
        canvas.drawCircle(52f, (totalHeight - 52).toFloat(), dotRadius, cornerDotPaint)
        canvas.drawCircle((cardWidth - 52).toFloat(), (totalHeight - 52).toFloat(), dotRadius, cornerDotPaint)

        // Function to draw ornamental divider with center diamond
        fun drawOrnamentalDivider(y: Float) {
            val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(80, 197, 155, 39)
                strokeWidth = 2f
            }
            val diamondPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(230, 81, 0)
                style = Paint.Style.FILL
            }
            val centerX = (cardWidth / 2).toFloat()
            val lineLength = 220f
            canvas.drawLine(centerX - lineLength, y, centerX - 16f, y, dividerPaint)
            canvas.drawLine(centerX + 16f, y, centerX + lineLength, y, dividerPaint)

            // Center diamond
            val path = android.graphics.Path().apply {
                moveTo(centerX, y - 8f)
                lineTo(centerX + 8f, y)
                lineTo(centerX, y + 8f)
                lineTo(centerX - 8f, y)
                close()
            }
            canvas.drawPath(path, diamondPaint)
        }

        // Render content sections sequentially
        var currentY = 80f

        // Title
        canvas.save()
        canvas.translate(horizontalMargin.toFloat(), currentY)
        titleLayout.draw(canvas)
        canvas.restore()
        currentY += titleLayout.height + 16f

        // Chapter Badge
        canvas.save()
        canvas.translate(horizontalMargin.toFloat(), currentY)
        chapterBadgeLayout.draw(canvas)
        canvas.restore()
        currentY += chapterBadgeLayout.height + 40f

        // Divider 1
        drawOrnamentalDivider(currentY)
        currentY += 36f

        // Bengali Shlok
        canvas.save()
        canvas.translate(horizontalMargin.toFloat(), currentY)
        shlokLayout.draw(canvas)
        canvas.restore()
        currentY += shlokLayout.height + 40f

        // Divider 2
        drawOrnamentalDivider(currentY)
        currentY += 36f

        // Translation heading
        canvas.save()
        canvas.translate(horizontalMargin.toFloat(), currentY)
        transHeadingLayout.draw(canvas)
        canvas.restore()
        currentY += transHeadingLayout.height + 16f

        // Translation body
        canvas.save()
        canvas.translate(horizontalMargin.toFloat(), currentY)
        translationLayout.draw(canvas)
        canvas.restore()
        currentY += translationLayout.height + 40f

        // Optional Purport
        if (hasPurport && purportHeadingLayout != null && purportLayout != null) {
            canvas.save()
            canvas.translate(horizontalMargin.toFloat(), currentY)
            purportHeadingLayout.draw(canvas)
            canvas.restore()
            currentY += purportHeadingLayout.height + 16f

            canvas.save()
            canvas.translate(horizontalMargin.toFloat(), currentY)
            purportLayout.draw(canvas)
            canvas.restore()
            currentY += purportLayout.height + 40f
        }

        // Divider 3
        drawOrnamentalDivider(currentY)
        currentY += 32f

        // Footer
        canvas.save()
        canvas.translate(horizontalMargin.toFloat(), currentY)
        footerLayout.draw(canvas)
        canvas.restore()

        return bitmap
    }

    fun saveVerseBitmapToFile(context: Context, bitmap: Bitmap, verse: Verse): File {
        val dir = File(context.cacheDir, "shared_verses")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, "shlok_${verse.chapterNumber}_${verse.verseNumber}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun shareVerseScreenshot(context: Context, verse: Verse) {
        try {
            val bitmap = generateVerseCardBitmap(context, verse)
            val file = saveVerseBitmapToFile(context, bitmap, verse)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "শ্রীমদ্ভগবদ্গীতা • অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}\n${verse.pronunciationBengali}\n— শ্রীমদ্ভগবদ্গীতা বাংলা"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "পুরো শ্লোক স্ক্রিনশট শেয়ার করুন").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback gracefully to text sharing
            shareVerseText(context, verse)
        }
    }
}
