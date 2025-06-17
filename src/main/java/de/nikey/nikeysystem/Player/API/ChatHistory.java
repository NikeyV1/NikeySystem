package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.chat.SignedMessage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ChatHistory {
    private final Deque<SignedMessage> messages = new ArrayDeque<>();

    public void addMessage(SignedMessage msg) {
        if (messages.size() >= 30) messages.removeFirst();
        messages.addLast(msg);
    }

    public SignedMessage getMessagebyIndex(int index) {
        int i = 1;
        for (SignedMessage msg : messages) {
            if (i == index) return msg;
            i++;
        }
        return null;
    }

    public List<SignedMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }
}
