package asia.groovelab.blesample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View.FOCUSABLE
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import asia.groovelab.blesample.adapter.ItemListAdapter
import asia.groovelab.blesample.coroutine.LifecycleScope
import asia.groovelab.blesample.coroutine.LifecycleScopeSupport
import asia.groovelab.blesample.databinding.ActivityCentralPeripheralBinding
import asia.groovelab.blesample.extension.toHexString
import asia.groovelab.blesample.extension.toast
import asia.groovelab.blesample.model.Item
import asia.groovelab.blesample.model.Peripheral
import asia.groovelab.blesample.viewmodel.CentralPeripheralViewModel
import kotlinx.coroutines.launch
import java.util.*


class CentralPeripheralActivity : AppCompatActivity(), LifecycleScopeSupport {
    companion object {
        private const val PERIPHERAL_EXTRA = "peripheral"

        fun intent(context: Context, peripheral: Peripheral) =
            Intent(context, CentralPeripheralActivity::class.java).putExtra(PERIPHERAL_EXTRA, peripheral)
    }

    override val scope = LifecycleScope(this)

    private val viewModel: CentralPeripheralViewModel by lazy {
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ViewModelProvider(this, factory).get(CentralPeripheralViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  setup binding
        val binding: ActivityCentralPeripheralBinding = DataBindingUtil.setContentView(this, R.layout.activity_central_peripheral)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.listView.setAdapter(
            ItemListAdapter(this)
        )
        binding.listView.setOnGroupClickListener { _, _, _, _ -> true }
        binding.listView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            viewModel.getItem(groupPosition, childPosition)?.let {
                if (it.isWritable && it.isReadable) {
                    showAlertDialogForReadWrite(it)
                } else if (it.isWritable) {
                    showAlertDialogForWrite(it)
                } else if (it.isReadable) {
                    viewModel.readCharacteristic(it)
                }
            }
            true
        }
        viewModel.reconnected.observe(this, Observer {
            scope.launch {
                toast(getString(R.string.connect_again))
            }
        })
        viewModel.disconnectedFromDevice.observe(this, Observer {
            scope.launch {
                toast(getString(R.string.disconnected))
                finish()
            }
        })
        viewModel.wroteCharacteristicHandler = { _, _ ->
            scope.launch {
                toast("success to write")
            }
        }
        viewModel.notifiedCharacteristicHandler = { uuid, bytes ->
            scope.launch {
                bytes?.let { toast("notified\n${uuid}:${it.toHexString()}") }
            }
        }

        //  toolbar
        setSupportActionBar(binding.toolbar)

        //  initialize data
        if (savedInstanceState == null) {
            (intent.getParcelableExtra(PERIPHERAL_EXTRA) as? Peripheral)?.also {
                viewModel.connect(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_central_peripheral, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect_button -> {
                disconnectedAndDismiss()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        disconnectedAndDismiss()
    }

    private fun disconnectedAndDismiss() {
        viewModel.disconnect {
            scope.launch {
                finish()
            }
        }
    }

    private fun showAlertDialogForReadWrite(item: Item) {
        AlertDialog.Builder(this)
            .setItems(arrayOf("Read", "Write", "Cancel")) { _, index ->
                when (index) {
                    0 -> viewModel.readCharacteristic(item)
                    1 -> showAlertDialogForWrite(item)
                }
            }
            .show()
    }

    private fun showAlertDialogForWrite(item: Item) {
        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            val hexChars = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
            source.toString()
                .toUpperCase(Locale.ROOT)
                .filter { hexChars.contains(it) }
        }
        val editText = EditText(this).apply {
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            filters = arrayOf(inputFilter)
            hint = "input hex"
            focusable = FOCUSABLE
        }

        AlertDialog.Builder(this)
            .setMessage("Write to [${item.uuid}]")
            .setView(editText)
            .setPositiveButton("Write") { _, _ ->
                viewModel.writeCharacteristic(item, editText.text.toString())
            }
            .setNeutralButton("Cancel") { _, _ -> }
            .create().apply {
                setOnShowListener {
                    editText.requestFocus()
                }
                editText.onFocusChangeListener = OnFocusChangeListener { _, _ ->
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }
            }
            .show()
    }
}