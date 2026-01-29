package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto

enum class VariableType(val indicator: String?, val symbol: String) {
    SCALAR("", "$"), LIST("LIST__", "@"), DICTIONARY("DICT__", "&"), ENVIRONMENT(null, "%");

    fun prefixed(name: String): String = "$symbol$name"

    fun fullFledgedWrapping(name: String): String = "${symbol}{${name}}"

    companion object {
        private val indicatorMap = VariableType.entries.filter { it.indicator !== null }.associateBy { it.indicator!! }

        @JvmStatic
        fun fromIndicator(indicator: String): VariableType = indicatorMap[indicator] ?: SCALAR
    }
}
