package pkgPlayer;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
   
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
  
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import displayImage.DisplayImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
       

//=============================================================================
/**
 *  This class has been created and developed by Team F (Slideshow Editor).
 *  Submitted on 12/7/2021.
 *  
 *  Class: PlayerMain, derived from Rick Coleman's version
 *  Purpose: This class implements the main window for the Player application.
 *    It allows the user to select a slideshow file specifying a sequence of 
 *    images to display sequentially. The user can switch images by clicking
 *    a button to move forward or backward in the list of images or by 
 *    using a timer to produce a slideshow.
 */
//=============================================================================
public class PlayerMain extends JFrame
{
	/** Programmer ID */
	public String m_sID = "Team F (Slideshow Editor)";
	
	/** Main screen width - based on screen width */
	public int m_iScnWidth;
	
	/** Main screen height - based on screen height */
	public int m_iScnHeight;
		
	/** Panel displaying the images */
	public ImagePanel m_ImagePanel;
	
	/** Panel holding the buttons */
	private JPanel m_ButtonPanel;
	
	/** Display Options button */
	private JButton m_DisplayOptionsBtn;
	
	/** Select image directory button */
	private JButton m_SelectImageDirBtn;
	
	/** Switch to previous image button */
	private JButton m_PrevImageBtn;
	
	/** Switch to next image button */
	private JButton m_NextImageBtn;
	
	/** Exit button */
	private JButton m_ExitBtn;
	
	//------------------------------------------
	// Display option variables
	//------------------------------------------
	/** Scale images flag */
	private boolean m_bScaleImages = true;
	
	/** Show image types flag. Default (3) is show both */
	private int m_iShowTypes = 3;
	
	/** Change images manually flag */
	private boolean m_bChangeManually = true;
	
	/** Time delay is using timer to change */
	private float m_iTimeDelay = 5;
	
	//------------------------------------------
	// Miscellaneous variables
	//------------------------------------------
	/** Image directory to show */
	private String m_sImageDir;
        
        /** Slideshow File **/
        private String m_sSlideshowFile;
	
	/** Vector of image names */
	private Vector<String> m_vImageNames = null;
        public static Vector<String> m_vSoundNames = null;
        private Vector<Integer> m_vTransitions = null;
        private Vector<Float> m_vTransitionLengths = null;
	
	/** Index of the current image */
	private int m_iCurImageIdx;
        private int m_iCurSoundIdx;
	
	/** Image currently displayed */
	private BufferedImage  m_TheImage = null;
        //private SimpleAudioPlayer m_TheSound = null;
	/** Timer for slideshows */
	private Timer m_SSTimer;
        
         Long currentFrame;
         Clip clip;
         String status;
         AudioInputStream audioInputStream;
         static String filePath;
         
	//---------------------------------------------------
	/** Default constructor */
	//---------------------------------------------------
	public PlayerMain() throws IOException
	{
		//------------------------------------------
		// Set all parameters for this JFrame object
		//------------------------------------------
		
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        m_iScnWidth = d.width - 100;
        m_iScnHeight = d.height - 100;
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(5, 5);
        this.setTitle("Slide Show Transitions Demonstration");
		this.setSize(m_iScnWidth, m_iScnHeight);
		this.setResizable(false);
		this.getContentPane().setLayout(null); // We'll do our own layouts, thank you.
		this.getContentPane().setBackground(Color.gray); // Set visible area to gray

		// Create the image panel
		m_ImagePanel = new ImagePanel(this);
		this.getContentPane().add(m_ImagePanel); // Add the panel to the window

		// Create the button panel
		m_ButtonPanel = new JPanel();
		m_ButtonPanel.setSize(this.getSize().width, 100);
		m_ButtonPanel.setLocation(0, this.getSize().height - 100);
		m_ButtonPanel.setBackground(Color.lightGray); // Set the panel color
		m_ButtonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		// Use the default Flow Layout manager
		this.getContentPane().add(m_ButtonPanel);
		
		
		// Create the select image directory button
                var image1 = ImageIO.read(getClass().getResource("/OpenDirectory.jpg"));
		m_SelectImageDirBtn = new JButton(new ImageIcon(image1));
//		m_SelectImageDirBtn = new JButton(new ImageIcon(getClass().getResource("OpenDirectory.jpg")));
		m_SelectImageDirBtn.setPreferredSize(new Dimension(40, 40));
		m_SelectImageDirBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_SelectImageDirBtn.setToolTipText("Click to select directory of images to view.");
		m_SelectImageDirBtn.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//	Handle getting the image directory to show
						getImageDir();
						if(m_sSlideshowFile != null)
						{
                                                   
                                                        buildImageList();
                                                        showImage(m_iCurImageIdx); // Show first image
                                                        filePath = m_vSoundNames.get(0);
                                                        try{
                                                        SimpleAudioPlayer audioPlayer = new SimpleAudioPlayer();
                                                        
                                                        audioPlayer.play();
                                                        }
                                                        catch (Exception ex)
                                                        {
                                                             System.out.println("Error with playing sound.");
                                                                ex.printStackTrace();                            
                                                        }
                                                 
						}
						// Are we doing a slideshow with timer?
						if(!m_bChangeManually)
						{
							doTimerSlideShow();
						}
					}
				});
		m_ButtonPanel.add(m_SelectImageDirBtn);	
		
		// Create the previous image button
                var pastArrowImg = ImageIO.read(getClass().getResource("/BackArrow.jpg"));
		m_PrevImageBtn = new JButton(new ImageIcon(pastArrowImg));
