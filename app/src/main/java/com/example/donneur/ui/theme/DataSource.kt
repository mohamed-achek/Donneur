package com.example.donneur.ui.theme

import com.example.donneur.R
import com.example.donneur.data.Post1

class Datasource {
    fun postList(): List<Post1> {
        return listOf<Post1>(
            Post1("Sarah Ben Ali ","sarah_bloodhero",14, R.string.post_1 ,R.drawable.post1_profile),
            Post1("Ahmed Khemiri","ahmed_helpme",3, R.string.post_2, R.drawable.post2_profile)
        )
    }
}