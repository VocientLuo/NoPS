package com.xluo.nops.operation

import com.xluo.core.entity.PvsBackgroundLayer
import com.xluo.core.entity.PvsImageLayer
import com.xluo.core.entity.PvsLayer
import com.xluo.core.entity.PvsRichLayer
import com.xluo.core.entity.PvsTextLayer

object OperationUtils {


    var operationListener: OperationListener? = null

    /**
     * 保存新操作及上一步操作的堆栈
     */
    private val stackPre: ArrayDeque<Operation> = ArrayDeque()

    private var backupStackPre: ArrayDeque<Operation> = ArrayDeque()

    /**
     * 保存下一步操作的堆栈
     */
    private val stackNext: ArrayDeque<Operation> = ArrayDeque()

    private var backupStackNext: ArrayDeque<Operation> = ArrayDeque()

    /**
     * 判断撤销堆栈中是否还有元素（即是否还可执行撤销操作）
     *
     * @return
     */
    fun hasUndoOperation(): Boolean {
        return !stackPre.isEmpty()
    }

    /**
     * 判断重做堆栈中是否还有元素（即是否还可执行重做操作）
     *
     * @return
     */
    fun hasRedoOperation(): Boolean {
        return !stackNext.isEmpty()
    }

    fun clear() {
        stackPre.clear()
        stackNext.clear()
        operationListener?.onDataChange()
    }

    fun backupData() {
        backupStackPre.addAll(stackPre)
        backupStackNext.addAll(stackNext)
        clear()
    }

    fun resumeData() {
        stackPre.addAll(backupStackPre)
        stackNext.addAll(backupStackNext)
        operationListener?.onDataChange()
    }

    /**
     * 撤销（相当于上一步）
     */
    fun undo(): Boolean {
        return executeOperationFromStack(true)
    }


    /**
     * 重做（相当于下一步）
     */
    fun redo(): Boolean {
        return executeOperationFromStack(false)
    }

