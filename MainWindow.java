
import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
//import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.regex.*;
import java.util.Date;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.net.URL;
//import javax.swing.ExampleFileFilter;

public class MainWindow extends javax.swing.JFrame implements KeyListener, java.awt.event.ActionListener{
	public static final long serialVersionUID =1;
	private boolean wasCorrect=false;
    private javax.swing.JPanel superPanel;
    private javax.swing.JPanel[] cards;
    private String[] cardIDs;
    private AtomicInteger theGUIState;
    private JButton startButton;
    private JRadioButton yesMC;
    private JRadioButton noMC;
    private JRadioButton[] MC;
    private static final int NUM_MCS=4;
    private static final int A=0;
    private static final int S=1;
    private static final int D=2;
    private static final int F=3;
    private static final String[] MCVals= {"A: ", "S: ", "D: ","F: "};
    private JButton buttonR1;
    private JButton vYes;
    private JButton vNo;
    private JButton vCont;
	private BufferedWriter logFile;
	private Image curImage;
	private Image nextImage;
	private static final int WELCOME = 0;
	private static final int LOADING = 1;
    private static final int SHOW_IMAGE = 2;
    private static final int SHOW_ANSWER = 3;
    private static final int NUM_CARDS = 4;
    private boolean multipleChoice=false;
    public static final double CHANGERATE = 0.1;
    public JLabel picLabel;
    public JLabel picLabel1;
    private java.util.ArrayList<SpeciesCombo> allCombos;
    private SpeciesCombo curSpecies;
    private SpeciesCombo nextSpecies;
    private String curSpeciesName;
    private String nextSpeciesName;
    private int curAns;
    private JLabel vIsRight;
    private JLabel vResult;
    private String database;
    private RandomGoogleImage getNewImage;
    private UpdateImage upImage = new UpdateImage();
    private int maxHeight;
    private int maxWidth;
    private JTextField maxHeightBox;
    private JTextField maxWidthBox;
    // curSkillLevel=(numChars*CONSTANT)/(MIN_INTERVAL*time)
    
    //private static final String LIB_PATH = "C:\\SpeciesSelector\\DB\\";
    //private static final String LIB_PATH = "C:\\SpeciesSelector\\all_species.txt";
	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
    private class SpeciesCombo{
    	private String spName;
    	private java.util.ArrayList<String> accessories;
    	
    	private SpeciesCombo(String line){
    		String[] stuff = line.split("\t");
    		accessories = new java.util.ArrayList<String>();
    		spName=stuff[0];
    		for(int i =1; i<stuff.length; i++){
    			accessories.add(stuff[i]);
    		}
    	}
    	private String getRandCombo(){
    		int accIndex = 0;
    		if (accessories.size()>0){
    			accIndex = (int)(Math.random()*accessories.size());
    			return spName+" "+accessories.get(accIndex);
    		}
        	return spName;
    	}
    	private String getKey(){
    		return spName;
    	}
    }
    
    
    private class SpeciesListLoader extends java.lang.Thread{
    	private String fileToLoad;
    	private SpeciesListLoader(String toLoad){
    		allCombos= new java.util.ArrayList<SpeciesCombo>();
    		fileToLoad = toLoad;
    	}
    	public void run() {
    		try{
    			Scanner scanner = new Scanner(new FileInputStream(fileToLoad));
    		    try {
    		      while (scanner.hasNextLine()){
    		        allCombos.add( new SpeciesCombo(scanner.nextLine()));
    		        
    		      }
    		    }
    		    finally{
    		      scanner.close();
    		    }
    			
    		}catch(Exception e){
    			
    		}
    		nextSpecies=getNextSpecies();
    		nextSpeciesName=nextSpecies.getRandCombo();
    		getNewImage = new RandomGoogleImage(nextSpeciesName);
    		getNewImage.start();
    		
    	}
    	
    }
    
