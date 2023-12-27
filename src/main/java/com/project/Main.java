package com.project;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Aquest exemple mostra com fer una 
 * connexió a SQLite amb Java
 * 
 * A la primera crida, crea l'arxiu 
 * de base de dades hi posa dades,
 * després les modifica
 * 
 * A les següent crides ja estan
 * originalment modificades
 * (tot i que les sobreescriu cada vegada)
 */

public class Main {
    static ResultSet rs = null;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        Boolean run = true;
        String basePath = System.getProperty("user.dir") + "/data/";
        String filePath = basePath + "database.db";
        rs = null;

        // Si no hi ha l'arxiu creat, el crea i li posa dades
        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) { initDatabase(filePath); }

        
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Llistar les taules
        ArrayList<String> taules = UtilsSQLite.listTables(conn);
        System.out.println(taules);

        while (run) {

            String menu = "Escull una opció:";
            menu = menu + "\n 0) Mostrar una taula";
            menu = menu + "\n 1) Mostrar personatges per facció";
            menu = menu + "\n 2) Mostrar el millor atacant per facció";
            menu = menu + "\n 3) Mostrar el millor defensor per facció";
            menu = menu + "\n 4) Sortir";
            menu = menu + "\nOpcio: ";
            System.out.println(menu);

            int opc = sc.nextInt();

            switch (opc) {
                case 0:
                    System.out.println("Escull una opció: \n 0) Mostrar taula 'faccio' \n 1) Mostrar taula 'personatge'\nOpcio: ");
                    int opt = sc.nextInt();
                    switch (opt) {
                        case 0:
                            printTableFaccio(conn);
                            break;
                        case 1:
                            printTablePersonatge(conn);
                            break;
                        default:
                            System.out.println("Opcio no valida.");
                            break;
                    }
                    break;
                case 1:
                    printPersonatgeFaccio(conn);
                    break;
                case 2:
                    printBestAtac(conn);
                    break;
                case 3:
                    printBestDefensa(conn);
                    break;
                case 4:
                    System.out.println("Sortint");
                    run = false;
                    break;
            
                default:
                    System.out.println("Opció no valida");
                    break;
            }
        }

        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }

    static void initDatabase (String filePath) {
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Esborrar les taules (per si existeixen)
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS faccio;");
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS personatge;");

        // Crear les taules
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS faccio ("
                                    + " id integer PRIMARY KEY AUTOINCREMENT,"
                                    + " name text VARCHAR(15),"
                                    + " resum text VARCHAR(500));");

        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS personatge ("
                                    + " id integer PRIMARY KEY AUTOINCREMENT,"
                                    + " name text VARCHAR(15),"
                                    + " atac REAL,"
                                    + " defensa REAL,"
                                    + " idFaccio integer,"
                                    + " FOREIGN KEY (idFaccio) REFERENCES Faccio(id));");

        // Afegir elements a les taules
        UtilsSQLite.queryUpdate(conn, "INSERT INTO faccio (name, resum) VALUES (\"Knights\", \"The Knights are European warriors who follow a code of chivalry, dedicated to justice and the protection of the weak. Their fighting style is based on discipline and tactics, using heavy armor and blunt weapons. They are renowned for their loyalty and bravery in battle, defending their ideals with ferocity.\");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO faccio (name, resum) VALUES (\"Vikings\", \"The Vikings are fiercely independent Nordic warriors, known for their courage on the battlefield and their connection with nature. They fight for survival and glory, standing out for their ferocity in combat and bold tactics. Their fighting style focuses on brute strength and endurance, employing weapons such as axes and shields.\");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO faccio (name, resum) VALUES (\"Samurai\", \"Samurais represent Japanese warriors committed to the code of Bushido. They are masters of the art of the sword, striving for perfection in every aspect of their lives. Their combat style is agile and precise, characterized by fluid movements and lethal techniques. They use katanas and other traditional Japanese weapons.\");");

        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Warden\",204,130,1)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Conqueror\",192,140,1)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Peacekeeper\",210,120,1)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Raider\",204,130,2)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Warlord\",204,140,2)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Berserker\",238,130,2)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Kensei\",192,125,3)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Orochi\",210,120,3)");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (name, atac, defensa, idFaccio) VALUES (\"Nobushi\",204,120,3)");

        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }

    static public void printTableFaccio(Connection conn) throws SQLException {
        String sep = "|----------------------------------------|";
        String head = sep + "\n| id|name      |resum                    |\n" + sep;
        // Demanar informació de la taula
        rs = UtilsSQLite.querySelect(conn, "SELECT * FROM faccio;");
        System.out.println("Contingut de la taula 'faccio':");
        System.out.println(head);
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            while (name.length() < 10) {
                name = name +" ";
            }
            String resum = rs.getString("resum");
            if (resum.length() > 23) {
                while (resum.length() >= 23) {
                    resum = resum.substring(0, resum.length() -1);
                }
                resum = resum + "...";
            }
            
            System.out.println("|  " + id + "|" + name + "|" + resum + "|");
            System.out.println(sep);
        }
        System.out.println("\nPress ENTER to continue");
        sc.nextLine();
        sc.nextLine();
    }

    static public void printTablePersonatge(Connection conn) throws SQLException {
        String sep = "|--------------------------------------|";
        String head = sep + "\n| id|name        | atac| defe| idFaccio|\n" + sep;
        // Demanar informació de la taula
        rs = UtilsSQLite.querySelect(conn, "SELECT * FROM personatge;");
        System.out.println("Contingut de la taula 'personatge':");
        System.out.println(head);
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            while (name.length() < 12) {
                name = name +" ";
            }
            System.out.println("|  " + id + "|" + name + "|  " + rs.getInt("atac") + "|  " + rs.getInt("defensa") + "|        " + rs.getString("idFaccio") + "|");
            System.out.println(sep);
        }
        System.out.println("\nPress ENTER to continue");
        sc.nextLine();
        sc.nextLine();
    }

    static public void printPersonatgeFaccio(Connection conn) throws SQLException {
        String sep = "|--------------------------------------|";
        String head = sep + "\n| id|name        | atac| defe| idFaccio|\n" + sep + "\n";

        String knights = head;
        String vikings = head;
        String samurais = head;
        // Demanar informació de la taula
        rs = UtilsSQLite.querySelect(conn, "SELECT * FROM personatge;");
        System.out.println("Contingut de la taula 'personatge' per faccions:");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            while (name.length() < 12) {
                name = name +" ";
            }
            int atac = rs.getInt("atac");
            int defe = rs.getInt("defensa");
            int idFaccio = rs.getInt("idFaccio");
            switch (idFaccio) {
                case 1:
                    knights += "|  " + id + "|" + name + "|  " + atac + "|  " + defe + "|" + "Knights" + "  |\n" + sep + "\n";
                    break;
                case 2:
                    vikings += "|  " + id + "|" + name + "|  " + atac + "|  " + defe + "|" + "Vikings" + "  |\n" + sep + "\n";
                    break;
                case 3:
                    samurais += "|  " + id + "|" + name + "|  " + atac + "|  " + defe + "|" + "Samurais" + " |\n" + sep + "\n";
                    break;
                default:
                    break;
            }
        }
        System.out.println(" - Knigths:\n" + knights);
        System.out.println();
        System.out.println(" - Vikings:\n" + vikings);
        System.out.println();
        System.out.println(" - Samurais:\n" + samurais);
        System.out.println("\nPress ENTER to continue");
        sc.nextLine();
        sc.nextLine();
    }

    static public void printBestAtac(Connection conn) throws SQLException {
        String sep = "|--------------------------------------|";
        String head = sep + "\n| id|name        | atac| defe| idFaccio|\n" + sep + "\n";
    
        String knights = head;
        String vikings = head;
        String samurais = head;
    
        // Variables para almacenar temporalmente la información del personaje con el mejor ataque por facción
        int bestAtacKnightsId = 0;
        String bestAtacKnightsName = "";
        int bestAtacKnightsAtac = 0;
        int bestAtacKnightsDefensa = 0;
    
        int bestAtacVikingsId = 0;
        String bestAtacVikingsName = "";
        int bestAtacVikingsAtac = 0;
        int bestAtacVikingsDefensa = 0;
    
        int bestAtacSamuraisId = 0;
        String bestAtacSamuraisName = "";
        int bestAtacSamuraisAtac = 0;
        int bestAtacSamuraisDefensa = 0;
    
        // Demanar informació de la taula
        rs = UtilsSQLite.querySelect(conn, "SELECT * FROM personatge;");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            while (name.length() < 12) {
                name = name +" ";
            }
            int atac = rs.getInt("atac");
            int defe = rs.getInt("defensa");
            int idFaccio = rs.getInt("idFaccio");
    
            // Comprobar y actualizar la información del personaje con el mejor ataque por facción
            switch (idFaccio) {
                case 1:
                    if (atac > bestAtacKnightsAtac) {
                        bestAtacKnightsId = id;
                        bestAtacKnightsName = name;
                        bestAtacKnightsAtac = atac;
                        bestAtacKnightsDefensa = defe;
                    }
                    break;
                case 2:
                    if (atac > bestAtacVikingsAtac) {
                        bestAtacVikingsId = id;
                        bestAtacVikingsName = name;
                        bestAtacVikingsAtac = atac;
                        bestAtacVikingsDefensa = defe;
                    }
                    break;
                case 3:
                    if (atac > bestAtacSamuraisAtac) {
                        bestAtacSamuraisId = id;
                        bestAtacSamuraisName = name;
                        bestAtacSamuraisAtac = atac;
                        bestAtacSamuraisDefensa = defe;
                    }
                    break;
                default:
                    break;
            }
        }
    
        // Imprimir la información del personaje con el mejor ataque por facción
        knights += "|  " + bestAtacKnightsId + "|" + bestAtacKnightsName + "|  " + bestAtacKnightsAtac + "|  " + bestAtacKnightsDefensa + "|" + "Knights" + "  |\n" + sep + "\n";
        vikings += "|  " + bestAtacVikingsId + "|" + bestAtacVikingsName + "|  " + bestAtacVikingsAtac + "|  " + bestAtacVikingsDefensa + "|" + "Vikings" + "  |\n" + sep + "\n";
        samurais += "|  " + bestAtacSamuraisId + "|" + bestAtacSamuraisName + "|  " + bestAtacSamuraisAtac + "|  " + bestAtacSamuraisDefensa + "|" + "Samurais" + " |\n" + sep + "\n";
    
        System.out.println(" - Best Attack:\n" + knights);
        System.out.println();
        System.out.println(" - Best Attack:\n" + vikings);
        System.out.println();
        System.out.println(" - Best Attack:\n" + samurais);
        System.out.println("\nPress ENTER to continue");
        sc.nextLine();
        sc.nextLine();
    }

    static public void printBestDefensa(Connection conn) throws SQLException {
        String sep = "|--------------------------------------|";
        String head = sep + "\n| id|name        | atac| defe| idFaccio|\n" + sep + "\n";
    
        String knights = head;
        String vikings = head;
        String samurais = head;
    
        // Variables para almacenar temporalmente la información del personaje con la mejor defensa por facción
        int bestDefensaKnightsId = 0;
        String bestDefensaKnightsName = "";
        int bestDefensaKnightsAtac = 0;
        int bestDefensaKnightsDefensa = 0;
    
        int bestDefensaVikingsId = 0;
        String bestDefensaVikingsName = "";
        int bestDefensaVikingsAtac = 0;
        int bestDefensaVikingsDefensa = 0;
    
        int bestDefensaSamuraisId = 0;
        String bestDefensaSamuraisName = "";
        int bestDefensaSamuraisAtac = 0;
        int bestDefensaSamuraisDefensa = 0;
    
        // Demanar informació de la taula
        rs = UtilsSQLite.querySelect(conn, "SELECT * FROM personatge;");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            while (name.length() < 12) {
                name = name +" ";
            }
            int atac = rs.getInt("atac");
            int defe = rs.getInt("defensa");
            int idFaccio = rs.getInt("idFaccio");
    
            // Comprobar y actualizar la información del personaje con la mejor defensa por facción
            switch (idFaccio) {
                case 1:
                    if (defe > bestDefensaKnightsDefensa) {
                        bestDefensaKnightsId = id;
                        bestDefensaKnightsName = name;
                        bestDefensaKnightsAtac = atac;
                        bestDefensaKnightsDefensa = defe;
                    }
                    break;
                case 2:
                    if (defe > bestDefensaVikingsDefensa) {
                        bestDefensaVikingsId = id;
                        bestDefensaVikingsName = name;
                        bestDefensaVikingsAtac = atac;
                        bestDefensaVikingsDefensa = defe;
                    }
                    break;
                case 3:
                    if (defe > bestDefensaSamuraisDefensa) {
                        bestDefensaSamuraisId = id;
                        bestDefensaSamuraisName = name;
                        bestDefensaSamuraisAtac = atac;
                        bestDefensaSamuraisDefensa = defe;
                    }
                    break;
                default:
                    break;
            }
        }
    
        // Imprimir la información del personaje con la mejor defensa por facción
        knights += "|  " + bestDefensaKnightsId + "|" + bestDefensaKnightsName + "|  " + bestDefensaKnightsAtac + "|  " + bestDefensaKnightsDefensa + "|" + "Knights" + "  |\n" + sep + "\n";
        vikings += "|  " + bestDefensaVikingsId + "|" + bestDefensaVikingsName + "|  " + bestDefensaVikingsAtac + "|  " + bestDefensaVikingsDefensa + "|" + "Vikings" + "  |\n" + sep + "\n";
        samurais += "|  " + bestDefensaSamuraisId + "|" + bestDefensaSamuraisName + "|  " + bestDefensaSamuraisAtac + "|  " + bestDefensaSamuraisDefensa + "|" + "Samurais" + " |\n" + sep + "\n";
    
        System.out.println(" - Best Defensa:\n" + knights);
        System.out.println();
        System.out.println(" - Best Defensa:\n" + vikings);
        System.out.println();
        System.out.println(" - Best Defensa:\n" + samurais);
        System.out.println("\nPress ENTER to continue");
        sc.nextLine();
        sc.nextLine();
    }

}