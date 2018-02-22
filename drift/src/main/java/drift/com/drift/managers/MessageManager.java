package drift.com.drift.managers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.helpers.TextHelper;
import drift.com.drift.model.Conversation;
import drift.com.drift.model.Message;
import drift.com.drift.model.MessageRequest;
import drift.com.drift.model.PreMessage;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.MessagesWrapper;

/**
 * Created by eoin on 11/08/2017.
 */

public class MessageManager {

    private static String TAG = MessageManager.class.getSimpleName();

    private static MessageManager _messageManager = new MessageManager();

    public static MessageManager getInstance() {
        return _messageManager;
    }

    private HashMap<Integer, ArrayList<Message>> messageCache = new HashMap<>();
    private HashMap<Integer, ArrayList<Message>> failedMessageCache = new HashMap<>();

    public void clearCache(){
        messageCache = new HashMap<>();
        failedMessageCache = new HashMap<>();
    }

    @NonNull
    public ArrayList<Message> getMessagesForConversationId(int conversationId) {

        ArrayList<Message> failedMessages = failedMessageCache.get(conversationId);

        if (failedMessages == null || failedMessages.isEmpty()) {
            return messageCache.get(conversationId);
        }

        ArrayList<Message> successMessages = messageCache.get(conversationId);

        if (successMessages == null || successMessages.isEmpty()) {
            successMessages = new ArrayList<>();
        }

        ArrayList<Message> returnedArr = new ArrayList<>();

        returnedArr.addAll(successMessages);
        returnedArr.addAll(failedMessages);


        Collections.sort(returnedArr, new Comparator<Message>() {
            public int compare(Message o1, Message o2) {
                if (o1.createdAt == null || o2.createdAt == null)
                    return 0;
                return o2.createdAt.compareTo(o1.createdAt);
            }
        });

        return returnedArr;
    }

    ArrayList<Message> getMessagesFromPreMessages(Message message, ArrayList<PreMessage> preMessages) {

        ArrayList<Message> fakeMessages = new ArrayList<>();

        for (int i = 0; i < preMessages.size() ; i++) {

            PreMessage preMessage = preMessages.get(i);

            Message fakeMessage = new Message();

            fakeMessage.createdAt = new Date(message.createdAt.getTime() - ((i+1) * 100000));
            fakeMessage.conversationId = message.conversationId;
            fakeMessage.body = TextHelper.cleanString(preMessage.messageBody);

            fakeMessage.fakeMessage = true;
            fakeMessage.preMessage = true;

            fakeMessage.uuid = UUID.randomUUID().toString();

            fakeMessage.sendStatus = Message.SendStatus.SENT;
            fakeMessage.contentType = "CHAT";
            fakeMessage.authorType = "USER";

            if (preMessage.sender != null) {
                fakeMessage.authorId = preMessage.sender.id;
                fakeMessages.add(fakeMessage);
            }
        }


        /*

        let date = message.createdAt
        var output: [Message] = []
        for (index, preMessage) in preMessages.enumerated() {
            let fakeMessage = Message()

            fakeMessage.createdAt = date.addingTimeInterval(TimeInterval(-(index + 1)))
            fakeMessage.conversationId = message.conversationId
            fakeMessage.body = TextHelper.cleanString(body: preMessage.messageBody)
            fakeMessage.formattedBody = TextHelper.attributedTextForString(text: fakeMessage.body ?? "")
            fakeMessage.fakeMessage = true
            fakeMessage.preMessage = true
            fakeMessage.uuid = UUID().uuidString

            fakeMessage.sendStatus = .Sent
            fakeMessage.contentType = ContentType.Chat
            fakeMessage.authorType = AuthorType.User

            if let sender = preMessage.user {
                fakeMessage.authorId = sender.userId
                output.append(fakeMessage)
            }
        }

        return output

         */

        return fakeMessages;
    }

