
package com.example.wyprawa_planer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var heroIcon: ImageView
    private lateinit var heroName: EditText
    private lateinit var raceSpinner: Spinner
    private lateinit var departureInfo: TextView
    private lateinit var pickDateBtn: Button
    private lateinit var pickTimeBtn: Button
    private lateinit var elfPathsSwitch: Switch
    private lateinit var gearCloak: CheckBox
    private lateinit var gearLembas: CheckBox
    private lateinit var gearTorch: CheckBox
    private lateinit var priorityGroup: RadioGroup
    private lateinit var walkTimeLabel: TextView
    private lateinit var walkTimeSeek: SeekBar
    private lateinit var chronometer: Chronometer
    private lateinit var chronoStart: Button
    private lateinit var chronoStop: Button
    private lateinit var countdownProgress: ProgressBar
    private lateinit var startCountdown: Button
    private lateinit var moraleRating: RatingBar
    private lateinit var updateSummary: Button
    private lateinit var summaryText: TextView

    private val calendar: Calendar = Calendar.getInstance()
    private var countDownTimer: CountDownTimer? = null

    private val races = listOf("hobbit", "człowiek", "elf", "krasnolud", "czarodziej")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        setupRaceSpinner()
        setupDateTimePickers()
        setupSeekBar()
        setupChronometer()
        setupCountdown()
        priorityGroup.check(R.id.priorityBalanced)

        updateSummary.setOnClickListener { renderSummary() }
        renderDepartureInfo()
    }

    private fun bindViews() {
        heroIcon = findViewById(R.id.heroIcon)
        heroName = findViewById(R.id.heroName)
        raceSpinner = findViewById(R.id.raceSpinner)
        departureInfo = findViewById(R.id.departureInfo)
        pickDateBtn = findViewById(R.id.pickDateBtn)
        pickTimeBtn = findViewById(R.id.pickTimeBtn)
        elfPathsSwitch = findViewById(R.id.elfPathsSwitch)
        gearCloak = findViewById(R.id.gearCloak)
        gearLembas = findViewById(R.id.gearLembas)
        gearTorch = findViewById(R.id.gearTorch)
        priorityGroup = findViewById(R.id.priorityGroup)
        walkTimeLabel = findViewById(R.id.walkTimeLabel)
        walkTimeSeek = findViewById(R.id.walkTimeSeek)
        chronometer = findViewById(R.id.chronometer)
        chronoStart = findViewById(R.id.chronoStart)
        chronoStop = findViewById(R.id.chronoStop)
        countdownProgress = findViewById(R.id.countdownProgress)
        startCountdown = findViewById(R.id.startCountdown)
        moraleRating = findViewById(R.id.moraleRating)
        updateSummary = findViewById(R.id.updateSummary)
        summaryText = findViewById(R.id.summaryText)
    }

    private fun setupRaceSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, races)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        raceSpinner.adapter = adapter
        raceSpinner.setSelection(0)

        raceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val res = when (races[position]) {
                    "hobbit" -> R.drawable.ring
                    "człowiek" -> R.drawable.gladius
                    "elf" -> R.drawable.bow
                    "krasnolud" -> R.drawable.axe
                    "czarodziej" -> R.drawable.staff
                    else -> R.drawable.ring
                }
                heroIcon.setImageResource(res)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupDateTimePickers() {
        pickDateBtn.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    calendar.set(Calendar.YEAR, y)
                    calendar.set(Calendar.MONTH, m)
                    calendar.set(Calendar.DAY_OF_MONTH, d)
                    renderDepartureInfo()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        pickTimeBtn.setOnClickListener {
            TimePickerDialog(
                this,
                { _, h, min ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, min)
                    calendar.set(Calendar.SECOND, 0)
                    renderDepartureInfo()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun renderDepartureInfo() {
        val dateFmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        departureInfo.text = "Wyruszasz: ${dateFmt.format(calendar.time)} o ${timeFmt.format(calendar.time)}"
    }

    private fun setupSeekBar() {
        walkTimeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                walkTimeLabel.text = "Czas marszu: $progress min"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    private fun setupChronometer() {
        chronoStart.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
        }
        chronoStop.setOnClickListener {
            chronometer.stop()
        }
    }

    private fun setupCountdown() {
        startCountdown.setOnClickListener {
            countDownTimer?.cancel()
            val totalSec = 30
            countdownProgress.max = totalSec
            countdownProgress.progress = 0

            countDownTimer = object : CountDownTimer(totalSec * 1000L, 1000L) {
                override fun onTick(msLeft: Long) {
                    val secPassed = totalSec - (msLeft / 1000L).toInt()
                    countdownProgress.progress = secPassed
                }
                override fun onFinish() {
                    countdownProgress.progress = totalSec
                    Toast.makeText(this@MainActivity, "Czas wyruszyć z Rivendell!", Toast.LENGTH_LONG).show()
                }
            }.start()
        }
    }

    private fun renderSummary() {
        val name = heroName.text.toString().ifBlank { "—" }
        val race = races.getOrNull(raceSpinner.selectedItemPosition) ?: "—"

        val gear = buildList {
            if (gearCloak.isChecked) add("Płaszcz elfów")
            if (gearLembas.isChecked) add("Lembasy")
            if (gearTorch.isChecked) add("Pochodnia")
        }.joinToString(", ").ifBlank { "brak" }

        val priority = when (priorityGroup.checkedRadioButtonId) {
            R.id.priorityHidden -> "Ukryty"
            R.id.priorityBalanced -> "Zbalansowany"
            R.id.priorityHard -> "Forsowny"
            else -> "—"
        }

        val walkTimeMin = walkTimeSeek.progress
        val morale = moraleRating.rating.toInt()

        val dateFmt = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

        summaryText.text =
            "Bohater: $name ($race)\n" +
            "Priorytet: $priority\n" +
            "Wyposażenie: $gear\n" +
            "Czas marszu: $walkTimeMin min • Morale: $morale/5\n" +
            "Termin: ${dateFmt.format(calendar.time)} o ${timeFmt.format(calendar.time)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}