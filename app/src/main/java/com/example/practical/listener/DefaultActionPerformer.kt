package com.example.practical.listener

interface DefaultActionPerformer {
    fun onActionPerform(headers: HashMap<String, String>, params: HashMap<String, String>)
}