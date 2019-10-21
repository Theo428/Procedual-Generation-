package main;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends Canvas
{
	public static final int MAX_WIDTH = 1080;
	public static final int MAX_HEIGHT = 1080;

	public static final int MIN_WIDTH = 1080;
	public static final int MIN_HEIGHT = 1080;
	
	public Window(int width, int height, String title, Game game)
	{
		JFrame frame = new JFrame(title);
		
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		frame.add(game);
		
		frame.setVisible(true);
	}
}
