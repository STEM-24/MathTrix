package com.mtcdb.stem.mathtrix.dictionary

import android.content.Context
import android.database.Cursor
import android.widget.SimpleCursorAdapter

class DictionarySuggestionAdapter(
    context: Context,
    layout: Int,
    c: Cursor?,
    from: Array<String>,
    to: IntArray
) : SimpleCursorAdapter(context, layout, c, from, to) {

    override fun convertToString(cursor: Cursor): CharSequence {
        return cursor.getString(cursor.getColumnIndexOrThrow("term"))
    }
}
