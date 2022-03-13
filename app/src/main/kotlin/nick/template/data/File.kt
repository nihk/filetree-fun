package nick.template.data

import androidx.annotation.DrawableRes

sealed class File {
    abstract val absolutePath: String
    abstract val name: String
    abstract val depth: Int
    abstract val parentAbsolutePath: String?

    data class Directory(
        override val absolutePath: String,
        override val name: String,
        override val depth: Int,
        override val parentAbsolutePath: String?,
        val isExpanded: Boolean,
        @DrawableRes val icon1: Int,
        @DrawableRes val icon2: Int,
    ) : File()

    data class Leaf(
        override val absolutePath: String,
        override val name: String,
        override val depth: Int,
        override val parentAbsolutePath: String?,
        @DrawableRes val icon: Int
    ) : File()
}
