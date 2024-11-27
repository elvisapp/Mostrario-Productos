import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.*;
import javax.imageio.ImageIO;

public class MostrarDatosGUI extends JFrame {

    public MostrarDatosGUI() {
        setTitle("Mostrario de Estoque - Agricolas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"ID", "Nombre", "Descripcion", "Precio", "Foto"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) {
                    return ImageIcon.class;
                }
                return Object.class;
            }
        };

        String jdbcUrl = "jdbc:mysql://localhost:3306/productos";
        String username = "elvis";
        String password = "Teamodios82$";

        try {
            // Cargar explícitamente el driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Conexión exitosa a la base de datos");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM agricolas");

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nombre = resultSet.getString("Nombre");
                String descripcion = resultSet.getString("Descripcion");
                double precio = resultSet.getDouble("Precio");
                byte[] fotoBytes = resultSet.getBytes("Foto");

                // Convertir los bytes de la foto a una imagen y ajustar al tamaño de la fila
                ImageIcon foto = null;
                if (fotoBytes != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(fotoBytes);
                    BufferedImage bufferedImage = ImageIO.read(bais);
                    Image img = bufferedImage.getScaledInstance(-1, table.getRowHeight(), Image.SCALE_SMOOTH);
                    foto = new ImageIcon(img);
                }

                model.addRow(new Object[]{id, nombre, descripcion, precio, foto});
                System.out.println("Datos extraídos: " + id + ", " + nombre + ", " + descripcion + ", " + precio);
            }

            connection.close(); // Cerrar la conexión después de usarla
            System.out.println("Conexión cerrada");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Establecer un renderizador personalizado para la columna de fotos
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel();
                    label.setIcon((ImageIcon) value);
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MostrarDatosGUI frame = new MostrarDatosGUI();
            frame.setVisible(true);
        });
    }
}
