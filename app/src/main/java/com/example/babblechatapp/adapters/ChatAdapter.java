package com.example.babblechatapp.adapters;

import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babblechatapp.databinding.ItemContainerReceivedMessageBinding;
import com.example.babblechatapp.databinding.ItemContainterSentMessageBinding;
import com.example.babblechatapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    /**
     * Constructor for ChatAdapter (converting from Java content -> appearance element (.xml))
     * @param chatMessages list of chat messages
     * @param receiverProfileImage profile image of the receiver
     * @param senderId id of the sender
     */
    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    /**
     * create new slot in the recycler view (GENERAL CREATION)
     * @param parent parent node of the recycler view
     * @param viewType the type of view (sent or received) - this determines to whether includes image or not
     * @return a new slot (view holder)
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return createSentMessageViewHolder(parent);
        }
        return createReceivedMessageViewHolder(parent);
    }

    /**
     * create new slot (view holder) for the sent message (not include image) in the recycler view (SENT MESSAGE CREATION)
     * @param parent the parent node of recycler view
     * @return new view holder into the recycler view
     */
    @NonNull
    private SentMessageViewHolder createSentMessageViewHolder(@NonNull ViewGroup parent) {
        return new SentMessageViewHolder(
                ItemContainterSentMessageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    /**
     * create new slot (view holder) for the received message (include image) in the recycler view (RECEIVED MESSAGE CREATION)
     * @param parent the parent node of recycler view
     * @return new view holder into the recycler view
     */
    @NonNull
    private ReceivedMessageViewHolder createReceivedMessageViewHolder(@NonNull ViewGroup parent) {
        return new ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    /**
     * Bind the UI to the content of the message
     * @param holder empty viewHolder slot in Recycler View
     * @param position position in the Recycler View
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT)
            setSentMessageView((SentMessageViewHolder) holder, position);
        else
            setReceivedMessageView((ReceivedMessageViewHolder) holder, position);
    }

    /**
     * set View to sent message content
     * @param holder viewholder slot in recycler view (intially empty)
     * @param position position in the Recycler View
     */
    private void setSentMessageView(SentMessageViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    /**
     * set View to received message content
     * @param holder viewholder slot in recycler view (intially empty)
     * @param position position in the recycler view
     */
    private void setReceivedMessageView(ReceivedMessageViewHolder holder, int position) {
        holder.setData(chatMessages.get(position), receiverProfileImage);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) return VIEW_TYPE_SENT;
        return VIEW_TYPE_RECEIVED;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainterSentMessageBinding binding;

        SentMessageViewHolder(ItemContainterSentMessageBinding itemContainterSentMessageBinding) {
            super(itemContainterSentMessageBinding.getRoot());
            binding = itemContainterSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }
    }
}
