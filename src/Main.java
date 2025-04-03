/**
 *
 * @author Yael Eli Orozco Sandoval
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import javax.swing.JFrame;

public class Main extends JFrame { // Define la clase que extiende JFrame para la interfaz gráfica
    // Campos de texto para la entrada de datos
    private final JTextField txtNombre;
    private final JTextField txtApellido;
    private final JTextField txtEdad;
    private final JTextField txtCargo;
    private final JTextField txtSalario;
    private String server = "";
    private String user = "";
    private String password = "";

    public Main() { // Constructor de la clase
        setTitle("Registro de Empleado"); // Establece el título de la ventana
        setSize(600, 500); // Define el tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setLayout(new GridLayout(7, 2, 5, 5)); // Define un diseño de cuadrícula con 6 filas y 2 columnas

        get_properties();

        // Labels y Campos de Texto
        add(new JLabel("Nombre:")); // Etiqueta para el nombre
        txtNombre = new JTextField(); // Campo de texto para el nombre
        add(txtNombre);

        add(new JLabel("Apellido:")); // Etiqueta para el apellido
        txtApellido = new JTextField(); // Campo de texto para el apellido
        add(txtApellido);

        add(new JLabel("Edad:")); // Etiqueta para la edad
        txtEdad = new JTextField(); // Campo de texto para la edad
        add(txtEdad);

        add(new JLabel("Cargo:")); // Etiqueta para el cargo
        txtCargo = new JTextField(); // Campo de texto para el cargo
        add(txtCargo);

        add(new JLabel("Salario:")); // Etiqueta para el salario
        txtSalario = new JTextField(); // Campo de texto para el salario
        add(txtSalario);

        /* Botones*/
        JButton btnGuardar = new JButton("Guardar"); // Botón para guardar los datos
        JButton btnLimpiar = new JButton("Limpiar"); // Botón para limpiar los campos de texto
        JButton btnMostrar = new JButton("Mostrar Todos"); // Imprime todos los valores

        add(btnGuardar); // Agrega el botón Guardar a la interfaz
        add(btnLimpiar); // Agrega el botón Limpiar a la interfaz
        add(btnMostrar); //Agrega una ventana para añadir una tabla con todos los valorfoes

        // Acciones de los botones
        // Evento al hacer clic en Guardar
        btnGuardar.addActionListener(_ -> {
            if (verificarCampos()){
                guardarEmpleado(); // Llama al método para guardar los datos
            }
        });

        // Evento al hacer clic en Limpiar
        btnLimpiar.addActionListener(_ -> {
            limpiarCampos(); // Llama al método para limpiar los campos de texto
        });

        btnMostrar.addActionListener(_ -> {
            imprimirTodos(); // Imprime en un JFrame todos los valores de la consulta
        });

    }

    private void get_properties(){
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("C:/Users/PC/OneDrive/Documentos/tareaJava/src/config.properties");

            prop.load(input);

            user = prop.getProperty("nombre");
            server = prop.getProperty("server");


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean verificarCampos() {
        String edad = txtEdad.getText(); // Obtiene el texto del campo Edad
        String salario = txtSalario.getText(); // Obtiene el texto del campo Salario

        try {
            Integer.parseInt(edad);
            Double.parseDouble(salario);
        }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El campo de edad o el campo de salario no son el tipo correcto", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }

    private void guardarEmpleado() { // Método para guardar los datos en la base de datos
        String nombre = txtNombre.getText(); // Obtiene el texto del campo Nombre
        String apellido = txtApellido.getText(); // Obtiene el texto del campo Apellido
        String edad = txtEdad.getText(); // Obtiene el texto del campo Edad
        String cargo = txtCargo.getText(); // Obtiene el texto del campo Cargo
        String salario = txtSalario.getText(); // Obtiene el texto del campo Salario

        // Verifica que ningún campo esté vacío
        if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || cargo.isEmpty() || salario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (insertarEnBD(nombre, apellido, edad, cargo, salario)) { // Intenta insertar en la base de datos
                JOptionPane.showMessageDialog(this, "Empleado registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el empleado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() { // Método para limpiar los campos de texto
        txtNombre.setText("");
        txtApellido.setText("");
        txtEdad.setText("");
        txtCargo.setText("");
        txtSalario.setText("");
    }

    private boolean insertarEnBD(String nombre, String apellido, String edad, String cargo, String salario) {
        String url = "jdbc:mariadb://localhost:3306/empresa"; // URL de conexión a la base de datos
        String usuario = user; // Usuario de la base de datos
        String contrasena = password; // Contraseña de la base de datos

        String sql = "INSERT INTO empleados (nombre, apellido, edad, cargo, salario) VALUES (?, ?, ?, ?, ?)"; // Consulta SQL de inserción

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); // Conecta a la base de datos
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara la consulta SQL

            stmt.setString(1, nombre); // Establece el valor del primer parámetro (nombre)
            stmt.setString(2, apellido); // Establece el valor del segundo parámetro (apellido)
            stmt.setInt(3, Integer.parseInt(edad)); // Convierte la edad a entero y la establece
            stmt.setString(4, cargo); // Establece el valor del cuarto parámetro (cargo)
            stmt.setDouble(5, Double.parseDouble(salario)); // Convierte el salario a decimal y lo establece

            stmt.executeUpdate(); // Ejecuta la consulta de inserción
            return true; // Retorna true si la inserción fue exitosa
        } catch (SQLException | NumberFormatException ex) { // Captura errores de SQL o conversión de datos
            ex.printStackTrace(); // Imprime el error en la consola
            return false; // Retorna false si hubo un error
        }
    }

    public void imprimirTodos(){
        JFrame listar = new JFrame("Listado de Empleados"); //Crear ventana
        listar.setSize(600, 400); //Ajustar tamaño
        listar.setLocationRelativeTo(null); //Posición relativa

        DefaultTableModel modelo = new DefaultTableModel(); //Crear tabla
        modelo.addColumn("Nombre");
        modelo.addColumn("Apellido");
        modelo.addColumn("Edad");
        modelo.addColumn("Cargo");
        modelo.addColumn("Salario");

        String url = "jdbc:mariadb://localhost:3306/empresa";
        String usuario = user;
        String contrasena = password;

        String sql = "SELECT nombre, apellido, edad, cargo, salario FROM empleados";
        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); //Conexión
             PreparedStatement ps = conn.prepareStatement(sql); //Consulta preparada
             ResultSet rs = ps.executeQuery()) { //Respuesta de la consulta

            while (rs.next()) {
                Object[] fila = {
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getInt("edad"),
                        rs.getString("cargo"),
                        rs.getDouble("salario")
                };
                modelo.addRow(fila);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al listar empleados", "Error", JOptionPane.ERROR_MESSAGE);
        }
        JTable tabla = new JTable(modelo);  //Crear tabla vacia para añadir los componentes que se sacaron de la consulta
        JScrollPane scroll = new JScrollPane(tabla);
        listar.add(scroll);
        listar.setVisible(true);
    }

    public static void main(String[] args) { // Método principal para ejecutar la aplicación
        SwingUtilities.invokeLater(() -> { // Ejecuta la interfaz en el hilo de eventos de Swing
            new Main().setVisible(true); // Crea y muestra la ventana de la aplicación
        });
    }
}