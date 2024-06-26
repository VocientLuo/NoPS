package com.xluo.nops.operation


data class Operation(var operationType: Int,
                     var params1: Any? = null,
                     var params2: Any? = null,
                     var params3: Any? = null,
                     var params4: Any? = null)

/**
 * 记录图层的显示、隐藏操作，
 */
data class OperaLayerInfo(val operaType: Int, val layerId: Long,
                          var oldValue: Any, var newValue: Any)