package com.gestion.evenements.model.membres.interfaces;

public interface INotifiable {
    void envoyerNotification(String message);
    void envoyerEmail(String sujet, String corps);
}