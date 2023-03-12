package com.simuel.wirebarley

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.simuel.wirebarley.databinding.ActivityMainBinding
import com.simuel.wirebarley.util.CountryState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val countryLists = arrayOf(
        CountryState.Korea.name,
        CountryState.Japan.name,
        CountryState.Philippines.name,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        setPicker()
        setViewModelObserve()
        setListener()
    }

    private fun setListener() {
        binding.etRemittanceTitle.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    calculateReceipt(text.toString())
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }
    }

    private fun calculateReceipt(amount: String?) {
        if (!amount.isNullOrEmpty()) {
            viewModel.calculateReceipt(amount.toInt())
            return
        }
        viewModel.calculateReceipt(0)
    }

    private fun setPicker() {
        binding.numberPickerCountry.apply {
            minValue = 0
            maxValue = 2
            wrapSelectorWheel = true
            displayedValues = countryLists
            setOnValueChangedListener { numberPicker, _, _ ->
                viewModel.updateSelected(countryLists[numberPicker.value])
                viewModel.changeCountryCurrencyRate(countryLists[numberPicker.value])
                calculateReceipt(binding.etRemittanceTitle.text.toString())
            }
        }
    }

    private fun setViewModelObserve() {
        viewModel.error.observe(this) {
            if (it.consumed) return@observe
            Toast.makeText(this, it.peek(), Toast.LENGTH_SHORT).show()
            it.consume()
        }
        viewModel.country.observe(this) {
            observeCountryPicker(it.name)
        }
    }

    private fun observeCountryPicker(name: String) {
        val index = countryLists.indexOf(name)
        binding.numberPickerCountry.value = index
    }
}