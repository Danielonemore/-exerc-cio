package br.com.brazcubas.Biblioteca;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BibliotecaView view = new BibliotecaView();
        BibliotecaModel model = new BibliotecaModel();
        BibliotecaController controller = new BibliotecaController(view, model);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Cadastrar Livro");
            System.out.println("2. Listar Livros");
            System.out.println("3. Emprestar Livro");
            System.out.println("4. Devolver Livro");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); 
            switch (opcao) {
                case 1:
                    System.out.print("Digite o título do livro: ");
                    String titulo = scanner.nextLine();
                    controller.cadastrarLivro(titulo);
                    break;
                case 2:
                    controller.listarLivros();
                    break;
                case 3:
                    System.out.print("Digite o título do livro que deseja emprestar: ");
                    titulo = scanner.nextLine();
                    controller.emprestarLivro(titulo);
                    break;
                case 4:
                    System.out.print("Digite o título do livro que deseja devolver: ");
                    titulo = scanner.nextLine();
                    controller.devolverLivro(titulo);
                    break;
                case 5:
                    System.exit(0);
            }
        }
    }
}

class Livro {
    private String titulo;
    private boolean emprestado;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isEmprestado() {
        return emprestado;
    }

    public void setEmprestado(boolean emprestado) {
        this.emprestado = emprestado;
    }
}

class BibliotecaView {
    public void mostrarLivros(List<Livro> livros) {
        for (Livro livro : livros) {
            System.out.println(livro.getTitulo() + " - " + (livro.isEmprestado() ? "Emprestado" : "Disponível"));
        }
    }
}

class BibliotecaModel {
    private List<Livro> livros;
    private Connection conn;

    public BibliotecaModel() {
        this.livros = new ArrayList<>();
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:biblioteca.db");
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS livros (titulo TEXT, emprestado BOOLEAN)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adicionarLivro(Livro livro) {
        livros.add(livro);
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO livros (titulo, emprestado) VALUES (?, ?)");
            pstmt.setString(1, livro.getTitulo());
            pstmt.setBoolean(2, livro.isEmprestado());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Livro> getLivros() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM livros");
            while (rs.next()) {
                Livro livro = new Livro();
                livro.setTitulo(rs.getString("titulo"));
                livro.setEmprestado(rs.getBoolean("emprestado"));
                livros.add(livro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livros;
    }

    public void emprestarLivro(String titulo) {
        for (Livro livro : livros) {
            if (livro.getTitulo().equals(titulo)) {
                livro.setEmprestado(true);
                try {
                    PreparedStatement pstmt = conn.prepareStatement("UPDATE livros SET emprestado = ? WHERE titulo = ?");
                    pstmt.setBoolean(1, true);
                    pstmt.setString(2, titulo);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void devolverLivro(String titulo) {
        for (Livro livro : livros) {
            if (livro.getTitulo().equals(titulo)) {
                livro.setEmprestado(false);
                try {
                    PreparedStatement pstmt = conn.prepareStatement("UPDATE livros SET emprestado = ? WHERE titulo = ?");
                    pstmt.setBoolean(1, false);
                    pstmt.setString(2, titulo);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}

class BibliotecaController {
    private BibliotecaView view;
    private BibliotecaModel model;

    public BibliotecaController(BibliotecaView view, BibliotecaModel model) {
        this.view = view;
        this.model = model;
    }

    public void cadastrarLivro(String titulo) {
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        model.adicionarLivro(livro);
    }

    public void listarLivros() {
        List<Livro> livros = model.getLivros();
        view.mostrarLivros(livros);
    }

    public void emprestarLivro(String titulo) {
        model.emprestarLivro(titulo);
    }

    public void devolverLivro(String titulo) {
        model.devolverLivro(titulo);
    }
}