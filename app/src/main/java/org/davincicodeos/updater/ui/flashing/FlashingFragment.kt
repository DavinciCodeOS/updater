package org.davincicodeos.updater.ui.flashing

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import org.davincicodeos.updater.*
import java.io.FileDescriptor
import java.nio.file.Path
import kotlin.io.path.name


class FlashingFragment : Fragment(), FileSelectionEntryPoint {
    override val fileSelectionOwner = this
    private val fileSelectionInteractor: StorageAccessFrameworkInteractor =
        StorageAccessFrameworkInteractor(this)
    private lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_flashing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val layoutManager = LinearLayoutManager(requireContext())

        // Drag & Drop manager
        val recyclerViewDragDropManager = RecyclerViewDragDropManager()
        // recyclerViewDragDropManager.setDraggingItemShadowDrawable(ContextCompat.getDrawable(view.context, R.drawable.material_shadow_z3) as NinePatchDrawable)
        recyclerViewDragDropManager.setInitiateOnLongPress(true)
        recyclerViewDragDropManager.setInitiateOnMove(false)

        recyclerView.layoutManager = layoutManager
        this.adapter = MyAdapter(requireContext())
        recyclerView.adapter =
            recyclerViewDragDropManager.createWrappedAdapter(this.adapter)

        recyclerViewDragDropManager.attachRecyclerView(recyclerView)

        // Make the file selection button work
        val addButton = view.findViewById<FloatingActionButton>(R.id.addButton)

        addButton.setOnClickListener {
            val params = SelectFileParams("application/zip")
            fileSelectionInteractor.beginSelectingFile(params)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFileSelected(fileName: String?, fileDescriptor: FileDescriptor?) {
        if (fileDescriptor == null || fileName == null) {
            Log.i("ZipSelectorCallback", "No file selected")
        } else {
            DataManager.createFileAndCopyFromFd(requireContext(), fileName, fileDescriptor)
            this.adapter.reloadItems()
            this.adapter.notifyDataSetChanged()
        }
    }

    internal class FlashableItem(val path: Path, val name: String)


    internal class MyViewHolder(itemView: View) :
        AbstractDraggableItemViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(android.R.id.text1)
        var removeButton: Button = itemView.findViewById(android.R.id.button1)
    }

    internal class MyAdapter(private val context: Context) : RecyclerView.Adapter<MyViewHolder>(),
        DraggableItemAdapter<MyViewHolder> {
        private lateinit var mItems: MutableList<FlashableItem>

        override fun getItemId(position: Int): Long {
            // need to return stable (= not change even after reordered) value
            // TODO: store an ID for each one one adding and save order
            return mItems[position].name.hashCode().toLong()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.flashing_list_item, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = mItems[position]
            holder.textView.text = item.name

            holder.removeButton.setOnClickListener {
                DataManager.deleteFile(context, item.name)
                mItems.remove(item)
                notifyItemRemoved(holder.adapterPosition)
            }
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        override fun onMoveItem(fromPosition: Int, toPosition: Int) {
            val movedItem = mItems.removeAt(fromPosition)
            mItems.add(toPosition, movedItem)
        }

        override fun onCheckCanStartDrag(
            holder: MyViewHolder,
            position: Int,
            x: Int,
            y: Int
        ): Boolean {
            return true
        }

        override fun onGetItemDraggableRange(
            holder: MyViewHolder,
            position: Int
        ): ItemDraggableRange? {
            return null
        }

        override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
            return true
        }

        override fun onItemDragStarted(position: Int) {}
        override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {}

        fun reloadItems() {
            mItems = ArrayList()

            DataManager.getFiles(context).forEach { file ->
                mItems.add(FlashableItem(file, file.name))
            }
        }

        init {
            setHasStableIds(true) // this is required for D&D feature.
            reloadItems()
        }
    }
}
