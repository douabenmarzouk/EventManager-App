package com.gestion.evenements.model.evenements.interfaces;
import java.util.List;
import java.util.function.Predicate;
public interface IRecherchable<T> {
    List<T> rechercher(String critere);
    List<T> filtrer(Predicate<T> critere);
}
