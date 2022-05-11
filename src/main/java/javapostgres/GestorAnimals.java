package javapostgres;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class GestorAnimals {
    public static void main(String[] args) {
        GestorAnimals gestorAnimals = new GestorAnimals();
        gestorAnimals.start();
    }

    void start() {
        Scanner lector = new Scanner(System.in);
        System.out.println("Usuari:");
        String user = lector.nextLine();
        System.out.println("Password:");
        String pass = lector.nextLine();
        UsaAnimal usaAnimal = new UsaAnimal();
        try {
            System.out.println("connectant...");
            Connection conn = usaAnimal.connectaDB(user, pass);
            System.out.println("connectant...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("no s'ha pogut connectar. torna a intentar.");
            start();
        }
        esperarOrdre(usaAnimal, lector);
    }

    void esperarOrdre(UsaAnimal usaAnimal, Scanner lector) {
        Boolean acabar = false;
        while (!acabar) {
            System.out.println("Escriu la ordre que vols fer:");
            String ordre = lector.nextLine();
            switch (ordre) {
                case "help":
                    help();
                    break;
                case "list":
                    list(usaAnimal, lector);
                    break;
                case "add":
                    add(usaAnimal, lector);
                    break;
                case "del":
                    del(usaAnimal, lector);
                    break;
                case "assign":
                    assign(usaAnimal, lector);
                    break;
                case "quit":
                    acabar = true;
                    break;
                default:
                    System.out.println("Aquesta ordre no existeix.");
                    break;
            }
        }
        lector.close();
        System.out.println("Tancant el programa. Adéu!");
    }

    void help() {
        System.out.println("---------------------------");
        System.out.println(" > help    ajuda");
        System.out.println(" > list    llistar animals");
        System.out.println(" > add     afegir animal");
        System.out.println(" > del     eliminar animal");
        System.out.println(" > assign  assignar categoria");
        System.out.println(" > quit    sortir del programa");
        System.out.println("---------------------------");
    }

    void assign(UsaAnimal usaAnimal, Scanner lector) {
        System.out.println("Nom de l'animal que vols modificar:");
        String nom = lector.nextLine();
        System.out.println("Categoria actual de l'animal que vols modificar:");
        String categoriaVella = lector.nextLine();
        System.out.println("Nova categoria:");
        String categoriaNova = lector.nextLine();
        try {
            usaAnimal.modificaAnimals(nom, categoriaVella, categoriaNova);
            System.out.println(nom + " ara és de la categoria " + categoriaNova);
        } catch (Exception e) {
            System.out.println("no s'ha pogut assignar l'animal");
        }
    }

    void add(UsaAnimal usaAnimal, Scanner lector) {
        System.out.println("Nom del nou animal:");
        String nom = lector.nextLine();
        System.out.println("Nom de la categoria del nou animal:");
        String categoria = lector.nextLine();
        try {
            if (usaAnimal.aquestAnimalExisteix(nom, categoria)) {
                System.out
                        .println("ja exiteix un animal amb el nom '" + nom + "' i categoria '" + categoria + "'");
            } else {
                usaAnimal.afegeixAnimal(nom, categoria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void del(UsaAnimal usaAnimal, Scanner lector) {
        System.out.println("Nom de l'animal que vols eliminar");
        String nom = lector.nextLine();
        System.out.println("Nom de la categoria de l'animal que vols eliminar:");
        String categoria = lector.nextLine();
        try {
            if (usaAnimal.aquestAnimalExisteix(nom, categoria)) {
                System.out.println("borrant animal...");
                usaAnimal.eliminaAnimal(nom, categoria);
            } else {
                System.out.println("aquest animal no existeix!");
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    void list(UsaAnimal usaAnimal, Scanner lector) {
        System.out.println("Quina categoria vols llistar? ([enter] per llistar totes les categories)");
        String categoria = lector.nextLine();
        try {
            usaAnimal.consulta(categoria);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
