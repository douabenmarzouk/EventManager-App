package com.gestion.evenements.model.evenements.entities;
class CategorieFactory {
    /**
     * Crée une catégorie selon le type spécifié
     */
    public static CategorieSealed creer(String type, int id, String
                                                nom,

                                        String description, String

                                                couleur, double prix) {
        return switch (type.toLowerCase()) {

            case "standard" -> new CategorieStandard(id, nom,

                    description, couleur, prix);

            case "vip" -> new CategorieVIP(id, nom, description,

                    couleur, prix, "");

            case "gratuite", "gratuit" -> new CategorieGratuite(id,

                    nom, description, couleur);

            default -> new CategorieStandard(id, nom, description, couleur, prix);
        };
    }
    /**
     * Crée une catégorie standard simple (pour compatibilité)
     */
    public static CategorieSealed creerStandard(int id, String nom,
                                                String description, String couleur) {
        return new CategorieStandard(id, nom, description, couleur,0.0);
    }
}
