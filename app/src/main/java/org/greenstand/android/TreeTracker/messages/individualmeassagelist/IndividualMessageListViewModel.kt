package org.greenstand.android.TreeTracker.messages.individualmeassagelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.greenstand.android.TreeTracker.models.Users
import org.greenstand.android.TreeTracker.models.messages.AnnouncementMessage
import org.greenstand.android.TreeTracker.models.messages.DirectMessage
import org.greenstand.android.TreeTracker.models.messages.Message
import org.greenstand.android.TreeTracker.models.messages.MessagesRepo
import org.greenstand.android.TreeTracker.models.messages.SurveyMessage
import org.greenstand.android.TreeTracker.models.user.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*

data class IndividualMessageListState(
    val messages: List<Message> = Collections.emptyList(),
    val currentUser: User? = null,
)

class IndividualMessageListViewModel(
    private val userId: Long,
    private val users: Users,
    private val messagesRepo: MessagesRepo,
) : ViewModel() {

    private val _state = MutableLiveData<IndividualMessageListState>()
    val state: LiveData<IndividualMessageListState> = _state

    init {
        viewModelScope.launch {
            val currentUser = users.getUser(userId)
            val messages = messagesRepo.getMessages(currentUser!!.wallet)

            // get list of senders to pick out one message per chat
            val externalChatMessages = messages
                .filterIsInstance<DirectMessage>()
                .sortedBy { it.composedAt }
                .filter { it.from != currentUser.wallet }
                .groupBy { it.from }

            // Get all types of messages to show
            val chatsToShow = externalChatMessages.keys.map { externalChatMessages[it]!!.first() }
            val surveysToShow = messages.filterIsInstance<SurveyMessage>()
            val announcementsToShow = messages.filterIsInstance<AnnouncementMessage>()

            // Merge messages together and sort by newest
            val messagesToShow = (chatsToShow + surveysToShow + announcementsToShow)
                .sortedBy { it.composedAt }

            _state.value = IndividualMessageListState(
                currentUser = currentUser,
                messages = messagesToShow,
            )
        }
    }
}

class IndividualMessageListViewModelFactory(private val userId: Long)
    : ViewModelProvider.Factory, KoinComponent {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IndividualMessageListViewModel(userId, get(), get()) as T
    }
}