    private class UpdateImage extends java.lang.Thread{
    	private UpdateImage(){
    		
    	}
    	public void run(){
    		int j=0;
    		try{
    			int i=0;
    			while (nextImage==null){
    				i+=1;
    				Thread.sleep(10);	//getting stuck here.
    				if (i%500==0){
    					getNewImage.stop();
    					getNewImage = new RandomGoogleImage(nextSpeciesName);
    					getNewImage.start();
    				}
    			}
    		}catch(Exception e){
    			j=j+1;
    		}
    		//update GUI
    		curImage=nextImage;
    		nextImage=null;
    		curSpecies=nextSpecies;
    		curSpeciesName=nextSpeciesName;
    	    picLabel.setIcon( new ImageIcon(curImage) );
    	    picLabel1.setIcon( new ImageIcon(curImage) );
    		theGUIState.set(SHOW_IMAGE);
    		if (multipleChoice){
    			loadMultipleChoice();
    		}
    		redrawGUI();
    		buttonR1.requestFocus();
    		nextSpecies=getNextSpecies();
    		nextSpeciesName=nextSpecies.getRandCombo();
    		
            getNewImage = new RandomGoogleImage(nextSpeciesName);
            getNewImage.start();
    		
    	}
    	
    }
    
    private class RandomGoogleImage extends java.lang.Thread{
    	private String searchForThis;
    	private RandomGoogleImage(String searchFor){
    		searchForThis=searchFor.replace(" ", "+");
    	}
    	public void run(){
    		try{
    			boolean foundOne=false;
    			
    			int i = 1;
    			
    			while (!foundOne){
	    			int startPoint = ((int)(Math.random()*8*5));
	    			//URL url = new URL("http://www.google.ca/search?hl=en&safe=off&q="+searchForThis+"&ie=UTF-8&tbm=isch");
	    			URL url = new URL("http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+searchForThis+"&rsz=1&safe=off&start="+Integer.toString(startPoint)+"&imgsz=large|xlarge|xxlarge");
	    			InputStream is = url.openStream();
	    			int ptr = 0;
	    			StringBuffer buffer = new StringBuffer();
	    			while ((ptr = is.read()) != -1) {
	    			    buffer.append((char)ptr);
	    			}
	    			String content = buffer.toString();
	    			int lastEnd=0;
	    			java.util.ArrayList<String> allImgURLs = new java.util.ArrayList<String>();
	    			while (content.indexOf("\"url\":\"", lastEnd)>0){
	    				int curHit=content.indexOf("\"url\":\"", lastEnd);
	    				int curEnd = content.indexOf("\"", curHit+7);
	    				allImgURLs.add(content.substring(curHit+7, curEnd));
	    				
	    				
	    				lastEnd=curEnd;
	    			}
	    			// pick a random image   - may want to top-weight
	    			int whichImage = (int)(Math.random()*allImgURLs.size());
	    			
	    			String curImgUrl = allImgURLs.get(whichImage);
	    			//read in the image
	    			
	    			String type = curImgUrl.substring(curImgUrl.length()-4, curImgUrl.length()); 
	    			
	    			if (type!=".bmp"){
	    				
		    			try {
		    			    URL urlI = new URL(curImgUrl);
		    			    nextImage = ImageIO.read(urlI);
		    			    int theHeight = nextImage.getHeight(null);
		    			    int theWidth = nextImage.getWidth(null);
		    			    if (theHeight>maxHeight){
		    			    	nextImage = nextImage.getScaledInstance(-1, maxHeight, Image.SCALE_SMOOTH);
		    			    }else if (theWidth>maxWidth){
		    			    	nextImage = nextImage.getScaledInstance(maxWidth, -1, Image.SCALE_SMOOTH);
		    			    }
		    			    //picLabel = new JLabel(new ImageIcon( curImage ));
		    			    foundOne=true;
		    			} catch (IOException e) {
		        			i = i+1;
		    			}
	    			}
    			}
    			
    			//find /imgurl([^&]*)&/
    		}catch (Exception e){
    			int i = 1;
    			i = i+1;
    		}
    		
    	}
    }
    
