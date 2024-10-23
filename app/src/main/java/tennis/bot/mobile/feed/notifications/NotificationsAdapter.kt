package tennis.bot.mobile.feed.notifications

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.databinding.RecyclerNotificationActionableBinding
import tennis.bot.mobile.databinding.RecyclerNotificationInfoBinding
import tennis.bot.mobile.databinding.RecyclerNotificationPointsBinding
import tennis.bot.mobile.databinding.RecyclerNotificationScoreBinding
import tennis.bot.mobile.profile.account.EmptyItemViewHolder
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject

class NotificationsAdapter@Inject constructor(): PagingDataAdapter<NotificationData, RecyclerView.ViewHolder>(NOTIFICATIONS_COMPARATOR) {

	companion object {
		val NOTIFICATIONS_COMPARATOR = object : DiffUtil.ItemCallback<NotificationData>() {
			override fun areItemsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean =
				oldItem == newItem
		}
		const val OTHER = 0
		const val ACTIONABLE_NOTIFICATIONS = 1
		const val INFO_NOTIFICATIONS = 2
		const val POINTS_NOTIFICATIONS = 3
		const val SCORE_NOTIFICATIONS = 4
	}
	var clickListenerTransfer: ((isIncomind: Boolean, id: Int) -> Unit)? = null
	var clickListenerTelegram: ((isTelegram: Boolean, payload: String) -> Unit)? = null


	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		getItem(position)?.let { item ->
			when (holder) {
				is NotificationsActionableViewHolder -> { bindActionableNotifications(item, holder) }
				is NotificationsInfoViewHolder -> { bindInfoNotifications(item, holder) }
				is NotificationsPointsViewHolder -> { bindPointsNotifications(item, holder) }
				is NotificationsScoreViewHolder -> { bindScoreNotifications(item, holder) }
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		return when(viewType) {
			ACTIONABLE_NOTIFICATIONS -> {
				val binding = RecyclerNotificationActionableBinding.inflate(inflater, parent, false)
				NotificationsActionableViewHolder(binding)
			}
			INFO_NOTIFICATIONS -> {
				val binding = RecyclerNotificationInfoBinding.inflate(inflater, parent, false)
				NotificationsInfoViewHolder(binding)
			}
			POINTS_NOTIFICATIONS -> {
				val binding = RecyclerNotificationPointsBinding.inflate(inflater, parent, false)
				NotificationsPointsViewHolder(binding)
			}
			SCORE_NOTIFICATIONS -> {
				val binding = RecyclerNotificationScoreBinding.inflate(inflater, parent, false)
				NotificationsScoreViewHolder(binding)
			}
			else -> {
				val binding = RecyclerEmptyItemBinding.inflate(inflater, parent, false)
				EmptyItemViewHolder(binding)
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		return when ((getItem(position) as NotificationData).content) {
			is NotificationContentParent.GameOrderInvite,
			is NotificationContentParent.GameOrderResponse -> ACTIONABLE_NOTIFICATIONS

			is NotificationContentParent.GameOrderAccept,
			is NotificationContentParent.GameOrderCreate -> INFO_NOTIFICATIONS

			is NotificationContentParent.Bonus -> POINTS_NOTIFICATIONS

			is NotificationContentParent.SinglesScore,
			is NotificationContentParent.DoublesScore -> SCORE_NOTIFICATIONS

			else -> OTHER
		}
	}

	private fun bindActionableNotifications(item: Any, holder: NotificationsActionableViewHolder) {
		val data = item as? NotificationData ?: throw IllegalArgumentException("Item must be NotificationData")
		val actionableItem = when (data.content) {
			is NotificationContentParent.GameOrderInvite -> data.content
			is NotificationContentParent.GameOrderResponse -> data.content
			else -> throw IllegalArgumentException("Content must be a valid type (1, 2)")
		}

		with(holder.binding) {
			if (actionableItem is NotificationContentParent.GameOrderResponse) {
					if (actionableItem.selfNotify) {
						title.text = root.context.getString(R.string.you_responsed_to_invite)
						subTitle.isVisible = false
					} else {
						title.text = root.context.getString(R.string.response_to_your_invite)
						subTitle.text = root.context.getString(
							R.string.from_player,
							actionableItem.playerName)
					}
				playerPhoto.setImage(AvatarImage(actionableItem.playerPhoto))
				root.setOnClickListener {
					clickListenerTransfer?.invoke(!actionableItem.selfNotify, actionableItem.gameOrderId)
				}
				} else if (actionableItem is NotificationContentParent.GameOrderInvite) {
					if (actionableItem.selfNotify) {
						title.text = root.context.getString(R.string.you_sent_invite)
						subTitle.text = root.context.getString(
							R.string.sent_to_player,
							actionableItem.playerName)
					} else {
						title.text = root.context.getString(R.string.you_invited)
						subTitle.text = root.context.getString(
							R.string.from_player,
							actionableItem.playerName)
				}
				playerPhoto.setImage(AvatarImage(actionableItem.playerPhoto))
				root.setOnClickListener {
					clickListenerTransfer?.invoke(!actionableItem.selfNotify, actionableItem.gameOrderId)
				}
			}


		}
	}

	private fun bindInfoNotifications(item: Any, holder: NotificationsInfoViewHolder) {
		val data = item as? NotificationData ?: throw IllegalArgumentException("Item must be NotificationData")
		val infoItem = when (data.content) {
			is NotificationContentParent.GameOrderAccept -> data.content
			is NotificationContentParent.GameOrderCreate -> data.content
			else -> throw IllegalArgumentException("Content must be a valid type (5, 6)")
		}

		with(holder.binding) {
			if (infoItem is NotificationContentParent.GameOrderAccept) {
				info.text = formatTextToTwoColors(
					root.context, root.context.getString(R.string.your_invite_accepted,
						infoItem.playerName,
					"@${infoItem.telegram}",
					"+${infoItem.phone}"), infoItem.playerName.lastIndex + 1, infoItem.telegram, infoItem.phone)
				playerPhoto.setImage(AvatarImage(infoItem.playerPhoto))

				info.setTextIsSelectable(false)
				info.movementMethod = LinkMovementMethod.getInstance()

			} else if (infoItem is NotificationContentParent.GameOrderCreate) {
				info.text = formatTextToTwoColors(root.context, root.context.getString(R.string.you_created_request),
					root.context.getString(R.string.you_created_request).indexOf(" "))
				info.setTextIsSelectable(false)
				playerPhoto.setImage(AvatarImage(infoItem.playerPhoto))
			}

			date.text = data.createdAt
		}
	}

	private fun bindPointsNotifications(item: Any, holder: NotificationsPointsViewHolder) {
		val data = item as? NotificationData ?: throw IllegalArgumentException("Item must be NotificationData")
		val pointsItem = data.content as? NotificationContentParent.Bonus ?: throw IllegalArgumentException("Content must be Bonus")

		with(holder.binding) {
			info.text = formatTextToTwoColors(root.context, root.context.getString(R.string.points_acquired,
				pointsItem.addBonus), root.context.getString(R.string.points_acquired).indexOf(" ", 3))
			date.text = data.createdAt
		}
	}

	private fun bindScoreNotifications(item: Any, holder: NotificationsScoreViewHolder) {
		val data = item as? NotificationData ?: throw IllegalArgumentException("Item must be NotificationData")
		val scoreItem = when (data.content) {
			is NotificationContentParent.SinglesScore -> data.content
			is NotificationContentParent.DoublesScore -> data.content
			else -> throw IllegalArgumentException("Content must be a valid type (3, 8)")
		}

		with(holder.binding) {
			if (scoreItem is NotificationContentParent.SinglesScore) {
				info.text = formatTextToTwoColors(root.context, root.context.getString(R.string.score_inserted,
					scoreItem.player1.name,
					scoreItem.player2.name,
					scoreItem.sets.joinToString("), (") { "${it.score1} - ${it.score2}" }), scoreItem.player2.name.lastIndex + 1)
			} else if (scoreItem is NotificationContentParent.DoublesScore) {
				info.text = formatTextToTwoColors(root.context, root.context.getString(R.string.score_inserted,
					("${scoreItem.player1.name}, ${scoreItem.player2.name}"),
					("${scoreItem.player3.name}, ${scoreItem.player4.name}"),
					scoreItem.sets.joinToString("), (") { "${it.score1} - ${it.score2}" }), scoreItem.player4.name.lastIndex + 1)
			}

			date.text = data.createdAt
		}
	}

	private fun formatTextToTwoColors(context: Context, formattedText: String, endIndex: Int, telegram: String = "", phoneNumber: String = ""): SpannableString {
		val spannableString = SpannableString(formattedText)
		val startIndex = 0 // testing
		if (endIndex != -1) {
			val color = context.getColor(R.color.tb_black)
			spannableString.setSpan(
				ForegroundColorSpan(color),
				startIndex,
				endIndex,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
			)
		}
		if (telegram.isNotEmpty()) {
			val telegramLink = object : ClickableSpan() { //
				override fun onClick(widget: View) {
					clickListenerTelegram?.invoke(true, telegram)
				}
			}

			val telegramStart = spannableString.indexOf("@")
			val telegramEnd = telegramStart + telegram.length + 1
			spannableString.setSpan(telegramLink, telegramStart, telegramEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		}
		if (phoneNumber.isNotEmpty()) {
			val phoneLink = object : ClickableSpan() {
				override fun onClick(widget: View) {
					clickListenerTelegram?.invoke(false, phoneNumber)
				}
			}

			val phoneStart = spannableString.indexOf("+")
			val phoneEnd = phoneStart + phoneNumber.length + 1
			spannableString.setSpan(phoneLink, phoneStart, phoneEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		}

		return spannableString
	}

}

class NotificationsActionableViewHolder( // 1, 2
	val binding: RecyclerNotificationActionableBinding
) : RecyclerView.ViewHolder(binding.root)

class NotificationsInfoViewHolder( // 5, 6
	val binding: RecyclerNotificationInfoBinding
) : RecyclerView.ViewHolder(binding.root)

class NotificationsPointsViewHolder( // 4
	val binding: RecyclerNotificationPointsBinding
) : RecyclerView.ViewHolder(binding.root)

class NotificationsScoreViewHolder( // 3, 8
	val binding: RecyclerNotificationScoreBinding
) : RecyclerView.ViewHolder(binding.root)
