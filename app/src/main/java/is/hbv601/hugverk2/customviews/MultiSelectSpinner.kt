package `is`.hbv601.hugverk2.customviews

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner

class MultiSelectSpinner : AppCompatSpinner, DialogInterface.OnMultiChoiceClickListener {

    private var items: Array<String> = arrayOf()
    private var selection: BooleanArray = booleanArrayOf()
    private var simpleAdapter: ArrayAdapter<String>
    private var listener: MultiSelectSpinnerListener? = null

    interface MultiSelectSpinnerListener {
        fun onItemsSelected(selected: List<String>)
    }

    constructor(context: Context) : super(context) {
        simpleAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, mutableListOf("Select options"))
        super.setAdapter(simpleAdapter)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        simpleAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, mutableListOf("Select options"))
        super.setAdapter(simpleAdapter)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        simpleAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, mutableListOf("Select options"))
        super.setAdapter(simpleAdapter)
    }

    override fun performClick(): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setMultiChoiceItems(items, selection, this)
        builder.setPositiveButton("OK") { dialog, which ->
            refreshSpinnerText()
            listener?.onItemsSelected(getSelectedItems())
        }
        builder.show()
        return true
    }

    override fun onClick(dialog: DialogInterface?, which: Int, isChecked: Boolean) {
        if (which in selection.indices) {
            selection[which] = isChecked
        }
    }

    fun setItems(items: Array<String>) {
        this.items = items
        this.selection = BooleanArray(items.size)
        simpleAdapter.clear()
        simpleAdapter.add("Select options")
    }

    fun getSelectedItems(): List<String> {
        val selected = mutableListOf<String>()
        for (i in items.indices) {
            if (selection[i]) {
                selected.add(items[i])
            }
        }
        return selected
    }

    fun setSelection(selectedItems: List<String>) {
        for (i in items.indices) {
            selection[i] = selectedItems.contains(items[i])
        }
        refreshSpinnerText()
    }

    private fun refreshSpinnerText() {
        val spinnerText = getSelectedItems().joinToString(", ")
        simpleAdapter.clear()
        simpleAdapter.add(if (spinnerText.isEmpty()) "Select options" else spinnerText)
    }

    fun setListener(listener: MultiSelectSpinnerListener) {
        this.listener = listener
    }
}