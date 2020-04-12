package asia.groovelab.blesample.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(val app: Application): AndroidViewModel(app) {
    enum class Action {
        Central,
        Peripheral,
        None
    }

    private val mAction = MutableLiveData<Action>()
    val action: LiveData<Action> = mAction

    init {
        mAction.value = Action.None
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickCentralButton(view: View) {
        mAction.postValue(Action.Central)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickPeripheralButton(view: View) {
        mAction.postValue(Action.Peripheral)
    }
}