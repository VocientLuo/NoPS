package com.xluo.nops.filter


const val BASIC_FILTER_CONFIG =
    "@adjust brightness 0 @adjust contrast 1 @adjust saturation 1 @adjust sharpen 0 @adjust exposure 0"

class AdjustConfig(
    var index: Int,
    var minValue: Float,
    var originValue: Float,
    var maxValue: Float
) {
    var intensity: Float
    var slierIntensity = 0.5f

    init {
        intensity = originValue
    }

    protected fun calcIntensity(_intensity: Float): Float {
        val result: Float
        result = if (_intensity <= 0.0f) {
            minValue
        } else if (_intensity >= 1.0f) {
            maxValue
        } else if (_intensity <= 0.5f) {
            minValue + (originValue - minValue) * _intensity * 2.0f
        } else {
            maxValue + (originValue - maxValue) * (1.0f - _intensity) * 2.0f
        }
        return result
    }

    fun getRealIntensity(_intensity: Float): Float {
        slierIntensity = _intensity
        intensity = calcIntensity(_intensity)
        return intensity
    }
}
