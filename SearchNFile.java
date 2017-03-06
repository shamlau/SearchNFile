import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.swing.*;


/**
 * SearchNFile.java
 * 
 * Description: This program is a search tool for files on a user's computer. The program asks the user for a directory to be searched
 * and a file type. It then returns any instance of the file type in the directory and all subdirectories and stores the data in a HashMap.
 * Lastly, it stores a text file in the previously mentioned directory. If another search is performed using the same guidelines, the program
 * will detect this and ask the user if they would like to use the previous search to compare to the current one. If the user indicates
 * affirmatively, the program will take the information from the old text file, store it into a hashMap and compare it to the newer HashMap.
 * The program will then notify the user of any changes (updates, deletions, additions) to the directory. It will then complete its sort
 * and create a new text file.
 * 
 * 
 * @author Samuel Lau
 * @ID 23056674 
 * @version 1.0
 * @released 08/03/2014
 *
 */

public class SearchNFile implements ActionListener{
	//The Following Frames, Panels, label, and buttons and the int answer are for method Sortogram() more will be explained 
	//when the file reaches those methods
	JFrame guiFrame=new JFrame("Get Data");
	JFrame answerFrame=null;
	JPanel choices=new JPanel(new GridLayout(3, 2, 10, 10));
	JPanel optionPanel = new JPanel();
	JLabel question=new JLabel("Would you like to find info on a file?");
	JButton yes = new JButton("Yes");
	JButton no = new JButton("No");
	JButton one= createSquareJButton(50);
	JButton two= createSquareJButton(50);
	JButton three=createSquareJButton(50);
	JButton four= createSquareJButton(50);
	JButton five= createSquareJButton(50);
	int answer=0;
	//this counts the number of files found and uses to display the file # later
	public static int count=1;

	//this method searches a directory(directoryName) for a specific fileType. It then puts all of these files with matching file types into a hashMap
	public static void searchAndAdd(String directoryName, String fileType,HashMap map){
		//File directory is a File that exists at location directoryName
		File directory = new File(directoryName);
		//This is an array that will take all of File directory's list
		File[] fList=directory.listFiles();
		//this loops through fList going through each of the file
		for (File file:fList){
			//this if statement checks if something is a file and if it ends with a data type such as .jpg or .pdf
			if (file.isFile() && file.getName().endsWith(fileType)){
				//the following stores the necessary variables
				Date dateLastModified = new Date(file.lastModified());
				Date dateToday= new Date();
				String filePath=file.getAbsolutePath();
				String nameOfFile=file.getName();
				String lastModified=dateLastModified.toString();
				String timeOfSearch=dateToday.toString();
				//the following prints out all the information to the console along with labels to make it easier to see
				System.out.println("File #: "+ count);
				count++;//a file has been found so count is updated

				System.out.println("File path: "+ file.getAbsolutePath());
				System.out.println("File Name: " + file.getName());
				System.out.println("Date Last Modified: " +dateLastModified);
				System.out.println("Date Last Searched: "+ dateToday);
				System.out.print("\n");       	
				//lastly searchAndAdd puts all information collected into a hashmap
				//I found hashmaps more convenient and easier to use than hash tables
				addToHash(map, filePath, nameOfFile ,fileType,lastModified,timeOfSearch);	
				//even though a folder has been search the following else/if ensures that all subdirectories are also checked
			}else if (file.isDirectory()){
				searchAndAdd(file.getAbsolutePath(),fileType,map);
			}
		}//for
	}//method

	//checkPrevious checks to see if a previous sort has been done before because all of its variables are the same as searchAndAdd 
	//and will be used for many methods the variables are only incremented (too difficult to come up with many names for the same thing
	public static boolean checkPrevious(String directoryName2, String fileType2){
		File directory2 = new File(directoryName2);
		File[] fList2=directory2.listFiles();
		for (File file:fList2){
			//returns true if the file is the data type.txt (i.e. pdf.txt). The files are saved without user input so they all follow this format
			if (file.getName().equals(fileType2+".txt")){
				return true;
			}
		}//for
		return false;
	}//method

