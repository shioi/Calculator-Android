package com.example.calculator_real

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.os.VibrationEffect.createOneShot
import android.os.VibrationEffect.createPredefined
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.calculator_real.databinding.ActivityMainBinding
import java.util.ArrayDeque
import kotlin.math.exp


class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var vibrator: Vibrator

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.result.text = ""

        //binding
        binding.button0.setOnClickListener(this)
        binding.button1.setOnClickListener(this)
        binding.button2.setOnClickListener(this)
        binding.button3.setOnClickListener(this)
        binding.button4.setOnClickListener(this)
        binding.button5.setOnClickListener(this)
        binding.button6.setOnClickListener(this)
        binding.button7.setOnClickListener(this)
        binding.button8.setOnClickListener(this)
        binding.button9.setOnClickListener(this)
        binding.buttonC.setOnClickListener(this)
        binding.buttonAc.setOnClickListener(this)
        binding.buttonCloseBracket.setOnClickListener(this)
        binding.buttonOpenBracket.setOnClickListener(this)
        binding.buttonMultiply.setOnClickListener(this)
        binding.buttonDivide.setOnClickListener(this)
        binding.buttonPlus.setOnClickListener(this)
        binding.buttonMinus.setOnClickListener(this)
        binding.buttonEquals.setOnClickListener(this)


        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vib = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vib.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

    }

    fun setResultValue(value: String) {
        binding.result.text = binding.result.text.toString() + value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        vibrator.vibrate(createOneShot(100,2))
        when(v?.id) {
            R.id.button_c -> binding.result.text = binding.result.text.dropLast(1)
            R.id.button_multiply -> setResultValue("X")
            R.id.button_divide -> setResultValue("/")
            R.id.button_minus -> setResultValue("-")
            R.id.button_plus -> setResultValue("+")
            R.id.button_plus -> setResultValue("(")
            R.id.button_plus -> setResultValue(")")
            R.id.button_1 -> setResultValue("1")
            R.id.button_0 -> setResultValue("0")
            R.id.button_2 -> setResultValue("2")
            R.id.button_3 -> setResultValue("3")
            R.id.button_4 -> setResultValue("4")
            R.id.button_5 -> setResultValue("5")
            R.id.button_6 -> setResultValue("6")
            R.id.button_7 -> setResultValue("7")
            R.id.button_8 -> setResultValue("8")
            R.id.button_9 -> setResultValue("9")
            R.id.button_ac -> binding.result.text = ""
            R.id.button_open_bracket -> setResultValue("(")
            R.id.button_close_bracket -> setResultValue(")")
            R.id.button_equals -> evaluation()
            else -> setResultValue("")
        }
    }

    private fun operatorPrec(oper: Char): Int {
        val prec = when (oper) {
            '^' -> 4
            'X' -> 3
            '/' -> 3
            '+' -> 2
            '-' -> 2
            else -> 1
        }
        return prec
    }

    private fun convertToPostFix(): String{
        val expr = binding.result.text.toString() + ')'
        val stack = ArrayDeque<Char>()
        var result = ""
        stack.push('(')

        for(ch in expr.iterator()) {
            //Log.d("SS",ch.toString())
            when(ch) {
                '(' -> stack.push('(')
                ')' -> {
                    while(stack.peek() != '(') {
                        result += "_${stack.pop()}"
                    }
                    stack.pop()
                }
                else -> {
                    if (ch.isDigit()) {
                        result += ch
                    }
                    else {
                        while(operatorPrec(ch) <= operatorPrec(stack.peek() as Char)){
                            result += "_${stack.pop()}"
                        }
                        result += "_"


                        stack.push(ch)
                    }

                }

            }
        }
        return result
    }

    //evaluation
    private fun evaluation() {
        val expr = convertToPostFix()
        //evaluation of postfix
        val stack = ArrayDeque<String>()
        var currentNo = ""
        var digitScanning = false
        var failed = false
        for(ch in expr.iterator()) {
            if(ch.isDigit()) {
                digitScanning = true
                currentNo+=ch.toString()
            } else if(ch == '_') {
                if(digitScanning){
                    stack.push(currentNo)
                    currentNo = ""
                    digitScanning = false
                } else continue
            }
            else {
                val res = performOp(stack.pop(), stack.pop(), ch)
                if(res == 'q'){
                    failed = true
                    break
                }
                stack.push(res.toString())
            }
        }
        if(!failed)
            binding.result.text = stack.pop()
    }


    private fun performOp(op1: String, op2: String, oper: Char) =
        when(oper) {
            'X' -> op1.toInt() * op2.toInt()
            '/' -> {
                if(op1.toInt() == 0){
                    Toast.makeText(this,"Cannot divide by zero",Toast.LENGTH_SHORT).show()
                    'q'
                } else {
                    op2.toInt() / op1.toInt()
                }
            }
            '-' -> op2.toInt() - op1.toInt()
            '+' -> op1.toInt() + op2.toInt()
            else -> 0
        }


}