    /*private String getRandomFileName(){
    	return LIB_PATH+"Long.pdf";
    }*/
    private SpeciesCombo getNextSpecies(){
    	//TODO: Implement weights
    	int comboIndex = 0;
    	comboIndex = (int)(Math.random()*allCombos.size());
    	return allCombos.get(comboIndex);
    }
    private void loadMultipleChoice(){
    	String [] otherOptions = new String[NUM_MCS];
    	otherOptions[0]=curSpecies.getKey();
    	for(int i = 1; i<MC.length; i++){
    		boolean anySame = true;
    		while(anySame){
    			otherOptions[i]=getNextSpecies().getKey();
    			anySame=false;
    			for(int j = 0; j<i;j++){
    				if (otherOptions[i].equals(otherOptions[j])){
    					anySame=true;
    				}
    			}
    		}
    	}
    	//shuffle order
    	int [] theOrder = new int[NUM_MCS];
    	theOrder[0]=(int)(Math.random()*NUM_MCS);
    	for(int i = 1; i<MC.length; i++){
    		boolean anySame =true;
    		while (anySame){
    			anySame=false;
    			theOrder[i]=(int)(Math.random()*NUM_MCS);
    			for(int j = 0; j<i;j++){
	    			if (theOrder[i]==theOrder[j]){
						anySame=true;
					}
    			}
    		}
    	}
    	curAns=theOrder[0];
    	for(int i = 0; i<MC.length; i++){
    		MC[theOrder[i]].setText(MCVals[theOrder[i]]+otherOptions[i]);
    	}
    }
   
    
    public void keyTyped(KeyEvent e) {
    	
        //if (e.getID() == KeyEvent.KEY_TYPED) {
            int c = e.getKeyChar();
            //Enter=10
            //y=121
            //Y=89
            //n=110
            //N=78
            //Space=32
            
            //vYes.setText((new Integer(c)).toString());
            switch (theGUIState.get()){
	        case WELCOME:
	        	switch (c){
	        	case 121://y
	        	case 89://Y
	        		yesMC.setSelected(true);
	        		break;	        		
	        	case 110://n
	        	case 78://N
	        		noMC.setSelected(true);
	        		break;
	        	case 32:
	        	case 10:
	        		startButton.doClick();
	        		break;
	        	}
	        	break;
	        case SHOW_IMAGE:
	        	if (multipleChoice){
	        		switch (c){
		        	case 65://A
		        	case 97://a
		        		MC[A].setSelected(true);
		        		break;
		        	case 83://S
		        	case 115://s
		        		MC[S].setSelected(true);
		        		break;
		        	case 68://D
		        	case 100://d
		        		MC[D].setSelected(true);
		        		break;
		        	case 70://F
		        	case 102://f
		        		MC[F].setSelected(true);
		        		break;
		        	case 32://space?
		        	case 10://enter?
		        		buttonR1.doClick();
		        		break;
		        	}
	        		
	        	}else{
		        	switch (c){
		        	case 121://y
		        	case 89://Y
		        	case 110://n
		        	case 78://N
		        	case 32://space?
		        	case 10://enter?
		        		buttonR1.doClick();
		        		break;
		        	}
	        	}
	        	break;
	        case SHOW_ANSWER:
	        	if (multipleChoice){
	        		switch (c){
		        	case 67://C
		        	case 99://c
		        	case 32:
		        	case 10:
		        		if (e.getSource()==vCont){
		        			vCont.doClick();
			        	}
		        		break;
		        	}
	        	}else{
		        	switch (c){
		        	case 121:
		        	case 89:
		        		//yes
		        		vYes.doClick();
		        		break;
		        	case 110:
		        	case 78:
		        		//no
		        		vNo.doClick();
		        		break;
		        	case 32:
		        	case 10:
		        		if (e.getSource()==vNo){
		        			vNo.doClick();
			        	}else if (e.getSource()==vYes){
			        		vYes.doClick();
			            }
		        		break;
		        	}
	        	}
	        	break;
	        }
        //}
    }
    public void keyPressed(KeyEvent e) {
    	//int c = e.getKeyCode();
        //vYes.setText((new Integer(c)).toString());

       // redrawGUI();
    }