	//bringUpOldFiles asks the user if they would like to view compare the previous results
	//it returns true if the user clicks yes allowing them read the old file
	public static boolean bringUpOldFiles(String directoryName, String fileType){
		JFrame frame = null;
		int n = JOptionPane.showConfirmDialog(frame,"Would you like to compare the previous result?",
				"You have already done this search", JOptionPane.YES_NO_OPTION);
		if(n==JOptionPane.YES_OPTION){
			return true;
		}
		return false;
	}//method 

	//addToHash takes an array list contain information in a specific order and adds it to a HashMap. The filepath is the key of the Map
	public static void addToHash(HashMap hashMap,String path, String fileName, String fileType, String DateModified,String currentTime){
		ArrayList<String> arraylist = new ArrayList<String>();
		arraylist.add(fileName);
		arraylist.add(fileType);
		arraylist.add(DateModified);
		arraylist.add(currentTime);
		hashMap.put(path, arraylist);
	}//method AddToHash

	//saveHashToFile saves a HashTable to a file
	@SuppressWarnings("rawtypes")
	public static void saveHashToFile(HashMap hashMap, PrintWriter printwriter) throws FileNotFoundException{
		Map<String, ArrayList<String>> sortedMap = new TreeMap<String, ArrayList<String>>(hashMap);
		Iterator iterator = sortedMap.keySet().iterator();
		//while the iterator is going through all the keys the printwriter writes the a file's path, name, type, time it was last editted
		//and time it was last searched to a text file
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			ArrayList<String> a= sortedMap.get(key);
			printwriter.println("File Path: " +key);
			printwriter.println("File Name: "+ a.get(0));
			printwriter.println("File Type: " +a.get(1));
			printwriter.println("Last Edited: " +a.get(2));
			printwriter.println("Last Searched: " +a.get(3));
		}//while
	}//method saveHashToFile

	//compareDates takes the files of an older hashmap and compares it to those of a newer one. using the date last editted of the files
	public static void compareDates(HashMap olderMap, HashMap newerMap){
		Set<String> olderkeys= olderMap.keySet();
		Set<String> newerkeys=newerMap.keySet();
		ArrayList<String> changes=new ArrayList<String>();
		ArrayList<String> missingFiles=new ArrayList<String>();
		ArrayList<String> newFiles=new ArrayList<String>();
		//this will go through all the files of the older hashmap(which was gotten through a text file)
		for(String key:olderkeys){
			//this ensures only two keys that match will be compared
			if(newerMap.containsKey(key)){
				ArrayList<String> a=(ArrayList<String>) olderMap.get(key);
				ArrayList<String> b=(ArrayList<String>) newerMap.get(key);
				//if the 2 dates of a file are not equal, something has been changed. The key is added to an array list of all the files 
				//that have had changes
				if(a.get(2).equals(b.get(2))==false){
					changes.add(key);
				}
			}
			//if a the older hashmap has a key that the newermap does not, something has been moved and is now missing. The key is 
			//added to an array list of all the files that are missing
			if(newerMap.containsKey(key)==false){
				missingFiles.add(key);
			}
			//this will go through all the files of the newer hashmap
			for(String key2: newerkeys){
				//if the newer map contains a file that the older one does not, something has been added. The key is added to an array list
				//of files that are new (since the last search)
				if(olderMap.containsKey(key)==false){
					newFiles.add(key);
				}
			}
		}
		//if there have been changes the arraylist will be printed out
		if(changes.isEmpty()==false){
			System.out.println("The following files have changed: ");
			for(String change: changes){
				System.out.println(change);
			}
		}
		//if there are files missing, the arraylist containing them will be printed out
		if(missingFiles.isEmpty()==false){
			System.out.println("\n The following files are no longer in the directory: ");
			for(String missing: missingFiles){
				System.out.println(missing);
			}
		}
		//if there are new files, the array list containing them will be printed out
		if(newFiles.isEmpty()==false){
			System.out.println("\n The following files are new since the last search");
			for(String notHereBefore: newFiles){
				System.out.println(notHereBefore);
			}
		}//if newFiles is empty
	}//method compareDates

	/*This is a disastrous method. The compiler forced me to change it to a constructor name. 
	 * For the most part it works. It's only at the end does it really start to come apart.
	 * The original intention of this was to create a gui frame that asks the user what key in a hashMap they would like
	 * to learn more about. The user can then click one of the 5 buttons to learn about the file path, file name, file type, 
	 * date last modifed, and date last searched, the information would be displayed at using a JOptionPane
	 * This specific constructor shapes up the guiFrame and optionPanel and gives text to the five buttons.
	 */
	public SearchNFile(){
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Select what Data you want");
		guiFrame.setSize(400,450);
		guiFrame.setLocationRelativeTo(null);
		guiFrame.add(choices);
		optionPanel.add(question);
		guiFrame.add(optionPanel, BorderLayout.NORTH);
		one.addActionListener(this);	
		one.setText("File Path");
		two.addActionListener(this);
		two.setText("File Name");
		three.addActionListener(this);
		three.setText("File Type");
		four.addActionListener(this);
		four.setText("Last Updated");
		five.addActionListener(this);
		five.setText("Last Searched for");
		choices.add(one);
		choices.add(two);
		choices.add(three);
		choices.add(four);
		choices.add(five);
		choices.setVisible(false);
		yes.addActionListener(this);
		yes.setPreferredSize(new Dimension(200, 50));
		optionPanel.add(yes);
		guiFrame.add(yes,BorderLayout.WEST);
		no.addActionListener(this);
		no.setPreferredSize(new Dimension(200, 50));
		optionPanel.add(no);
		guiFrame.add(no,BorderLayout.EAST);
		guiFrame.setVisible(true);
		optionPanel.setVisible(true);
	}//method constructor Sortogram

	//createSquareJButton creates a JButton that is size by size
	private JButton createSquareJButton(int size)
	{
		JButton tempPanel = new JButton();
		tempPanel.setMinimumSize(new Dimension(size, size));
		tempPanel.setMaximumSize(new Dimension(size, size));
		tempPanel.setSize(new Dimension(size, size));
		return tempPanel;
	}//method createSquareJButton

	//this is the Actionperformed of the buttons
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		if(event.getSource().equals(no)){
			System.exit(0);
			//if the user does not wish to use this he can click no.
		}else{
			question.setText("pick something to learn");//the user is prompted to pick a choice
			yes.setVisible(false);
			no.setVisible(false);//both buttons become invisible
			choices.setVisible(true);//and the selection board becomes visible
		}if(event.getSource().equals(one)){
			//if the event chosen is one the user has selected file path
			answer=1;//originally the answer variable would allow the program to select information based on 
			//what the value of answer is at the end of this method but because answer needs to be static to be called by int main
			//I could not get its value to change
			one.setVisible(false);
			two.setVisible(false);
			three.setVisible(false);
			four.setVisible(false);
			five.setVisible(false);
		}if(event.getSource().equals(two)){
			//if the event chosen is two the user has selected file name
			answer=2;
			one.setVisible(false);
			two.setVisible(false);
			three.setVisible(false);
			four.setVisible(false);
			five.setVisible(false);
		}if(event.getSource().equals(three)){
			//if the event chosen is three the user has selected file type
			answer=3;
			one.setVisible(false);
			two.setVisible(false);
			three.setVisible(false);
			four.setVisible(false);
			five.setVisible(false);
		}if(event.getSource().equals(four)){
			//if the event chosen is four the user has selected time last updated
			answer=4;
			one.setVisible(false);
			two.setVisible(false);
			three.setVisible(false);
			four.setVisible(false);
			five.setVisible(false);
		}if(event.getSource().equals(five)){
			//if the event chosen is file the user has selected time last searched
			answer=5;
			one.setVisible(false);
			two.setVisible(false);
			three.setVisible(false);
			four.setVisible(false);
			five.setVisible(false);
		}			
	}//method ActionPerformed

	@SuppressWarnings("rawtypes")
	//This method was supposed to give a response to an answer from the JButton based on what the user pressed
	public void AnswerFromJButton(HashMap hMap){
		//if answer had been changed to 1-5, then AnswerFromJButton would respond
		if (answer!=0){
			String keyName=JOptionPane.showInputDialog("What is the path of the file?");
			Map<String, ArrayList<String>> sortedMap = new TreeMap<String, ArrayList<String>>(hMap);
			Iterator iterator = sortedMap.keySet().iterator();
			//it would look through the hashMap containing the key and it would find the requested information
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				ArrayList<String> a= sortedMap.get(key);
				if(keyName.equals(key)){
					if(answer==1){
						JOptionPane.showMessageDialog(answerFrame, "The path is: "+ key);
					}
					if(answer==2){
						JOptionPane.showMessageDialog(answerFrame, "The file name is: "+ a.get(0));
					}
					if(answer==3){
						JOptionPane.showMessageDialog(answerFrame, "The file type is: "+ a.get(1));
					}
					if(answer==4){
						JOptionPane.showMessageDialog(answerFrame, "This file was last updated on: "+ a.get(2));
					}
					if(answer==5){
						JOptionPane.showMessageDialog(answerFrame, "The file was last searched for on: "+ a.get(3));
					}
				}//if keyName.equals
			}//while
		}
	}//method AnswerFromJButton



	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException {
		//asks the user which directory and which file type
		String inputDirectory=JOptionPane.showInputDialog("Folder you would like to search?");
		String inputFileType=JOptionPane.showInputDialog("Filetype?");
		HashMap oldMap = new HashMap();
		//creates an hashMap and checks if a search has been performed before
		if (checkPrevious(inputDirectory,inputFileType)==true){
			if(bringUpOldFiles(inputDirectory,inputFileType)==true){
				try{
					//opens up previous text file
					String inputFileName=inputDirectory+"\\"+inputFileType+".txt";
					BufferedReader inFile2=new BufferedReader(new FileReader (new File(inputFileName)));
					String line;
					ArrayList<String> tempArrayList=new ArrayList<String>();
					//fills up the array list with every single line
					while((line = inFile2.readLine()) != null){
						tempArrayList.add(line);
					}
					//while there is still information on the arraylist it records it onto the hash table 
					//after deleting the labels (File Path:, File Name, etc.)
					while(tempArrayList.isEmpty()==false){
						String oldPath=tempArrayList.get(0);
						oldPath=oldPath.replaceAll("File Path: ","");
						String  oldName=tempArrayList.get(1);
						oldName=oldName.replaceAll("File Name: ","");
						String  oldType=tempArrayList.get(2);
						oldType=oldType.replaceAll("File Type: ","");
						String  oldEdit=tempArrayList.get(3);
						oldEdit=oldEdit.replaceAll("Last Edited: ","");
						String  oldSearch=tempArrayList.get(4);
						oldSearch=oldSearch.replaceAll("Last Searched: ","");
						addToHash(oldMap, oldPath, oldName, oldType,oldEdit, oldSearch);
						//then it removes the info from a certain file from the array list
						tempArrayList.remove(4);
						tempArrayList.remove(3);
						tempArrayList.remove(2);
						tempArrayList.remove(1);
						tempArrayList.remove(0);
					}
				}finally{
					//prints out that the old text file has been found
					System.out.println("Old File located");
					System.out.println();
				}
			}
		}
		//this creates the main hash map
		final HashMap<String, ArrayList<String>> mainMap = new HashMap<String, ArrayList<String>>();
		//and searches for all the files in the directory and adds to it
		searchAndAdd(inputDirectory,inputFileType,mainMap);
		try {
			//then it creates a new outputFile with a specifically formatted name and saves the Hashfile to the text file
			compareDates(oldMap, mainMap);
			String name=inputDirectory+ "\\" +  inputFileType+ ".txt";
			System.out.println("Your new file will be:"+name);
			PrintWriter pr = new PrintWriter(name);
			saveHashToFile(mainMap, pr);
			pr.close();
		}finally{
			//finally let's the user know it is finished
			System.out.println("Task Completed");
		}
		//this calls on the method that uses the JButton Gui but because there is no way i could get AnswerFromJButton to work, I shut it off 
		/*
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new Sortogram();
				//AnswerFromJButton(hMap);
				 //all though AnswerFromJButton is called answer is perpetually zero so it is never activated  
			}
		});*/
	}//int main
}//class Sortogram
