package com.cureya.cure4mind.model

import android.provider.ContactsContract
import com.cureya.cure4mind.util.defaultProfilePic
import java.util.*

class User {

    var name: String? = null
    var email: String? = null
    var phone: String? = null
    var photoUrl: String = defaultProfilePic
    var password: String? = null
    var gender: String? = null
    var counsellarstatus: Boolean = false
    var joinedCureya: Date = Date()

    // Empty constructor for firebase serialization
    constructor()

    constructor(name: String?, email: String?,
                phone: String?, photoUrl: String,
                password: String?, gender: String?,
                counsellarstatus: Boolean, joinedCureya: Date
    ) {
        this.name = name
        this.email = email
        this.phone = phone
        this.photoUrl = photoUrl
        this.password = password
        this.gender = gender
        this.counsellarstatus = counsellarstatus
        this.joinedCureya = joinedCureya
    }
}