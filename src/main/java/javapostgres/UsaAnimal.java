package javapostgres;

import java.sql.*;

public class UsaAnimal {
    private Connection conn = null; // connexió

    // connexió a la BD donats el nom d'usuari i la contrassenya.
    Connection connectaDB(String user, String pass) throws SQLException {
        if (conn == null) {
            String usuari = user;
            String password = pass;
            String host = "localhost";
            String bd = "testdb";
            String url = "jdbc:postgresql://" + host + "/" + bd;
            conn = DriverManager.getConnection(url, usuari, password);
            System.out.println("Connectat amb " + url);
        }
        return conn;
    }

    // tanca la connexió en cas que estigui connectada
    private void desconnecta() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
            System.out.println("Desconnectat");
            conn = null;
        }
    }

    // crea la taula d'animals
    private void creaTaula() throws SQLException {
        eliminaTaula(); // eliminem si ja existia
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "CREATE TABLE  ANIMALS (" +
                    "       id        SERIAL PRIMARY KEY," +
                    "       nom       TEXT,              " +
                    "       categoria VARCHAR(40))";
            st.executeUpdate(sql);
            System.out.println("Creada taula ANIMALS");
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // elimina la taula d'animals si existeix
    private void eliminaTaula() throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS ANIMALS";
            st.executeUpdate(sql);
            System.out.println("Eliminada taula ANIMALS");
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // torna true si troba un animal amb un determinat nom i categoria, o false si
    // no el troba.
    Boolean aquestAnimalExisteix(String nom, String categoria) throws SQLException {
        String sql = "SELECT * FROM ANIMALS WHERE nom= '" + nom + "' AND categoria='" + categoria + "';";
        Statement st = null;
        int nAnimals = 0;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                nAnimals++;
            }
            rs.close();
        } finally {
            if (st != null) {
                st.close();
            }
        }
        if (nAnimals == 0) {
            return false;
        } else {
            return true;
        }

    }

    // mostra la llista d'animals d'una categoria. Si categoria és "", mostra tots
    // els animals.
    void consulta(String categoria) throws SQLException {
        String sql = "";
        if (categoria == "") {
            sql = "SELECT * FROM ANIMALS ORDER BY nom";
        } else {
            sql = "SELECT * FROM ANIMALS WHERE categoria='" + categoria + "' ORDER BY nom";
        }

        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int nAnimals = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String cat = rs.getString("categoria");
                Animal animal = new Animal(id, nom, cat);
                System.out.println(animal);
                nAnimals++;
            }
            if (nAnimals == 0)
                System.out.println("Cap animal de moment");
            rs.close();
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // mostra el nombre d'animals que hi ha insertats
    private void comptaAnimals() throws SQLException {
        String SELECT_SQL = "SELECT COUNT(*) FROM Animals";
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(SELECT_SQL);
            rs.next();
            System.out.println("El nombre d'animals a la bd és: " + rs.getInt(1));
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // elimina animals donats el nom i categoria
    public void eliminaAnimal(String nom, String categoria) throws SQLException {
        String sql = "DELETE FROM Animals WHERE nom = '" + nom + "' AND categoria ='" + categoria + "';";
        // envia la comanda
        Statement st = null;
        try {
            st = conn.createStatement();
            int numeroAnimalsEliminats = st.executeUpdate(sql);
            System.out.println("Nombre d'animals eliminats: " + numeroAnimalsEliminats);
            if (numeroAnimalsEliminats > 0) {
                System.out.println("s'ha eliminat l'animal amb nom " + nom + " i categoria " + categoria);
            } else {
                System.out.println("no s'ha pogut eliminar l'animal");
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // afegeix un animal a la taula i obté l'id generat
    public void afegeixAnimal(String nom, String categoria) throws SQLException {
        // crea l'animal
        Animal a = new Animal(nom, categoria);
        // crea la comanda
        String sql = "INSERT INTO ANIMALS (nom, categoria) values ('"
                + a.getNom()
                + "', '"
                + a.getCategoria()
                + "')";
        // envia la comanda
        Statement st = null;
        try {
            st = conn.createStatement();
            int num = st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Nombre d'animals afegits: " + num);
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            a.setId(id); // assignem l'id de l'animal
            System.out.println("Afegit l'animal " + a);
            rs.close();
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }

    // afegeix uns quants animals a la taula
    // Ho fa com a transacció (o tots o cap)
    // Fa servir un PreparedStatement per optimitzar repetides crides
    private void afegeixMoltsAnimals() throws SQLException {
        // plantilla de la sentència d'inserció
        String sql = "INSERT INTO Animals (nom, categoria) values (?,?)";
        // crea els animals
        Animal[] llista = {
                new Animal("estruç", "ocell"),
                new Animal("kiwi", "ocell"),
                new Animal("gos", "mamifer"),
                new Animal("bacallà", "peix"),
                new Animal("dofí", "peix")
        };
        // crea la sentència a executar (només un cop!)
        PreparedStatement ps = null;
        try {
            // obté l'estat anterior de l'autocommit.
            boolean anteriorAutoCommit = conn.getAutoCommit();
            ps = conn.prepareStatement(sql);
            try {
                // fem que no faci autocommit a cada execució
                conn.setAutoCommit(false);
                // afegeix cada animal de la llista
                for (Animal a : llista) {
                    // afegim els valors a insertar
                    ps.setString(1, a.getNom()); // primer camp
                    ps.setString(2, a.getCategoria()); // segon camp
                    ps.executeUpdate();
                    System.out.println("Afegit l'animal " + a);
                }
                // si no hi ha problemes accepta tot
                conn.commit();
            } catch (SQLException e) {
                // trobat problemes amb la inserció: tot enrere
                conn.rollback();
            } finally {
                // tornem l'estat de autocomit tal i com estava
                conn.setAutoCommit(anteriorAutoCommit);
            }
        } finally {
            if (ps != null) {
                ps.close();
                ps.close();
            }
        }
    }

    public void modificaAnimals(String nom, String categoriaVella, String categoriaNova) throws SQLException {
        // tenim el dofí en una categoria equivocada. Canviem-la a la BD
        String sql = "UPDATE Animals set categoria = '" + categoriaNova + "' WHERE nom = '" + nom + "' AND categoria='"
                + categoriaVella + "'";
        Statement st = null;
        try {
            st = conn.createStatement();
            int numeroAnimalsModificats = st.executeUpdate(sql);
            System.out.println(numeroAnimalsModificats + " animals modificats");
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }
}