package es.gva.edu.iesjuandegaray.bicis;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConexionDBB extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    
    // Variables globales para acceso desde los eventos
    private Connection conn = null;
    private JTextArea textArea;
    private JLabel lblEstadoConexion;
    private JLabel lblMensajeInfo;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ConexionDBB frame = new ConexionDBB();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        });
    }

    public ConexionDBB() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblNewLabel = new JLabel("Introduce el numero de estaciones:");
        lblNewLabel.setBounds(10, 10, 222, 12);
        contentPane.add(lblNewLabel);
        
        textField = new JTextField();
        textField.setBounds(269, 7, 96, 18);
        contentPane.add(textField);
        
        textArea = new JTextArea();
        textArea.setBounds(134, 44, 231, 95);
        contentPane.add(textArea);
        
        lblEstadoConexion = new JLabel("Estado: Desconectado");
        lblEstadoConexion.setBounds(136, 174, 150, 12);
        contentPane.add(lblEstadoConexion);
        
        lblMensajeInfo = new JLabel("Acciones pendientes...");
        lblMensajeInfo.setBounds(128, 207, 277, 12);
        contentPane.add(lblMensajeInfo);

        // BOTÓN DATOS
        JButton btnNewButtonDatos = new JButton("Datos");
        btnNewButtonDatos.addActionListener(e -> {
        	
        	
        	
        	String jsonRecibido = DatosJSon.obtenerDatosValenbisi();
            
            try {
                JSONObject root = new JSONObject(jsonRecibido);
                JSONArray features = root.getJSONArray("features");
                
                textArea.setText("LISTADO DE ESTACIONES:\n");
                
                for (int i = 0; i < Math.min(features.length(), 10); i++) {
                    JSONObject attr = features.getJSONObject(i).getJSONObject("attributes");
                    String nombre = attr.getString("name");
                    int bicis = attr.getInt("available");
                    
                    textArea.append("Estación: " + nombre + " | Bicis: " + bicis + "\n");
                }
            } catch (Exception ex) {
                textArea.setText("Error al leer los datos: " + ex.getMessage());
            }
        });
        btnNewButtonDatos.setBounds(10, 54, 114, 20);
        contentPane.add(btnNewButtonDatos);
        
        // BOTÓN CONECTAR
        JButton btnNewButtonConectar = new JButton("Conectar");
        btnNewButtonConectar.addActionListener(e -> {
            conector();
        });
        btnNewButtonConectar.setBounds(10, 170, 114, 20);
        contentPane.add(btnNewButtonConectar);
        
        // BOTÓN AÑADIR
        JButton btnNewButtonAñadir = new JButton("Añadir a BDD");
        btnNewButtonAñadir.addActionListener(e -> {
        	if (conn == null) {
                lblEstadoConexion.setText("Error: ¡Conecta primero!");
                return;
            }
            
            String jsonTexto = DatosJSon.obtenerDatosValenbisi(); 
            
            try {
                JSONObject root = new JSONObject(jsonTexto);
                JSONArray features = root.getJSONArray("features");

                String query = "INSERT INTO historico (estacion_id, bicis_disponibles, anclajes_libres, estado_operativo, datos) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                
                int cont = 0;
                for (int i = 0; i < Math.min(features.length(), 5); i++) {
                    JSONObject attr = features.getJSONObject(i).getJSONObject("attributes");
                    
                    int id = attr.optInt("number", 0);
                    int disponibles = attr.optInt("available", 0);
                    int libres = attr.optInt("free", 0);
                    String direccion = attr.optString("address", "Sin dirección");
                    
                    System.out.println("Procesando estación: " + id + " - " + direccion);
                    
                    pst.setInt(1, id);
                    pst.setInt(2, disponibles);
                    pst.setInt(3, libres);
                    pst.setBoolean(4, true); 
                    pst.setString(5, direccion);
                    
                    pst.executeUpdate();
                    cont++;
                }
                pst.close();
                
                textArea.setText("¡Éxito! Se guardaron " + cont + " estaciones en la BDD.");
                
            } catch (Exception ex) {
                textArea.setText("ERROR: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        btnNewButtonAñadir.setBounds(10, 203, 114, 20);
        contentPane.add(btnNewButtonAñadir);
        
        JButton btnNewButtonCerrar = new JButton("Cerrar conexion");
        btnNewButtonCerrar.addActionListener(e -> {
        	if (conn == null) {
                lblEstadoConexion.setText("Error: ¡Conecta primero!");
                return;
            }
        	try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    conn = null; // Muy importante para que el programa sepa que ya no hay conexión
                    lblEstadoConexion.setText("Estado: Desconectado");
                    textArea.setText("Conexión cerrada correctamente.");
                } else {
                    textArea.setText("No hay ninguna conexión activa para cerrar.");
                }
            } catch (SQLException ex) {
                textArea.setText("Error al cerrar: " + ex.getMessage());
            }
        });
        btnNewButtonCerrar.setBounds(140, 233, 161, 20);
        contentPane.add(btnNewButtonCerrar);
    }

    private void conector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:33306/valenbicibd";
            conn = DriverManager.getConnection(url, "root", "draco21");
            lblEstadoConexion.setText("Estado: Conectado");
        } catch (ClassNotFoundException | SQLException e) {
            lblEstadoConexion.setText("Error al conectar");
            e.printStackTrace();
        }
    }
}