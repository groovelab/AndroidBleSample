package asia.groovelab.blesample.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingUtil
import asia.groovelab.blesample.R
import asia.groovelab.blesample.databinding.ViewPeripheralBinding
import asia.groovelab.blesample.model.Peripheral

@BindingMethods(BindingMethod(type = Peripheral::class,
    attribute = "bind:peripheral",
    method = "setPeripheral"))
class PeripheralView : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val binding: ViewPeripheralBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_peripheral,this, true)

    fun setPeripheral(peripheral: Peripheral) {
        binding.peripheral = peripheral
    }
}