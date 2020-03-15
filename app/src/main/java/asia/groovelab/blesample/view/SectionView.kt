package asia.groovelab.blesample.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingUtil
import asia.groovelab.blesample.R
import asia.groovelab.blesample.databinding.ViewSectionBinding
import asia.groovelab.blesample.model.Section

@BindingMethods(BindingMethod(type = Section::class,
    attribute = "bind:section",
    method = "setSection"))
class SectionView : RelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val binding: ViewSectionBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_section,this, true)

    fun setSection(section: Section) {
        binding.section = section
    }
}