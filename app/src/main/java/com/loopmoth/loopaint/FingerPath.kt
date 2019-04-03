package com.loopmoth.loopaint

import android.graphics.Path

class FingerPath {
    var color: Int
    val emboss: Boolean
    var blur: Boolean
    var strokeWidth: Int
    var path: Path

    constructor(color: Int, emboss: Boolean, blur: Boolean, strokeWidth: Int, path: Path) {
        this.color = color
        this.emboss = emboss
        this.strokeWidth = strokeWidth
        this.blur = blur
        this.path = path
    }
}