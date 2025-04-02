/**
 *
 * @author Yael Eli Orozco Sandoval
 */
import javax.swing.*; // Importa la biblioteca Swing para crear la interfaz gráfica
import java.awt.*; // Importa la biblioteca AWT para el manejo de diseño de la interfaz
import java.awt.event.ActionEvent; // Importa eventos de acción para los botones
import java.awt.event.ActionListener; // Importa el listener para manejar eventos de los botones
import java.sql.*;

public class Main extends JFrame { // Define la clase que extiende JFrame para la interfaz gráfica
    private final JTextField txtNombre;
    private final JTextField txtApellido;
    private final JTextField txtEdad;
    private final JTextField txtCargo;
    private final JTextField txtSalario; // Campos de texto para la entrada de datos

    public Main() { // Constructor de la clase
        setTitle("Registro de Empleado"); // Establece el título de la ventana
        setSize(600, 500); // Define el tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setLayout(new GridLayout(7, 2, 5, 5)); // Define un diseño de cuadrícula con 6 filas y 2 columnas

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

        // Botones
        JButton btnGuardar = new JButton("Guardar"); // Botón para guardar los datos
        // Botones para guardar y limpiar datos
        JButton btnLimpiar = new JButton("Limpiar"); // Botón para limpiar los campos de texto
        JButton btnMostrar = new JButton("Mostrar Todos"); // Imprime

        add(btnGuardar); // Agrega el botón Guardar a la interfaz
        add(btnLimpiar); // Agrega el botón Limpiar a la interfaz
        add(btnMostrar);

        // Acciones de los botones
        btnGuardar.addActionListener(new ActionListener() { // Evento al hacer clic en Guardar
            @Override
            public void actionPerformed(ActionEvent e) {
                if (verificarCampos()){
                    guardarEmpleado(); // Llama al método para guardar los datos
                }
            }
        });

        btnLimpiar.addActionListener(new ActionListener() { // Evento al hacer clic en Limpiar
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos(); // Llama al método para limpiar los campos de texto
            }
        });

        btnMostrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imprimirTodos();
            }
        });

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
        String usuario = "root"; // Usuario de la base de datos
        String contrasena = ""; // Contraseña de la base de datos

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

    public boolean imprimirTodos(){
        String url = "jdbc:mariadb://localhost:3306/empresa"; // URL de conexión a la base de datos
        String usuario = "root"; // Usuario de la base de datos
        String contrasena = ""; // Contraseña de la base de datos
        ResultSet rs = null;

        String sql = "Select * from empleados"; // Consulta SQL de inserción

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena); // Conecta a la base de datos
             Statement st = conn.createStatement();) {
            rs = st.executeQuery(sql);

            String queryResult = rs.getString("nombre");

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, queryResult, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }


            return true; // Retorna true si la inserción fue exitosa
        } catch (SQLException | NumberFormatException ex) { // Captura errores de SQL o conversión de datos
            ex.printStackTrace(); // Imprime el error en la consola
            return false; // Retorna false si hubo un error
        }
    }

    public static void main(String[] args) { // Método principal para ejecutar la aplicación
        SwingUtilities.invokeLater(() -> { // Ejecuta la interfaz en el hilo de eventos de Swing
            new Main().setVisible(true); // Crea y muestra la ventana de la aplicación
        });
    }
}