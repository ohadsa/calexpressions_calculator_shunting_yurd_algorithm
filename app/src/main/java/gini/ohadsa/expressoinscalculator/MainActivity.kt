package gini.ohadsa.expressoinscalculator

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.Group
import gini.ohadsa.expressoinscalculator.calculator.Calculator
import gini.ohadsa.expressoinscalculator.databinding.ActivityMainBinding
import gini.ohadsa.expressoinscalculator.extenssions.displayFormatting


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!


    private var displayedValue: String
        get() = binding.display.editResult.text.toString()
        set(value) {
            binding.display.editResult.text = value
        }
    private var calculator = Calculator()

    private var edText: String
        get() = findViewById<TextView>(R.id.edit_text).text.toString()
        set(value) {
            findViewById<TextView>(R.id.edit_text).text = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        val buttons: List<Button> =
            (findViewById<Group>(R.id.all_group).referencedIds.map(this::findViewById))
        buttons.forEach { it.setOnClickListener(this::buttonsRouter) }
        val controllers: List<Button> =
            (findViewById<Group>(R.id.controllers_group).referencedIds.map(this::findViewById))
        controllers.forEach { it.setOnClickListener(this::controllerRouter) }
        binding.buttonEquals.setOnClickListener {

            try {
                if (edText != "") {
                    if (calculator.hasMem()) binding.buttonAc.text = ControllerButtons.C.value
                    displayedValue = calculator.calculate(edText.trim()).displayFormatting()
                }
            } catch (e: Exception) {
                binding.display.editText.setTextColor(Color.rgb(180, 0, 0))
            }
        }
    }


    private fun controllerRouter(view: View) {
        when ("${(view as Button).text}") {
            ControllerButtons.AC.value -> {
                edText = ""
                calculator.deleteMem()
                displayedValue = ""
            }
            ControllerButtons.C.value -> {
                edText = ""
                binding.buttonAc.text = ControllerButtons.AC.value
            }
            ControllerButtons.UNDO.value -> {
                val tmp = calculator.unDo()
                edText = tmp.first
                displayedValue = tmp.second
            }
            ControllerButtons.REDO.value -> {
                val tmp = calculator.reDo()
                edText = tmp.first
                displayedValue = tmp.second
            }
            ControllerButtons.ANS.value -> edText += calculator.currentValue.second
            ControllerButtons.DELETE.value -> {
                edText = if (edText.isNotEmpty()) edText.substring(0, edText.length - 1) else ""
                binding.display.editText.setTextColor(Color.parseColor("#C8C5BF"))
            }
        }
        if (edText.isNotEmpty()) binding.buttonAc.text = ControllerButtons.C.value
    }

    private fun buttonsRouter(view: View) {

        val op = "${(view as Button).text}"
        val pair = fromOpViewsToOpExpression(op)
        edText += pair.first
        binding.display.editText.setTextColor(Color.parseColor("#C8C5BF"))
        if (edText.isNotEmpty()) binding.buttonAc.text = ControllerButtons.C.value
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        calculator = savedInstanceState.getParcelable("calculator") ?: Calculator()
        edText = savedInstanceState.getString("editor") ?: ""
        displayedValue = savedInstanceState.getString("display") ?: ""
        binding.buttonAc.text = savedInstanceState.getString("ac_State") ?: "AC"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("calculator", calculator)
        outState.putString("editor", edText)
        outState.putString("display", displayedValue)
        outState.putString("ac_State", binding.buttonAc.text.toString())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Log.d("gini.ohadsa.motionActivity", "landscape")
            findViewById<Flow>(R.id.flow_btn).setMaxElementsWrap(8)
            findViewById<MotionLayout>(R.id.motion_base).transitionToEnd()
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("gini.ohadsa.motionActivity", "portrait")
            findViewById<Flow>(R.id.flow_btn).setMaxElementsWrap(4)
            findViewById<MotionLayout>(R.id.motion_base).transitionToStart()
        }
    }
}


enum class ControllerButtons(val value: String) {
    AC("AC"), C("C"), UNDO("◁"), REDO("▷"), DELETE("⌦"), ANS("ans");
}

fun fromOpViewsToOpExpression(value: String): Pair<String, String> {
    val map = mutableMapOf<String, Pair<String, String>>()


    map["0"] = Pair("0", ".")
    map["1"] = Pair("1", ".")
    map["2"] = Pair("2", ".")
    map["3"] = Pair("3", ".")
    map["4"] = Pair("4", ".")
    map["5"] = Pair("5", ".")
    map["6"] = Pair("6", ".")
    map["7"] = Pair("7", ".")
    map["8"] = Pair("8", ".")
    map["9"] = Pair("9", ".")
    map["."] = Pair(".", ".")
    map["e"] = Pair("e", "e")
    map["eˣ"] = Pair("e^", "e^")
    map["2ˣ"] = Pair("2^", "2^")
    map["π"] = Pair("π", "π")
    map["√"] = Pair("√", "√")
    map["∛"] = Pair("∛", "∛")
    map["%"] = Pair("%", "%")
    map["x²"] = Pair("²", "²")
    map["x³"] = Pair("³", "³")
    map["x⁻¹"] = Pair("⁻", "⁻")
    map["sin"] = Pair("sin", "sin")
    map["tan"] = Pair("tan", "tan")
    map["cos"] = Pair("cos", "cos")
    map["ln"] = Pair("ln", "ln")
    map["log"] = Pair("log", "log")
    map["!"] = Pair("!", "!")
    map["-/+"] = Pair("˗", "˗")
    map["+"] = Pair("+", "+")
    map["-"] = Pair("-", "-")
    map["x"] = Pair("x", "x")
    map["÷"] = Pair("÷", "÷")
    map["xʸ"] = Pair("^", "^")
    map[")"] = Pair(")", ")")
    map["("] = Pair("(", "(")

    return map[value] ?: Pair("", "")

}

