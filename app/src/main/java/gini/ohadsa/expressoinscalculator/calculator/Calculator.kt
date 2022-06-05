package gini.ohadsa.expressoinscalculator.calculator

import android.os.Parcelable
import gini.ohadsa.expressoinscalculator.extenssions.toEquationList
import kotlinx.android.parcel.Parcelize

@Parcelize
class Calculator : Evaluator , Parcelable {
    var lastExpressions = mutableListOf<Pair<String , String>>()
    private var current = -1
    val currentValue
        get() = if(current != -1) lastExpressions[current] else Pair("","")

    fun calculate(equationString: String): String {
        val result = calculateExpression(equationString.toEquationList())
        val pairResult = Pair(equationString , result)
        if (lastExpressions.contains(pairResult)) {
            lastExpressions.remove(pairResult)
            lastExpressions.add(pairResult)
        }
        else{
            lastExpressions.add(pairResult)
            current++
        }
        return result
    }

    fun unDo(): Pair<String,String> {
        return if (current > 0)  lastExpressions[--current]
        else if  (current != -1) lastExpressions[current]
        else  Pair("","")
    }

    fun reDo(): Pair<String,String> {
       return if (current >= 0 && current < lastExpressions.size - 1) lastExpressions[++current]
       else if  (current != -1) lastExpressions[current]
       else  Pair("","")
    }

    fun hasMem(): Boolean {
        return lastExpressions.size == 0
    }

    fun deleteMem() {
        lastExpressions = mutableListOf()
        current =-1
    }
}
