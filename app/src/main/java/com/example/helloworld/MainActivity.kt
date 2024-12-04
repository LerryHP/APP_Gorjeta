package com.example.helloworld

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

private const val TAG = "MainActivity"
private const val FILE_NAME = "compras.txt"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {

    private lateinit var QuantiaBase: EditText
    private lateinit var seekBarGorjeta: SeekBar
    private lateinit var PorcenGorjeta: TextView
    private lateinit var QuantiaGorjeta: TextView
    private lateinit var QuantiaTotal: TextView
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var textViewCompras: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialização dos componentes
        QuantiaBase = findViewById(R.id.QuantiaBase)
        seekBarGorjeta = findViewById(R.id.seekBarGorjeta)
        PorcenGorjeta = findViewById(R.id.PorcenGorjeta)
        QuantiaGorjeta = findViewById(R.id.QuantiaGorjeta)
        QuantiaTotal = findViewById(R.id.QuantiaTotal)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        textViewCompras = findViewById(R.id.textViewCompras)

        // Configuração inicial
        seekBarGorjeta.progress = INITIAL_TIP_PERCENT
        PorcenGorjeta.text = "$INITIAL_TIP_PERCENT%"
        loadPurchases()

        // Listener do SeekBar para atualizar a porcentagem de gorjeta
        seekBarGorjeta.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                PorcenGorjeta.text = "$progress%"
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Listener para o botão de salvar compra
        btnSave.setOnClickListener {
            savePurchase()
        }

        // Listener para o botão de apagar histórico
        btnClear.setOnClickListener {
            clearHistory()
        }

        // Atualização dos valores ao mudar o texto do campo de base
        QuantiaBase.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                computeTipAndTotal()
            }
        })
    }

    private fun computeTipAndTotal() {
        if (QuantiaBase.text.isEmpty()) {
            QuantiaGorjeta.text = ""
            QuantiaTotal.text = ""
            return
        }

        val baseAmount = QuantiaBase.text.toString().toDouble()
        val tipPercent = seekBarGorjeta.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount

        QuantiaGorjeta.text = "%.2f".format(tipAmount)
        QuantiaTotal.text = "%.2f".format(totalAmount)
    }

    private fun savePurchase() {
        val baseAmount = QuantiaBase.text.toString()
        val tipPercent = seekBarGorjeta.progress
        val tipAmount = QuantiaGorjeta.text.toString()
        val totalAmount = QuantiaTotal.text.toString()

        if (baseAmount.isEmpty() || tipAmount.isEmpty() || totalAmount.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val record = "Conta: $baseAmount, Gorjeta: $tipAmount (${tipPercent}%), Total: $totalAmount\n"
        val file = File(filesDir, FILE_NAME)

        try {
            file.appendText(record)
            Toast.makeText(this, "Compra salva com sucesso!", Toast.LENGTH_SHORT).show()
            loadPurchases()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao salvar a compra.", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Erro ao salvar compra: ${e.message}")
        }
    }

    private fun loadPurchases() {
        val file = File(filesDir, FILE_NAME)
        if (file.exists()) {
            val purchases = file.readText()
            textViewCompras.text = purchases
        } else {
            textViewCompras.text = "Nenhuma compra registrada."
        }
    }

    private fun clearHistory() {
        val file = File(filesDir, FILE_NAME)
        if (file.exists()) {
            file.delete()
            Toast.makeText(this, "Histórico apagado com sucesso!", Toast.LENGTH_SHORT).show()
        }
        textViewCompras.text = "Nenhuma compra registrada."
    }
}
