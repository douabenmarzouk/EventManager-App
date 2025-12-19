package com.gestion.evenements.model.evenements.records;

public record InfoContact(String email, String telephone, String
adresse) {
    public boolean isComplet() {
        return email != null && !email.isEmpty() &&

                telephone != null && !telephone.isEmpty() &&
                adresse != null && !adresse.isEmpty();

    }
    @Override
    public String toString() {
        return String.format("Contact: %s | %s | %s", email,
                telephone, adresse);
    }
}
