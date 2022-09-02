package com.sdfeu.yane.sqllitetest;
import java.awt.*;
import java.awt.event.*;



public class GUI extends Frame implements FilmDisplay
{
	Panel results;
	Panel buttons;
	Button quit;
	Button search;
	Button add;

	TextField searchField;
	TextField addText;
	TextField addYear;
	Window searchWindow;
	Window addWindow;
	Button ok;
	SQLiteTest test;

	public GUI(SQLiteTest test){
		super("Movies");
		this.test=test;
		setupGUI();
	}

	public void display(String str){
		results.add(new Label(str));
	}

	void setupGUI(){
		results=new Panel(new GridLayout(0,1));
		buttons=new Panel(new FlowLayout());
		this.setLayout(new BorderLayout());

		quit =new Button("Quit");
		search=new Button("Search");
		add=new Button("Add");

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
			addWindow=new Window(this);
			addWindow.setLayout(new GridLayout(0,2));
			addText = new TextField(30);
			addYear = new TextField(4);
			addWindow.add(new Label("Title:"));
			addWindow.add(addText);
			addWindow.add(new Label("Year:"));
			addWindow.add(addYear);
			Button addBtn=new Button("OK");
			addBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					doAdd();
				}
			});
			addWindow.add(new Label());
			addWindow.add(addBtn);
			addWindow.pack();

		}
	}



	public void setupSearch(){
		if (searchWindow==null){
			searchWindow=new Window(this);
			searchWindow.setLayout(new FlowLayout());
			searchWindow.add(new Label("Search:"));
			ok=new Button("OK");
			ok.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					runSearch(false);
				}
			});
			searchField=new TextField(30);
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


