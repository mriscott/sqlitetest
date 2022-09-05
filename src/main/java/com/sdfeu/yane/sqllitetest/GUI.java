package com.sdfeu.yane.sqllitetest; 
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;



public class GUI extends JFrame implements FilmDisplay
{
	JPanel results;
	JPanel buttons;
	JButton quit;
	JButton search;
	JButton add;

	JTextField searchField;
	JTextField addText;
	JTextField addYear;
	JWindow searchWindow;
	JWindow addWindow;
	JButton ok;
	SQLiteTest test;

	public GUI(SQLiteTest test){
		super("Movies");
		setTitle("Movies");
		this.test=test;
		setupGUI();
	}

	public void display(String str){
		results.add(new
			    JLabel(str));
	}

	void setupGUI(){
		results=new JPanel(new GridLayout(0,1));
		buttons=new JPanel(new FlowLayout());
		this.setLayout(new BorderLayout());

		quit =new JButton("Quit");
		search=new JButton("Search");
		add=new JButton("Add");

		buttons.add(search);
		buttons.add(add);
		buttons.add(quit);

		quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				shutdown();
			}
		});

		search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search();
			}
		});

		add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				add();
			}
		});


		this.setLayout(new BorderLayout());
		this.add(buttons,BorderLayout.SOUTH);
		this.add(results,BorderLayout.CENTER);
		runSearch(true);


		this.pack();
		this.show();

		
	}
	public void doAdd(){
		addWindow.hide();
		try{
			test.addMovie(addText.getText(),addYear.getText());
			runSearch(true);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setupAdd(){
		if (addWindow==null){
			addWindow=new JWindow(this);
			addWindow.setLayout(new GridLayout(0,2));
			addText = new JTextField(30);
			addYear = new JTextField(4);
			addWindow.add(new JLabel("Title:"));
			addWindow.add(addText);
			addWindow.add(new JLabel("Year:"));
			addWindow.add(addYear);
			JButton addBtn=new JButton("OK");
			addBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					doAdd();
				}
			});
			addWindow.add(new JLabel());
			addWindow.add(addBtn);
			addWindow.pack();

		}
	}



	public void setupSearch(){
		if (searchWindow==null){
			searchWindow=new JWindow(this);
			searchWindow.setLayout(new FlowLayout());
			searchWindow.add(new JLabel("Search:"));
			ok=new JButton("OK");
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					runSearch(false);
				}
			});
			searchField=new JTextField(30);
			searchWindow.add(searchField);
			searchWindow.add(ok);
			searchWindow.pack();

		}
	}

	public void add()
	{
		setupAdd();
		addWindow.show();
	}

	public void search()
	{
		setupSearch();
		searchWindow.show();
	}

	public void runSearch(boolean showAll){
		if(!showAll) searchWindow.hide();
		results.removeAll();
		try{
			String searchText=showAll?"":searchField.getText();
			test.read(searchText,this);
		}catch(java.sql.SQLException e){
			e.printStackTrace();
		}
		this.pack();
	}

	public void shutdown(){
		System.exit(0);
	}
}


