package asia.groovelab.blesample

import android.os.Bundle
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import asia.groovelab.blesample.coroutine.LifecycleScope
import asia.groovelab.blesample.coroutine.LifecycleScopeSupport
import asia.groovelab.blesample.databinding.ActivityPeripheralBinding
import asia.groovelab.blesample.extension.toast
import asia.groovelab.blesample.viewmodel.PeripheralViewModel


class PeripheralActivity : AppCompatActivity(), LifecycleScopeSupport {
    override val scope = LifecycleScope(this)

    private val viewModel: PeripheralViewModel by lazy {
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ViewModelProvider(this, factory).get(PeripheralViewModel::class.java)
    }
    private var scrollView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  setup binding
        val binding: ActivityPeripheralBinding = DataBindingUtil.setContentView(this, R.layout.activity_peripheral)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //  toolbar
        setSupportActionBar(binding.toolbar)

        //  initialize data
        if (savedInstanceState == null) {
            if (!viewModel.canAdvertise) {
                toast("can not start peripheral")
            }
        }
    }

    override fun onBackPressed() {
        viewModel.clear()
        super.onBackPressed()
    }
}