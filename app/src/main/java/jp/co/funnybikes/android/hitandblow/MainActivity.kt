package jp.co.funnybikes.android.hitandblow

import android.app.AlertDialog
import android.content.DialogInterface;
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val MenuNewGame = 0
    private val MenuExit = 1
    private val LengthOfAnswer = 4

    private var answer = ""
    private var inputedAnswer = ""
    private var countOfInput = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnApply  = findViewById<Button>(R.id.btnApply)
        val btnCancel  = findViewById<Button>(R.id.btnCancel)
        val keyWidth = getWidthBase()

        btnApply.width = keyWidth * 3
        btnApply.height = keyWidth
        btnCancel.width = keyWidth * 2
        btnCancel.height = keyWidth

        btnApply.setOnClickListener {l ->
            onInputAnswer()
        }

        btnCancel.setOnClickListener { l ->
            inputedAnswer = ""
            setUserInterface()
        }

        setTenkey()

        if(answer.length < LengthOfAnswer)
        {
            InitGame()
        }
    }

    private fun getWidthBase() : Int {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return  dm.widthPixels / 5
    }

    private fun setTenkey() {
        val tenkey = findViewById<LinearLayout>(R.id.tenkey)
        val keyWidth = getWidthBase()
        var keyIndex = 0
        val size = LinearLayout.LayoutParams(keyWidth, keyWidth)

        for(i in 0..1)
        {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL

            for(j in 0..LengthOfAnswer) {
                val button = Button(this)
                val buttonValue = keyIndex
                button.minWidth = 0
                button.id = buttonValue
                button.text = buttonValue.toString()
                button.width = keyWidth
                button.height = keyWidth
                button.setOnClickListener { l ->  tenkeyOnInput(button, buttonValue)}
                row.addView(button, size)
                keyIndex++
            }
            tenkey.addView(row)
        }
    }

    private fun InitGame()
    {
        lblMain.text = getString(R.string.InputNumber)
        btnApply.isEnabled = false
        lblHistory.text = ""
        inputedAnswer = ""
        countOfInput = 0
        answer = getNewAnswer()
        tenKeySetEnabled(true)
    }

    private fun onInputAnswer()
    {
        countOfInput++

        var message = ""
        var hit = 0
        var blow = 0
        for (i in 0 until inputedAnswer.length) {
            for (j in 0 until answer.length) {
                if (inputedAnswer.substring(i, i + 1) == answer.substring(j, j + 1)) {
                    if (i == j) {
                        hit++
                    } else {
                        blow++
                    }
                }
            }
        }

        message += countOfInput.toString() + ":"
        message += " input=" + inputedAnswer
        message += " hit=" + hit.toString()
        message += " blow=" + blow.toString()
        message += "\n" + lblHistory.text

        lblHistory.text = message
        inputedAnswer = ""
        setUserInterface()

        if (hit == LengthOfAnswer) {
            showAnswerDialog()
        }
    }

    private fun setUserInterface()
    {
        btnApply.isEnabled = (inputedAnswer.length == LengthOfAnswer)

        if (inputedAnswer.length == 0) {
            lblMain.text = getString(R.string.InputNumber)
            lblMain.isCursorVisible = true
            tenKeySetEnabled(true);
        } else {
            lblMain.setText(inputedAnswer)
            lblMain.isCursorVisible = false;
        }
    }

    private fun tenkeyOnInput(button : Button, key : Int)
    {
        if (inputedAnswer.length >= LengthOfAnswer) return

        button.isEnabled = false
        inputedAnswer += key.toString()
        lblMain.text = inputedAnswer;
        setUserInterface()
    }

    private fun getNewAnswer(): String {
        var answer = ""
        val random = Random()

        while (answer.length != LengthOfAnswer) {
            var isHit = false
            val c = (random.nextInt(10)).toString()
            for (i in 0 until answer.length) {
                if (answer.subSequence(i, i + 1) == c) {
                    isHit = true
                    break
                }
            }
            if (!isHit) {
                answer += c
            }
        }
        return answer
    }

    private fun tenKeySetEnabled(isClickable: Boolean) {
        for (i in 0 until tenkey.childCount) {
            val row = tenkey.getChildAt(i) as LinearLayout
            for (j in 0 until row.childCount) {
                val btn = row.getChildAt(j) as Button
                btn.isEnabled = isClickable
            }
        }
    }

    private fun showAnswerDialog() {
        var message = ""

        message += "answer=" + answer + "\n"
        message += "You inputed " + countOfInput.toString() + " times."

        var alertDialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.GoodJob))
                .setMessage(message)
                .setPositiveButton(getString(R.string.NewGame), DialogInterface.OnClickListener { dialog, which -> InitGame() })
                .setNegativeButton(getString(R.string.Exit), DialogInterface.OnClickListener { dialog, which -> finish() }).show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, MenuNewGame, Menu.NONE, getString(R.string.NewGame))
        menu.add(Menu.NONE, MenuExit, Menu.NONE, getString(R.string.Exit))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when (item?.itemId) {
            MenuNewGame -> InitGame()
            MenuExit -> finish()
        }
        return true;
    }
}