        /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {
    	//int c = e.getKeyCode();
        //vYes.setText((new Integer(c)).toString());

        //redrawGUI();
    }

    
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        switch (theGUIState.get()){
        case WELCOME:
        	
        	
        	try{
        		while (allCombos.size()==0){
        			Thread.sleep(2);
        		}
        		//curLoader.join();
        	}catch(InterruptedException e){
        		
        	}
        	if (evt.getSource()==startButton){
        		multipleChoice=yesMC.isSelected();
                if(multipleChoice){
                	buttonR1.setText("Submit");
                	vYes.setVisible(false);
                	vNo.setVisible(false);
                }else{
                	yesMC.setVisible(false);
            		noMC.setVisible(false);
            		for(int i = 0; i<MC.length; i++){
            			MC[i].setVisible(false);
            		}
            		vCont.setVisible(false);
                }
                maxHeight=Integer.parseInt(maxHeightBox.getText());
                maxWidth=Integer.parseInt(maxWidthBox.getText());
                
            	theGUIState.set(LOADING);
                redrawGUI();//UpdateImage upImage = new UpdateImage();
                upImage.start();
                buttonR1.requestFocus();
        	}
            
            break;
        case SHOW_IMAGE:
        	theGUIState.set(SHOW_ANSWER);
        	vResult.setText("This is a "+curSpeciesName);
        	if (multipleChoice){
        		try{
	        		if (MC[curAns].isSelected()){
	        			vIsRight.setText("You got it right!");
	        			logFile.write((new Date()).getTime()+"\t"+curSpeciesName+"\t1\n");
	        		}else{
	        			logFile.write((new Date()).getTime()+"\t"+curSpeciesName+"\t0\n");
	        			vIsRight.setText("You didn't get it right...");
	        		}
	        		logFile.flush();
        		}catch(Exception e){
            		
            	}      
        	}
        	
            redrawGUI();
            if (multipleChoice){
            	vCont.requestFocus();
            }else{
            	vYes.requestFocus();
            }
            break;
            
        case SHOW_ANSWER:
        	
        	
        		
        	if (!multipleChoice){
        		if (evt.getSource()==vYes){
            		wasCorrect=true;
            	}else{
            		wasCorrect=false;
            	}
        		try{
        				logFile.write((new Date()).getTime()+"\t"+curSpeciesName+"\t");
		        	if (wasCorrect){
		        		//correct
		        		logFile.write("1\n");
		        	}else{
		        		//incorrect
		        		logFile.write("0\n");
		        	}
		        	logFile.flush();
        		}catch(Exception e){
            		
            	}
        	}
            theGUIState.set(LOADING);
            redrawGUI();
            upImage = new UpdateImage();
            upImage.start();
        }
    	
    }
    private void redrawGUI(){
    	java.awt.CardLayout cl = (java.awt.CardLayout)(superPanel.getLayout());
        int nextState = theGUIState.get();
        cl.show(superPanel, cardIDs[nextState]);
    	//pack();
    	//setVisible(false);
    	
    	this.repaint();
    }
    private void initializeCards(){
    	cards = new javax.swing.JPanel[NUM_CARDS];
    	cardIDs = new String[NUM_CARDS];
    	for(int i =0; i<cards.length; i++){
    		cards[i]=new javax.swing.JPanel();
    		javax.swing.BoxLayout layout = new javax.swing.BoxLayout(cards[i], BoxLayout.Y_AXIS);
    		//java.awt.FlowLayout layout = new java.awt.FlowLayout();
    		//cards[i], java.awt.FlowLayout.CENTER);
    		cards[i].setLayout(layout);
    		cardIDs[i]=(new Integer(i)).toString();
    	}
    	//cards[WELCOME]
    	//~~~~~~~~~~~~~~
		JLabel welcomeLabel = new javax.swing.JLabel();
		welcomeLabel.setText("Welcome!  Press start to begin!");
		welcomeLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		startButton = new javax.swing.JButton();
		startButton.setText("Start!");
		startButton.addActionListener(this);
		startButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		
		JLabel useMC = new javax.swing.JLabel();
		useMC.setText("Do you want to have multiple choice?");
		useMC.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		
		yesMC = new javax.swing.JRadioButton("Yes");
		yesMC.setMnemonic(KeyEvent.VK_Y);
		yesMC.setActionCommand("Y");
		noMC = new javax.swing.JRadioButton("No");
		noMC.setMnemonic(KeyEvent.VK_N);
		noMC.setActionCommand("N");
		yesMC.setSelected(true);
		yesMC.addActionListener(this);
		yesMC.addKeyListener(this);
		noMC.addActionListener(this);
		noMC.addKeyListener(this);
		ButtonGroup pickMC =new ButtonGroup();
		pickMC.add(yesMC);
		pickMC.add(noMC);
		maxHeight=600;
		maxWidth=1000;
		maxHeightBox = new JTextField(4);
		maxHeightBox.setText(""+maxHeight);
		maxWidthBox = new JTextField(4);
		maxWidthBox.setText(""+maxWidth);
		maxWidthBox.setMaximumSize(maxWidthBox.getPreferredSize());
		maxHeightBox.setMaximumSize(maxHeightBox.getPreferredSize());
		Box whBox = Box.createHorizontalBox();
		Box lBox = Box.createVerticalBox();
		Box tfBox = Box.createVerticalBox();
		

		JLabel hLab = new javax.swing.JLabel();
		hLab.setText("Max image height: ");
		JLabel wLab = new javax.swing.JLabel();
		wLab.setText("Max image width: ");
		
		lBox.add(wLab);
		lBox.add(hLab);
		tfBox.add(maxWidthBox);
		
		
		tfBox.add(maxHeightBox);
		whBox.add(lBox);
		whBox.add(tfBox);
		Box depth1Box = Box.createVerticalBox(); 
		//cards[WELCOME].add(Box.createRigidArea(new java.awt.Dimension(500,100)));
		cards[WELCOME].add(depth1Box);
		depth1Box.add(welcomeLabel, java.awt.Component.CENTER_ALIGNMENT);
		depth1Box.add(Box.createRigidArea(new java.awt.Dimension(20,20)), java.awt.Component.CENTER_ALIGNMENT);
		depth1Box.add(useMC, java.awt.Component.CENTER_ALIGNMENT);
		depth1Box.add(yesMC, java.awt.Component.CENTER_ALIGNMENT);
		depth1Box.add(noMC, java.awt.Component.CENTER_ALIGNMENT);
		depth1Box.add(whBox);
		depth1Box.add(Box.createRigidArea(new java.awt.Dimension(20,20)), java.awt.Component.CENTER_ALIGNMENT);
		depth1Box.add(startButton,java.awt.BorderLayout.CENTER);
		depth1Box.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		
		
		
		//cards[LOADING]
		//~~~~~~~~~~~~
		Box lbox = Box.createVerticalBox(); 
		JLabel loads = new javax.swing.JLabel();
		loads.setText("loading...");
		loads.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		lbox.add(loads);
		//cards[LOADING].add(Box.createRigidArea(new java.awt.Dimension(500,100)));
		cards[LOADING].add(lbox);
		
		
    	//cards[SHOW_IMAGE]
		//~~~~~~~~~~~~
		Box imageBox = Box.createVerticalBox(); 
		imageBox.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		JLabel whatisLabel = new javax.swing.JLabel();
		whatisLabel.setText("What is this?");
		whatisLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		picLabel1 = new JLabel(new ImageIcon());
		picLabel1.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		MC = new JRadioButton[NUM_MCS];
		MC[A] = new javax.swing.JRadioButton("A");
		MC[S] = new javax.swing.JRadioButton("S");
		MC[D] = new javax.swing.JRadioButton("D");
		MC[F] = new javax.swing.JRadioButton("F");
		MC[A].setMnemonic(KeyEvent.VK_A);
		MC[A].setActionCommand("A");
		MC[S].setMnemonic(KeyEvent.VK_S);
		MC[S].setActionCommand("S");
		MC[D].setMnemonic(KeyEvent.VK_D);
		MC[D].setActionCommand("D");
		MC[F].setMnemonic(KeyEvent.VK_F);
		MC[F].setActionCommand("F");
		ButtonGroup theMC = new ButtonGroup();
		for(int i = 0; i<MC.length; i++){
			MC[i].addKeyListener(this);
			MC[i].addActionListener(this);
			MC[i].setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			theMC.add(MC[i]);
		}
		
		
		
		buttonR1 = new javax.swing.JButton();
		buttonR1.setText("Display Answer");
		buttonR1.addActionListener(this);
		buttonR1.addKeyListener(this);
		buttonR1.setMnemonic('n');
		buttonR1.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		imageBox.add(whatisLabel);
		imageBox.add(picLabel1);
		Box vMCBox = Box.createVerticalBox();
		vMCBox.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		for(int i = 0; i<MC.length; i++){
			vMCBox.add(MC[i]);
		}
		imageBox.add(vMCBox);
		imageBox.add(buttonR1);
		//cards[SHOW_IMAGE].add(Box.createRigidArea(new java.awt.Dimension(500,100)));
		cards[SHOW_IMAGE].add(imageBox);
		
		
		
		//cards[SHOW_ANSWER]
		//~~~~~~~~~~~~~~~~~
		picLabel = new JLabel(new ImageIcon());
		picLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		vResult = new JLabel();
		vResult.setText("This is a "+curSpecies);
		vResult.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		vIsRight = new JLabel();
		vIsRight.setText("Did you guess correctly?");
		vIsRight.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		vYes = new JButton();
		vYes.setText("Yes!");
		vYes.addActionListener(this);
		vYes.setMnemonic('y');
		//vYes.setAccelerator(KeyStroke.getKeyStroke('y'));
		vNo = new JButton();
		vNo.setText("No...");
		vNo.addActionListener(this);
		vNo.setMnemonic('n');
		vYes.addKeyListener(this);
		vNo.addKeyListener(this);
		
		vCont = new JButton();
		vCont.setText("Continue...");
		vCont.addActionListener(this);
		vCont.setMnemonic('C');
		vCont.addKeyListener(this);
		
		Box vButtonBox = Box.createHorizontalBox();
		vButtonBox.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		vButtonBox.add(vYes);
		vButtonBox.add(vNo);
		vButtonBox.add(vCont);
		Box vBox = Box.createVerticalBox();
		vBox.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		//vBox.add(Box.createRigidArea(new java.awt.Dimension(100,350)));
		vBox.add(vResult);
		vBox.add(picLabel);
		vBox.add(vIsRight);
		vBox.add(vButtonBox);
		//vBox.add(Box.createRigidArea(new java.awt.Dimension(100,500)));
		//cards[SHOW_ANSWER].add(Box.createRigidArea(new java.awt.Dimension(500,100)));
		cards[SHOW_ANSWER].add(vBox);
		//cards[SHOW_ANSWER].add(Box.createRigidArea(new java.awt.Dimension(500,100)));
			
		
		superPanel.addKeyListener(this);
    	for(int i =0; i <cards.length; i++){
    		superPanel.add(cards[i], cardIDs[i]);
    	}
    }
	private void createAndShowGUI() {
		//Create and set up the window.
    	//allCombos = new java.util.ArrayList<SpeciesCombo>();
    	
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Species Selector");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        theGUIState = new AtomicInteger();
        setVisible(true);
        superPanel = new JPanel(new java.awt.CardLayout());
        this.add(superPanel);
        this.initializeCards();

        //load PDF file
        //String fileName = getRandomFileName();
        theGUIState.set(WELCOME);
		redrawGUI();
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select Database...");
		//In response to a button click
		int returnVal = fc.showOpenDialog(superPanel);
		if (returnVal == JFileChooser.APPROVE_OPTION){
			database = fc.getSelectedFile().getAbsolutePath();
		}else{
			System.exit(0);
		}
		SpeciesListLoader loadSpec = new SpeciesListLoader(database);
    	loadSpec.start();
		//startButton.requestFocus();
		
		try{
			logFile = new BufferedWriter(new FileWriter("logfile.txt", true));
		}catch(Exception e){
			
		}
		
	}
	
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow myWindow = new MainWindow();
				myWindow.createAndShowGUI();
				 
		            
			}
		});
	}
}
