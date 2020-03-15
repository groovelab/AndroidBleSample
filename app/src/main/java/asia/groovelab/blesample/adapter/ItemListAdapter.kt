package asia.groovelab.blesample.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import asia.groovelab.blesample.model.Item
import asia.groovelab.blesample.model.Section
import asia.groovelab.blesample.view.ItemView
import asia.groovelab.blesample.view.SectionView


class ItemListAdapter(private val context: Context) : BaseExpandableListAdapter() {
    var sections: List<Section> = emptyList()
    var items: List<List<Item>> = emptyList()

    //  section
    override fun getGroupCount() = sections.size

    override fun getGroup(groupPosition: Int) = sections[groupPosition]

    override fun getGroupId(groupPosition: Int) = 0L

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ) = ((convertView as? SectionView) ?: SectionView(context)).apply {
        setSection(sections[groupPosition])
    }

    //  item
    override fun getChildrenCount(groupPosition: Int) = if (groupPosition < items.size) items[groupPosition].size else 0

    override fun getChild(groupPosition: Int, childPosition: Int) = items[groupPosition][childPosition]

    override fun getChildId(groupPosition: Int, childPosition: Int) = 0L

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ) = ((convertView as? ItemView) ?: ItemView(context)).apply {
        setItem(items[groupPosition][childPosition])
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun hasStableIds() = false
}