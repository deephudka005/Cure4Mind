package com.cureya.cure4mind.chat.data.models

import com.google.firebase.database.IgnoreExtraProperties

/*
* In database, the JSON model would be
*  Chat
*   - messages [msgID1,msgID2,msgID3...]
*   - lastMessage [msgID57]
* */

@IgnoreExtraProperties
data class Chat(
    val messages: List<Message> = listOf()
)


/*
* Chats
*   - userID1+userID2
*       - updatedAt
*       - messages[]
*   - userID1+userID2
*       - updatedAt
*       - messages[]
*    - userID1+userID2
*       - updatedAt
*       - messages[]
*    - userID1+userID2
*       - updatedAt
*       - messages[]
*/