import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import java.awt.SystemColor;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTable;
import java.awt.Font;
import java.io.IOException;
import javax.swing.JButton;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.UIManager;



public class InterfazGrafica extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabla_errores;
	DefaultTableModel resultado;
	DefaultTableModel errores;
	@SuppressWarnings("unused")
	private JFileChooser fc;
	public AnalizarArchivo ensamblador;
	private JTable table_1;
	boolean bandera_analisis;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazGrafica frame = new InterfazGrafica();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InterfazGrafica() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\OMAR ALVIZO\\Pictures\\Motorola.png"));
		
		ensamblador = new AnalizarArchivo ();
		
		setFont(new Font("Arial", Font.ITALIC, 12));
		setResizable(false);
		setBackground(SystemColor.windowText);
		setTitle("CC207 TALLER PROGRAMACION DE SISTEMAS Practica 1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 927, 529);
		
		
		final JFileChooser fc = new JFileChooser();
		
		contentPane = new JPanel();
		contentPane.setForeground(Color.LIGHT_GRAY);
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(SystemColor.activeCaption);
		scrollPane.setBorder(null);
		scrollPane.setBounds(10, 30, 550, 300);
		contentPane.add(scrollPane);
		
		final JTextArea asm = new JTextArea();
		asm.setBorder(null);
		asm.setDisabledTextColor(SystemColor.activeCaption);
		asm.setEditable(false);
		scrollPane.setViewportView(asm);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setForeground(SystemColor.activeCaption);
		scrollPane_1.setBorder(null);
		scrollPane_1.setBackground(SystemColor.activeCaption);
		scrollPane_1.setBounds(570, 30, 341, 300);
		contentPane.add(scrollPane_1);
		
		inicializarTablaAnalizar();
		inicializarTablaErrores();
		scrollPane_1.setViewportView(table_1);
		
		JLabel lblArchivoAsm = new JLabel("Archivo ASM");
		lblArchivoAsm.setForeground(new Color(255, 255, 255));
		lblArchivoAsm.setBounds(10, 11, 400, 14);
		contentPane.add(lblArchivoAsm);
		
		JLabel lblNewLabel = new JLabel("Archivo INST");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBounds(570, 11, 89, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Errores");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setBounds(10, 339, 169, 14);
		contentPane.add(lblNewLabel_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 357, 901, 112);
		contentPane.add(scrollPane_2);

		scrollPane_2.setViewportView(tabla_errores);
		
		
		JButton btnAnalizar = new JButton("Analizar");
		btnAnalizar.setFocusPainted(false);
		btnAnalizar.setFocusTraversalKeysEnabled(false);
		btnAnalizar.setBorder(null);
		btnAnalizar.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		btnAnalizar.setBackground(Color.GRAY);
		btnAnalizar.setForeground(Color.WHITE);
		btnAnalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (bandera_analisis == false ){
					try {
						ensamblador.analizar(resultado,errores);
						bandera_analisis = true;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				else
					JOptionPane.showMessageDialog( null , "Archivo ya analizado","ERROR DE ANANLISIS" , JOptionPane.ERROR_MESSAGE);
		
			}
		});
		btnAnalizar.setBounds(822, 7, 89, 23);
		contentPane.add(btnAnalizar);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		menuBar.setForeground(Color.WHITE);
		menuBar.setBorder(UIManager.getBorder("MenuBar.border"));
		menuBar.setBackground(new Color(105, 105, 105));
		setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu("Archivo");
		mnArchivo.setForeground(SystemColor.controlHighlight);
		menuBar.add(mnArchivo);
		
		JMenuItem mntmAbrir = new JMenuItem("Abrir . . . ");
		mntmAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int returnVal = fc.showOpenDialog(InterfazGrafica.this);

	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                
	                try {
						ensamblador.abrirArchivo(fc.getSelectedFile());
						ensamblador.leerArchivo(asm);
						bandera_analisis = false;
						repaint(); // Para la grafica
						validate(); // Para los JComponents
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                //this is where a real application would open the file.
	               
	                
	            } else {
	            	System.out.println("Open command cancelled by user." );
	            }
			}
		});

		mnArchivo.add(mntmAbrir);
		//ManejadorEventos manejador = new ManejadorEventos();
	}
	
	
	public void inicializarTablaAnalizar(){
		resultado = new DefaultTableModel();
		resultado.addColumn("No.");
		resultado.addColumn("Etiqueta");
		resultado.addColumn("CODOP");
		resultado.addColumn("Operando");
		table_1 = new JTable(resultado);
		table_1.setBorder(null);
		table_1.setGridColor(SystemColor.activeCaption);
		table_1.setSelectionBackground(SystemColor.textHighlight);
		table_1.setForeground(Color.WHITE);
		table_1.setBackground(SystemColor.activeCaption);
		table_1.setEnabled(false);
		table_1.getColumnModel().getColumn(0).setMaxWidth(50);
		table_1.getColumnModel().getColumn(0).setPreferredWidth(50);
		table_1.getColumnModel().getColumn(1).setMaxWidth(70);
		table_1.getColumnModel().getColumn(1).setPreferredWidth(70);
		table_1.getColumnModel().getColumn(2).setMaxWidth(80);
		table_1.getColumnModel().getColumn(2).setPreferredWidth(80);

	}
	
	public void inicializarTablaErrores(){
		errores = new DefaultTableModel();
		errores.addColumn("Linea");
		errores.addColumn("Error");
		errores.addColumn("Descripción del error");
		tabla_errores = new JTable(errores);
		tabla_errores.setBorder(null);
		tabla_errores.setGridColor(SystemColor.activeCaption);
		tabla_errores.setSelectionBackground(Color.LIGHT_GRAY);
		tabla_errores.setForeground(Color.WHITE);
		tabla_errores.setBackground(SystemColor.activeCaption);
		tabla_errores.setEnabled(false);
		tabla_errores.getColumnModel().getColumn(0).setMaxWidth(50);
		tabla_errores.getColumnModel().getColumn(0).setPreferredWidth(50);
		tabla_errores.getColumnModel().getColumn(1).setMaxWidth(50);
		tabla_errores.getColumnModel().getColumn(1).setPreferredWidth(50);

	}
}