//		m_PrevImageBtn = new JButton(new ImageIcon(getClass().getResource("BackArrow.jpg")));
		m_PrevImageBtn.setPreferredSize(new Dimension(40, 40));
		m_PrevImageBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_PrevImageBtn.setToolTipText("View previous image.");
		m_PrevImageBtn.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//	Show the previous image
						showPreviousImage();
					}
				});
		m_ButtonPanel.add(m_PrevImageBtn);	
		
		// Create the next image button
                var nextArrowImg = ImageIO.read(getClass().getResource("/NextArrow.jpg"));
		m_NextImageBtn = new JButton(new ImageIcon(nextArrowImg));
//		m_NextImageBtn = new JButton(new ImageIcon(getClass().getResource("NextArrow.jpg")));
		m_NextImageBtn.setPreferredSize(new Dimension(40, 40));
		m_NextImageBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_NextImageBtn.setToolTipText("View next image.");
		m_NextImageBtn.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//	Show the next image
						showNextImage();
                                             
                                            
					}
				});
		m_ButtonPanel.add(m_NextImageBtn);	

		// Create the exit button
                var exitImg = ImageIO.read(getClass().getResource("/Exit.jpg"));
		m_ExitBtn = new JButton(new ImageIcon(exitImg));
//		m_ExitBtn = new JButton(new ImageIcon(getClass().getResource("Exit.jpg")));
		m_ExitBtn.setPreferredSize(new Dimension(40, 40));
		m_ExitBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_ExitBtn.setToolTipText("Click to exit the application.");
		m_ExitBtn.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						// Exit the application with status=0 (normal exit)
						System.exit(0);
					}
				});
		m_ButtonPanel.add(m_ExitBtn);	
		
		// Make the window visible
		this.setVisible(true);
	}
	
	//----------------------------------------------------------------------
	/** Show an open file dialog box in order to get the directory of
	 *   images to display. */
	//----------------------------------------------------------------------
	private void getImageDir()
	{
		int retValue;	// Return value from the JFileChooser
		
	     JFileChooser chooser = new JFileChooser();	// Create the file chooser dialog box
	     chooser.setDialogTitle("Select Slideshow File"); // Set dialog title
	     chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Only select dirs
	     chooser.setApproveButtonText("Select");
	     retValue = chooser.showOpenDialog(this); // Show the dialog box
	     if(retValue == JFileChooser.APPROVE_OPTION) // User selected a file
	     {
	    	 // Got a directory so get it's full path
	    	 m_sSlideshowFile = chooser.getSelectedFile().getAbsolutePath();
	     }
		System.out.println("File: " + m_sSlideshowFile);
	}
	
	//----------------------------------------------------------------------
	/** Build the list of images to show */
	//----------------------------------------------------------------------
	private void buildImageList()
	{
            System.out.println("this ran");
            
            
        // Create the vector of image file paths
        if(m_vImageNames != null) // If we already have one
        	m_vImageNames.removeAllElements(); // Clean it out
        else                      // If we don't have one
        	m_vImageNames = new Vector(); // Create a new one.
        
         // Create the vector of transition
        if(m_vTransitionLengths != null) // If we already have one
        	m_vTransitionLengths.removeAllElements(); // Clean it out
        else                      // If we don't have one
        	m_vTransitionLengths= new Vector(); // Create a new one.
               
        // Create the vector of transition
        if(m_vTransitions != null) // If we already have one
        	m_vTransitions.removeAllElements(); // Clean it out
        else                      // If we don't have one
        	m_vTransitions= new Vector(); // Create a new one.
               
        // Create the vector of transition
        if(m_vSoundNames != null) // If we already have one
        	m_vSoundNames.removeAllElements(); // Clean it out
        else                      // If we don't have one
        	m_vSoundNames= new Vector(); // Create a new one.
               
        
        try(FileReader fileReader = new FileReader(m_sSlideshowFile)){
            
            JSONParser jsonParser = new JSONParser();

            // Read JSON file
            Object obj = jsonParser.parse(fileReader);

            JSONObject jo = (JSONObject) obj;
            
            //set manual change boolean
            System.out.println(jo.get("changeManually"));  
            m_bChangeManually = jo.get("changeManually").equals("true");
            System.out.println(m_bChangeManually);
            
            //set image duration
            m_iTimeDelay =  Float.parseFloat((String) jo.get("imageDuration"));
            
            jo.get("imageDirectory");
            
            JSONArray images = (JSONArray) jo.get("images");
            images.forEach( i -> m_vImageNames.add((String) i));
            
            System.out.println(m_vImageNames);
            
            JSONArray transitions = (JSONArray) jo.get("transitions");
            transitions.forEach(t -> m_vTransitions.add(Integer.parseInt( (String) t)));
            
            JSONArray transitionLengths = (JSONArray) jo.get("transitionLengths");
            transitionLengths.forEach(l -> m_vTransitionLengths.add(Float.parseFloat( (String) l)));
            
            JSONArray sounds = (JSONArray) jo.get("sounds");
            sounds.forEach(s -> m_vSoundNames.add((String) s));
            
            System.out.println(m_vSoundNames);
            
            
        } catch(FileNotFoundException e){
            System.err.println("FileNotFoundException: " + e.getMessage());
        } catch(IOException e){
            System.err.println("IOException: " + e.getMessage());
        } catch(ParseException e){
            System.err.println("ParseException: " + e.getMessage());
        }
        
        m_iCurImageIdx = 0; // Initialize the current image index
    }
        
	
	//----------------------------------------------------------------------
	/** Show the image at index. */
	//----------------------------------------------------------------------
	private void showImage(int idx)
	{
        File		imageFile; // the jpg or gif file
		// Make sure we have an image file
        if((m_vImageNames.size() < 0) || (idx >= m_vImageNames.size()))
        {
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to display image " + idx + ". Does not exist.", 
					"Error Loading Image", JOptionPane.ERROR_MESSAGE);
			return;
        }
        imageFile = new File((String)(m_vImageNames.elementAt(idx)));
		if(!imageFile.exists()) // If we failed to opened it
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to load " + (String)(m_vImageNames.elementAt(idx)), 
					"Error Loading Image", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Load the image
        // Use ImageIO and pass a BufferedImage.TYPE_INT_RGB to ImagePanel
        if(m_TheImage != null)
        	m_TheImage = null; // Clear the previous image
        try
        {
//       
                
            DisplayImage newImg = new DisplayImage(this.getSize().width, this.getSize().height);
            BufferedImage dispImg = newImg.getDisplayImage(m_vImageNames.elementAt(idx));

            m_TheImage = dispImg;
       
            //m_TheImage = bi2;
     
        }
        catch (IOException e)
        {
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to load " + (String)(m_vImageNames.elementAt(idx)), 
					"Error Loading Image", JOptionPane.ERROR_MESSAGE);
			return;
        }
        m_ImagePanel.setImage(m_TheImage, m_vTransitions.elementAt(idx), m_vTransitionLengths.elementAt(idx));
	}
	
	//----------------------------------------------------------------------
	/** Show the previous image. */
	//----------------------------------------------------------------------
	private void showPreviousImage()
	{
		if(m_iCurImageIdx > 0)
		{
			m_iCurImageIdx--; // Decrement to previous image
			showImage(m_iCurImageIdx); // Show it
		}
	}
	
	//----------------------------------------------------------------------
	/** Show the next image. */
	//----------------------------------------------------------------------
	private void showNextImage()
	{
		if(m_iCurImageIdx < (m_vImageNames.size() - 1))
		{
			m_iCurImageIdx++; // Increment to next image
			showImage(m_iCurImageIdx); // Show it
		}
	}

	//----------------------------------------------------------------------
	/** Show the next image. */
	//----------------------------------------------------------------------
	private void doTimerSlideShow()
	{
		// Disable the previous and next buttons while the slideshow runs
		m_PrevImageBtn.setEnabled(false);
		m_NextImageBtn.setEnabled(false);
		
		// Create a javax.swing.timer
		m_SSTimer = new Timer((int) m_iTimeDelay * 1000,
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					// Show the next image
					if(m_iCurImageIdx < m_vImageNames.size() - 1)
					{
						showNextImage();
					}
					else
					{
						m_SSTimer.stop();
						// Enable the previous and next buttons again
						m_PrevImageBtn.setEnabled(true);
						m_NextImageBtn.setEnabled(true);
					}
				}
			});
		m_SSTimer.setRepeats(true); // Repeat till we kill it
		m_SSTimer.start();  // Start the timer
	}

	//----------------------------------------------------------------------
	/** Main function for this demonstration
	 * @param args - Array of strings from the command line
	 */
	//----------------------------------------------------------------------
	public static void main(String[] args) throws IOException 
	{
		// When you start this application this function gets called by the
		//  operating system.  Main just creates an ImageViewer object.
		//  To follow the execution trail from here go to the ImageViewer
		//  constructor.
            
		PlayerMain IV = new PlayerMain();
	}

}
