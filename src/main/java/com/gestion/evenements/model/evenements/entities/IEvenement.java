
package com.gestion.evenements.model.evenements.entities;
public interface IEvenement {
    String getTitre();
    boolean estComplet();
    double calculerRevenu();
}
