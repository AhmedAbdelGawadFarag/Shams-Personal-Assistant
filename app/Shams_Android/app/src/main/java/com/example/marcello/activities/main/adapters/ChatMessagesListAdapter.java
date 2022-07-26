package com.example.marcello.activities.main.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marcello.R;
import com.example.marcello.models.Message;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.marcello.models.MessageType;
import com.example.marcello.providers.OpenAppManager;

public class ChatMessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "ChatMessagesListAdapter";
    private ArrayList<Message> chatMessages = new ArrayList<Message>();
    private final int VIEW_TYPE_USER_MESSAGE = 1;
    private final int VIEW_TYPE_BOT_MESSAGE = 2;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypeSenderId) {
        int senderId = viewTypeSenderId % 10;
        int viewType = viewTypeSenderId / 10;
        if(senderId == VIEW_TYPE_USER_MESSAGE){
            return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_user_side,parent,false));
        }else if(senderId == VIEW_TYPE_BOT_MESSAGE){
            switch (viewType){
                case MessageType.CONTACT_ADD:
                    return new ContactAddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_contact,parent,false));
                case MessageType.CALENDAR_NEW:
                    return new CalendarAddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar, parent, false));
                case MessageType.ALARM_SET:
                    return new AlarmSetViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm, parent,false));
                default:
                    return new BotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bot_side,parent,false));

            }
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int senderId = chatMessages.get(position).getMessageSender();
        HashMap<Object, Object> data = chatMessages.get(position).getData();
        if(senderId == VIEW_TYPE_USER_MESSAGE){
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            userViewHolder.textMessage.setText(chatMessages.get(position).getMessageText());
        }else if(senderId == VIEW_TYPE_BOT_MESSAGE){
            switch (chatMessages.get(position).getMessageType()){
                case MessageType.CONTACT_ADD:
                    ContactAddViewHolder contactAddViewHolder = (ContactAddViewHolder) holder;
                    contactAddViewHolder.addedName.setText(data.get("displayName").toString());
                    break;
                case MessageType.CALENDAR_NEW:
                    CalendarAddViewHolder calendarAddViewHolder = (CalendarAddViewHolder) holder;
                    calendarAddViewHolder.eventName.setText(data.get("title").toString());
                    calendarAddViewHolder.eventDate.setText(data.get("startDate").toString());
                    calendarAddViewHolder.openCalendar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<Object, Object> data = new HashMap<>();
                            data.put("appName", "calendar");
                            OpenAppManager.getInstance().openApp(calendarAddViewHolder.openCalendar.getContext(), data);
                        }
                    });
                    break;
                case MessageType.ALARM_SET:
                    AlarmSetViewHolder alarmSetViewHolder = (AlarmSetViewHolder) holder;
                    double dHour = Double.parseDouble(data.get("hour").toString());
                    double dMinute = Double.parseDouble(data.get("minute").toString());

                    int hour = (int) dHour;
                    int minute = (int) dMinute;

                    alarmSetViewHolder.time.setText((hour + ":" + (minute < 10 ? "0" + minute : minute)).toString());
                    alarmSetViewHolder.openAlarm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<Object, Object> data = new HashMap<>();
                            data.put("appName", "clock");
                            OpenAppManager.getInstance().openApp(alarmSetViewHolder.openAlarm.getContext(), data);
                        }
                    });
                    break;
                default:
                    BotViewHolder botViewHolder = (BotViewHolder) holder;
                    botViewHolder.textMessage.setText(chatMessages.get(position).getMessageText());
                    break;

            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        int senderId = chatMessages.get(position).getMessageSender();
        return (chatMessages.get(position).getMessageType() * 10 + senderId);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void setList(ArrayList<Message> chatMessages){
        this.chatMessages = chatMessages;
        notifyDataSetChanged();
    }
    public class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
    public class CalendarAddViewHolder extends RecyclerView.ViewHolder{
        TextView eventName, eventDate;
        Button openCalendar;
        public CalendarAddViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            openCalendar = itemView.findViewById(R.id.open_calendar);

//            eventTime = itemView.findViewById(R.id.event_time);
        }
    }
    public class AlarmSetViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        Button openAlarm;
        public AlarmSetViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.alarm_time);
            openAlarm = itemView.findViewById(R.id.open_alarm);
        }
    }
    public class ContactAddViewHolder extends RecyclerView.ViewHolder{
        TextView addedName;
        public ContactAddViewHolder(@NonNull View itemView) {
            super(itemView);
            addedName = itemView.findViewById(R.id.addContactTv);
        }
    }
    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}
