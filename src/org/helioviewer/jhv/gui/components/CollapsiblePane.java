package org.helioviewer.jhv.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.helioviewer.jhv.gui.IconBank;
import org.helioviewer.jhv.gui.IconBank.JHVIcon;
import org.helioviewer.jhv.gui.MainFrame;

/**
 * Panel managing a collapsible area.
 * 
 * <p>
 * This panel consists of a toggle button and one arbitrary component. Clicking
 * the toggle button will toggle the visibility of the component.
 */
class CollapsiblePane extends JComponent implements ActionListener
{
	private static final ImageIcon ICON_EXPANDED = IconBank.getIcon(JHVIcon.NEW_DOWN, 16, 16);
	private static final ImageIcon ICON_COLLAPSED = IconBank.getIcon(JHVIcon.NEW_RIGHT, 16, 16);

	private JToggleButton toggleButton;
	private Component component;

	/**
	 * Default constructor.
	 * 
	 * @param title
	 *            Text on the toggle button
	 * @param component
	 *            Component to manage
	 * @param startExpanded
	 *            if true, the component will be visible right from the start
	 */
	public CollapsiblePane(String title, Component component, boolean startExpanded)
	{
		setLayout(new BorderLayout());

		toggleButton = new JToggleButton(title);
		toggleButton.setHorizontalAlignment(SwingConstants.LEFT);
		toggleButton.setSelected(startExpanded);
		toggleButton.setFont(new Font("SansSerif", Font.BOLD, 12));
		if (startExpanded)
		{
			toggleButton.setIcon(IconBank.getIcon(JHVIcon.NEW_DOWN, 16, 16));
		}
		else
		{
			toggleButton.setIcon(IconBank.getIcon(JHVIcon.RIGHT2));
		}
		toggleButton.setPreferredSize(new Dimension(0, (int) toggleButton.getPreferredSize().getHeight()));
		toggleButton.addActionListener(this);

		this.component = component;
		component.setVisible(startExpanded);

		add(toggleButton, BorderLayout.PAGE_START);
		add(component, BorderLayout.CENTER);

		setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
	}

	/**
	 * Sets the text on the toggle button
	 * 
	 * @param title
	 *            Text on the toggle button
	 */
	public void setTitle(final String title)
	{
		toggleButton.setText(title);
	}

	/**
	 * Expands the pane.
	 */
	public void expand()
	{
		toggleButton.setSelected(true);
		component.setVisible(true);
		toggleButton.setIcon(ICON_EXPANDED);
		MainFrame.SINGLETON.getContentPane().revalidate();
	}

	/**
	 * Collapses the pane.
	 */
	public void collapse()
	{
		toggleButton.setSelected(false);
		component.setVisible(false);
		toggleButton.setIcon(ICON_COLLAPSED);
		MainFrame.SINGLETON.getContentPane().revalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(@Nullable ActionEvent e)
	{
		if (component.isVisible())
		{
			collapse();
		}
		else
		{
			expand();
		}
		setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
	}
}