    ArrayList<Message> sortMessagesForConversations(ArrayList<Message> rawMessages) {
        ArrayList<Message> output = new ArrayList<>();

        ArrayList<Message> sorted = new ArrayList<Message>(rawMessages);

        for (Message message : sorted) {


            if (message.preMessage) {
                continue;
            }

            if (message.attributes != null && !message.attributes.preMessages.isEmpty()) {
                output.addAll(getMessagesFromPreMessages(message, message.attributes.preMessages));
            }

            if (message.attributes != null && message.attributes.offerSchedule != -1) {
                continue;
            }

            if (message.attributes != null && message.attributes.appointmentInfo != null) {


                for (Message message1 : output) {

                    if (message1.attributes != null && message1.attributes.presentSchedule != null) {
                        message1.attributes.presentSchedule = null;
                        break;
                    }

                }

            }

            output.add(message);

        }

        /*

         var output:[Message] = []

        let sorted = self.sorted(by: { $0.createdAt.compare($1.createdAt as Date) == .orderedAscending})

        for message in sorted {

            if message.preMessage {
                //Ignore pre messages, we will recreate them
                continue
            }

            if !message.preMessages.isEmpty {
                output.append(contentsOf: getMessagesFromPreMessages(message: message, preMessages: message.preMessages))
            }

            if message.offerSchedule != -1 {
                continue
            }

            if let appointmentInformation = message.appointmentInformation {
                //Go backwards and remove the most recent message asking for an apointment

                output = output.map({

                    if let _ = $0.presentSchedule {
                        $0.presentSchedule = nil
                    }
                    return $0
                })

            }

            output.append(message)
        }

         */

        Collections.sort(output, new Comparator<Message>() {
            public int compare(Message o1, Message o2) {
                if (o1.createdAt == null || o2.createdAt == null)
                    return 0;
                return o2.createdAt.compareTo(o1.createdAt);
            }
        });


        return output;
    }

    public void addMessageFailedToConversation(Message message, int conversationId){

        ArrayList<Message> successMessages = messageCache.get(conversationId);

        if (successMessages != null && successMessages.contains(message)) {
            successMessages.remove(message);
        }

        ArrayList<Message> failedMessages = failedMessageCache.get(conversationId);

        if (failedMessages != null && !failedMessages.contains(message)){
            failedMessages.add(message);
            return;
        }

        failedMessages = new ArrayList<>();
        failedMessages.add(message);

        failedMessageCache.put(conversationId, failedMessages);
    }


    public void removeMessageFromFailedCache(Message message, int conversationId) {

        ArrayList<Message> failedMessages = failedMessageCache.get(conversationId);

        if (failedMessages != null && failedMessages.contains(message)){
            failedMessages.remove(message);
        }
    }

    public void getMessagesForConversation(final int conversationId, final APICallbackWrapper<ArrayList<Message>> conversationsCallback) {

        MessagesWrapper.getMessagesForConversationId(conversationId, new APICallbackWrapper<ArrayList<Message>>() {
            @Override
            public void onResponse(ArrayList<Message> response) {

                if (response != null) {
                    Collections.reverse(response);
                    ArrayList<Message> conversationMessages = sortMessagesForConversations(response);

                    messageCache.put(conversationId, conversationMessages);
                }

                conversationsCallback.onResponse(getMessagesForConversationId(conversationId));

            }
        });
    }

    public void sendMessageForConversationId(int conversationId, MessageRequest messageRequest,  final APICallbackWrapper<Message> conversationsCallback) {


        MessagesWrapper.sendMessageToConversation(conversationId, messageRequest, new APICallbackWrapper<Message>() {
            @Override
            public void onResponse(Message response) {

                conversationsCallback.onResponse(response);
            }
        });

    }

    public void createConversation(String body, @Nullable String welcomeMessage, @Nullable Integer welcomeUserId, final APICallbackWrapper<Message> callbackWrapper) {

        MessagesWrapper.createConversation(body, welcomeMessage, welcomeUserId, new APICallbackWrapper<Message>() {
            @Override
            public void onResponse(Message response) {

                callbackWrapper.onResponse(response);
            }
        });

    }
}
