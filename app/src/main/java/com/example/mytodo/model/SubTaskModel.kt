package com.example.mytodo.model

import java.io.Serializable

data class SubTaskModel(var isComplete: Boolean = false, var subTask: String = "") : Serializable