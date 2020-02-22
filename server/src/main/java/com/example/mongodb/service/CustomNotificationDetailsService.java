package com.example.mongodb.service;

import java.util.List;

import com.example.mongodb.dao.Notificationdao;
import com.example.mongodb.objects.Notifications;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomNotificationDetailsService {
    @Autowired
    private Notificationdao notificationdao;

    public List<Notifications> findNotificationByRecipient(String recipient) {
        return notificationdao.findByRecipient(recipient);
    }
    public Notifications findNotificationById(ObjectId notificationsIds) {
        return notificationdao.findByNotificationsId(notificationsIds);
    }

}