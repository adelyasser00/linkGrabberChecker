import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Gui {
	static Font title = new Font(Font.SANS_SERIF,Font.BOLD,30);
	static Font titleButton = new Font(Font.SANS_SERIF,Font.PLAIN,18);
	static Font subtitle = new Font(Font.SANS_SERIF, Font.BOLD, 22);
	static Font labelButton = new Font(Font.SANS_SERIF,Font.PLAIN,14);
	private static int verifyDepth(String input) {
		if (input.length() > 0) {
			if (input.matches("[0-9]+")) {
				return 0; // successful
			}
			else {
				return 2; // used non digits
			}
		}
		else {
			return 1; // empty input
		}
	}
	private static void viewResults(List<Link> links) {
		JFrame frame = new JFrame("Results for " + Core.inputUrl);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(1000,600);
		
		JPanel panelTable = new JPanel();
		panelTable.setSize(1000, 600);
		panelTable.setLayout(null);
		
		String[] columnNames = {"valid", "text", "link", "domain"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		int num = links.size(), numValid=0;

		for(int i = 0; i < num; i++) {
			if (links.get(i).valid) {
				numValid++;
			}
			model.addRow(new Object[] {links.get(i).valid,
					links.get(i).text, links.get(i).url, 
					links.get(i).domain});
		}
		
		JLabel label1 = new JLabel("Number of valid links: " + numValid);
		label1.setBounds(40, 10, 500, 40);
		label1.setFont(subtitle);
		panelTable.add(label1);
		
		JLabel label2 = new JLabel("Number of invalid links: " + (num - numValid));
		label2.setBounds(40, 50, 500, 40);
		label2.setFont(subtitle);
		panelTable.add(label2);
		
		JLabel label3 = new JLabel("Number of optimal threads: " + Core.best1);
		label3.setBounds(500, 10, 500, 40);
		label3.setFont(subtitle);
		panelTable.add(label3);
		
		JLabel label4 = new JLabel("Minimum execution time: " + String.format("%.03f",(Core.min1/1000.000)) + "s");
		label4.setBounds(500, 50, 500, 40);
		label4.setFont(subtitle);
		panelTable.add(label4);
		
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 100, 985, 460);
		table.setFillsViewportHeight(true);
		
		/**
		 * Resize table columns appropriately.
		 */
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMinWidth(50);
		table.getColumnModel().getColumn(1).setMaxWidth(200);
		table.getColumnModel().getColumn(2).setMinWidth(300);
		table.getColumnModel().getColumn(2).setMaxWidth(600);
		table.getColumnModel().getColumn(3).setMinWidth(50);
		table.getColumnModel().getColumn(3).setMaxWidth(250);
		
		panelTable.add(scrollPane);
		frame.add(panelTable);
		frame.setVisible(true);
	}
	public static void createGui() {
		
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex){
			ex.printStackTrace();
		}
		
		JFrame messageBox = new JFrame();
		
		JFrame frame = new JFrame("Hyperlinks Integrity Checker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,250);
		
		JPanel panel = new JPanel();
		panel.setSize(500, 250);
		panel.setLayout(null);
		
		JLabel label1 = new JLabel("Hyperlinks Integrity Checker");
		label1.setBounds(0, 0, 500, 40);
		label1.setHorizontalAlignment(0);
		label1.setFont(subtitle);
		panel.add(label1);
		
		JLabel label2 = new JLabel("URL:");
		label2.setBounds(40, 50, 500, 25);
		label2.setFont(titleButton);
		panel.add(label2);
		
		JTextField userText1 = new JTextField();
		userText1.setBounds(107, 50, 313, 25);
		panel.add(userText1);
		
		JLabel label3 = new JLabel("Depth:");
		label3.setBounds(140, 100, 500, 25);
		label3.setFont(titleButton);
		panel.add(label3);
		
		JTextField userText2 = new JTextField();
		userText2.setBounds(220, 100, 60, 25);
		panel.add(userText2);
		
		JButton button1 = new JButton("Check");
		button1.setBounds(190,150,120,50);
		button1.setFont(titleButton);
		panel.add(button1);
		
		frame.add(panel);
		
		ActionListener buttonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				if (src == button1) {
					if(verifyDepth(userText2.getText())==0) {
						button1.setText("Loading..");
						button1.setEnabled(false);
						button1.setVisible(false);
						button1.setVisible(true);
						userText1.setEnabled(false);
						userText2.setEnabled(false);
						
						panel.paintImmediately(panel.getBounds());
						try {
							Core.initiate(userText1.getText(), Integer.parseInt(userText2.getText()));
						} catch (Exception e1) {
						}
						
						viewResults(Core.links);
						
						button1.setText("Check");
						button1.setEnabled(true);
						userText1.setEnabled(true);
						userText2.setEnabled(true);
						panel.paintImmediately(panel.getBounds());
					}
					else {
						JOptionPane.showMessageDialog(messageBox, "Please check entered depth. You may only enter a non negative integer", "Alert!", JOptionPane.ERROR_MESSAGE);
					}
				}				
			}
		};
		
		button1.addActionListener(buttonListener);
		frame.setVisible(true);
	}
	
}