    /**
     * 执行撤销重做逻辑
     * @param isUndo Boolean  是否是撤销
     * @return Boolean
     */
    private fun executeOperationFromStack(isUndo: Boolean): Boolean {
        val fromStack: ArrayDeque<Operation>
        val toStack: ArrayDeque<Operation>
        if (isUndo) {
            fromStack = stackPre
            toStack = stackNext
        } else {
            fromStack = stackNext
            toStack = stackPre
        }
        if (fromStack.size == 0) {
            return false
        }
        val operation = fromStack.removeFirst()
        when (operation.operationType) {
            OperationType.UPDATE_BACKGROUND_INFO -> {
                updateBackgroundInfo(
                    operation.params1 as PvsBackgroundLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.DEL_RICH_LAYER -> {
                addRichLayer(
                    operation.params2 as Int,
                    operation.params1 as PvsRichLayer,
                    true,
                    operation,
                    toStack
                )
            }

            OperationType.UPDATE_LAYER_ATTR -> {
                updateRichLayerAttr(
                    operation.params1 as OperaLayerInfo,
                    true,
                    operation,
                    toStack
                )
            }

            OperationType.ADD_RICH_LAYER -> {
                deleteRichLayer(
                    operation.params2 as Int,
                    operation.params1 as PvsRichLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.UPDATE_RICH_LAYER -> {
                updateRichLayer(
                    operation.params2 as PvsRichLayer,
                    operation.params1 as PvsRichLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.ADD_IMAGE_LAYER -> {
                deleteImageLayer(
                    operation.params2 as Int,
                    operation.params1 as PvsImageLayer,
                    true,
                    operation,
                    toStack
                )
            }

            OperationType.DEL_IMAGE_LAYER -> {
                addImageLayer(
                    operation.params2 as Int,
                    operation.params1 as PvsImageLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.UPDATE_IMAGE_LAYER -> {
                updateImageLayer(
                    operation.params2 as PvsImageLayer,
                    operation.params1 as PvsImageLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.ADD_TEXT_LAYER -> {
                deleteTextLayer(
                    operation.params2 as Int,
                    operation.params1 as PvsTextLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.DEL_TEXT_LAYER -> {
                addTextLayer(
                    operation.params2 as Int,
                    operation.params1 as PvsTextLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.UPDATE_TEXT_LAYER -> {
                updateTextLayer(
                    operation.params2 as PvsTextLayer,
                    operation.params1 as PvsTextLayer,
                    true,
                    operation,
                    toStack
                )
            }
            OperationType.SORT_ALL_LAYER -> {
                sortAllLayer(
                    operation.params2 as List<PvsLayer>,
                    operation.params1 as List<PvsLayer>,
                    true,
                    operation,
                    toStack
                )
            }
        }
        return true
    }

    /**
     * 添加新操作到撤销栈中，同时清除重做栈中所有的操作数据
     *
     * @param operation
     */
    private fun addNewOperation(operation: Operation?) {
        if (operation == null) {
            return
        }
        stackPre.addFirst(operation)
        stackNext.clear()
    }

    fun updateBackgroundInfo(pvsBackgroundLayer: PvsBackgroundLayer) {
        if (pvsBackgroundLayer == null) {
            return
        }
        updateBackgroundInfo(pvsBackgroundLayer, false, null, null)
    }

    private fun updateBackgroundInfo(
        pvsBackgroundLayer: PvsBackgroundLayer,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        TimeLineData.pvsTimeLine?.let { pvsTimeLine ->
            val oldBackgroundLayer = pvsTimeLine.bgLayer
            if (!isFromStack) {
                val newOperation = Operation(OperationType.UPDATE_BACKGROUND_INFO)
                newOperation.params1 = oldBackgroundLayer
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.UPDATE_BACKGROUND_INFO
                operation?.params1 = oldBackgroundLayer
                operation?.let {
                    toStack?.addFirst(it)
                }
            }
            pvsTimeLine.bgLayer = pvsBackgroundLayer
            operationListener?.onUpdateBackground(pvsBackgroundLayer)
            operationListener?.onDataChange()
        }
    }

    /**
     * ===========================图片图层=============================
     */

    fun addImageLayer(index: Int, pvsImageLayer: PvsImageLayer?) {
        addImageLayer(index, pvsImageLayer, false, null, null)
    }

    fun deleteImageLayer(index: Int, pvsImageLayer: PvsImageLayer?) {
        deleteImageLayer(index, pvsImageLayer, false, null, null)
    }

    private fun addImageLayer(
        index: Int,
        pvsImageLayer: PvsImageLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        if (index == -1) {
            return
        }
        pvsImageLayer?.let { layer ->
            if (!isFromStack) {
                val newOperation = Operation(OperationType.ADD_IMAGE_LAYER)
                newOperation.params1 = layer.copy()
                newOperation.params2 = index
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.ADD_IMAGE_LAYER
                operation?.params1 = layer.copy()
                operation?.params2 = index
                operation?.let {
                    toStack?.addFirst(it)
                }
            }
            TimeLineData.pvsTimeLine?.layerList?.add(index, layer)
            operationListener?.onDataChange()
        }
    }


    private fun deleteImageLayer(
        index: Int,
        pvsImageLayer: PvsImageLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        if (index == -1) {
            return
        }
        pvsImageLayer?.let { layer ->
            if (!isFromStack) {
                val newOperation = Operation(OperationType.DEL_IMAGE_LAYER)
                newOperation.params1 = layer.copy()
                newOperation.params2 = index
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.DEL_IMAGE_LAYER
                operation?.params1 = layer.copy()
                operation?.params2 = index
                operation?.let {
                    toStack?.addFirst(it)
                }
            }
            TimeLineData.removeLayerById(layer.objectId)
            operationListener?.onDataChange()
        }
    }


    fun updateImageLayer(pvsOldLayer: PvsImageLayer?, pvsImageLayer: PvsImageLayer?) {
        updateImageLayer(pvsOldLayer, pvsImageLayer, false, null, null)
    }

    private fun updateImageLayer(
        pvsOldLayer: PvsImageLayer?,
        pvsImageLayer: PvsImageLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        pvsImageLayer?.let { layer ->
            //先找到老的图片图层
            if (pvsOldLayer != null) {
                val index = TimeLineData.findOldLayerIndex(pvsOldLayer.objectId)
                if (index == -1) {
                    return
                }
                if (!isFromStack) {
                    val newOperation = Operation(OperationType.UPDATE_IMAGE_LAYER)
                    newOperation.params1 = pvsOldLayer.copy()
                    newOperation.params2 = layer.copy()
                    addNewOperation(newOperation)
                } else {
                    operation?.operationType = OperationType.UPDATE_IMAGE_LAYER
                    operation?.params1 = pvsOldLayer.copy()
                    operation?.params2 = layer.copy()
                    operation?.let {
                        toStack?.addFirst(it)
                    }
                }
                TimeLineData.pvsTimeLine?.layerList?.set(index, layer)
                operationListener?.onDataChange()
            }
        }
    }

    /**
     * ===========================富图层=============================
     */

    fun addRichLayerAttr(info: OperaLayerInfo) {
        updateRichLayerAttr(info, false, null, null)
    }

    private fun updateRichLayerAttr(
        info: OperaLayerInfo,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?
    ) {
        if (!isFromStack) {
            val operation = Operation(OperationType.UPDATE_LAYER_ATTR)
            operation.params1 = info
            addNewOperation(operation)
        } else {
            val temp = info.newValue
            info.newValue = info.oldValue
            info.oldValue = temp
            operation?.operationType = OperationType.UPDATE_LAYER_ATTR
            operation?.params1 = info
            operation?.let {
                toStack?.addFirst(it)
            }
        }
        val index = TimeLineData.findOldLayerIndex(info.layerId)
        TimeLineData.pvsTimeLine?.let {
            val layer = it.layerList[index]
            when (info.operaType) {
                OperationType.UPDATE_LAYER_VISIBLE -> {
                    layer.isShow = info.newValue as Boolean
                }
                OperationType.UPDATE_LAYER_ALPHA -> {
                    layer.alpha = info.newValue as Int
                }
            }
            operationListener?.onDataChange()
        }
    }

    fun addRichLayer(index: Int, pvsRichLayer: PvsRichLayer?) {
        addRichLayer(index, pvsRichLayer, false, null, null)
    }

    fun deleteRichLayer(index: Int, pvsRichLayer: PvsRichLayer?) {
        deleteRichLayer(index, pvsRichLayer, false, null, null)
    }

    private fun addRichLayer(
        index: Int,
        pvsRichLayer: PvsRichLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        if (index == -1) {
            return
        }
        pvsRichLayer?.let { layer ->
            if (!isFromStack) {
                val newOperation = Operation(OperationType.ADD_RICH_LAYER)
                newOperation.params1 = layer.copy()
                newOperation.params2 = index
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.ADD_RICH_LAYER
                operation?.params1 = layer.copy()
                operation?.params2 = index
                operation?.let {
                    toStack?.addFirst(it)
                }
            }
            TimeLineData.pvsTimeLine?.layerList?.add(index, layer)
            operationListener?.onDataChange()
        }
    }


    private fun deleteRichLayer(
        index: Int,
        pvsRichLayer: PvsRichLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        if (index == -1) {
            return
        }
        pvsRichLayer?.let { layer ->
            if (!isFromStack) {
                val newOperation = Operation(OperationType.DEL_RICH_LAYER)
                newOperation.params1 = layer.copy()
                newOperation.params2 = index
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.DEL_RICH_LAYER
                operation?.params1 = layer.copy()
                operation?.params2 = index
                operation?.let {
                    toStack?.addFirst(it)
                }
            }
            TimeLineData.removeLayerById(layer.objectId)
            operationListener?.onDataChange()
        }
    }

    fun updateRichLayer(pvsOldLayer: PvsRichLayer?, pvsRichLayer: PvsRichLayer?) {
        updateRichLayer(pvsOldLayer, pvsRichLayer, false, null, null)
    }

    private fun updateRichLayer(
        pvsOldLayer: PvsRichLayer?,
        pvsRichLayer: PvsRichLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        pvsRichLayer?.let { layer ->
            //先找到老的图层
            if (pvsOldLayer != null) {
                val index = TimeLineData.findOldLayerIndex(pvsOldLayer.objectId)
                if (index == -1) {
                    return
                }
                if (!isFromStack) {
                    val newOperation = Operation(OperationType.UPDATE_RICH_LAYER)
                    newOperation.params1 = pvsOldLayer.copy()
                    newOperation.params2 = layer.copy()
                    addNewOperation(newOperation)
                } else {
                    operation?.operationType = OperationType.UPDATE_RICH_LAYER
                    operation?.params1 = pvsOldLayer.copy()
                    operation?.params2 = layer.copy()
                    operation?.let {
                        toStack?.addFirst(it)
                    }
                    TimeLineData.pvsTimeLine?.layerList?.set(index, layer)
                    operationListener?.onDataChange()
                }
            }
        }
    }

    /**
     * ===========================文本图层=============================
     */
    fun addTextLayer(index: Int, pvsTextLayer: PvsTextLayer?) {
        addTextLayer(index, pvsTextLayer, false, null, null)
    }

    fun deleteTextLayer(index: Int, pvsTextLayer: PvsTextLayer?) {
        deleteTextLayer(index, pvsTextLayer, false, null, null)
    }

    private fun addTextLayer(
        index: Int,
        pvsTextLayer: PvsTextLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        if (index == -1) {
            return
        }
        pvsTextLayer?.let { layer ->
            if (!isFromStack) {
                val newOperation = Operation(OperationType.ADD_TEXT_LAYER)
                newOperation.params1 = layer.copy()
                newOperation.params2 = index
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.ADD_TEXT_LAYER
                operation?.params1 = layer.copy()
                operation?.params2 = index
                operation?.let {
                    toStack?.addFirst(it)
                }
            }
            TimeLineData.pvsTimeLine?.layerList?.add(index, layer)
            operationListener?.onDataChange()
        }
    }


    private fun deleteTextLayer(
        index: Int,
        pvsTextLayer: PvsTextLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        if (index == -1) {
            return
        }
        pvsTextLayer?.let { layer ->
            if (!isFromStack) {
                val newOperation = Operation(OperationType.DEL_TEXT_LAYER)
                newOperation.params1 = layer.copy()
                newOperation.params2 = index
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.DEL_TEXT_LAYER
                operation?.params1 = layer.copy()
                operation?.params2 = index
                operation?.let {
                    toStack?.addFirst(it)
                }
            }
            TimeLineData.removeLayerById(layer.objectId)
            operationListener?.onDataChange()
        }
    }

    fun updateTextLayer(pvsOldLayer: PvsTextLayer?, pvsTextLayer: PvsTextLayer?) {
        updateTextLayer(pvsOldLayer, pvsTextLayer, false, null, null)
    }

    private fun updateTextLayer(
        pvsOldLayer: PvsTextLayer?,
        pvsTextLayer: PvsTextLayer?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        pvsTextLayer?.let { layer ->
            //先找到老的图片图层
            if (pvsOldLayer != null) {
                val index = TimeLineData.findOldLayerIndex(pvsOldLayer.objectId)
                if (index == -1) {
                    return
                }
                if (!isFromStack) {
                    val newOperation = Operation(OperationType.UPDATE_TEXT_LAYER)
                    newOperation.params1 = pvsOldLayer.copy()
                    newOperation.params2 = layer.copy()
                    addNewOperation(newOperation)
                } else {
                    operation?.operationType = OperationType.UPDATE_TEXT_LAYER
                    operation?.params1 = pvsOldLayer.copy()
                    operation?.params2 = layer.copy()
                    operation?.let {
                        toStack?.addFirst(it)
                    }
                }
                TimeLineData.pvsTimeLine?.layerList?.set(index, layer)
                operationListener?.onDataChange()
            }
        }
    }

    fun sortAllLayer(oldLayerList: List<PvsLayer>, sortLayerList: List<PvsLayer>) {
        sortAllLayer(oldLayerList, sortLayerList, false, null, null)
    }

    private fun sortAllLayer(
        oldLayerList: List<PvsLayer>?,
        sortLayerList: List<PvsLayer>?,
        isFromStack: Boolean,
        operation: Operation?,
        toStack: ArrayDeque<Operation>?,
    ) {
        if (!oldLayerList.isNullOrEmpty() && !sortLayerList.isNullOrEmpty()) {
            if (!isFromStack) {
                val newOperation = Operation(OperationType.SORT_ALL_LAYER)
                newOperation.params1 = oldLayerList
                newOperation.params2 = sortLayerList
                addNewOperation(newOperation)
            } else {
                operation?.operationType = OperationType.SORT_ALL_LAYER
                operation?.params1 = oldLayerList
                operation?.params2 = sortLayerList
                operation?.let {
                    toStack?.addFirst(it)
                }

            }
            TimeLineData.pvsTimeLine?.layerList?.clear()
            TimeLineData.pvsTimeLine?.layerList?.addAll(sortLayerList)
            operationListener?.onDataChange()
        }
    }
}