package com.example.marcello.core;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.marcello.api.ApiInterface;
import com.example.marcello.api.RetrofitClient;
import com.example.marcello.models.Message;
import com.example.marcello.models.MessageType;
import com.example.marcello.providers.AlarmClockManager;
import com.example.marcello.providers.ArabicTranslationManager;
import com.example.marcello.providers.CalendarManager;
import com.example.marcello.providers.ContactManager;
import com.example.marcello.providers.EmailManager;
import com.example.marcello.providers.NotificationProvider;
import com.example.marcello.providers.OpenAppManager;
import com.example.marcello.providers.Requirements.AlarmClockRequirements;
import com.example.marcello.providers.Requirements.CalendarRequirements;
import com.example.marcello.providers.Requirements.ContactRequirements;
import com.example.marcello.providers.Requirements.EmailRequirements;
import com.example.marcello.providers.Requirements.OpenAppRequirements;
import com.example.marcello.providers.Requirements.TranslationRequirements;
import com.example.marcello.providers.Requirements.WebSearchRequirements;
import com.example.marcello.providers.WebSearchManager;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BotManager implements DialogManager.IDialogResult {

    private static final String TAG = "BotManager";
    public static final int QUERY_TYPE_TEXT = 0;
    public static final int QUERY_TYPE_AUDIO = 1;
    public static final int QUERY_TYPE_FILLING_REQUIREMENTS = 2;


    private static BotManager instance =  new BotManager();
    private final AlarmClockManager alarmClockManager =  AlarmClockManager.getInstance();
    private final CalendarManager calendarManager = CalendarManager.getInstance();
    private final ContactManager contactManager = ContactManager.getInstance();
    private final WebSearchManager webSearchManager = WebSearchManager.getInstance();
    private final DialogManager dialogManager = DialogManager.getInstance();
    private final EmailManager emailManager = EmailManager.getInstance();
    private final OpenAppManager openAppManager = OpenAppManager.getInstance();
    private final NotificationProvider notificationProvider = NotificationProvider.getInstance();
    private final ArabicTranslationManager arabicTranslationManager = ArabicTranslationManager.getInstance();

    private final String REGEX_MATCH_TIME = "(?<hours>\\d{1,2})(:(?<minutes>\\d{1,2}))?\\s*(?<format>[A|P]M)?";

    // CallBack interfaces
    private ICommandExecution mCommandExecution;
    private IUserAudioCommandExecution mUserAudioCommandExecution;
    public void setICommandExecution(ICommandExecution commandExecution){
        this.mCommandExecution = commandExecution;
    }
    public void setIUserAudioCommandExecution(IUserAudioCommandExecution userAudioCommandExecution){
        this.mUserAudioCommandExecution = userAudioCommandExecution;
    }

    private BotManager(){}
    public static synchronized BotManager getInstance(){
        return instance;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dealWith(Context context,String message, int messageType) {
        ApiInterface client = RetrofitClient.getInstance().create(ApiInterface.class);
        HashMap<Object, Object> payload = new HashMap<>();
        Call<HashMap<Object, Object>> call = null;
        switch (messageType){
            case QUERY_TYPE_TEXT:
                payload.put("text", message);
                call = client.uploadText(payload);
                break;
            case QUERY_TYPE_AUDIO:
                payload.put("audio", message);
                call = client.uploadAudio(payload);
                break;

        }

        assert call != null;
        call.enqueue(new Callback<HashMap<Object, Object>>() {
            @Override
            public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                Log.d(TAG, "upload is success.");
                try {
                    Log.d(TAG, "onResponse: " + response.body());
                    if(messageType == QUERY_TYPE_AUDIO && response.body().get("error") == null){
                        mUserAudioCommandExecution.onUserAudioCommandSent(new Message(response.body().get("userSTT").toString(), Message.MESSAGE_SENDER_USER, MessageType.TEXT));
                    }else if(response.body().get("error") != null){
                        mCommandExecution.onCommandExecutionFinished(new Message("اسف لم استطع فهمك.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
                    }
                    process(context, response.body());
                } catch (Exception e) {
                    e.printStackTrace();
//                    if(messageType == QUERY_TYPE_AUDIO){
//                        mUserAudioCommandExecution.onUserAudioCommandSent(new Message(response.body().get("userSTT").toString(), Message.MESSAGE_SENDER_USER, MessageType.TEXT));
//                    }
                }
            }

            @Override
            public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                Log.d(TAG, "upload failed due to: " + t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void process(Context context, HashMap<Object, Object> command) throws ParseException {


        Log.d(TAG, "process: prcessing user command.");
        dialogManager.setIDialogResult(BotManager.this);
        Log.d(TAG, "process: Intent = " + command.get("intent").toString());
        switch (command.get("intent").toString()){
            case "call contact":
                dialogManager.start(context, command,
                        ContactRequirements.CallContact.REQUIREMENTS,
                        ContactRequirements.CallContact.MESSAGES);
                break;
            case "add contact":
                dialogManager.start(context, command,
                        ContactRequirements.AddContact.REQUIREMENTS,
                        ContactRequirements.AddContact.MESSAGES);
                break;
            case "web search":
                dialogManager.start(context,command,
                        WebSearchRequirements.WebSearch.REQUIREMENTS,
                        WebSearchRequirements.WebSearch.MESSAGES);
                break;
            case "read email":
                dialogManager.start(context, command);
                break;
            case "send email":
                dialogManager.start(context, command,
                        EmailRequirements.ComposeEmail.REQUIREMENTS,
                        EmailRequirements.ComposeEmail.MESSAGES);
                break;
            case "new calendar":
                dialogManager.start(context, command,
                        CalendarRequirements.InsertCalendar.REQUIREMENTS,
                        CalendarRequirements.InsertCalendar.MESSAGES);
                break;
            case "open app":
                dialogManager.start(context, command,
                        OpenAppRequirements.OpenApp.REQUIREMENTS,
                        OpenAppRequirements.OpenApp.MESSAGES);
                break;
            case "read notification":
                dialogManager.start(context, command);
                break;
            case "read calendar":
                dialogManager.start(context, command,
                        CalendarRequirements.ReadCalendar.REQUIREMENTS,
                        CalendarRequirements.ReadCalendar.MESSAGES);
                break;
            case "set alarm":
                dialogManager.start(context, command,
                        AlarmClockRequirements.SetAlarm.REQUIREMENTS,
                        AlarmClockRequirements.SetAlarm.MESSAGES);
                break;
            case "translate":
                dialogManager.start(context, command,
                        TranslationRequirements.Translate.REQUIREMENTS,
                        TranslationRequirements.Translate.MESSAGES);
                break;
            case "greetings":
                dialogManager.start(context, command);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDialogResults(Context context, HashMap<Object, Object> result) {

        Message message = new Message();
        if(result.get("intent").equals("add contact")){
            contactManager.addContact(context, result);
            message.setMessageType(MessageType.CONTACT_ADD);
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            message.setMessageText( " الى جهات الاتصال" + result.get("displayName").toString() + "تم اضافه ");
            message.setData(result);
            mCommandExecution.onCommandExecutionFinished(message);
        }else if(result.get("intent").equals("delete contact")){
            contactManager.deleteContact(context, result);
        }else if(result.get("intent").equals("call contact")){
            message.setMessageType(MessageType.CONTACT_ADD);
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            message.setData(result);
            contactManager.makeACall(context, result);
            mCommandExecution.onCommandExecutionFinished(message);
            mCommandExecution.onCommandExecutionFinished(new Message("جارى الاتصال." , Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        }else if(result.get("intent").equals("web search")){
            message.setMessageType(MessageType.TEXT);
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            String resMsg = webSearchManager.doSearch(context, result);
            message.setMessageText(resMsg);
            mCommandExecution.onCommandExecutionFinished(message);
        }else if(result.get("intent").equals("read email")){
            emailManager.readMyMails(context);
            message.setMessageText("جارى فتج ال Gmail");
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            message.setMessageType(MessageType.TEXT);
            mCommandExecution.onCommandExecutionFinished(message);
        }else if(result.get("intent").equals("send email")){
            emailManager.composeEmail(context, result);
            message.setMessageType(MessageType.TEXT);
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            message.setMessageText("حسناً");
            mCommandExecution.onCommandExecutionFinished(message);

        }else if(result.get("intent").equals("new calendar")){
            message.setMessageType(MessageType.CALENDAR_NEW);
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            message.setData(result);
            message.setMessageText("تم تسجيل الايفينت.");
            try {
                calendarManager.insertCalendar(context, result);
                mCommandExecution.onCommandExecutionFinished(message);

            }catch (Exception e){
                Log.d(TAG, "onDialogResults: error: " + e.getMessage());
            }
        }else if(result.get("intent").equals("open app")){
            openAppManager.openApp(context, result);
            message.setMessageText("جارى فتح التطبيق");
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            message.setMessageType(MessageType.TEXT);
            mCommandExecution.onCommandExecutionFinished(message);

        }else if(result.get("intent").equals("read notification")){
            notificationProvider.showNotifications(context);
//            mCommandExecution.onCommandExecutionFinished(new Message("done", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        }else if(result.get("intent").equals("read calendar")){
            try {
                List<HashMap<Object, Object>> events =  calendarManager.getEventsOfCalender(context, result);
                Log.d(TAG, "onDialogResults: events count = " + events.size());
                for(HashMap<Object, Object> event : events){
                    Message eMessage = new Message();
                    eMessage.setData(event);
                    eMessage.setMessageSender(Message.MESSAGE_SENDER_BOT);
                    eMessage.setMessageType(MessageType.CALENDAR_NEW);
                    mCommandExecution.onCommandExecutionFinished(eMessage);
                }
                if(events.isEmpty()){
                    mCommandExecution.onCommandExecutionFinished(new Message("لا يوجد اى مواعيد.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
                }else{
                    mCommandExecution.onCommandExecutionFinished(new Message("هذه هى مواعيدك اليوم.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(result.get("intent").equals("set alarm")){
            message.setMessageType(MessageType.ALARM_SET);
            message.setMessageSender(Message.MESSAGE_SENDER_BOT);
            message.setData(result);

            AlarmClockManager.getInstance().createAlarmClock(context, result);
            mCommandExecution.onCommandExecutionFinished(message);
            mCommandExecution.onCommandExecutionFinished(new Message("تم ضبط المنبه.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        }else if(result.get("intent").equals("translate")){
            String translatedText = arabicTranslationManager.translateToArabic(context, result);
            mCommandExecution.onCommandExecutionFinished(new Message(translatedText, Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        }else if(result.get("intent").equals("greetings")){
            mCommandExecution.onCommandExecutionFinished(new Message("حبيبي تسلم", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        }
        else {
            mCommandExecution.onCommandExecutionFinished(new Message("اسف لم استطع فهمك.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        }
//        mCommandExecution.onCommandExecutionFinished("done");
    }

    @Override
    public void onDialogSTT(Message message) {
        mUserAudioCommandExecution.onUserAudioCommandSent(message);
    }

    public interface IUserAudioCommandExecution {
        void onUserAudioCommandSent(Message message);
    }
    public interface ICommandExecution{
        void onCommandExecutionFinished(Message message);
    }

}
