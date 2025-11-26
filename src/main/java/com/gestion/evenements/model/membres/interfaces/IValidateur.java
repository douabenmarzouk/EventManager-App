package com.gestion.evenements.model.membres.interfaces;

import com.gestion.evenements.model.membres.exceptions.MembreException;

@FunctionalInterface
public interface IValidateur<T> {
    boolean  valider (T object) throws MembreException;
}

